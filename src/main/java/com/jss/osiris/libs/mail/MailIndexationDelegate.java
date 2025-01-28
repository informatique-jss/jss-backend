package com.jss.osiris.libs.mail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import jakarta.mail.Multipart;
import jakarta.mail.NoSuchProviderException;
import jakarta.mail.Session;
import jakarta.mail.Store;

@Service
public class MailIndexationDelegate {

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

    @Autowired
    OsirisMailService osirisMailService;

    @Autowired
    BatchService batchService;

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

    public void exportMailToFile(Integer id) throws OsirisException, IOException {
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
                    Address[] fromAddresses = message.getFrom();
                    Address[] toAddresses = message.getAllRecipients();
                    String from = String.join(" / ",
                            Arrays.asList(fromAddresses).stream().map(Address::toString).toList());
                    String to = String.join(" / ",
                            Arrays.asList(toAddresses).stream().map(Address::toString).toList());
                    String mailHtml = autoCloseTags(getHtmlContent(message)).replace("</head>",
                            "</head><div><p>Objet : " + message.getSubject() + "</p><br><p>De : "
                                    + from.replaceAll("<", "").replaceAll(">", "")
                                    + "</p><p>A :"
                                    + to.replaceAll("<", "").replaceAll(">", "")
                                    + "</p></div>")
                            .replaceAll("(?m)^(\\s*)<(\\w+)>([^<]+)$", "$1<$2>$3</$2>");

                    mail.setSubject(message.getSubject());
                    mail.setId(id);
                    mail.setMailPdf(new ByteArrayInputStream(mailHtml.getBytes()));

                    if (osirisMailService.attachIndexationMailToEntity(mail)) {
                        // Move to trash
                        folderInbox.copyMessages(new Message[] { message }, folderTrash);
                        message.setFlag(Flags.Flag.DELETED, true);
                        return;
                    }
                }
            } catch (Exception e) {
                throw new OsirisException(e, "Impossible to process message received");
            }
        }

        closeConnection(store, folderInbox, folderTrash);
    }

    public void purgeDeletedElements() throws OsirisException {
        // LocalDate purgeDate = LocalDate.now().minus(30, ChronoUnit.DAYS);

        Store store = null;
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
            folderTrash = store.getFolder("Éléments supprimés");
            folderTrash.open(Folder.READ_WRITE);
            folderTrash.expunge();
            folderTrash.close();
            store.close();
        } catch (MessagingException e) {
            throw new OsirisException(e, "Impossible to write into Trash folder");
        }
    }

    private String autoCloseTags(String html) {
        html = html.replace("text/html", "texthtml");
        html = html.replace("span.", "").replace("div.", "").replace(":&quot;", ":").replace("&quot;,", ",")
                .replace("mso-ignore:vglayout", "").replace("mso-ignore:vglayout;", "");
        String selfClosingTags = "img|br|hr|input|meta|link|area|base|col|command|embed|keygen|param|source|track|wbr";
        Pattern tagPattern = Pattern
                .compile("<(" + selfClosingTags + ")([^>/]*?)>");
        Matcher matcher = tagPattern.matcher(html);
        StringBuffer correctedHtml = new StringBuffer();
        while (matcher.find()) {
            String tagName = matcher.group(1);
            String attributes = matcher.group(2);
            matcher.appendReplacement(correctedHtml, "<" + tagName + attributes + " />");
        }

        matcher.appendTail(correctedHtml);

        return correctedHtml.toString();
    }

    private String getHtmlContent(Message message) throws Exception, MessagingException,
            IOException {
        String htmlContent = "";
        if (message.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) message.getContent();
            htmlContent = processMultipart(multipart);
        }
        return htmlContent;
    }

    private static String processMultipart(Multipart multipart) throws Exception {
        StringBuilder htmlBuilder = new StringBuilder();
        Map<String, String> base64Images = new HashMap<>();

        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart part = multipart.getBodyPart(i);

            if ("inline".equalsIgnoreCase(part.getDisposition()) && part.getContentType().contains("image")) {
                String cid = extractCid(part);
                String base64 = convertToBase64(part.getInputStream());
                base64Images.put(cid, base64);
            }

            if (part.isMimeType("text/html")) {
                String content = (String) part.getContent();
                htmlBuilder.append(content);
            }
        }

        String htmlContent = htmlBuilder.toString();
        for (Map.Entry<String, String> entry : base64Images.entrySet()) {
            String cid = entry.getKey();
            String base64 = entry.getValue();
            htmlContent = htmlContent.replace("cid:" + cid, "data:image/png;base64," + base64);
        }
        return htmlContent;
    }

    private static String extractCid(BodyPart part) throws MessagingException {
        String[] disposition = part.getHeader("Content-ID");
        if (disposition != null && disposition.length > 0) {
            String cid = disposition[0];
            return cid.substring(1, cid.length() - 1);
        }
        return null;
    }

    private static String convertToBase64(InputStream is) throws IOException {
        byte[] bytes = is.readAllBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }
}