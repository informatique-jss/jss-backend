package com.jss.osiris.libs.mail;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;

import jakarta.mail.Folder;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;

@Service
public class MailSharedDelegate {

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
    @Value("${mail.imap.shared.folder.order.input.formalite}")
    private String inputOrderFormaliteFolder;
    @Value("${mail.imap.shared.folder.order.input.announcement}")
    private String inputOrderAnnouncementFolder;
    @Value("${mail.imap.shared.folder.order.input.classement}")
    private String inputClassementFolder;

    private Store store = null;
    private Folder folderInboxFormalite = null;
    private Folder folderInboxAnnouncement = null;
    private Folder folderTrash = null;
    private Folder folderClassement = null;
    private final ReentrantLock mailboxLock = new ReentrantLock();

    public <T> T executeWithLock(Callable<T> action) throws OsirisException {
        try {
            if (mailboxLock.tryLock(5, TimeUnit.MINUTES)) {
                try {
                    connectMailbox();
                    return action.call();
                } finally {
                    closeStore();
                    mailboxLock.unlock();
                }
            } else {
                throw new OsirisException(null, "Lock treatment in mailbox");
            }
        } catch (Exception e) {
            if (e instanceof OsirisException)
                throw (OsirisException) e;
            throw new OsirisException(e, "Error when maillocking");
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
        properties.put("mail.imap.connectiontimeout", "60000");
        properties.put("mail.imap.timeout", "60000");
        properties.put("mail.imap.connectionpoolsize", "5");
        properties.put("mail.imap.fetchsize", "819200");

        Session session = Session.getInstance(properties, null);
        return session;
    }

    private void connectMailbox() throws OsirisException {
        try {
            if (store == null || !store.isConnected()) {
                String accessToken = getAccessToken();
                store = getSessionToAccessMail().getStore("imap");

                store.connect(mailImapHost, Integer.parseInt(mailImapPort), sharedMailboxUsername, accessToken);
            } else {
                try {
                    store.getPersonalNamespaces();
                } catch (MessagingException e) {
                    closeStore();
                    throw new OsirisException(e, "IMAP store lost or failed");
                }
            }

            store.getPersonalNamespaces();
            folderInboxFormalite = ensureOpen(folderInboxFormalite, inputOrderFormaliteFolder);
            folderClassement = ensureOpen(folderClassement, inputClassementFolder);
            folderInboxAnnouncement = ensureOpen(folderInboxAnnouncement, inputOrderAnnouncementFolder);
            folderTrash = ensureOpen(folderTrash, "Deleted Items");
        } catch (MessagingException e) {
            try {
                store.close();
            } catch (MessagingException e1) {
                try {
                    closeStore();
                } catch (MessagingException e2) {
                    throw new OsirisException(e, "IMAP Connection lost or failed");
                }
                throw new OsirisException(e, "IMAP Connection lost or failed");
            }
            try {
                closeStore();
            } catch (MessagingException e1) {
                throw new OsirisException(e, "IMAP Connection lost or failed");
            }
            throw new OsirisException(e, "IMAP Connection lost or failed");
        }
    }

    private Folder ensureOpen(Folder folder, String folderName) throws MessagingException {
        if (folder == null) {
            folder = store.getFolder(folderName);
        }
        if (!folder.isOpen()) {
            folder.open(Folder.READ_WRITE);
        }
        return folder;
    }

    public Folder getFolderInboxFormalite() throws OsirisException {
        return folderInboxFormalite;
    }

    public Folder getFolderInboxAnnouncement() throws OsirisException {
        return folderInboxAnnouncement;
    }

    public Folder getFolderTrash() throws OsirisException {
        return folderTrash;
    }

    public Folder getFolderClassement() throws OsirisException {
        return folderClassement;
    }

    private void closeStore() throws MessagingException {
        if (store != null)
            store.close();
        store = null;
        folderInboxFormalite = null;
        folderClassement = null;
        folderInboxAnnouncement = null;
        folderTrash = null;
    }
}
