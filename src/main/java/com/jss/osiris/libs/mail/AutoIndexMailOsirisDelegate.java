package com.jss.osiris.libs.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.mail.model.ExportOsirisMail;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;

import jakarta.mail.Address;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;

@org.springframework.stereotype.Service
public class AutoIndexMailOsirisDelegate {

    @Value("${mail.imap.host}")
    private String mailImapHost;
    @Value("${mail.imap.port}")
    private String mailImapPort;
    @Value("${mail.imap.username}")
    private String mailImapUsername;
    @Value("${mail.imap.password}")
    private String mailImapPassword;
    @Value("${mail.imap.app.id}")
    private String mailImapAppId;
    @Value("${mail.imap.tenant.id}")
    private String mailImapTenantId;
    @Value("${mail.imap.secret.value}")
    private String mailImapSecretValue;
    @Value("${mail.imap.secret.id}")
    private String mailImapSecretId;
    @Value("${mail.imap.auth}")
    private String mailImapAuth;
    @Value("${mail.imap.ssl.enable}")
    private String mailImapEnable;
    @Value("${mail.imap.auth.mechanisms}")
    private String mailImapMechanism;

    private String getTokenConnectionOutlookOsiris() throws OsirisException {
        try {
            ConfidentialClientApplication app = ConfidentialClientApplication
                    .builder(mailImapAppId, ClientCredentialFactory.createFromSecret(mailImapSecretValue))
                    .authority("https://login.microsoftonline.com/" + mailImapTenantId).build();
            ClientCredentialParameters parameters = ClientCredentialParameters
                    .builder(Collections.singleton("https://outlook.office365.com/.default")).build();

            IAuthenticationResult result = app.acquireToken(parameters).join();
            return result.accessToken();

        } catch (MalformedURLException ex) {
            throw new OsirisException(ex, "Wrong client-id or client-secret");
        }
    }

    private Session getSessionOutlookOsiris() {
        Properties properties = new Properties();
        properties.put("mail.imap.ssl.enable", mailImapEnable);
        properties.put("mail.imap.auth", mailImapAuth);
        properties.put("mail.imap.auth.mechanisms", mailImapMechanism);
        Session session = Session.getInstance(properties, null);
        return session;
    }

    private ExportOsirisMail exportMailToFile(Message message) throws OsirisException {
        try {
            String subject = message.getSubject();
            Address[] fromAddresses = message.getFrom();
            Address[] toAddresses = message.getReplyTo();
            Date receivedDate = message.getReceivedDate();
            String from = fromAddresses[0].toString();
            String to = toAddresses[0].toString();

            String body = "";
            if (message.getContent() != null) {
                if (message.isMimeType("text/plain"))
                    body = (String) message.getContent();
                else if (message.isMimeType("text/html") && message.getContent() != null)
                    body = (String) message.getContent();
            }

            ExportOsirisMail exportedMail = new ExportOsirisMail();
            File tempFile2 = File.createTempFile("Mail_" + subject, "pdf");
            try (FileOutputStream outputStream = new FileOutputStream(tempFile2)) {
                byte[] contentBytes = body.getBytes();
                outputStream.write(contentBytes);
                exportedMail.setExportedMail(outputStream);
            }
            exportedMail.setSubjectMail(subject);
            exportedMail.setFileName(tempFile2.getName());
            return exportedMail;

        } catch (IOException e) {
            throw new OsirisException(e, "no content in mail");
        } catch (MessagingException e) {
            throw new OsirisException(e, "wrong login or password");
        }
    }

    private Boolean purgeInboxMailOsiris(Message message) throws OsirisException {
        LocalDate purgeDate = LocalDate.now().minus(30, ChronoUnit.DAYS);
        try {
            Date receivedDate = message.getReceivedDate();
            LocalDate messageDate = convertToLocalDate(receivedDate);
            if (messageDate.isBefore(purgeDate)) {
                message.setFlag(Flags.Flag.DELETED, true);
                return true;
            }
        } catch (MessagingException e) {
            throw new OsirisException(e, "no received date in mail");
        }
        return false;
    }

    private static LocalDate convertToLocalDate(Date date) {
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public List<ExportOsirisMail> getPdfMailsFromJavaMailImap() throws OsirisException {
        List<ExportOsirisMail> mailExports = new ArrayList<>();

        String accessToken = getTokenConnectionOutlookOsiris();
        Store store;
        Folder folder;
        try {
            store = getSessionOutlookOsiris().getStore("imap");
            store.connect(mailImapHost, Integer.parseInt(mailImapPort), mailImapUsername, accessToken);
        } catch (NumberFormatException | MessagingException e) {
            throw new OsirisException(e, "no imap store");
        }
        try {
            store.connect(mailImapHost, mailImapUsername, mailImapPassword);
            folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            Message[] messages = folder.getMessages();
            for (Message message : messages) {
                if (!purgeInboxMailOsiris(message))
                    mailExports.add(exportMailToFile(message));
            }
            folder.close(false);
            store.close();
        } catch (MessagingException e) {
            throw new OsirisException(e, "wrong login or password");
        }
        return mailExports;
    }
}