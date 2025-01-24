package com.jss.osiris.libs.mail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    private Store store;
    private Folder folder;

    private String getTokenConnectionMail() throws OsirisException {
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

    private Session getSessionToAccessMail() {
        Properties properties = new Properties();
        properties.setProperty("mail.imap.ssl.protocols", "TLSv1.2");
        properties.put("mail.imap.ssl.enable", mailImapEnable);
        properties.put("mail.imap.ssl.trust", "*");
        properties.put("mail.imap.starttls.enable", "true");
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
            InputStream file = message.getInputStream();
            String body = "";
            if (message.getContent() != null) {
                if (message.isMimeType("text/plain"))
                    body = (String) message.getContent();
                else if (message.isMimeType("text/html") && message.getContent() != null)
                    body = (String) message.getContent();
            }

            ExportOsirisMail exportedMail = new ExportOsirisMail();
            byte[] contentBytes = message.getInputStream().readAllBytes();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(contentBytes);
            out.close();
            InputStream in = new ByteArrayInputStream(out.toByteArray());
            exportedMail.setExportedMail(contentBytes);
            exportedMail.setSubjectMail(subject);
            exportedMail.setFileName("Mail_" + subject + ".pdf");
            exportedMail.setMailContent(in);
            return exportedMail;

        } catch (IOException e) {
            throw new OsirisException(e, "no content in mail");
        } catch (MessagingException e) {
            throw new OsirisException(e, "wrong login or password");
        }
    }

    private Boolean purgeInboxMail(Message message) throws OsirisException {
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

    public List<ExportOsirisMail> getPdfMailsFromInbox() throws OsirisException {
        List<ExportOsirisMail> mailExports = new ArrayList<>();
        try {

            String accessToken = getTokenConnectionMail();
            store = getSessionToAccessMail().getStore("imap");
            store.connect(mailImapHost, Integer.parseInt(mailImapPort), mailImapUsername, accessToken);
        } catch (NumberFormatException | MessagingException e) {
            throw new OsirisException(e, "no imap store");
        }
        try {
            folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);

            Message[] messages = folder.getMessages();
            for (Message message : messages) {
                if (!purgeInboxMail(message) && !message.getFlags().contains(Flags.Flag.SEEN)) {
                    message.setFlags(new Flags(Flags.Flag.SEEN), true);
                    mailExports.add(exportMailToFile(message));
                }
            }

        } catch (MessagingException e) {
            throw new OsirisException(e, "wrong login or password");
        }
        return mailExports;
    }

    public void closeConnection() throws OsirisException {
        try {
            folder.close(false);
            store.close();
        } catch (MessagingException e) {
            throw new OsirisException(e, "error closing connection folder/store");
        }
    }
}