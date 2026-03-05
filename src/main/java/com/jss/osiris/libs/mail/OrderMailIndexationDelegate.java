package com.jss.osiris.libs.mail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.beans.factory.annotation.Autowired;
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

import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.Flags.Flag;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
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

    @Autowired
    MailSharedDelegate mailSharedDelegate;

    private Integer numberDaysToKeepInTrash = 60;

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

    public void checkMailsToIndexFormalites() throws OsirisException {
        mailSharedDelegate.executeWithLock(() -> {
            Message[] messages;
            try {
                messages = mailSharedDelegate.getFolderInboxFormalite().getMessages();
            } catch (MessagingException e) {
                throw new OsirisException(e, "Impossible to get messages from INBOX folder");
            }
            for (Message message : messages) {
                try {
                    batchService.declareNewBatch(Batch.CREATE_ORDER_FROM_MAIL,
                            (Integer.parseInt((message.getReceivedDate().getTime() / 1000) + "")));
                } catch (MessagingException e) {
                    throw new OsirisException(e, "Impossible to process message received");
                }
            }
            return null;
        });
    }

    public void checkMailsToIndexAnnouncements() throws OsirisException {
        mailSharedDelegate.executeWithLock(() -> {
            Message[] messages;
            try {
                messages = mailSharedDelegate.getFolderInboxAnnouncement().getMessages();
            } catch (MessagingException e) {
                throw new OsirisException(e, "Impossible to get messages from INBOX folder");
            }
            // for all messages received in inbox folder, launch a batch calling method
            // exportMailToFile()
            for (Message message : messages) {
                try {
                    batchService.declareNewBatch(Batch.CREATE_ORDER_FROM_MAIL_ANNOUNCEMENT,
                            (Integer.parseInt((message.getReceivedDate().getTime() / 1000) + "")));
                } catch (MessagingException e) {
                    throw new OsirisException(e, "Impossible to process message received");
                }
            }
            return null;
        });
    }

    @Transactional
    public void exportMailToOrderFormalite(Integer id) throws OsirisException {
        mailSharedDelegate.executeWithLock(() -> {
            exportMailToOrder(id, false);
            return null;
        });
    }

    @Transactional
    public void exportMailToOrderAnnouncement(Integer id) throws OsirisException {
        mailSharedDelegate.executeWithLock(() -> {
            exportMailToOrder(id, true);
            return null;
        });
    }

    private void exportMailToOrder(Integer id, Boolean isFromAnnouncementMailbox) throws OsirisException {
        Integer messageCount;
        Folder sourceFolder = isFromAnnouncementMailbox ? mailSharedDelegate.getFolderInboxAnnouncement()
                : mailSharedDelegate.getFolderInboxFormalite();
        try {
            messageCount = sourceFolder.getMessageCount();
        } catch (MessagingException e) {
            throw new OsirisException(e, "Impossible to get messages from INBOX folder");
        }
        // matching mail and getting all content, adresses and subject into an
        // InputStream to call method addAttachment
        for (int i = 1; i <= messageCount; i++) {
            try {
                Message message = sourceFolder.getMessage(i);
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
                    order.setIsFromAnnouncementMailbox(isFromAnnouncementMailbox);
                    order.setAssoAffaireOrders(new ArrayList<AssoAffaireOrder>());
                    order.setCreatedDate(
                            message.getReceivedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                    order.setDescription(message.getSubject());
                    order.setDocuments(new ArrayList<Document>());
                    order.setCustomerOrderStatus(
                            customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.DRAFT));
                    order = customerOrderService.addOrUpdateCustomerOrder(order, false, false);

                    // Comment order with mail text
                    customerOrderCommentService.createCustomerOrderComment(order, null, mailHtml, false, false);

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
                                    addAttachmentToCustomerOrder(downloadAttachment(part), order,
                                            filename);
                                }
                            }
                        }
                    }

                    // Move to trash
                    sourceFolder.copyMessages(new Message[] { message }, mailSharedDelegate.getFolderTrash());
                    message.setFlag(Flag.DELETED, true);
                }
            } catch (Exception e) {
                throw new OsirisException(e, "Impossible to process message received");
            }
        }
        try {
            sourceFolder.expunge();
        } catch (Exception e) {
            throw new OsirisException(e, "Impossible to process message received");
        }
    }

    public ByteArrayInputStream downloadAttachment(BodyPart part) throws Exception {
        try (InputStream is = part.getInputStream();
                ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            is.transferTo(os);
            return new ByteArrayInputStream(os.toByteArray());
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
        try {
            Message[] messages;
            try {
                messages = mailSharedDelegate.getFolderTrash().getMessages();
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

            mailSharedDelegate.getFolderTrash().expunge();
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