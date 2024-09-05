package com.jss.osiris.libs.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.util.XRLog;

import com.jss.osiris.libs.QrCodeHelper;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.model.CustomerMail;
import com.jss.osiris.libs.mail.model.MailComputeResult;
import com.jss.osiris.libs.mail.model.VatMail;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.service.InvoiceHelper;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.Mail;
import com.jss.osiris.modules.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.miscellaneous.service.VatService;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.Announcement;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.AssoServiceDocument;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.IQuotation;
import com.jss.osiris.modules.quotation.model.MissingAttachmentQuery;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.model.Service;
import com.jss.osiris.modules.quotation.model.guichetUnique.FormaliteGuichetUnique;
import com.jss.osiris.modules.quotation.model.infoGreffe.EvenementInfogreffe;
import com.jss.osiris.modules.quotation.model.infoGreffe.FormaliteInfogreffe;
import com.jss.osiris.modules.quotation.service.AssoAffaireOrderService;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.quotation.service.QuotationService;
import com.jss.osiris.modules.quotation.service.ServiceService;
import com.jss.osiris.modules.quotation.service.infoGreffe.FormaliteInfogreffeService;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Rff;
import com.jss.osiris.modules.tiers.model.Tiers;
import com.jss.osiris.modules.tiers.service.ResponsableService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@org.springframework.stereotype.Service
public class MailHelper {

    @Value("${mail.smtp.host}")
    private String mailHost;

    @Value("${mail.smtp.port}")
    private String mailPort;

    @Value("${mail.smtp.username}")
    private String mailUsername;

    @Value("${mail.smtp.password}")
    private String mailPassword;

    @Value("${payment.cb.entry.point}")
    private String paymentCbEntryPoint;

    @Value("${jss.iban}")
    private String ibanJss;

    @Value("${jss.bic}")
    private String bicJss;

    private JavaMailSender javaMailSender;

    public static final String EMAIL_TEMPLATE_ENCODING = "UTF-8";

    private static final String PNG_MIME = "image/png";

    private boolean disableCbLink = false;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    QuotationService quotationService;

    @Autowired
    DocumentService documentService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    InvoiceHelper invoiceHelper;

    @Autowired
    AccountingRecordService accountingRecordService;

    @Autowired
    AssoAffaireOrderService assoAffaireOrderService;

    @Autowired
    QrCodeHelper qrCodeHelper;

    @Autowired
    ConstantService constantService;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    MailComputeHelper mailComputeHelper;

    @Autowired
    CustomerMailService mailService;

    @Autowired
    VatService vatService;

    @Autowired
    ResponsableService responsableService;

    @Autowired
    ServiceService serviceService;
    FormaliteInfogreffeService formaliteInfogreffeService;

    @Bean
    public TemplateEngine emailTemplateEngine() {
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(htmlTemplateResolver());
        // Message source, internationalization specific to emails
        return templateEngine;
    }

    private ITemplateResolver htmlTemplateResolver() {
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setOrder(Integer.valueOf(1));
        templateResolver.setPrefix("mails/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(EMAIL_TEMPLATE_ENCODING);
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    public JavaMailSender getMailSender() throws OsirisException {
        if (javaMailSender != null)
            return javaMailSender;

        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(mailHost);
        mailSender.setPort(Integer.parseInt(mailPort));
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);
        final Properties javaMailProperties = new Properties();
        try {
            javaMailProperties
                    .load(this.applicationContext.getResource("classpath:application.properties").getInputStream());
        } catch (IOException e) {
            throw new OsirisException(e, "Unable to find application.properties in classpath");
        }
        mailSender.setJavaMailProperties(javaMailProperties);

        javaMailSender = mailSender;

        return mailSender;
    }

    public MimeMessage generateGenericMail(CustomerMail mail)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        final Context ctx = new Context();
        setContextVariable(ctx, mail, false);
        // Prepare message using a Spring helper
        MimeMessage mimeMessage;
        mimeMessage = getMailSender().createMimeMessage();

        try {
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            try {
                message.setFrom(new InternetAddress("no-reply@jss.fr", "Journal Spécial des Sociétés"));
            } catch (UnsupportedEncodingException e) {
                throw new OsirisException(e, "Wrong From mail for customer mail " + mail.getId());
            }

            if (mail.getReplyTo() != null)
                try {
                    message.setReplyTo(
                            new InternetAddress(mail.getReplyTo().getMail(),
                                    mail.getReplyTo().getFirstname() + " " + mail.getReplyTo().getLastname()));
                } catch (UnsupportedEncodingException e) {
                    throw new OsirisException(e, "Wrong Reply To mail for customer mail " + mail.getId());
                }

            if (mail.getReplyToMail() != null)
                message.setReplyTo(mail.getReplyToMail());

            if (mail.getCopyToMail() != null)
                message.addCc(mail.getCopyToMail());

            if (mail.getCopyToMe() != null && mail.getCopyToMe())
                message.addCc(mail.getSendToMeEmployee().getMail());

            if (mail.getSendToMe() != null && mail.getSendToMe()) {
                message.addTo(mail.getSendToMeEmployee().getMail());
            } else {
                if (mail.getMailComputeResult().getRecipientsMailTo() == null
                        || mail.getMailComputeResult().getRecipientsMailTo().size() == 0)
                    throw new OsirisException(null, "No recipient found for mail n°" + mail.getId());

                for (Mail mailTo : mail.getMailComputeResult().getRecipientsMailTo())
                    message.addTo(mailTo.getMail());

                if (mail.getMailComputeResult().getRecipientsMailCc() != null
                        && mail.getMailComputeResult().getRecipientsMailTo().size() > 0)
                    for (Mail mailCc : mail.getMailComputeResult().getRecipientsMailCc())
                        message.addCc(mailCc.getMail());
            }

            message.setSubject(mail.getSubject());

            // Create the HTML body using Thymeleaf
            final String htmlContent = StringEscapeUtils
                    .unescapeHtml4(emailTemplateEngine().process(mail.getMailTemplate(), ctx));
            message.setText(htmlContent, true);

            // header picture
            try {
                InputStreamSource imageSourceQuotationHeader = new ByteArrayResource(
                        IOUtils.toByteArray(new ClassPathResource(mail.getHeaderPicture()).getInputStream()));
                message.addInline("headerPicture", imageSourceQuotationHeader, PNG_MIME);

                // QR Code
                if (mail.getCbLink() != null) {
                    final InputStreamSource imageSourceQrCode = new ByteArrayResource(
                            qrCodeHelper.getQrCode(mail.getCbLink(), 150));
                    message.addInline("qrCodePicture", imageSourceQrCode, PNG_MIME);
                }

                // jss-header.png
                final InputStreamSource imageSourceJssHeader = new ByteArrayResource(
                        IOUtils.toByteArray(new ClassPathResource("images/jss-header.png").getInputStream()));
                message.addInline("jssHeaderPicture", imageSourceJssHeader, PNG_MIME);

                // facebook
                final InputStreamSource imageSourceFacebook = new ByteArrayResource(
                        IOUtils.toByteArray(new ClassPathResource("images/facebook.png").getInputStream()));
                message.addInline("facebook", imageSourceFacebook, PNG_MIME);

                // linkedin
                final InputStreamSource imageSourceLinkedin = new ByteArrayResource(
                        IOUtils.toByteArray(new ClassPathResource("images/linkedin.png").getInputStream()));
                message.addInline("linkedin", imageSourceLinkedin, PNG_MIME);

                // instagram
                final InputStreamSource imageSourceInstagram = new ByteArrayResource(
                        IOUtils.toByteArray(new ClassPathResource("images/instagram.png").getInputStream()));
                message.addInline("instagram", imageSourceInstagram, PNG_MIME);

                // twitter
                final InputStreamSource imageSourceTwitter = new ByteArrayResource(
                        IOUtils.toByteArray(new ClassPathResource("images/twitter.png").getInputStream()));
                message.addInline("twitter", imageSourceTwitter, PNG_MIME);
            } catch (IOException e) {
                throw new OsirisException(e, "Unable to find some pictures for customer mail " + mail.getId());
            }

            if (mail.getAttachments() != null) {
                List<Integer> attachmentsDone = new ArrayList<Integer>();
                for (Attachment attachment : mail.getAttachments()) {
                    if ((attachment.getParentAttachment() == null && !attachmentsDone.contains(attachment.getId()))
                            || (attachment.getParentAttachment() != null
                                    && !attachmentsDone.contains(attachment.getParentAttachment().getId()))) {
                        if (attachment.getParentAttachment() != null)
                            attachmentsDone.add(attachment.getParentAttachment().getId());
                        else
                            attachmentsDone.add(attachment.getId());
                        message.addAttachment(attachment.getUploadedFile().getFilename(),
                                new File(attachment.getUploadedFile().getPath()));

                        if (mail.getSendToMe() == null || mail.getSendToMe() == false) {
                            attachment.setIsAlreadySent(true);
                            attachmentService.addOrUpdateAttachment(attachment);
                            if (attachment.getParentAttachment() != null)
                                attachment.getParentAttachment().setIsAlreadySent(true);
                            else if (attachment != null)
                                attachment.setIsAlreadySent(true);

                            if (attachment != null && attachment.getParentAttachment() != null)
                                attachmentService.addOrUpdateAttachment(attachment.getParentAttachment());
                        }
                    }
                }
            }
        } catch (MessagingException e) {
        }

        return mimeMessage;
    }

