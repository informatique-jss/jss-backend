package com.jss.osiris.libs.mail;

import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.mail.model.MailTracker;
import com.jss.osiris.libs.mail.repository.MailTrackerRepository;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;

import jakarta.mail.Address;
import jakarta.mail.FetchProfile;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.NoSuchProviderException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.UIDFolder;

@Service
public class MailTrackerDelegate {

    @Value("${mail.imap.host}")
    private String mailImapHost;
    @Value("${mail.imap.port}")
    private String mailImapPort;
    @Value("${mail.imap.username}")
    private String mailImapUsername;
    @Value("${mail.imap.app.id}")
    private String mailImapAppId;
    @Value("${mail.imap.tenant.id}")
    private String mailImapTenantId;
    @Value("${mail.imap.secret.value}")
    private String mailImapSecretValue;
    @Value("${mail.imap.auth}")
    private String mailImapAuth;
    @Value("${mail.imap.ssl.enable}")
    private String mailImapEnable;
    @Value("${mail.imap.auth.mechanisms}")
    private String mailImapMechanism;
    @Value("${mail.imap.tls.version}")
    private String mailImapTlsVersion;
    @Value("${microsoft.host}")
    private String microsoftHost;
    @Value("${outlook.default.url}")
    private String outlookDefaultUrl;
    @Value("${mail.imap.shared.username}")
    private String sharedMailboxUsername;
    @Value("${mail.imap.shared.folder.order.input.classement}")
    private String inputClassementFolder;

    private Store store = null;
    private Folder folderClassement = null;

    @Autowired
    private MailTrackerRepository repository;

    public void performFullScan() throws MessagingException, OsirisException {
        LocalDateTime currentScanTime = LocalDateTime.now();

        connectMailbox();

        scanAllFolders(folderClassement, currentScanTime);

        markExitedMailsInJava(currentScanTime);
    }

    private void scanAllFolders(Folder rootFolder, LocalDateTime scanTime) throws MessagingException {
        if ((rootFolder.getType() & Folder.HOLDS_FOLDERS) != 0) {
            for (Folder subFolder : rootFolder.list()) {
                scanAllFolders(subFolder, scanTime);
            }
        }

        if ((rootFolder.getType() & Folder.HOLDS_MESSAGES) != 0) {
            processFolderMessages(rootFolder, scanTime);
        }
    }

    private void processFolderMessages(Folder folder, LocalDateTime scanTime) throws MessagingException {
        try {
            if (!folder.isOpen())
                folder.open(Folder.READ_ONLY);
            UIDFolder uidFolder = (UIDFolder) folder;

            Message[] messages = folder.getMessages();
            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            folder.fetch(messages, fp);

            String currentFolderPath = folder.getFullName();

            for (Message msg : messages) {
                long uid = uidFolder.getUID(msg);

                MailTracker activeRecord = repository.findByUidAndExitDateIsNull(uid);

                if (activeRecord == null) {
                    createNewRecord(uid, msg.getSubject(), currentFolderPath, scanTime, msg.getReceivedDate(),
                            String.join(" / ", Arrays.asList(msg.getFrom()).stream().map(Address::toString).toList()));
                } else {
                    if (activeRecord.getCurrentFolder().equals(currentFolderPath)) {
                        activeRecord.setLastSeen(scanTime);
                        repository.save(activeRecord);
                    } else {
                        activeRecord.setExitDate(scanTime);
                        repository.save(activeRecord);

                        createNewRecord(uid, msg.getSubject(), currentFolderPath, scanTime, msg.getReceivedDate(),
                                String.join(" / ",
                                        Arrays.asList(msg.getFrom()).stream().map(Address::toString).toList()));
                    }
                }
            }
        } finally {
            if (folder.isOpen()) {
                folder.close(false);
            }
        }
    }

    private void createNewRecord(long uid, String subject, String folderName, LocalDateTime scanTime,
            Date receivedDate, String fromRecipient) {
        MailTracker tracker = new MailTracker();
        tracker.setUid(uid);
        tracker.setSubject(subject);
        tracker.setCurrentFolder(folderName);
        tracker.setEntryDate(scanTime);
        tracker.setLastSeen(scanTime);
        tracker.setFromRecipient(fromRecipient);
        tracker.setReceivedDate(receivedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        repository.save(tracker);
    }

    private void markExitedMailsInJava(LocalDateTime currentScanTime) {
        List<MailTracker> activeMails = repository.findByExitDateIsNull();

        for (MailTracker mail : activeMails) {
            if (mail.getLastSeen().isBefore(currentScanTime)) {
                mail.setExitDate(currentScanTime);
                repository.save(mail);
            }
        }
    }

    private String getAccessToken() throws OsirisException {
        try {
            ConfidentialClientApplication app = ConfidentialClientApplication
                    .builder(mailImapAppId, ClientCredentialFactory.createFromSecret(mailImapSecretValue))
                    .authority(microsoftHost + mailImapTenantId).build();
            ClientCredentialParameters parameters = ClientCredentialParameters
                    .builder(Collections.singleton(outlookDefaultUrl)).build();

            IAuthenticationResult result = app.acquireToken(parameters).join();
            return result.accessToken();

        } catch (MalformedURLException ex) {
            throw new OsirisException(ex, "Wrong client-id or client-secret");
        }
    }

    private Session getSessionToAccessMail() {
        Properties properties = new Properties();
        properties.setProperty("mail.imap.ssl.protocols", mailImapTlsVersion);
        properties.put("mail.imap.ssl.enable", mailImapEnable);
        properties.put("mail.imap.ssl.trust", "*");
        properties.put("mail.imap.starttls.enable", "true");
        properties.put("mail.imap.auth", mailImapAuth);
        properties.put("mail.imap.auth.mechanisms", mailImapMechanism);

        Session session = Session.getInstance(properties, null);
        return session;
    }

    private void connectMailbox() throws OsirisException {
        if (store == null || !store.isConnected()) {
            String accessToken = getAccessToken();
            try {
                store = getSessionToAccessMail().getStore("imap");
            } catch (NoSuchProviderException e) {
                throw new OsirisException(e, "IMAP store not found");
            }

            try {
                store.connect(mailImapHost, Integer.parseInt(mailImapPort), sharedMailboxUsername,
                        accessToken);
            } catch (NumberFormatException e) {
                throw new OsirisException(e, "Malformated connection to IMAP");
            } catch (MessagingException e) {
                throw new OsirisException(e, "Impossible to connect to IMAP");
            }
        }

        if (folderClassement != null && folderClassement.isOpen())
            try {
                folderClassement.close();
            } catch (MessagingException e) {
                throw new OsirisException(e, "Impossible to close INBOX folder");
            }

        try {
            folderClassement = store.getFolder(inputClassementFolder);
        } catch (MessagingException e) {
            throw new OsirisException(e, "Impossible to find INBOX folder");
        }
        try {
            folderClassement.open(Folder.READ_WRITE);
        } catch (MessagingException e) {
            throw new OsirisException(e, "Impossible to write into INBOX folder");
        }
    }

}