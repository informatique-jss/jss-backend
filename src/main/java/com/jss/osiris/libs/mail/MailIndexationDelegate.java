package com.jss.osiris.libs.mail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.mail.model.IndexationMail;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;

import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.NoSuchProviderException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.MimeMultipart;

@Service
public class MailIndexationDelegate {

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
    @Value("${mail.imap.tls.version}")
    private String mailImapTlsVersion;

    @Autowired
    OsirisMailService osirisMailService;

    @Autowired
    BatchService batchService;

    @Autowired
    GeneratePdfDelegate generatePdfDelegate;

    private String getAccessToken() throws OsirisException {
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
        properties.setProperty("mail.imap.ssl.protocols", mailImapTlsVersion);
        properties.put("mail.imap.ssl.enable", mailImapEnable);
        properties.put("mail.imap.ssl.trust", "*");
        properties.put("mail.imap.starttls.enable", "true");
        properties.put("mail.imap.auth", mailImapAuth);
        properties.put("mail.imap.auth.mechanisms", mailImapMechanism);

        Session session = Session.getInstance(properties, null);
        return session;
    }

    public void getPdfMailsFromInbox() throws OsirisException {
        Store store = null;
        Folder folder = null;

        String accessToken = getAccessToken();
        try {
            store = getSessionToAccessMail().getStore("imap");
        } catch (NoSuchProviderException e) {
            throw new OsirisException(e, "IMAP store not found");
        }

        try {
            store.connect(mailImapHost, Integer.parseInt(mailImapPort), mailImapUsername, accessToken);
        } catch (NumberFormatException e) {
            throw new OsirisException(e, "Malformated connection to IMAP");
        } catch (MessagingException e) {
            throw new OsirisException(e, "Impossible to connect to IMAP");
        }
        try {
            folder = store.getFolder("INBOX");
        } catch (MessagingException e) {
            throw new OsirisException(e, "Impossible to find INBOX folder");
        }
        try {
            folder.open(Folder.READ_WRITE);
        } catch (MessagingException e) {
            throw new OsirisException(e, "Impossible to write into INBOX folder");
        }

        Message[] messages;
        try {
            messages = folder.getMessages();
        } catch (MessagingException e) {
            throw new OsirisException(e, "Impossible to get messages from INBOX folder");
        }

        for (Message message : messages) {
            // TODO : declare batch
            try {
                batchService.declareNewBatch(Batch.INDEX_MAIL_TO_ENTITY,
                        (Integer.parseInt((message.getReceivedDate().getTime() / 1000) + "")));
            } catch (MessagingException e) {
                throw new OsirisException(e, "Impossible to process message received");
            }
        }

        closeConnection(store, folder, null);
    }

    public void closeConnection(Store store, Folder folder, Folder folderSecondary) throws OsirisException {
        try {
            folder.close(true);
            if (folderSecondary != null)
                folderSecondary.close(true);
            store.close();
        } catch (MessagingException e) {
            throw new OsirisException(e, "Error when closing connection folder/store");
        }
    }