    public File generateGenericPdfOfMail(CustomerMail mail)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        final Context ctx = new Context();
        setContextVariable(ctx, mail, true);
        String htmlContent = "";
        // Create the HTML body using Thymeleaf
        try {
            htmlContent = StringEscapeUtils.unescapeHtml4(emailTemplateEngine().process(mail.getMailTemplate(), ctx));
        } catch (Exception e) {
            throw new OsirisException(e, "Unable to parse HTML for mail " + mail.getId());
        }

        File tempFile;
        OutputStream outputStream;
        try {
            tempFile = File.createTempFile("genericMail", "pdf");
            outputStream = new FileOutputStream(tempFile);
        } catch (IOException e) {
            throw new OsirisException(e, "Unable to create temp file");
        }
        ITextRenderer renderer = new ITextRenderer();
        XRLog.setLevel(XRLog.CSS_PARSE, Level.SEVERE);
        try {
            renderer.setDocumentFromString(
                    htmlContent.replaceAll("\\p{C}", " ")
                            .replace("&mail", "mail").replace("&validationToken", "validationToken")
                            .replaceAll("&", "<![CDATA[&]]>").replaceAll("&#160;", " "));

            renderer.setScaleToFit(true);
            renderer.layout();
            renderer.createPDF(outputStream);

            outputStream.close();
        } catch (Exception e) {
            throw new OsirisException(e, "Unable to create PDF file for mail " + mail.getId());
        }
        return tempFile;
    }

    private void setContextVariable(Context ctx, CustomerMail mail, boolean setPlainPictures)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        // Prepare the evaluation context
        ctx.setVariable("instagram", "instagram");
        ctx.setVariable("facebook", "facebook");
        ctx.setVariable("linkedin", "linkedin");
        ctx.setVariable("twitter", "twitter");
        if (setPlainPictures)
            ctx.setVariable("jssHeaderPicturePlain", "/images/jss-header.png");
        else
            ctx.setVariable("jssHeaderPicture", "jssHeaderPicture");

        ctx.setVariable("headerPicture", mail.getHeaderPicture() != null ? "headerPicture" : null);
        ctx.setVariable("customerOrder", mail.getCustomerOrder());
        ctx.setVariable("quotation", mail.getQuotation());
        ctx.setVariable("replyToEmployee", mail.getReplyTo() != null ? mail.getReplyTo() : mail.getSendToMeEmployee());

        IQuotation quotation = mail.getCustomerOrder() != null ? mail.getCustomerOrder() : mail.getQuotation();
        if (quotation != null) {
            ctx.setVariable("customerName", getCustomerName(quotation));

            AssoAffaireOrder assoAffaireOrderToUse = null;
            if (mail.getProvision() != null)
                outerloop: for (AssoAffaireOrder assoAffaireOrder : quotation.getAssoAffaireOrders())
                    for (Service service : assoAffaireOrder.getServices())
                        for (Provision provision : service.getProvisions())
                            if (provision.getId().equals(mail.getProvision().getId())) {
                                assoAffaireOrderToUse = assoAffaireOrder;
                                break outerloop;
                            }

            ctx.setVariable("affaireLabel", getCustomerOrderAffaireLabel(quotation, assoAffaireOrderToUse));
            ctx.setVariable("affaireLabelDetails",
                    getCustomerOrderAffaireDetailLabel(quotation, assoAffaireOrderToUse));
            ctx.setVariable("referenceLabel", getCustomerOrderReferenceLabel(quotation, assoAffaireOrderToUse));
            ctx.setVariable("invoiceLabelResult",
                    invoiceHelper.computeInvoiceLabelResult(
                            documentService.getBillingDocument(quotation.getDocuments()),
                            quotation, quotationService.getCustomerOrderOfQuotation(quotation)));
        }

        // Compute deposit amount
        ctx.setVariable("depositAmount",
                quotation != null
                        ? Math.round(customerOrderService.getTotalForCustomerOrder(quotation) * 100f)
                                / 100f
                        : "");

        // Compute remaining to pay on invoice
        Invoice invoice = null;
        Invoice creditNote = null;
        if (mail.getCustomerOrder() != null && mail.getCustomerOrder().getInvoices() != null
                && mail.getCustomerOrder().getInvoices().size() > 0) {
            for (Invoice invoiceCo : mail.getCustomerOrder().getInvoices()) {
                if (invoiceCo.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusSend().getId())
                        || invoiceCo.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusPayed().getId()))
                    invoice = invoiceCo;
                if (invoiceCo.getCreditNote() != null && invoiceCo.getCreditNote().getInvoiceStatus().getId()
                        .equals(constantService.getInvoiceStatusCreditNoteEmited().getId()))
                    creditNote = invoiceCo.getCreditNote();
            }
            ctx.setVariable("remainingToPay",
                    Math.round(invoiceService.getRemainingAmountToPayForInvoice(invoice) * 100f) / 100f);
            ctx.setVariable("invoice", invoice);
            ctx.setVariable("creditNote", creditNote);
        }

        ctx.setVariable("ibanJss", ibanJss);
        ctx.setVariable("bicJss", bicJss);
        ctx.setVariable("cbLink", mail.getCbLink());
        ctx.setVariable("mailComputeResult", mail.getMailComputeResult());
        ctx.setVariable("attachments", mail.getAttachments());
        ctx.setVariable("provision", mail.getProvision());
        ctx.setVariable("tiers", mail.getTiers());
        ctx.setVariable("explaination", mail.getExplaination());
        ctx.setVariable("qrCodePicture", mail.getCbLink() != null ? "qrCodePicture" : null);
        ctx.setVariable("endOfYearDateString",
                LocalDate.now().withMonth(12).withDayOfMonth(31).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        if (mail.getProvision() != null && mail.getProvision().getAnnouncement() != null
                && mail.getProvision().getAnnouncement().getIsAnnouncementAlreadySentToConfrere() != null
                && mail.getProvision().getAnnouncement().getIsAnnouncementAlreadySentToConfrere()) {
            ctx.setVariable("sentDateToConfrere", mail.getProvision().getAnnouncement()
                    .getFirstConfrereSentMailDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")));
        }

        if (mail.getProvision() != null && mail.getProvision().getAnnouncement() != null) {
            ctx.setVariable("announcementPublicationDate", mail.getProvision().getAnnouncement().getPublicationDate()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }

        Integer publicationPaperNumber = 0;
        if (mail.getProvision() != null) {
            if (mail.getProvision().getPublicationPaperClientNumber() != null)
                publicationPaperNumber += mail.getProvision().getPublicationPaperClientNumber();
            if (mail.getProvision().getPublicationPaperAffaireNumber() != null)
                publicationPaperNumber += mail.getProvision().getPublicationPaperAffaireNumber();
        }
        ctx.setVariable("publicationPaperNumber", publicationPaperNumber);

        ctx.setVariable("rff", mail.getRff());
        if (mail.getRff() != null) {
            ctx.setVariable("rffYear", mail.getRff().getEndDate().format(DateTimeFormatter.ofPattern("yyyy")));
            ctx.setVariable("rffMonth", mail.getRff().getEndDate().format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        }

        if (quotation != null)
            setQuotationPrice(quotation, ctx);

        if (mail.getMissingAttachmentQuery() != null) {
            for (AssoServiceDocument asso : mail.getMissingAttachmentQuery().getAssoServiceDocument())
                if (asso.getFormalisteComment() != null)
                    asso.setFormalisteComment(asso.getFormalisteComment().replaceAll("\r?\n", "<br/>"));
            ctx.setVariable("assoServiceDocuments", mail.getMissingAttachmentQuery().getAssoServiceDocument());
            ctx.setVariable("serviceLabel",
                    serviceService.getServiceLabel(mail.getMissingAttachmentQuery().getService()));
        }

        ctx.setVariable("isLastReminder", mail.getIsLastReminder() != null && mail.getIsLastReminder());

        // For AC reminder
        if (mail.getCompetentAuthority() != null) {
            ctx.setVariable("competentAuthority", mail.getCompetentAuthority());
            ctx.setVariable("provisionDetails", mail.getExplaination().split("removeme"));
        }

    }

    private String getCustomerOrderAffaireLabel(IQuotation customerOrder, AssoAffaireOrder asso) {
        String affaireLabel = "";
        if (customerOrder != null && customerOrder.getAssoAffaireOrders() != null
                && customerOrder.getAssoAffaireOrders().size() > 0) {
            Affaire affaire = null;
            if (asso == null)
                affaire = customerOrder.getAssoAffaireOrders().get(0).getAffaire();
            else
                affaire = asso.getAffaire();
            affaireLabel = affaire.getDenomination() != null ? affaire.getDenomination()
                    : (affaire.getFirstname() + " " + affaire.getLastname());
        }
        return affaireLabel;
    }

    private String getCustomerOrderReferenceLabel(IQuotation customerOrder, AssoAffaireOrder asso)
            throws OsirisException {
        ArrayList<String> references = new ArrayList<String>();
        AssoAffaireOrder assoToUse = asso;
        if (assoToUse == null)
            assoToUse = customerOrder.getAssoAffaireOrders().get(0);

        if (assoToUse.getAffaire().getExternalReference() != null
                && assoToUse.getAffaire().getExternalReference().length() > 0)
            references.add(assoToUse.getAffaire().getExternalReference());

        if (customerOrder.getDocuments() != null) {
            Document invoiceDocument = documentService.getBillingDocument(customerOrder.getDocuments());
            if (invoiceDocument != null) {
                if (invoiceDocument.getIsCommandNumberMandatory() != null
                        && invoiceDocument.getIsCommandNumberMandatory() && invoiceDocument.getCommandNumber() != null
                        && invoiceDocument.getCommandNumber().length() > 0) {
                    references.add(invoiceDocument.getCommandNumber());
                }
                if (invoiceDocument.getExternalReference() != null
                        && invoiceDocument.getExternalReference().length() > 0)
                    references.add(invoiceDocument.getExternalReference());
            }
        }

        if (references.size() > 0)
            return "Référence : " + String.join(" / ", references);
        return null;
    }

    private String getCustomerOrderAffaireDetailLabel(IQuotation customerOrder, AssoAffaireOrder asso)
            throws OsirisException {
        String affaireLabel = "";
        AssoAffaireOrder assoToUse = asso;
        if (assoToUse == null)
            assoToUse = customerOrder.getAssoAffaireOrders().get(0);

        Affaire affaire = assoToUse.getAffaire();

        if (affaire.getAddress() != null) {
            affaireLabel += affaire.getAddress();
        }

        if (affaire.getPostalCode() != null) {
            affaireLabel += " ";
            affaireLabel += affaire.getPostalCode();
        }

        if (affaire.getCity() != null) {
            affaireLabel += " ";
            affaireLabel += affaire.getCity().getLabel();
        }

        if (affaire.getSiret() != null && affaire.getSiret().length() > 0) {
            affaireLabel += " - ";
            affaireLabel += affaire.getSiret();
        } else if (affaire.getSiren() != null && affaire.getSiren().length() > 0) {
            affaireLabel += " - ";
            affaireLabel += affaire.getSiren();
        }

        return affaireLabel;
    }

    private String getCustomerName(IQuotation customerOrder) {
        String customerName = "";
        if (customerOrder != null) {
            if (customerOrder.getResponsable() != null) {
                customerName = customerOrder.getResponsable().getCivility().getLabel() + " "
                        + customerOrder.getResponsable().getFirstname() + " "
                        + customerOrder.getResponsable().getLastname();
            } else if (customerOrder.getTiers() != null
                    && customerOrder.getTiers().getIsIndividual() == true) {
                customerName = customerOrder.getTiers().getCivility().getLabel() + " "
                        + customerOrder.getTiers().getFirstname() + " "
                        + customerOrder.getTiers().getLastname();
            }
        }
        return customerName;
    }

    public String generateGenericHtmlConfirmation(String title, String subtitle, String label, String explaination,
            String explainationWarning, String greetings) { // TODO : delete when new website done
        // Prepare the evaluation context
        final Context ctx = new Context();
        ctx.setVariable("title", title);
        ctx.setVariable("subtitle", subtitle);
        ctx.setVariable("label", label);
        ctx.setVariable("explaination", explaination);
        ctx.setVariable("explainationWarning", explainationWarning);
        ctx.setVariable("greetings", greetings);

        // Create the HTML body using Thymeleaf
        return emailTemplateEngine().process("model", ctx);
    }

    public void setQuotationPrice(IQuotation quotation, Context ctx)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        // Compute prices
        Float preTaxPriceTotal = 0f;
        Float discountTotal = null;
        Float preTaxPriceTotalWithDicount = null;
        ArrayList<VatMail> vats = null;
        Float vatTotal = 0f;
        Float priceTotal = null;

        for (AssoAffaireOrder asso : quotation.getAssoAffaireOrders()) {
            for (Service service : asso.getServices())
                for (Provision provision : service.getProvisions()) {
                    for (InvoiceItem invoiceItem : provision.getInvoiceItems()) {
                        preTaxPriceTotal += invoiceItem.getPreTaxPrice() != null ? invoiceItem.getPreTaxPrice() : 0f;
                        if (invoiceItem.getDiscountAmount() != null && invoiceItem.getDiscountAmount() > 0) {
                            if (discountTotal == null)
                                discountTotal = invoiceItem.getDiscountAmount();
                            else
                                discountTotal += invoiceItem.getDiscountAmount();
                        }
                        if (vats == null)
                            vats = new ArrayList<VatMail>();
                        if (invoiceItem.getVat() != null && invoiceItem.getVatPrice() != null
                                && invoiceItem.getVatPrice() > 0) {
                            vatTotal += invoiceItem.getVatPrice();
                            boolean vatFound = false;
                            for (VatMail vatMail : vats) {
                                if (vatMail.getLabel().equals(invoiceItem.getVat().getLabel())) {
                                    vatFound = true;
                                    if (vatMail.getTotal() == null) {
                                        vatMail.setTotal(invoiceItem.getVatPrice());
                                        vatMail.setBase(
                                                invoiceItem.getPreTaxPrice() != null ? invoiceItem.getPreTaxPrice()
                                                        : 0f);
                                    } else {
                                        vatMail.setTotal(vatMail.getTotal() + invoiceItem.getVatPrice());
                                        vatMail.setBase(vatMail.getBase()
                                                + (invoiceItem.getPreTaxPrice() != null ? invoiceItem.getPreTaxPrice()
                                                        : 0f)
                                                - (invoiceItem.getDiscountAmount() != null
                                                        ? invoiceItem.getDiscountAmount()
                                                        : 0f));
                                    }
                                }
                            }
                            if (!vatFound) {
                                VatMail vatmail = new VatMail();
                                vatmail.setTotal(invoiceItem.getVatPrice());
                                vatmail.setLabel(invoiceItem.getVat().getLabel());
                                vatmail.setBase(
                                        (invoiceItem.getPreTaxPrice() != null ? invoiceItem.getPreTaxPrice() : 0f)
                                                - (invoiceItem.getDiscountAmount() != null
                                                        ? invoiceItem.getDiscountAmount()
                                                        : 0f));
                                vats.add(vatmail);
                            }
                        }
                    }

                }
        }

        if (discountTotal != null)
            preTaxPriceTotalWithDicount = preTaxPriceTotal - discountTotal;

        priceTotal = preTaxPriceTotal - (discountTotal != null ? discountTotal : 0) + (vats != null ? vatTotal : 0);

        if (discountTotal != null && (Math.round(discountTotal * 100f) / 100f) == 0f)
            discountTotal = null;

        ctx.setVariable("preTaxPriceTotal", preTaxPriceTotal);
        ctx.setVariable("discountTotal", discountTotal);
        ctx.setVariable("preTaxPriceTotalWithDicount", preTaxPriceTotalWithDicount);
        ctx.setVariable("vats", vats);
        ctx.setVariable("priceTotal", Math.round(priceTotal * 100f) / 100f);
    }

    public void sendCustomerOrderDepositMailToCustomer(CustomerOrder customerOrder, boolean sendToMe,
            boolean isReminder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {

        CustomerMail mail = new CustomerMail();
        mail.setCustomerOrder(customerOrder);

        MailComputeResult mailComputeResult = mailComputeHelper
                .computeMailForDepositRequest(customerOrder);

        mail.setHeaderPicture("images/mails/waiting-deposit.png");

        if (!disableCbLink) {
            if (mailComputeResult.getRecipientsMailTo() == null
                    || mailComputeResult.getRecipientsMailTo().get(0) == null
                    || mailComputeResult.getRecipientsMailTo().get(0).getMail() == null)
                throw new OsirisException(null,
                        "Unable to find mail for CB generation for customerOrder n°" + customerOrder.getId());

            mail.setCbLink(
                    paymentCbEntryPoint + "/order/deposit?customerOrderId=" + customerOrder.getId() + "&mail="
                            + mailComputeResult.getRecipientsMailTo().get(0).getMail());
        }

        mail.setReplyTo(customerOrder.getAssignedTo());
        mail.setSendToMe(sendToMe);
        mail.setMailComputeResult(mailComputeResult);
        mail.setMailTemplate(CustomerMail.TEMPLATE_WAITING_DEPOSIT);

        mail.setSubject("Attente d'acompte - Votre commande n°" + customerOrder.getId() + " - "
                + getCustomerOrderAffaireLabel(customerOrder, null));

        mailService.addMailToQueue(mail);
    }

    @Transactional(rollbackFor = Exception.class)
    public void generateQuotationMail(Quotation quotation)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        quotation = quotationService.getQuotation(quotation.getId());
        sendQuotationToCustomer(quotation, true);
    }

    public void sendQuotationToCustomer(Quotation quotation, boolean sendToMe)
            throws OsirisClientMessageException, OsirisException {
        CustomerMail mail = new CustomerMail();
        mail.setQuotation(quotation);
        mail.setHeaderPicture("images/mails/waiting-quotation-validation.jpg");
        mail.setReplyTo(quotation.getAssignedTo());
        mail.setSendToMe(sendToMe);
        mail.setMailComputeResult(mailComputeHelper.computeMailForQuotationMail(quotation));

        if (quotation.getAttachments() != null && quotation.getAttachments().size() > 0) {
            for (Attachment attachment : attachmentService.sortAttachmentByDateDesc(quotation.getAttachments())) {
                if (attachment.getAttachmentType() != null && attachment.getAttachmentType().getId()
                        .equals(constantService.getAttachmentTypeQuotation().getId())) {
                    if (mail.getAttachments() == null)
                        mail.setAttachments(new ArrayList<Attachment>());
                    mail.getAttachments().add(attachment);
                    break;
                }
            }
        }
        mail.setSubject("Votre devis n°" + quotation.getId());
        mail.setMailTemplate(CustomerMail.TEMPLATE_WAITING_QUOTATION_VALIDATION);

        mail.setCbLink(paymentCbEntryPoint + "/quotation/deposit?quotationId=" + quotation.getId() + "&mail="
                + mail.getMailComputeResult().getRecipientsMailTo().get(0).getMail());

        mail.setSubject(
                "Votre demande de devis n°" + quotation.getId() + " - "
                        + getCustomerOrderAffaireLabel(quotation, null));

        mailService.addMailToQueue(mail);
    }

    public void sendCustomerOrderCreationConfirmationOnQuotationValidation(Quotation quotation,
            CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        CustomerMail mail = new CustomerMail();
        mail.setQuotation(quotation);
        mail.setCustomerOrder(customerOrder);
        mail.setHeaderPicture("images/mails/quotation-validated.png");
        mail.setMailTemplate(CustomerMail.TEMPLATE_QUOTATION_VALIDATED);
        mail.setReplyTo(quotation.getAssignedTo());
        mail.setSendToMe(false);
        mail.setMailComputeResult(mailComputeHelper.computeMailForQuotationCreationConfirmation(quotation));
        mail.setSubject(
                "Validation de votre devis n°" + quotation.getId() + " - "
                        + getCustomerOrderAffaireLabel(quotation, null));
        mailService.addMailToQueue(mail);
    }

    public void sendCustomerOrderInProgressToCustomer(CustomerOrder customerOrder, boolean sendToMe)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        CustomerMail mail = new CustomerMail();
        mail.setCustomerOrder(customerOrder);
        mail.setHeaderPicture("images/mails/customer-order-in-progress.png");
        mail.setMailTemplate(CustomerMail.TEMPLATE_CUSTOMER_ORDER_IN_PROGRESS);
        mail.setReplyTo(customerOrder.getAssignedTo());
        mail.setSendToMe(sendToMe);
        mail.setMailComputeResult(mailComputeHelper.computeMailForCustomerOrderCreationConfirmation(customerOrder));
        mail.setSubject(
                "Votre commande n°" + customerOrder.getId() + " - " + getCustomerOrderAffaireLabel(customerOrder, null)
                        + " est en cours de traitement");
        mailService.addMailToQueue(mail);
    }

    @Transactional
    public void sendCustomerOrderAttachmentsToCustomer(CustomerOrder customerOrder, AssoAffaireOrder asso,
            boolean sendToMe, List<Attachment> attachmentsToSend)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {

        customerOrder = customerOrderService.getCustomerOrder(customerOrder.getId());
        asso = assoAffaireOrderService.getAssoAffaireOrder(asso.getId());

        if (attachmentsToSend == null || attachmentsToSend.size() == 0)
            return;

        List<Attachment> finalAttachments = new ArrayList<Attachment>();
        for (Attachment attachment : attachmentsToSend)
            finalAttachments.add(attachmentService.getAttachment(attachment.getId()));

        CustomerMail mail = new CustomerMail();
        mail.setCustomerOrder(customerOrder);
        MailComputeResult mailComputeResult = mailComputeHelper.computeMailForSendNumericAttachment(customerOrder);
        mail.setMailTemplate(CustomerMail.TEMPLATE_SEND_ATTACHMENTS);
        mail.setAttachments(finalAttachments);
        mail.setHeaderPicture("images/mails/send-attanchments.png");
        mail.setReplyTo(asso.getAssignedTo());
        mail.setSendToMe(sendToMe);
        mail.setMailComputeResult(mailComputeResult);
        mail.setSubject("Vos pièces numériques - commande n°" + customerOrder.getId() + " - "
                + getCustomerOrderAffaireLabel(customerOrder, null));
        mailService.addMailToQueue(mail);
    }

    public void sendPublicationReceiptToCustomer(CustomerOrder customerOrder, boolean sendToMe,
            Announcement announcement)
            throws OsirisException, OsirisClientMessageException {

        CustomerMail mail = new CustomerMail();
        mail.setCustomerOrder(customerOrder);
        mail.setHeaderPicture("images/mails/send-publication-receipt.png");
        mail.setMailTemplate(CustomerMail.TEMPLATE_SEND_PUBLICATION_RECEIPT);

        Provision currentProvision = null;
        if (customerOrder.getAssoAffaireOrders() != null && customerOrder.getAssoAffaireOrders().size() > 0)
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                for (Service service : asso.getServices())
                    for (Provision provision : service.getProvisions())
                        if (provision.getAnnouncement() != null
                                && provision.getAnnouncement().getId().equals(announcement.getId())) {
                            currentProvision = provision;
                        }

        if (currentProvision == null)
            return;

        List<Attachment> attachments = new ArrayList<Attachment>();
        if (currentProvision.getAttachments() != null) {
            for (Attachment attachment : attachmentService.sortAttachmentByDateDesc(currentProvision.getAttachments()))
                if (attachment.getAttachmentType().getId().equals(constantService.getAttachmentTypePublicationReceipt()
                        .getId())) {
                    attachments.add(attachment);
                    break;
                }
        }

        // Do not check when send to me because we don't necessarily already generate
        // publication receipt
        if (attachments.size() == 0 && !sendToMe)
            if (announcement.getConfrere() != null
                    && announcement.getConfrere().getId().equals(constantService.getConfrereJssSpel().getId()))
                throw new OsirisException(null,
                        "Publication receipt attachment not found for announcement n°" + announcement.getId());
            else
                throw new OsirisClientMessageException("Veuillez uploader le justificatif de parution !");

        mail.setAttachments(attachments);
        mail.setReplyTo(currentProvision.getAssignedTo());
        mail.setSendToMe(sendToMe);
        mail.setMailComputeResult(mailComputeHelper.computeMailForPublicationReceipt(customerOrder));
        mail.setSubject("Attestation de parution - commande n°" + customerOrder.getId() + " - "
                + getCustomerOrderAffaireLabel(customerOrder, currentProvision.getService().getAssoAffaireOrder()));
        mailService.addMailToQueue(mail);
    }

    public void sendProofReadingToCustomer(CustomerOrder customerOrder, boolean sendToMe, Announcement announcement,
            boolean isReminder)
            throws OsirisException, OsirisClientMessageException {

        CustomerMail mail = new CustomerMail();
        mail.setCustomerOrder(customerOrder);

        mail.setHeaderPicture("images/mails/send-proof-reading.png");
        mail.setMailTemplate(CustomerMail.TEMPLATE_SEND_PROOF_READING);

        Provision currentProvision = null;
        if (customerOrder.getAssoAffaireOrders() != null && customerOrder.getAssoAffaireOrders().size() > 0)
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                for (Service service : asso.getServices())
                    for (Provision provision : service.getProvisions())
                        if (provision.getAnnouncement() != null
                                && provision.getAnnouncement().getId().equals(announcement.getId())) {
                            currentProvision = provision;
                        }

        if (currentProvision == null)
            return;

        List<Attachment> attachments = new ArrayList<Attachment>();
        for (Attachment attachment : attachmentService.sortAttachmentByDateDesc(currentProvision.getAttachments()))
            if (attachment.getAttachmentType().getId()
                    .equals(constantService.getAttachmentTypeProofReading()
                            .getId())) {
                attachments.add(attachment);
                break;
            }

        if (attachments.size() == 0)
            throw new OsirisException(null,
                    "Unable to find reading proof PDF for CustomerOrder n°" + customerOrder.getId());

        mail.setAttachments(attachments);
        mail.setReplyTo(currentProvision.getAssignedTo());
        mail.setSendToMe(sendToMe);
        mail.setMailComputeResult(mailComputeHelper.computeMailForReadingProof(customerOrder));
        mail.setSubject("BAT à valider concernant la commande n°" + customerOrder.getId() + " - "
                + getCustomerOrderAffaireLabel(customerOrder, currentProvision.getService().getAssoAffaireOrder()));
        mailService.addMailToQueue(mail);
    }

    public void sendReminderToCustomerForBilanPublication(Announcement announcement, CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException {

        CustomerMail mail = new CustomerMail();
        mail.setCustomerOrder(customerOrder);

        mail.setHeaderPicture("images/mails/send-customer-bilan-publication-reminder.png");
        mail.setMailTemplate(CustomerMail.TEMPLATE_SEND_CUSTOMER_BILAN_PUBLICATION_REMINDER);

        Provision currentProvision = null;
        if (customerOrder.getAssoAffaireOrders() != null && customerOrder.getAssoAffaireOrders().size() > 0)
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                for (Service service : asso.getServices())
                    for (Provision provision : service.getProvisions())
                        if (provision.getAnnouncement() != null
                                && provision.getAnnouncement().getId().equals(announcement.getId())) {
                            currentProvision = provision;
                        }

        if (currentProvision == null)
            return;

        mail.setProvision(currentProvision);
        mail.setReplyTo(customerOrder.getAssignedTo());
        mail.setSendToMe(false);
        mail.setMailComputeResult(mailComputeHelper.computeMailForCustomerOrderFinalizationAndInvoice(customerOrder));
        mail.setSubject("Publication de vos comptes annuels - "
                + getCustomerOrderAffaireLabel(customerOrder, currentProvision.getService().getAssoAffaireOrder()));
        mailService.addMailToQueue(mail);
    }

    public void sendPublicationFlagToCustomer(CustomerOrder customerOrder, boolean sendToMe, Announcement announcement)
            throws OsirisException, OsirisClientMessageException {

        CustomerMail mail = new CustomerMail();
        mail.setCustomerOrder(customerOrder);
        mail.setHeaderPicture("images/mails/send-publication-flag.png");
        mail.setMailTemplate(CustomerMail.TEMPLATE_SEND_PUBLICATION_FLAG);

        Provision currentProvision = null;
        if (customerOrder.getAssoAffaireOrders() != null && customerOrder.getAssoAffaireOrders().size() > 0)
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                for (Service service : asso.getServices())
                    for (Provision provision : service.getProvisions())
                        if (provision.getAnnouncement() != null
                                && provision.getAnnouncement().getId().equals(announcement.getId())) {
                            currentProvision = provision;
                        }

        if (currentProvision == null)
            return;

        List<Attachment> attachments = new ArrayList<Attachment>();
        if (currentProvision.getAttachments() != null) {
            for (Attachment attachment : attachmentService.sortAttachmentByDateDesc(currentProvision.getAttachments()))
                if (attachment.getAttachmentType().getId().equals(constantService.getAttachmentTypePublicationFlag()
                        .getId())) {
                    attachments.add(attachment);
                    break;
                }
        }

        // Do not check when send to me because we don't necessarily already generate
        // publication receipt
        if (attachments.size() == 0 && !sendToMe)
            if (announcement.getConfrere() != null
                    && announcement.getConfrere().getId().equals(constantService.getConfrereJssSpel().getId()))
                throw new OsirisException(null,
                        "Publication receipt attachment not found for announcement n°" + announcement.getId());
            else
                throw new OsirisClientMessageException("Veuillez uploader le témoin de parution !");

        mail.setAttachments(attachments);
        mail.setReplyTo(currentProvision.getAssignedTo());
        mail.setSendToMe(sendToMe);
        mail.setMailComputeResult(mailComputeHelper.computeMailForPublicationReceipt(customerOrder));
        mail.setSubject("Témoin de parution - commande n°" + customerOrder.getId() + " - "
                + getCustomerOrderAffaireLabel(customerOrder, currentProvision.getService().getAssoAffaireOrder()));
        mailService.addMailToQueue(mail);
    }

    public void sendAnnouncementRequestToConfrere(CustomerOrder customerOrder, AssoAffaireOrder asso,
            boolean sendToMe, Provision provision, Announcement announcement, boolean isReminder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {

        if (announcement == null || announcement.getConfrere() == null || announcement.getPublicationDate() == null
                || announcement.getNoticeTypes() == null || announcement.getNoticeTypes().size() == 0)
            return;

        CustomerMail mail = new CustomerMail();
        mail.setCustomerOrder(customerOrder);
        mail.setHeaderPicture("images/mails/send-publication-flag.png");

        mail.setMailTemplate(CustomerMail.TEMPLATE_SEND_ANNOUNCEMENT_TO_CONFRERE);
        if (isReminder)
            mail.setMailTemplate(CustomerMail.TEMPLATE_SEND_ANNOUNCEMENT_TO_CONFRERE_REMINDER);

        List<Attachment> attachments = new ArrayList<Attachment>();
        for (Attachment attachment : attachmentService.sortAttachmentByDateDesc(provision.getAttachments()))
            if (attachment.getAttachmentType().getId()
                    .equals(constantService.getAttachmentTypeAnnouncement()
                            .getId())) {
                attachments.add(attachment);
                break;
            }

        mail.setAttachments(attachments);
        mail.setReplyTo(asso.getAssignedTo());
        mail.setSendToMe(sendToMe);
        mail.setProvision(provision);
        mail.setMailComputeResult(mailComputeHelper.computeMailForSendAnnouncementToConfrere(announcement));
        mail.setSubject("Demande d'insertion légale pour notre commande n°" + customerOrder.getId() + " - "
                + getCustomerOrderAffaireLabel(customerOrder, asso));
        if (isReminder)
            mail.setSubject("Relance attestation de parution pour notre commande n°" + customerOrder.getId() + " - "
                    + getCustomerOrderAffaireLabel(customerOrder, asso));
        mailService.addMailToQueue(mail);
    }

    public void sendAnnouncementErratumToConfrere(CustomerOrder customerOrder, AssoAffaireOrder asso,
            boolean sendToMe, Provision provision, Announcement announcement)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {

        if (announcement == null || announcement.getConfrere() == null || announcement.getPublicationDate() == null
                || announcement.getNoticeTypes() == null || announcement.getNoticeTypes().size() == 0)
            return;

        CustomerMail mail = new CustomerMail();
        mail.setCustomerOrder(customerOrder);
        mail.setHeaderPicture("images/mails/send-publication-flag.png");

        mail.setMailTemplate(CustomerMail.TEMPLATE_SEND_ANNOUNCEMENT_ERRATUM_TO_CONFRERE);

        List<Attachment> attachments = new ArrayList<Attachment>();
        for (Attachment attachment : attachmentService.sortAttachmentByDateDesc(provision.getAttachments()))
            if (attachment.getAttachmentType().getId()
                    .equals(constantService.getAttachmentTypeAnnouncement()
                            .getId())) {
                attachments.add(attachment);
                break;
            }

        mail.setAttachments(attachments);
        mail.setReplyTo(asso.getAssignedTo());
        mail.setSendToMe(sendToMe);
        mail.setProvision(provision);
        mail.setMailComputeResult(mailComputeHelper.computeMailForSendAnnouncementToConfrere(announcement));
        mail.setSubject(
                "Erratum - modification d'insertion légale pour notre commande n°" + customerOrder.getId() + " - "
                        + getCustomerOrderAffaireLabel(customerOrder, asso));
        mailService.addMailToQueue(mail);
    }

    public void sendConfrereReminderProviderInvoice(CustomerOrder customerOrder, AssoAffaireOrder asso,
            boolean sendToMe, Provision provision, Announcement announcement)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {

        if (announcement == null || announcement.getConfrere() == null || announcement.getPublicationDate() == null
                || announcement.getNoticeTypes() == null || announcement.getNoticeTypes().size() == 0)
            return;

        CustomerMail mail = new CustomerMail();
        mail.setCustomerOrder(customerOrder);
        mail.setHeaderPicture("images/mails/send-confrere-provider-invoice-reminder.png");

        mail.setMailTemplate(CustomerMail.TEMPLATE_SEND_CONFRERE_PROVIDER_INVOICE_REMINDER);

        List<Attachment> attachments = new ArrayList<Attachment>();
        for (Attachment attachment : attachmentService.sortAttachmentByDateDesc(provision.getAttachments()))
            if (attachment.getAttachmentType().getId()
                    .equals(constantService.getAttachmentTypeAnnouncement()
                            .getId())) {
                attachments.add(attachment);
                break;
            }

        mail.setAttachments(attachments);
        mail.setReplyTo(asso.getAssignedTo());
        mail.setSendToMe(sendToMe);
        mail.setProvision(provision);
        mail.setMailComputeResult(mailComputeHelper.computeMailForSendAnnouncementToConfrere(announcement));
        mail.setSubject("Relance facture - commande n°" + customerOrder.getId() + " - "
                + getCustomerOrderAffaireLabel(customerOrder, asso));
        mailService.addMailToQueue(mail);
    }

    public void sendCustomerOrderFinalisationToCustomer(CustomerOrder customerOrder, boolean sendToMe,
            boolean isReminder, boolean isLastReminder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {

        CustomerMail mail = new CustomerMail();
        mail.setCustomerOrder(customerOrder);
        mail.setMailComputeResult(mailComputeHelper.computeMailForCustomerOrderFinalizationAndInvoice(customerOrder));

        List<Attachment> attachments = new ArrayList<Attachment>();
        List<Integer> attachmentTypeIdsDone = new ArrayList<Integer>();

        if (customerOrder.getAttachments() != null) {
            for (Attachment attachment : attachmentService.sortAttachmentByDateDesc(customerOrder.getAttachments())) {
                if (attachment.getAttachmentType().getId().equals(constantService.getAttachmentTypeInvoice().getId())) {
                    attachments.add(attachment);
                    attachmentTypeIdsDone.add(attachment.getAttachmentType().getId());
                    break;
                }
            }
        }

        mail.setAttachments(attachments);
        mail.setHeaderPicture("images/mails/customer-order-finalization.png");
        mail.setReplyToMail(constantService.getStringAccountingSharedMaiblox());
        mail.setSendToMe(sendToMe);
        mail.setMailTemplate(CustomerMail.TEMPLATE_CUSTOMER_ORDER_FINALIZATION);

        if (isReminder || isLastReminder)
            mail.setMailTemplate(CustomerMail.TEMPLATE_INVOICE_REMINDER);

        mail.setIsLastReminder(isLastReminder);

        Invoice invoice = null;
        for (Invoice invoiceCo : mail.getCustomerOrder().getInvoices())
            if (invoiceCo.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusSend().getId())
                    || invoiceCo.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusPayed().getId()))
                invoice = invoiceCo;

        mail.setCbLink(
                paymentCbEntryPoint + "/order/invoice?customerOrderId=" + mail.getCustomerOrder().getId() + "&mail="
                        + mail.getMailComputeResult().getRecipientsMailTo().get(0).getMail());

        if (invoice != null)
            mail.setSubject(
                    "Votre facture n°" + invoice.getId() + " concernant la commande n°" + customerOrder.getId() + " - "
                            + getCustomerOrderAffaireLabel(customerOrder, null));
        else
            mail.setSubject(
                    "Votre facture concernant la commande n°" + customerOrder.getId() + " - "
                            + getCustomerOrderAffaireLabel(customerOrder, null));

        mailService.addMailToQueue(mail);
    }

    public void sendBillingClosureToCustomer(List<Attachment> attachments, Tiers tiers, boolean sendToMe)
            throws OsirisException, OsirisClientMessageException {

        CustomerMail mail = new CustomerMail();
        mail.setMailTemplate(CustomerMail.TEMPLATE_BILLING_CLOSURE);
        mail.setHeaderPicture("images/mails/billing-closure.png");
        if (attachments.size() == 0)
            return;
        mail.setAttachments(attachments);
        mail.setReplyToMail(constantService.getStringAccountingSharedMaiblox() + "");
        mail.setSendToMe(sendToMe);
        mail.setMailComputeResult(mailComputeHelper.computeMailForBillingClosure(tiers));
        mail.setSubject("Votre relevé de compte client n°" + tiers.getId());
        mail.setTiers(tiers);

        mailService.addMailToQueue(mail);
    }

    public void sendNewPasswordMail(Responsable responsable, String password) throws OsirisException {
        responsableService.getResponsable(responsable.getId());
        CustomerMail mail = new CustomerMail();
        mail.setMailTemplate(CustomerMail.TEMPLATE_RENEW_PASSWORD);
        mail.setHeaderPicture("images/mails/renew-password.png");
        mail.setReplyToMail(constantService.getStringSalesSharedMailbox() + "");
        mail.setSendToMe(false);
        mail.setExplaination(password);
        MailComputeResult mailComputeResult = new MailComputeResult();
        mailComputeResult.setRecipientsMailTo(new ArrayList<Mail>());

        for (Mail mailResponsable : responsable.getMails()) {
            mailComputeResult.getRecipientsMailTo().add(mailResponsable);
        }
        mail.setMailComputeResult(mailComputeResult);

        mail.setSubject("Votre nouveau mot de passe");

        mailService.addMailToQueue(mail);
    }

    public void sendRibRequestToAffaire(Affaire affaire, AssoAffaireOrder assoAffaireOrder) throws OsirisException {
        CustomerMail mail = new CustomerMail();
        mail.setMailTemplate(CustomerMail.TEMPLATE_REQUEST_RIB);
        mail.setHeaderPicture("images/mails/request-rib.png");
        mail.setReplyTo(assoAffaireOrder.getCustomerOrder().getAssignedTo());
        mail.setSendToMe(false);
        MailComputeResult mailComputeResult = new MailComputeResult();
        mailComputeResult.setRecipientsMailTo(new ArrayList<Mail>());
        mailComputeResult.getRecipientsMailTo().addAll(affaire.getMails());
        mail.setMailComputeResult(mailComputeResult);
        mail.setCustomerOrder(assoAffaireOrder.getCustomerOrder());

        mail.setSubject("Demande de RIB concernant la commande n°" + assoAffaireOrder.getCustomerOrder().getId() + " - "
                + getCustomerOrderAffaireLabel(assoAffaireOrder.getCustomerOrder(), assoAffaireOrder));

        mailService.addMailToQueue(mail);
    }

    public void sendRffToCustomer(Rff rff, boolean sendToMe) throws OsirisException, OsirisClientMessageException {
        CustomerMail mail = new CustomerMail();
        mail.setHeaderPicture("images/mails/send-rff.png");
        mail.setReplyToMail(rff.getTiers().getSalesEmployee().getMail());
        mail.setSendToMe(sendToMe);
        mail.setMailComputeResult(mailComputeHelper.computeMailForRff(rff));
        mail.setSubject("Vos remboursements forfaitaires de frais pour le compte n°" + rff.getTiers().getId());
        mail.setRff(rff);
        mail.setTiers(rff.getTiers());
        mail.setMailTemplate(CustomerMail.TEMPLATE_SEND_RFF);

        mailService.addMailToQueue(mail);
    }

    public void sendMissingAttachmentQueryToCustomer(MissingAttachmentQuery query, Boolean isLastReminder)
            throws OsirisException, OsirisClientMessageException {

        CustomerMail mail = new CustomerMail();
        CustomerOrder customerOrder = query.getService().getAssoAffaireOrder().getCustomerOrder();
        mail.setMailTemplate(CustomerMail.TEMPLATE_MISSING_ATTACHMENT);
        mail.setCustomerOrder(customerOrder);
        mail.setMissingAttachmentQuery(query);
        mail.setHeaderPicture("images/mails/missing-attachment.png");
        if (query.getComment() != null)
            mail.setExplaination(query.getComment().replaceAll("\r?\n", "<br/>"));

        Employee sendToEmployee = employeeService.getCurrentEmployee();
        if (sendToEmployee == null)
            sendToEmployee = query.getService().getAssoAffaireOrder().getAssignedTo();
        mail.setReplyTo(sendToEmployee);
        mail.setSendToMe(query.getSendToMe());
        mail.setMailComputeResult(mailComputeHelper.computeMailForGenericDigitalDocument(customerOrder));
        mail.setIsLastReminder(isLastReminder);

        if (query.getCopyToMe()) {
            mail.setCopyToMe(true);
        }

        List<Attachment> attachments = new ArrayList<Attachment>();
        if (mail.getMissingAttachmentQuery() != null) {
            for (AssoServiceDocument asso : mail.getMissingAttachmentQuery().getAssoServiceDocument())
                if (asso.getTypeDocument() != null)
                    if (asso.getTypeDocument().getAttachments() != null
                            && asso.getTypeDocument().getAttachments().size() > 0)
                        for (Attachment attachment : asso.getTypeDocument().getAttachments())
                            attachments.add(attachment);
        }

        mail.setAttachments(attachments);

        mail.setSubject("Pièces manquantes concernant la commande n°" + customerOrder.getId() + " - "
                + getCustomerOrderAffaireLabel(customerOrder, null));

        mailService.addMailToQueue(mail);
    }

    public void sendQuotationCreationConfirmationToCustomer(Quotation quotation)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        CustomerMail mail = new CustomerMail();
        mail.setQuotation(quotation);
        mail.setHeaderPicture("images/mails/quotation-validated.png");
        mail.setMailTemplate(CustomerMail.TEMPLATE_QUOTATION_VALIDATED);
        mail.setReplyToMail(constantService.getStringSalesSharedMailbox());
        mail.setSendToMe(false);
        mail.setMailComputeResult(mailComputeHelper.computeMailForQuotationCreationConfirmation(quotation));
        mail.setSubject(
                "Validation de votre devis n°" + quotation.getId() + " - "
                        + getCustomerOrderAffaireLabel(quotation, null));
        mailService.addMailToQueue(mail);
    }

    public void sendCreditNoteToCustomer(CustomerOrder customerOrder, boolean sendToMe, Invoice creditNote)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {

        CustomerMail mail = new CustomerMail();
        mail.setCustomerOrder(customerOrder);
        mail.setMailComputeResult(mailComputeHelper.computeMailForCustomerOrderFinalizationAndInvoice(customerOrder));

        List<Attachment> attachments = new ArrayList<Attachment>();
        List<Integer> attachmentTypeIdsDone = new ArrayList<Integer>();

        if (customerOrder.getAttachments() != null) {
            for (Attachment attachment : attachmentService.sortAttachmentByDateDesc(customerOrder.getAttachments())) {
                if (attachment.getAttachmentType().getId()
                        .equals(constantService.getAttachmentTypeCreditNote().getId())) {
                    attachments.add(attachment);
                    attachmentTypeIdsDone.add(attachment.getAttachmentType().getId());
                    break;
                }
            }
        }

        mail.setAttachments(attachments);
        mail.setHeaderPicture("images/mails/send-credit-note.png");
        mail.setReplyToMail(constantService.getStringAccountingSharedMaiblox());
        mail.setSendToMe(sendToMe);
        mail.setMailTemplate(CustomerMail.TEMPLATE_SEND_CREDIT_NOTE);

        if (creditNote != null)
            mail.setSubject(
                    "Votre avoir n°" + creditNote.getId() + " concernant la commande n°" + customerOrder.getId()
                            + " - "
                            + getCustomerOrderAffaireLabel(customerOrder, null));
        else
            mail.setSubject(
                    "Votre avoir concernant la commande n°" + customerOrder.getId() + " - "
                            + getCustomerOrderAffaireLabel(customerOrder, null));

        mailService.addMailToQueue(mail);
    }

    public void sendCompetentAuthorityMailForReminder(Employee employee, CompetentAuthority competentAuthority,
            List<Provision> provisionToSend, List<Mail> mails) throws OsirisException, OsirisClientMessageException {

        CustomerMail mail = new CustomerMail();
        mail.setCompetentAuthority(competentAuthority);
        mail.setMailComputeResult(mailComputeHelper.computeMailForMailList(mails));
        mail.setHeaderPicture("images/mails/send-credit-note.png");
        mail.setReplyTo(employee);
        mail.setSendToMe(false);
        mail.setCopyToMe(false);
        mail.setCopyToMail(employee.getMail());
        mail.setMailTemplate(CustomerMail.TEMPLATE_SEND_COMPETENT_AUTHORITY_REMINDER);

        List<String> provisionDetails = new ArrayList<String>();
        for (Provision provision : provisionToSend) {
            String label = "";
            label += provision.getLastStatusReminderAcDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            Affaire affaire = provision.getService().getAssoAffaireOrder().getAffaire();
            label += " dénomination " + (affaire.getDenomination() != null ? affaire.getDenomination()
                    : (affaire.getFirstname() + " " + affaire.getLastname()));
            if ((affaire.getIsUnregistered() == null || !affaire.getIsUnregistered())
                    && (affaire.getSiren() != null || affaire.getSiret() != null)) {
                label += "  / RCS " + (affaire.getSiret() != null ? affaire.getSiret() : affaire.getSiren());
            }
            label += " / "
                    + provision.getService().getServiceType().getServiceFamily().getServiceFamilyGroup().getLabel();

            if (provision.getFormalite() != null && provision.getFormalite().getFormalitesGuichetUnique() != null) {
                ArrayList<String> liasseList = new ArrayList<String>();
                for (FormaliteGuichetUnique formaliteGuichetUnique : provision.getFormalite()
                        .getFormalitesGuichetUnique()) {
                    if (!formaliteGuichetUnique.getStatus().getIsCloseState())
                        liasseList.add(formaliteGuichetUnique.getLiasseNumber());
                }
                if (liasseList.size() > 0)
                    label += " / liasse(s) GU " + String.join(", ", liasseList);
            }
            if (provision.getFormalite() != null && provision.getFormalite().getFormalitesInfogreffe() != null) {
                ArrayList<String> liasseList = new ArrayList<String>();
                for (FormaliteInfogreffe formaliteInfogreffe : provision.getFormalite()
                        .getFormalitesInfogreffe()) {
                    EvenementInfogreffe evenementInfogreffe = formaliteInfogreffeService
                            .getLastEvenementInfogreffe(formaliteInfogreffe, true);
                    if (evenementInfogreffe != null && (evenementInfogreffe.getCodeEtat().equals("ENVOYE_AU_GRF")
                            || evenementInfogreffe.getCodeEtat().equals("RECU_PAR_LE_GRF"))) {
                        liasseList.add(formaliteInfogreffe.getReferenceTechnique());
                    }
                }
                if (liasseList.size() > 0)
                    label += " / liasse(s) Infogreffe " + String.join(", ", liasseList);
            }
            provisionDetails.add(label);
        }

        mail.setExplaination(String.join("removeme", provisionDetails));
        mail.setSubject(competentAuthority.getLabel() + " - Dossier en attente de validation");

        mailService.addMailToQueue(mail);
    }

    public void sendCustomerOrderAttachmentOnFinalisationToCustomer(CustomerOrder customerOrder, boolean sendToMe)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        List<Attachment> attachments = new ArrayList<Attachment>();
        List<Integer> attachmentTypeIdsDone = new ArrayList<Integer>();

        if (customerOrder.getAttachments() != null) {
            for (Attachment attachment : attachmentService.sortAttachmentByDateDesc(customerOrder.getAttachments())) {
                if (attachment.getAttachmentType().getIsToSentOnFinalizationMail()
                        && !attachmentTypeIdsDone.contains(attachment.getAttachmentType().getId())
                        && !attachment.getAttachmentType().getId()
                                .equals(constantService.getAttachmentTypeInvoice().getId())) {
                    attachments.add(attachment);
                    attachmentTypeIdsDone.add(attachment.getAttachmentType().getId());
                }
            }
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                for (Service service : asso.getServices())
                    for (Provision provision : service.getProvisions()) {
                        if (provision.getAttachments() != null && provision.getAttachments().size() > 0)
                            for (Attachment attachment : attachmentService
                                    .sortAttachmentByDateDesc(provision.getAttachments()))
                                if ((sendToMe == true || attachment.getIsAlreadySent() == false)
                                        && attachment.getAttachmentType().getIsToSentOnFinalizationMail()
                                        && !attachmentTypeIdsDone.contains(attachment.getAttachmentType().getId())
                                        && !attachment.getAttachmentType().getId()
                                                .equals(constantService.getAttachmentTypeInvoice().getId())) {
                                    attachments.add(attachment);
                                    attachmentTypeIdsDone.add(attachment.getAttachmentType().getId());
                                }
                        // Send once per provision
                        attachmentTypeIdsDone = new ArrayList<Integer>();
                    }
        }

        if (attachments.size() > 0)
            sendCustomerOrderAttachmentsToCustomer(customerOrder, customerOrder.getAssoAffaireOrders().get(0), sendToMe,
                    attachments);
    }
}
