package com.jss.osiris.libs.mail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.MailService;
import com.jss.osiris.modules.osiris.quotation.controller.QuotationValidationHelper;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderCommentService;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderStatusService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;

import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.Flags.Flag;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.NoSuchProviderException;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.MimeUtility;

@Service
public class OrderMailIndexationDelegate {

    @Autowired
    CustomerOrderCommentService customerOrderCommentService;

    @Autowired
    ResponsableService responsableService;

    @Autowired
    ConstantService constantService;

    @Autowired
    CustomerOrderStatusService customerOrderStatusService;

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
    @Value("${mail.imap.shared.folder.order.input}")
    private String inputOrderFolder;

    private Integer numberDaysToKeepInTrash = 60;

    private Store store = null;
    private Folder folderInbox = null;
    private Folder folderTrash = null;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}",
            Pattern.CASE_INSENSITIVE);

    @Autowired
    BatchService batchService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    QuotationValidationHelper quotationValidationHelper;

    @Autowired
    MailService mailService;

    @Autowired
    AttachmentService attachmentService;

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

        if (folderInbox != null && folderInbox.isOpen())
            try {
                folderInbox.close();
            } catch (MessagingException e) {
                throw new OsirisException(e, "Impossible to close INBOX folder");
            }

        try {
            folderInbox = store.getFolder(inputOrderFolder);
        } catch (MessagingException e) {
            throw new OsirisException(e, "Impossible to find INBOX folder");
        }
        try {
            folderInbox.open(Folder.READ_WRITE);
        } catch (MessagingException e) {
            throw new OsirisException(e, "Impossible to write into INBOX folder");
        }

        if (folderTrash != null && folderTrash.isOpen())
            try {
                folderTrash.close();
            } catch (MessagingException e) {
                throw new OsirisException(e, "Impossible to close TRASH folder");
            }

        try {
            String trashName = "Éléments supprimés";
            for (Folder f : store.getDefaultFolder().list())
                if (f.getFullName().equals("Deleted Items")) {
                    trashName = "Deleted Items";
                    break;
                }
            folderTrash = store.getFolder(trashName);
        } catch (MessagingException e) {
            throw new OsirisException(e, "Impossible to find Trash folder");
        }
        try {
            folderTrash.open(Folder.READ_WRITE);
        } catch (MessagingException e) {
            throw new OsirisException(e, "Impossible to write into Trash folder");
        }
    }

    public void checkMailsToIndex() throws OsirisException {
        connectMailbox();
        Message[] messages;
        try {
            messages = folderInbox.getMessages();
        } catch (MessagingException e) {
            throw new OsirisException(e, "Impossible to get messages from INBOX folder");
        }
        // for all messages received in inbox folder, launch a batch calling method
        // exportMailToFile()
        for (Message message : messages) {
            try {
                batchService.declareNewBatch(Batch.CREATE_ORDER_FROM_MAIL,
                        (Integer.parseInt((message.getReceivedDate().getTime() / 1000) + "")));
            } catch (MessagingException e) {
                throw new OsirisException(e, "Impossible to process message received");
            }
        }

    }

    public void closeConnection() throws OsirisException {
        try {
            if (store.isConnected()) {
                if (folderInbox != null)
                    folderInbox.close(true);
                if (folderTrash != null)
                    folderTrash.close(true);
                if (store != null)
                    store.close();
            }
        } catch (MessagingException e) {
            throw new OsirisException(e, "Error when closing connection folder/store");
        }
    }

    @Transactional
    public void exportMailToOrder(Integer id) throws OsirisException {
        connectMailbox();
        Integer messageCount;
        try {
            messageCount = folderInbox.getMessageCount();
        } catch (MessagingException e) {
            throw new OsirisException(e, "Impossible to get messages from INBOX folder");
        }
        // matching mail and getting all content, adresses and subject into an
        // InputStream to call method addAttachment
        for (int i = 1; i <= messageCount; i++) {
            try {
                Message message = folderInbox.getMessage(i);
                if (Integer.parseInt((message.getReceivedDate().getTime() / 1000) + "") == id) {

                    // Extract html
                    Address[] fromAddresses = message.getFrom();
                    Address[] toAddresses = message.getAllRecipients();
                    String fromName = String.join(" / ",
                            Arrays.asList(fromAddresses).stream().map(Address::toString).toList());
                    String toName = String.join(" / ",
                            Arrays.asList(toAddresses).stream().map(Address::toString).toList());
                    String linkMailFrom = createEmailLink(extractEmail(String.join(" / ",
                            Arrays.asList(fromAddresses).stream().map(Address::toString).toList())));
                    String linkMailTo = createEmailLink(extractEmail(String.join(" / ",
                            Arrays.asList(toAddresses).stream().map(Address::toString).toList())));
                    String mailHtmlWithAddresse = getHtmlContent(message).replace("</head>",
                            "</head><div><p class=\"MsoNormal\">Objet : " + message.getSubject()
                                    + "</p><br><p class=\"MsoNormal\">De : "
                                    + fromName + " " + linkMailFrom
                                    + "</p><p class=\"MsoNormal\">A :"
                                    + toName + " " + linkMailTo
                                    + "</p></div><hr/>")
                            .replace("charset=iso-8859-1", "charset=utf-8");

                    String mailHtml = getHtmlContent(message).replace("</head>",
                            "</head><div><p class=\"MsoNormal\">Objet : " + message.getSubject()
                                    + "</p> </div><hr/>")
                            .replace("charset=iso-8859-1", "charset=utf-8");

                    // Correct encoding
                    mailHtml = new String(mailHtml.getBytes(StandardCharsets.UTF_8));
                    mailHtmlWithAddresse = new String(mailHtmlWithAddresse.getBytes(StandardCharsets.UTF_8));

                    // Try to find responsible
                    String mailFound = null;
                    int nbrFound = 0;
                    List<Responsable> responsables = null;

                    List<String> mailList = Arrays.asList(fromAddresses).stream().map(Address::toString).toList();
                    if (mailList != null)
                        for (String mailString : mailList) {
                            if (!mailString.contains("@jss.fr")) {
                                nbrFound++;
                                mailFound = mailString;
                            }
                        }

                    if (mailFound != null && nbrFound == 1) {
                        responsables = responsableService
                                .getResponsableByMail(mailService.populateMailId(new Mail(mailFound)));
                    }

                    // handle fwd
                    if (responsables == null || responsables.size() == 0) {
                        mailList = extractExternalEmails(mailHtml);
                        if (mailList != null && mailList.size() == 1)
                            responsables = responsableService
                                    .getResponsableByMail(mailService.populateMailId(new Mail(mailList.get(0))));
                    }

                    CustomerOrder order = new CustomerOrder();
                    if (responsables != null && responsables.size() == 1) {
                        order.setResponsable(responsables.get(0));
                        quotationValidationHelper.completeIQuotationDocuments(order, true);
                    }

                    order.setCustomerOrderOrigin(constantService.getCustomerOrderOriginOsiris());
                    order.setAssoAffaireOrders(new ArrayList<AssoAffaireOrder>());
                    order.setCreatedDate(
                            message.getReceivedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                    order.setDescription(message.getSubject());
                    order.setDocuments(new ArrayList<Document>());
                    order.setCustomerOrderStatus(
                            customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.DRAFT));
                    order = customerOrderService.addOrUpdateCustomerOrder(order, false, false);

                    // Comment order with mail text
                    customerOrderCommentService.createCustomerOrderComment(order, null, mailHtml, true, false);

                    // Add initial demande
                    attachMailToCustomerOrder(message.getSubject(), mailHtmlWithAddresse, order);

                    // Index attachments
                    if (message.isMimeType("multipart/*")) {
                        Multipart multipart = (Multipart) message.getContent();

                        for (int j = 0; j < multipart.getCount(); j++) {
                            BodyPart part = multipart.getBodyPart(j);
                            String disposition = part.getDisposition();
                            String filename = part.getFileName();

                            if (filename != null) {
                                try {
                                    filename = MimeUtility.decodeText(filename);
                                } catch (Exception e) {
                                }
                            }

                            boolean isAttachment = disposition != null && disposition.equalsIgnoreCase(Part.ATTACHMENT);
                            boolean hasUnnamedAttachment = (disposition == null && filename != null);
                            boolean isEmbeddedMessage = part.isMimeType("message/rfc822");

                            if (isAttachment || hasUnnamedAttachment || isEmbeddedMessage) {
                                if (isEmbeddedMessage) {
                                    // Outl ook message
                                    Object content = part.getContent();
                                    if (content instanceof Message msg) {
                                        String subject = msg.getSubject();
                                        if (subject == null || subject.isBlank())
                                            subject = "mail_sans_titre";
                                        subject = subject.replaceAll("[\\\\/:*?\"<>|]", "_");
                                        filename = subject + ".eml";
                                    } else {
                                        filename = "message_outlook.msg";
                                    }
                                }

                                if (filename == null)
                                    filename = "attachment_" + j + ".bin";

                                if (filename.toLowerCase().endsWith(".zip")) {
                                    List<NamedInputStream> streams = extractZipStreams(part.getInputStream());
                                    if (streams != null)
                                        for (NamedInputStream stream : streams)
                                            addAttachmentToCustomerOrder(stream.getStream(), order, stream.getName());
                                } else {
                                    addAttachmentToCustomerOrder(part.getInputStream(), order, filename);
                                }
                            }
                        }
                    }

                    // Move to trash
                    folderInbox.copyMessages(new Message[] { message }, folderTrash);
                    message.setFlag(Flag.DELETED, true);
                }
            } catch (Exception e) {
                throw new OsirisException(e, "Impossible to process message received");
            }
        }
    }

    private void addAttachmentToCustomerOrder(InputStream is, CustomerOrder order, String filename)
            throws OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException, OsirisException {
        attachmentService.addAttachment(is, order.getId(), null,
                CustomerOrder.class.getSimpleName() + "Pending",
                constantService.getAttachmentTypeClientCommunication(), filename, false,
                filename, null, null, null);
    }

    private List<NamedInputStream> extractZipStreams(InputStream zipStream) throws IOException {
        List<NamedInputStream> files = new ArrayList<>();

        try (ZipInputStream zis = new ZipInputStream(zipStream)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    zis.transferTo(baos);
                    baos.flush();

                    files.add(new NamedInputStream(entry.getName(),
                            new ByteArrayInputStream(baos.toByteArray())));
                }
                zis.closeEntry();
            }
        }

        return files;
    }

    private class NamedInputStream {
        public final String name;
        public final InputStream stream;

        public NamedInputStream(String name, InputStream stream) {
            this.name = name;
            this.stream = stream;
        }

        public String getName() {
            return name;
        }

        public InputStream getStream() {
            return stream;
        }
    }

    public void purgeDeletedElements() throws OsirisException {
        connectMailbox();
        try {
            Message[] messages;
            try {
                messages = folderTrash.getMessages();
            } catch (MessagingException e) {
                throw new OsirisException(e, "Impossible to get messages from Trash folder");
            }

            for (Message message : messages) {
                try {
                    LocalDate dateToFlush = LocalDate.now().minusDays(numberDaysToKeepInTrash);
                    if (message.getReceivedDate()
                            .before(Date.from(dateToFlush.atStartOfDay(ZoneId.systemDefault()).toInstant()))) {
                        message.setFlag(Flag.DELETED, true);
                    }
                } catch (Exception e) {
                    throw new OsirisException(e, "Impossible to process message deleted");
                }
            }

            folderTrash.expunge();
        } catch (MessagingException e) {
            throw new OsirisException(e, "Impossible to write into Trash folder");
        }
    }

    public static List<String> extractExternalEmails(String mailText) {
        if (mailText == null)
            return null;

        Matcher matcher = EMAIL_PATTERN.matcher(mailText);
        Set<String> results = new HashSet<>();

        while (matcher.find()) {
            String email = matcher.group().toLowerCase(Locale.ROOT);
            if (!email.endsWith("@" + "jss.fr".toLowerCase(Locale.ROOT))) {
                results.add(email);
            }
        }

        return results.stream().sorted().collect(Collectors.toList());
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

    // create link href tag like <a href="mailto:...">...</a>
    private String createEmailLink(String email) {
        if (email != null) {
            return "<a href=\"mailto:" + email + "\">" + email + "</a>";
        }
        return null;
    }

    private void attachMailToCustomerOrder(String subject, String contentHtml, CustomerOrder order)
            throws OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException, OsirisException {
        attachmentService.addAttachment(new ByteArrayInputStream(contentHtml.getBytes(StandardCharsets.UTF_8)),
                order.getId(), null,
                CustomerOrder.class.getSimpleName(),
                constantService.getAttachmentTypeClientCommunication(),
                ("Mail client - " + subject.replace(":", " ")
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HHmm")) + ".html"),
                false, null, null, null, null);
    }

    // exctract email from text
    private String extractEmail(String adresses) {
        if (adresses != null) {
            String emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(emailPattern);
            java.util.regex.Matcher matcher = pattern.matcher(adresses);

            if (matcher.find()) {
                return matcher.group(0);
            }
        }
        return adresses;
    }

    // method to get images from html and display it into attachment
    private String processMultipart(Multipart multipart) throws Exception {
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

            if (part.isMimeType("multipart/*")) {
                htmlBuilder.append(processMultipart((Multipart) part.getContent()));
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

    private String extractCid(BodyPart part) throws MessagingException {
        String[] disposition = part.getHeader("Content-ID");
        if (disposition != null && disposition.length > 0) {
            String cid = disposition[0];
            return cid.substring(1, cid.length() - 1);
        }
        return null;
    }

    private String convertToBase64(InputStream is) throws IOException {
        byte[] bytes = is.readAllBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }
}