    public void exportMailToFile(Integer id) throws OsirisException {
        IndexationMail mail = new IndexationMail();

        Store store = null;
        Folder folderInbox = null;
        Folder folderTrash = null;

        String accessToken = getAccessToken();
        try {
            store = getSessionToAccessMail().getStore("imap");
        } catch (NoSuchProviderException e) {
            throw new OsirisException(e, "IMAP store not found");
        }

        try {
            store.connect(mailImapHost, Integer.parseInt(mailImapPort), mailImapUsername, accessToken);
        } catch (NumberFormatException e) {
            throw new OsirisException(e, "Malformated connection to IMAP");
        } catch (MessagingException e) {
            throw new OsirisException(e, "Impossible to connect to IMAP");
        }
        try {
            folderInbox = store.getFolder("INBOX");
        } catch (MessagingException e) {
            throw new OsirisException(e, "Impossible to find INBOX folder");
        }
        try {
            folderInbox.open(Folder.READ_WRITE);
        } catch (MessagingException e) {
            throw new OsirisException(e, "Impossible to write into INBOX folder");
        }

        try {
            folderTrash = store.getFolder("Éléments supprimés");
        } catch (MessagingException e) {
            throw new OsirisException(e, "Impossible to find Trash folder");
        }
        try {
            folderTrash.open(Folder.READ_WRITE);
        } catch (MessagingException e) {
            throw new OsirisException(e, "Impossible to write into Trash folder");
        }

        Message[] messages;
        try {
            messages = folderInbox.getMessages();
        } catch (MessagingException e) {
            throw new OsirisException(e, "Impossible to get messages from INBOX folder");
        }

        for (Message message : messages) {
            try {
                if (Integer.parseInt((message.getReceivedDate().getTime() / 1000) + "") == id) {
                    // Generate PDF
                    try {
                        Address[] fromAddresses = message.getFrom();
                        Address[] toAddresses = message.getAllRecipients();
                        String from = String.join(" / ",
                                Arrays.asList(fromAddresses).stream().map(Address::toString).toList());
                        String to = String.join(" / ",
                                Arrays.asList(toAddresses).stream().map(Address::toString).toList());

                        mail.setMailPdf(new FileInputStream(
                                generatePdfDelegate.generateGenericFromHtml(
                                        getHtmlContent(message).replace("</head>",
                                                "</head><div><p>De : " + from.replaceAll("<", "").replaceAll(">", "")
                                                        + "</p><p>A :"
                                                        + to.replaceAll("<", "").replaceAll(">", "")
                                                        + "</p></div>"),
                                        id)));
                    } catch (FileNotFoundException e) {
                        throw new OsirisException(e, "Temp file not found");
                    } catch (IOException e) {
                        throw new OsirisException(e, "Impossible to read temp file");
                    }

                    mail.setSubject(message.getSubject());
                    mail.setId(id);

                    if (osirisMailService.attachIndexationMailToEntity(mail)) {
                        // Move to trash
                        folderInbox.copyMessages(new Message[] { message }, folderTrash);
                        message.setFlag(Flags.Flag.DELETED, true);
                        return;
                    }
                }
            } catch (MessagingException e) {
                throw new OsirisException(e, "Impossible to process message received");
            }
        }

        closeConnection(store, folderInbox, folderTrash);
    }

    // TODO : batch quotidient la nuit à faire que sur la corbeille
    private Boolean purgeDeletedElements(Message message) throws OsirisException {
        LocalDate purgeDate = LocalDate.now().minus(30, ChronoUnit.DAYS);
        try {
            Date receivedDate = message.getReceivedDate();
            // LocalDate messageDate = convertToLocalDate(receivedDate);
            LocalDate messageDate = LocalDate.now();
            if (messageDate.isBefore(purgeDate)) {
                message.setFlag(Flags.Flag.DELETED, true);
                return true;
            }
        } catch (MessagingException e) {
            throw new OsirisException(e, "no received date in mail");
        }
        return false;
    }

    public String getHtmlContent(Message message) throws MessagingException, IOException {
        Object content = message.getContent();

        if (content instanceof String) {
            // If text/plain ou text/html
            return (String) content;
        } else if (content instanceof MimeMultipart) {
            // If multipart
            MimeMultipart multipart = (MimeMultipart) content;
            return getHtmlFromMultipart(multipart);
        }
        return null;
    }

    private String getHtmlFromMultipart(MimeMultipart multipart) throws MessagingException, IOException {
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/html")) {
                // get HTML
                return (String) bodyPart.getContent();
            } else if (bodyPart.isMimeType("multipart/*")) {
                return getHtmlFromMultipart((MimeMultipart) bodyPart.getContent());
            }
        }
        return null;
    }
}