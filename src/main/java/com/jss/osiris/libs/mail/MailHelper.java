package com.jss.osiris.libs.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.itextpdf.text.DocumentException;
import com.jss.osiris.libs.QrCodeHelper;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.mail.model.CustomerMail;
import com.jss.osiris.libs.mail.model.CustomerMailAssoAffaireOrder;
import com.jss.osiris.libs.mail.model.MailComputeResult;
import com.jss.osiris.libs.mail.model.VatMail;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.Deposit;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.service.InvoiceHelper;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.Mail;
import com.jss.osiris.modules.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.Announcement;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.IQuotation;
import com.jss.osiris.modules.quotation.model.NoticeType;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.service.AssoAffaireOrderService;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.quotation.service.QuotationService;
import com.jss.osiris.modules.tiers.model.ITiers;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

@Service
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
        templateResolver.setPrefix("mails/");
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

    public MimeMessage generateGenericMail(CustomerMail mail) throws OsirisException {
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

            if (mail.getSendToMe() != null && mail.getSendToMe())
                message.setTo(mail.getSendToMeEmployee().getMail());
            else {
                if (mail.getMailComputeResult().getRecipientsMailTo() == null
                        || mail.getMailComputeResult().getRecipientsMailTo().size() == 0)
                    throw new OsirisException(null, "No recipient found");

                for (Mail mailTo : mail.getMailComputeResult().getRecipientsMailTo())
                    message.setTo(mailTo.getMail());

                if (mail.getMailComputeResult().getRecipientsMailCc() != null
                        && mail.getMailComputeResult().getRecipientsMailTo().size() > 0)
                    for (Mail mailCc : mail.getMailComputeResult().getRecipientsMailCc())
                        message.setCc(mailCc.getMail());
            }

            message.setSubject(mail.getSubject());

            // Create the HTML body using Thymeleaf
            final String htmlContent = emailTemplateEngine().process("model", ctx);
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
                for (Attachment attachment : mail.getAttachments())
                    message.addAttachment(attachment.getUploadedFile().getFilename(),
                            new File(attachment.getUploadedFile().getPath()));
            }
        } catch (MessagingException e) {
        }

        return mimeMessage;
    }

    public File generateGenericPdf(CustomerMail mail) throws OsirisException {
        final Context ctx = new Context();
        setContextVariable(ctx, mail, true);
        String htmlContent = "";
        // Create the HTML body using Thymeleaf
        try {
            htmlContent = emailTemplateEngine().process("model", ctx);
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
        try {
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            outputStream.close();
        } catch (Exception e) {
            throw new OsirisException(e, "Unable to create PDF file for mail " + mail.getId());
        }
        return tempFile;
    }

    private void setContextVariable(Context ctx, CustomerMail mail, boolean setPlainPictures) {
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
        ctx.setVariable("title", mail.getTitle());
        ctx.setVariable("subtitle", mail.getSubtitle());
        ctx.setVariable("label", mail.getLabel());
        ctx.setVariable("explaination", mail.getExplaination());
        ctx.setVariable("explainationElements",
                mail.getExplainationElements() != null ? mail.getExplainationElements().split("!#") : null);
        ctx.setVariable("explaination2", mail.getExplaination2());
        ctx.setVariable("explaination3", mail.getExplaination3());
        ctx.setVariable("paymentExplainationWarning", mail.getPaymentExplainationWarning());
        ctx.setVariable("paymentExplaination", mail.getPaymentExplaination());
        ctx.setVariable("paymentExplaination2", mail.getPaymentExplaination2());

        if (mail.getCustomerMailAssoAffaireOrders() != null) {
            ArrayList<AssoAffaireOrder> assos = new ArrayList<AssoAffaireOrder>();
            for (CustomerMailAssoAffaireOrder customerAsso : mail.getCustomerMailAssoAffaireOrders()) {
                AssoAffaireOrder tmpAsso = assoAffaireOrderService
                        .getAssoAffaireOrder(customerAsso.getAssoAffaireOrderId());
                if (tmpAsso != null)
                    assos.add(tmpAsso);
            }
            if (assos.size() > 0)
                ctx.setVariable("assos", assos);
        }

        ctx.setVariable("preTaxPriceTotal", mail.getPreTaxPriceTotal());
        ctx.setVariable("discountTotal", mail.getDiscountTotal());
        ctx.setVariable("preTaxPriceTotalWithDicount", mail.getPreTaxPriceTotalWithDicount());
        ctx.setVariable("vats", mail.getVatMails());
        ctx.setVariable("priceTotal", mail.getPriceTotal());
        ctx.setVariable("totalSubtitle", mail.getTotalSubtitle());
        ctx.setVariable("greetings", mail.getGreetings());
        ctx.setVariable("cbExplanation", mail.getCbExplanation());
        ctx.setVariable("cbLink", mail.getCbLink());
        ctx.setVariable("qrCodePicture", mail.getCbLink() != null ? "qrCodePicture" : null);
    }

    public String generateGenericHtmlConfirmation(String title, String subtitle, String label, String explaination,
            String explainationWarning, String greetings) {
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

    private void computeQuotationPrice(CustomerMail mail, IQuotation quotation) {
        // Compute prices
        Float preTaxPriceTotal = 0f;
        Float discountTotal = null;
        Float preTaxPriceTotalWithDicount = null;
        ArrayList<VatMail> vats = null;
        Float vatTotal = 0f;
        Float priceTotal = null;

        for (AssoAffaireOrder asso : quotation.getAssoAffaireOrders()) {
            for (Provision provision : asso.getProvisions()) {
                for (InvoiceItem invoiceItem : provision.getInvoiceItems()) {
                    preTaxPriceTotal += invoiceItem.getPreTaxPrice();
                    if (invoiceItem.getDiscountAmount() != null && invoiceItem.getDiscountAmount() > 0) {
                        if (discountTotal == null)
                            discountTotal = invoiceItem.getDiscountAmount();
                        else
                            discountTotal += invoiceItem.getDiscountAmount();
                    }
                    if (invoiceItem.getVat() != null && invoiceItem.getVatPrice() != null
                            && invoiceItem.getVatPrice() > 0) {
                        vatTotal += invoiceItem.getVatPrice();
                        if (vats == null)
                            vats = new ArrayList<VatMail>();
                        boolean vatFound = false;
                        for (VatMail vatMail : vats) {
                            if (vatMail.getLabel().equals(invoiceItem.getVat().getLabel())) {
                                vatFound = true;
                                if (vatMail.getTotal() == null) {
                                    vatMail.setTotal(invoiceItem.getVatPrice());
                                    vatMail.setBase(invoiceItem.getPreTaxPrice());
                                } else {
                                    vatMail.setTotal(vatMail.getTotal() + invoiceItem.getVatPrice());
                                    vatMail.setBase(vatMail.getBase() + invoiceItem.getPreTaxPrice());
                                }
                            }
                        }
                        if (!vatFound) {
                            VatMail vatmail = new VatMail();
                            vatmail.setTotal(invoiceItem.getVatPrice());
                            vatmail.setLabel(invoiceItem.getVat().getLabel());
                            vatmail.setBase(invoiceItem.getPreTaxPrice());
                            vatmail.setCustomerMail(mail);
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

        mail.setPreTaxPriceTotal(preTaxPriceTotal);
        mail.setDiscountTotal(discountTotal);
        mail.setPreTaxPriceTotalWithDicount(preTaxPriceTotalWithDicount);
        mail.setVatMails(vats);
        mail.setPriceTotal(Math.round(priceTotal * 100f) / 100f);
    }

    @Transactional(rollbackFor = Exception.class)
    public void generateQuotationMail(Quotation quotation) throws OsirisException, OsirisClientMessageException {
        sendQuotationToCustomer(quotation, true);
    }

    public void sendQuotationToCustomer(Quotation quotation, boolean sendToMe)
            throws OsirisException, OsirisClientMessageException {
        CustomerMail mail = new CustomerMail();
        mail.setQuotation(quotation);
        mail.setHeaderPicture("images/quotation-header.png");
        mail.setTitle("Votre nouveau devis est prêt !");
        mail.setSubtitle("Il n'attend plus que votre validation.");
        mail.setLabel("Devis n°" + quotation.getId());

        MailComputeResult mailComputeResult = mailComputeHelper.computeMailForQuotationMail(quotation);

        if (quotation.getAssoAffaireOrders().size() == 1) {
            Affaire affaire = quotation.getAssoAffaireOrders().get(0).getAffaire();
            mail.setExplaination("Vous trouverez ci-dessous le devis pour la société "
                    + (affaire.getDenomination() != null ? affaire.getDenomination()
                            : (affaire.getFirstname() + " " + affaire.getLastname())));
        } else {
            mail.setExplaination("Vous trouverez ci-dessous le devis pour les sociétés suivantes :");
            ArrayList<String> details = new ArrayList<String>();
            for (AssoAffaireOrder asso : quotation.getAssoAffaireOrders())
                details.add(asso.getAffaire().getDenomination() != null ? asso.getAffaire().getDenomination()
                        : (asso.getAffaire().getFirstname() + " " + asso.getAffaire().getLastname()));
            mail.setExplainationElements(String.join("!#", details));
        }

        mail.setExplaination2(
                "Pour valider ce devis ou nous faire part de vos remarques, répondez simplement à ce mail.");

        if (quotation.getAssoAffaireOrders() != null && quotation.getAssoAffaireOrders().size() > 0) {
            ArrayList<CustomerMailAssoAffaireOrder> customerAssos = new ArrayList<CustomerMailAssoAffaireOrder>();
            for (AssoAffaireOrder assos : quotation.getAssoAffaireOrders()) {
                CustomerMailAssoAffaireOrder asso = new CustomerMailAssoAffaireOrder();
                asso.setAssoAffaireOrderId(assos.getId());
                asso.setCustomerMail(mail);
                customerAssos.add(asso);
            }
            if (customerAssos.size() > 0)
                mail.setCustomerMailAssoAffaireOrders(customerAssos);
        }

        computeQuotationPrice(mail, quotation);

        ITiers tiers = quotationService.getCustomerOrderOfQuotation(quotation);
        boolean isDepositMandatory = false;
        boolean isPaymentTypePrelevement = false;

        if (tiers instanceof Tiers) {
            isDepositMandatory = ((Tiers) tiers).getIsProvisionalPaymentMandatory();
            isPaymentTypePrelevement = ((Tiers) tiers).getPaymentType() != null && ((Tiers) tiers).getPaymentType()
                    .getId().equals(constantService.getPaymentTypePrelevement().getId());
        } else if (tiers instanceof Confrere) {
            isDepositMandatory = ((Confrere) tiers).getIsProvisionalPaymentMandatory();
            isPaymentTypePrelevement = ((Confrere) tiers).getPaymentType() != null && ((Tiers) tiers).getPaymentType()
                    .getId().equals(constantService.getPaymentTypePrelevement().getId());
        } else if (tiers instanceof Responsable) {
            isDepositMandatory = ((Responsable) tiers).getTiers().getIsProvisionalPaymentMandatory();
            isPaymentTypePrelevement = ((Responsable) tiers).getTiers().getPaymentType() != null
                    && ((Responsable) tiers).getTiers().getPaymentType()
                            .getId().equals(constantService.getPaymentTypePrelevement().getId());
        }

        if (!isPaymentTypePrelevement) {
            if (isDepositMandatory)
                mail.setPaymentExplaination(
                        "Votre commande est en attente de provision. Effectuez dès maintenant un virement de "
                                + mail.getPriceTotal() + " € sur le compte ci-dessous pour la débloquer.");
            else
                mail.setPaymentExplaination("Vous pouvez d'ores et déjà régler cette commande d'un montant de "
                        + mail.getPriceTotal() + " € en suivant les instructions ci-dessous.");

            mail.setPaymentExplaination2("IBAN / BIC : " + ibanJss + " / " + bicJss);

            mail.setCbExplanation(
                    "Vous avez aussi la possibilité de payer par carte bancaire en flashant le QR Code ci-dessous ou en cliquant ");

            mail.setCbLink(paymentCbEntryPoint + "/quotation/deposit?quotationId=" + quotation.getId() + "&mail="
                    + mailComputeResult.getRecipientsMailTo().get(0).getMail());

            mail.setPaymentExplainationWarning(
                    "Référence à indiquer abolument dans le libellé de votre virement : " + quotation.getId());

        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        mail.setTotalSubtitle("Ce devis est valable jusqu'au "
                + LocalDate.now().withMonth(12).withDayOfMonth(31).format(formatter)
                + " et sous réserve que les formalités à réaliser, au vu des documents transmis, correspondent à la demande de devis. Toute modification entraînera son actualisation.");
        mail.setGreetings("Bonne journée !");

        if (isDepositMandatory)
            mail.setExplaination3("Dès paiement de l'acompte de " + mail.getPriceTotal()
                    + " € via le lien ci-dessous nous entamerons le traitement de cette commande avec tout notre savoir-faire.");
        else if (!isPaymentTypePrelevement)
            mail.setExplaination3(
                    "Dès réception de votre validation concernant ce devis ou paiement de son montant via le lien ci-dessous nous entamerons le traitement de cette commande avec tout notre savoir-faire. Vous pourrez effectuer la validation en ligne sur notre site web : https://www.jss.fr/ Espace abonné, rubrique \"Mon compte\".");

        ITiers customerOrder = quotationService.getCustomerOrderOfQuotation(quotation);
        mail.setReplyTo(customerOrder.getSalesEmployee());
        mail.setSendToMe(sendToMe);
        mail.setMailComputeResult(mailComputeResult);

        mail.setSubject("Votre devis n°" + quotation.getId());

        mailService.addMailToQueue(mail);
    }

    public void sendQuotationCreationConfirmationToCustomer(Quotation quotation)
            throws OsirisException, OsirisClientMessageException {
        CustomerMail mail = new CustomerMail();
        mail.setQuotation(quotation);
        mail.setHeaderPicture("images/quotation-header.png");
        mail.setTitle("Votre demande de devis");
        mail.setLabel("Devis n°" + quotation.getId());
        mail.setLabelSubtitle("Nous vous confirmons la réception de votre demande de devis décrite ci-dessous.");

        if (quotation.getAssoAffaireOrders() != null) {
            if (quotation.getAssoAffaireOrders().size() == 1) {
                Affaire affaire = quotation.getAssoAffaireOrders().get(0).getAffaire();
                mail.setExplaination("Ce devis concerne la société "
                        + (affaire.getDenomination() != null ? affaire.getDenomination()
                                : (affaire.getFirstname() + " " + affaire.getLastname())));
            } else {
                mail.setExplaination("Ce devis concerne les sociétés suivantes :");
                ArrayList<String> details = new ArrayList<String>();
                for (AssoAffaireOrder asso : quotation.getAssoAffaireOrders())
                    details.add(asso.getAffaire().getDenomination() != null ? asso.getAffaire().getDenomination()
                            : (asso.getAffaire().getFirstname() + " " + asso.getAffaire().getLastname()));
                mail.setExplainationElements(String.join("!#", details));
            }

            if (quotation.getAssoAffaireOrders() != null && quotation.getAssoAffaireOrders().size() > 0) {
                ArrayList<CustomerMailAssoAffaireOrder> customerAssos = new ArrayList<CustomerMailAssoAffaireOrder>();
                for (AssoAffaireOrder assos : quotation.getAssoAffaireOrders()) {
                    CustomerMailAssoAffaireOrder asso = new CustomerMailAssoAffaireOrder();
                    asso.setAssoAffaireOrderId(assos.getId());
                    asso.setCustomerMail(mail);
                    customerAssos.add(asso);
                }
                if (customerAssos.size() > 0)
                    mail.setCustomerMailAssoAffaireOrders(customerAssos);
            }

            computeQuotationPrice(mail, quotation);
        } else {
            mail.setExplaination("Description du devis : " + quotation.getDescription());
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        mail.setTotalSubtitle("Ce devis est valable jusqu'au "
                + LocalDate.now().withMonth(12).withDayOfMonth(31).format(formatter)
                + " et sous réserve que les formalités à réaliser, au vu des documents transmis, correspondent à la demande de devis. Toute modification entraînera son actualisation.");
        mail.setExplaination3(
                "Nos équipes vont vous contacter dans les plus brefs délais pour détailler ensemble votre besoin, si nécessaire.");
        mail.setGreetings("Bonne journée !");

        mail.setReplyToMail(constantService.getStringSalesSharedMailbox());
        mail.setSendToMe(false);
        mail.setMailComputeResult(mailComputeHelper.computeMailForQuotationCreationConfirmation(quotation));

        mail.setSubject("Votre devis n°" + quotation.getId() + " du " + LocalDate.now().format(formatter));

        mailService.addMailToQueue(mail);
    }

    public void generateCustomerOrderCreationConfirmationToCustomer(CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException {
        sendCustomerOrderCreationConfirmationToCustomer(customerOrder, true, false);
    }

    public void sendCustomerOrderCreationConfirmationToCustomer(CustomerOrder customerOrder, boolean sendToMe,
            boolean isReminder)
            throws OsirisException, OsirisClientMessageException {

        CustomerMail mail = new CustomerMail();
        mail.setCustomerOrder(customerOrder);
        computeQuotationPrice(mail, customerOrder);

        MailComputeResult mailComputeResult = mailComputeHelper
                .computeMailForCustomerOrderCreationConfirmation(customerOrder);

        ITiers tiers = quotationService.getCustomerOrderOfQuotation(customerOrder);

        boolean isDepositMandatory = false;
        boolean isPaymentTypePrelevement = false;

        if (tiers instanceof Tiers) {
            isDepositMandatory = ((Tiers) tiers).getIsProvisionalPaymentMandatory();
            isPaymentTypePrelevement = ((Tiers) tiers).getPaymentType() != null && ((Tiers) tiers).getPaymentType()
                    .getId().equals(constantService.getPaymentTypePrelevement().getId());
        } else if (tiers instanceof Confrere) {
            isDepositMandatory = ((Confrere) tiers).getIsProvisionalPaymentMandatory();
            isPaymentTypePrelevement = ((Confrere) tiers).getPaymentType() != null
                    && ((Confrere) tiers).getPaymentType()
                            .getId().equals(constantService.getPaymentTypePrelevement().getId());
        } else if (tiers instanceof Responsable) {
            isDepositMandatory = ((Responsable) tiers).getTiers().getIsProvisionalPaymentMandatory();
            isPaymentTypePrelevement = ((Responsable) tiers).getTiers().getPaymentType() != null
                    && ((Responsable) tiers).getTiers().getPaymentType()
                            .getId().equals(constantService.getPaymentTypePrelevement().getId());
        }
        Float remainingToPay = Math
                .round(accountingRecordService.getRemainingAmountToPayForCustomerOrder(customerOrder) * 100f) / 100f;

        mail.setHeaderPicture("images/waiting-deposit-header.png");
        mail.setTitle("Votre commande est prête !");

        if (isDepositMandatory && remainingToPay > 0 && !isPaymentTypePrelevement)
            mail.setSubtitle("Elle n'attend plus que votre paiement.");

        mail.setLabel("Commande n°" + customerOrder.getId());

        String explainationText = "";

        if (isReminder && !isPaymentTypePrelevement)
            explainationText = "Nous sommes toujours  en attente du réglement de l'acompte concernant votre commande n°"
                    + customerOrder.getId();
        else if (customerOrder.getQuotations() != null && customerOrder.getQuotations().size() > 0)
            explainationText = "Nous vous confirmons la validation du devis n°"
                    + customerOrder.getQuotations().get(0).getId() + " correspondant à votre commande n°"
                    + customerOrder.getId();
        else
            explainationText = "Nous vous confirmons la validation de votre commande n°" + customerOrder.getId();

        String responsibleString = "";
        if (customerOrder.getAssoAffaireOrders().size() == 1) {
            Affaire affaire = customerOrder.getAssoAffaireOrders().get(0).getAffaire();
            responsibleString = customerOrder.getAssoAffaireOrders().get(0).getAssignedTo().getFirstname() + " "
                    + customerOrder.getAssoAffaireOrders().get(0).getAssignedTo().getLastname() + " et ";
            mail.setExplaination(explainationText + " concernant la société " +
                    (affaire.getDenomination() != null ? affaire.getDenomination()
                            : (affaire.getFirstname() + " " + affaire.getLastname()))
                    + ".");
        } else {
            mail.setExplaination(explainationText + " concernant les sociétés indiquées ci-dessous.");

            ArrayList<String> details = new ArrayList<String>();
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                details.add(asso.getAffaire().getDenomination() != null ? asso.getAffaire().getDenomination()
                        : (asso.getAffaire().getFirstname() + " " + asso.getAffaire().getLastname()));
            mail.setExplainationElements(String.join("!#", details));
        }

        if (customerOrder.getAssoAffaireOrders() != null && customerOrder.getAssoAffaireOrders().size() > 0) {
            ArrayList<CustomerMailAssoAffaireOrder> customerAssos = new ArrayList<CustomerMailAssoAffaireOrder>();
            for (AssoAffaireOrder assos : customerOrder.getAssoAffaireOrders()) {
                CustomerMailAssoAffaireOrder asso = new CustomerMailAssoAffaireOrder();
                asso.setAssoAffaireOrderId(assos.getId());
                asso.setCustomerMail(mail);
                customerAssos.add(asso);
            }
            if (customerAssos.size() > 0)
                mail.setCustomerMailAssoAffaireOrders(customerAssos);
        }

        if (remainingToPay > 0 && !isPaymentTypePrelevement) {
            if (isDepositMandatory) {
                mail.setPaymentExplaination(
                        "Dès reception de votre réglement d'un montant de " + mail.getPriceTotal()
                                + " €, le traitement de votre commande débutera");
            } else {
                mail.setPaymentExplaination(
                        "Votre commande sera traitée dans les meilleurs délais et avec tout notre savoir-faire par "
                                + responsibleString
                                + "toutes nos équipes. Vous pourrez suivre l'état de son avancement en ligne sur notre site https://www.jss.fr/ Espace abonné, rubrique \"Mon compte\". \nVous pouvez d'ores et déjà régler cette commande d'un montant de "
                                + mail.getPriceTotal() + " € en suivant les instructions ci-dessous.");
            }

            mail.setPaymentExplaination2("IBAN / BIC : " + ibanJss + " / " + bicJss);

            mail.setCbExplanation(
                    "Vous avez aussi la possibilité de payer par carte bancaire en flashant le QR Code ci-dessous ou en cliquant ");

            if (mailComputeResult.getRecipientsMailTo() == null
                    || mailComputeResult.getRecipientsMailTo().get(0) == null
                    || mailComputeResult.getRecipientsMailTo().get(0).getMail() == null)
                throw new OsirisException(null,
                        "Unable to find mail for CB generation for customerOrder n°" + customerOrder.getId());

            mail.setCbLink(paymentCbEntryPoint + "/order/deposit?customerOrderId=" + customerOrder.getId() + "&mail="
                    + mailComputeResult.getRecipientsMailTo().get(0).getMail());

            mail.setPaymentExplainationWarning(
                    "Référence à indiquer abolument dans le libellé de votre virement : " + customerOrder.getId());

        } else {
            mail.setExplaination3(
                    "Votre commande sera traitée dans les meilleurs délais et avec tout notre savoir-faire par "
                            + responsibleString
                            + "toutes nos équipes. Vous pourrez suivre l'état de son avancement en ligne sur notre site https://www.jss.fr/ Espace abonné, rubrique \"Mon compte\".");
        }

        mail.setGreetings("Bonne journée !");

        mail.setReplyTo(tiers.getSalesEmployee());
        mail.setSendToMe(sendToMe);
        mail.setMailComputeResult(mailComputeResult);

        if (isDepositMandatory && remainingToPay > 0)
            mail.setSubject("Votre commande n°" + customerOrder.getId() + " est en attente de paiement");
        else
            mail.setSubject("Votre commande n°" + customerOrder.getId());

        mailService.addMailToQueue(mail);
    }

    public void generateCustomerOrderDepositConfirmationToCustomer(CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException {
        sendCustomerOrderDepositConfirmationToCustomer(customerOrder, true);
    }

    public void sendCustomerOrderDepositConfirmationToCustomer(CustomerOrder customerOrder, boolean sendToMe)
            throws OsirisException, OsirisClientMessageException {

        CustomerMail mail = new CustomerMail();
        mail.setCustomerOrder(customerOrder);
        computeQuotationPrice(mail, customerOrder);

        ITiers tiers = quotationService.getCustomerOrderOfQuotation(customerOrder);

        mail.setHeaderPicture("images/waiting-deposit-header.png");
        mail.setTitle("Votre acompte a bien été reçu !");

        mail.setLabel("Commande n°" + customerOrder.getId());

        String explainationText = "Nous vous confirmons la prise en compte d'un réglement de " + mail.getPriceTotal()
                + " € concernant vote commande n°" + customerOrder.getId();

        String responsibleString = "";
        if (customerOrder.getAssoAffaireOrders().size() == 1) {
            Affaire affaire = customerOrder.getAssoAffaireOrders().get(0).getAffaire();
            responsibleString = customerOrder.getAssoAffaireOrders().get(0).getAssignedTo().getFirstname() + " "
                    + customerOrder.getAssoAffaireOrders().get(0).getAssignedTo().getLastname() + " et ";
            mail.setExplaination(explainationText + " concernant la société " +
                    (affaire.getDenomination() != null ? affaire.getDenomination()
                            : (affaire.getFirstname() + " " + affaire.getLastname()))
                    + ".");
        } else {
            mail.setExplaination(explainationText + " concernant les sociétés indiquées ci-dessous.");

            ArrayList<String> details = new ArrayList<String>();
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                details.add(asso.getAffaire().getDenomination() != null ? asso.getAffaire().getDenomination()
                        : (asso.getAffaire().getFirstname() + " " + asso.getAffaire().getLastname()));
            mail.setExplainationElements(String.join("!#", details));
        }

        if (customerOrder.getAssoAffaireOrders() != null && customerOrder.getAssoAffaireOrders().size() > 0) {
            ArrayList<CustomerMailAssoAffaireOrder> customerAssos = new ArrayList<CustomerMailAssoAffaireOrder>();
            for (AssoAffaireOrder assos : customerOrder.getAssoAffaireOrders()) {
                CustomerMailAssoAffaireOrder asso = new CustomerMailAssoAffaireOrder();
                asso.setAssoAffaireOrderId(assos.getId());
                asso.setCustomerMail(mail);
                customerAssos.add(asso);
            }
            if (customerAssos.size() > 0)
                mail.setCustomerMailAssoAffaireOrders(customerAssos);
        }

        mail.setExplaination3(
                "Votre commande sera traitée dans les meilleurs délais et avec tout notre savoir-faire par "
                        + responsibleString
                        + "toutes nos équipes. Vous pourrez suivre l'état de son avancement en ligne sur notre site https://www.jss.fr/ Espace abonné, rubrique \"Mon compte\".");

        mail.setGreetings("Bonne journée !");

        mail.setReplyTo(tiers.getSalesEmployee());
        mail.setSendToMe(sendToMe);
        mail.setMailComputeResult(mailComputeHelper.computeMailForDepositConfirmation(customerOrder));

        mail.setSubject("Réception de réglement pour votre commande n°" + customerOrder.getId());

        mailService.addMailToQueue(mail);
    }

    public File generateInvoicePdf(CustomerOrder customerOrder, Invoice invoice) throws OsirisException {
        final Context ctx = new Context();

        ctx.setVariable("preTaxPriceTotal", invoiceHelper.getPreTaxPriceTotal(invoice));
        ctx.setVariable("discountTotal", invoiceHelper.getDiscountTotal(invoice));
        ctx.setVariable("assos", customerOrder.getAssoAffaireOrders());
        ctx.setVariable("preTaxPriceTotalWithDicount", invoiceHelper.getDiscountTotal(invoice)
                + (invoiceHelper.getDiscountTotal(invoice) != null ? invoiceHelper.getDiscountTotal(invoice) : 0f));
        ArrayList<VatMail> vats = null;
        Float vatTotal = 0f;
        for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
            if (invoiceItem.getVat() != null && invoiceItem.getVatPrice() != null
                    && invoiceItem.getVatPrice() > 0) {
                vatTotal += invoiceItem.getVatPrice();
                if (vats == null)
                    vats = new ArrayList<VatMail>();
                boolean vatFound = false;
                for (VatMail vatMail : vats) {
                    if (vatMail.getLabel().equals(invoiceItem.getVat().getLabel())) {
                        vatFound = true;
                        if (vatMail.getTotal() == null) {
                            vatMail.setTotal(invoiceItem.getVatPrice());
                            vatMail.setBase(invoiceItem.getPreTaxPrice()
                                    - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : 0f));
                        } else {
                            vatMail.setTotal(vatMail.getTotal() + invoiceItem.getVatPrice());
                            vatMail.setBase(vatMail.getBase() + invoiceItem.getPreTaxPrice()
                                    - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : 0f));
                        }
                    }
                }
                if (!vatFound) {
                    VatMail vatmail = new VatMail();
                    vatmail.setTotal(invoiceItem.getVatPrice());
                    vatmail.setLabel(invoiceItem.getVat().getLabel());
                    vatmail.setBase(invoiceItem.getPreTaxPrice());
                    vats.add(vatmail);
                }
            }
        }

        ctx.setVariable("vats", vats);
        ctx.setVariable("priceTotal", Math.round(invoiceHelper.getPriceTotal(invoice) * 100f) / 100f);
        ctx.setVariable("invoice", invoice);

        // Exclude deposits generated after invoice
        ArrayList<Deposit> deposits = new ArrayList<Deposit>();
        Float depositTotal = 0f;
        if (customerOrder.getDeposits() != null)
            for (Deposit deposit : customerOrder.getDeposits())
                if (deposit.getDepositDate().isBefore(invoice.getCreatedDate())) {
                    deposits.add(deposit);
                    depositTotal += deposit.getDepositAmount();
                }

        ctx.setVariable("deposits", deposits);
        ctx.setVariable("remainingToPay",
                Math.round((invoiceHelper.getPriceTotal(invoice) - depositTotal) * 100f) / 100f);

        LocalDateTime localDate = invoice.getCreatedDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        ctx.setVariable("invoiceCreatedDate", localDate.format(formatter));

        // Create the HTML body using Thymeleaf
        final String htmlContent = emailTemplateEngine().process("invoice-page", ctx);

        File tempFile;
        OutputStream outputStream;
        try {
            tempFile = File.createTempFile("invoice", "pdf");
            outputStream = new FileOutputStream(tempFile);
        } catch (IOException e) {
            throw new OsirisException(e, "Unable to create temp file");
        }
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        try {
            renderer.createPDF(outputStream);
            outputStream.close();
        } catch (DocumentException | IOException e) {
            throw new OsirisException(e, "Unable to create PDF file for invoice " + invoice.getId());
        }
        return tempFile;
    }

    public File generatePublicationReceiptPdf(Announcement announcement, boolean withStamp) throws OsirisException {
        final Context ctx = new Context();

        ctx.setVariable("noticeHeader",
                (announcement.getNoticeHeader() != null && !announcement.getNoticeHeader().equals(""))
                        ? announcement.getNoticeHeader().replaceAll("<br>", "<br/>").replaceAll("&nbsp;", " ")
                                .replaceAll("&", "&amp;")
                        : null);
        ctx.setVariable("notice", announcement.getNotice().replaceAll("<br>", "<br/>").replaceAll("&nbsp;", " ")
                .replaceAll("&", "&amp;"));
        LocalDate localDate = announcement.getPublicationDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        ctx.setVariable("date", localDate.format(formatter));
        ctx.setVariable("withStamp", withStamp);

        // Create the HTML body using Thymeleaf
        final String htmlContent = emailTemplateEngine().process("publication-receipt", ctx);

        File tempFile;
        OutputStream outputStream;
        try {
            tempFile = File.createTempFile("Attestation de parution", "pdf");
            outputStream = new FileOutputStream(tempFile);
        } catch (IOException e) {
            throw new OsirisException(e, "Unable to create temp file");
        }
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        try {
            renderer.createPDF(outputStream);
            outputStream.close();
        } catch (DocumentException | IOException e) {
            throw new OsirisException(e,
                    "Unable to create publication receipt PDF file for announcement " + announcement.getId());
        }
        return tempFile;
    }

    public File generatePublicationFlagPdf(Announcement announcement) throws OsirisException {
        final Context ctx = new Context();

        ctx.setVariable("noticeHeader",
                (announcement.getNoticeHeader() != null && !announcement.getNoticeHeader().equals(""))
                        ? announcement.getNoticeHeader().replaceAll("<br>", "<br/>").replaceAll("&nbsp;", " ")
                                .replaceAll("&", "&amp;")
                        : null);
        ctx.setVariable("notice", announcement.getNotice().replaceAll("<br>", "<br/>").replaceAll("&nbsp;", " ")
                .replaceAll("&", "&amp;"));
        if (announcement.getDepartment() != null)
            ctx.setVariable("department",
                    announcement.getDepartment().getCode() + " - " + announcement.getDepartment().getLabel());
        if (announcement.getNoticeTypeFamily() != null)
            ctx.setVariable("noticeType", announcement.getNoticeTypeFamily().getLabel());
        if (announcement.getNoticeTypes() != null && announcement.getNoticeTypes().size() > 0)
            ctx.setVariable("noticeSubtype", announcement.getNoticeTypes().stream().map(NoticeType::getLabel)
                    .collect(Collectors.joining(" - ")));
        ctx.setVariable("qrCodePicture",
                Base64.getEncoder().encodeToString(qrCodeHelper.getQrCode("www.jss.fr/" + announcement.getId(), 60)));
        LocalDate localDate = announcement.getPublicationDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy");
        ctx.setVariable("date", StringUtils.capitalize(localDate.format(formatter)));

        // Create the HTML body using Thymeleaf
        final String htmlContent = emailTemplateEngine().process("publication-flag", ctx);

        File tempFile;
        OutputStream outputStream;
        try {
            tempFile = File.createTempFile("Témoin de publication", "pdf");
            outputStream = new FileOutputStream(tempFile);
        } catch (IOException e) {
            throw new OsirisException(e, "Unable to create temp file");
        }
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        try {
            renderer.createPDF(outputStream);
            outputStream.close();
        } catch (DocumentException | IOException e) {
            throw new OsirisException(e,
                    "Unable to create publication flag PDF file for announcement " + announcement.getId());
        }
        return tempFile;
    }

    public void generateCustomerOrderFinalisationToCustomer(CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException {
        sendCustomerOrderFinalisationToCustomer(customerOrder, true, false, false);
    }

    public void sendCustomerOrderFinalisationToCustomer(CustomerOrder customerOrder, boolean sendToMe,
            boolean isReminder, boolean isLastReminder)
            throws OsirisException, OsirisClientMessageException {

        CustomerMail mail = new CustomerMail();
        mail.setCustomerOrder(customerOrder);
        computeQuotationPrice(mail, customerOrder);

        MailComputeResult mailComputeResult = mailComputeHelper
                .computeMailForCustomerOrderFinalizationAndInvoice(customerOrder);

        ITiers tiers = quotationService.getCustomerOrderOfQuotation(customerOrder);
        boolean isPaymentTypePrelevement = false;

        if (tiers instanceof Tiers) {
            isPaymentTypePrelevement = ((Tiers) tiers).getPaymentType() != null && ((Tiers) tiers).getPaymentType()
                    .getId().equals(constantService.getPaymentTypePrelevement().getId());
        } else if (tiers instanceof Confrere) {
            isPaymentTypePrelevement = ((Confrere) tiers).getPaymentType() != null
                    && ((Confrere) tiers).getPaymentType()
                            .getId().equals(constantService.getPaymentTypePrelevement().getId());
        } else if (tiers instanceof Responsable) {
            isPaymentTypePrelevement = ((Responsable) tiers).getTiers().getPaymentType() != null
                    && ((Responsable) tiers).getTiers().getPaymentType()
                            .getId().equals(constantService.getPaymentTypePrelevement().getId());
        }

        Float remainingToPay = Math
                .round(accountingRecordService.getRemainingAmountToPayForCustomerOrder(customerOrder) * 100f) / 100f;

        List<Attachment> attachments = findAttachmentForCustomerOrder(customerOrder, isReminder);

        mail.setAttachments(attachments);

        mail.setHeaderPicture("images/waiting-deposit-header.png");
        mail.setTitle("Votre commande est terminée !");

        if (remainingToPay > 0 && !isPaymentTypePrelevement)
            mail.setSubtitle("Elle n'attend plus que votre paiement.");

        mail.setLabel("Commande n°" + customerOrder.getId());

        String explainationText = "";
        if (isReminder) {
            explainationText = "Ceci est un mail de relance automatique concernant le réglement de votre commande n°"
                    + customerOrder.getId() + " dont vous trouverez la facture ci-jointe.";
            if (isLastReminder)
                explainationText += " Sans réglement de votre part dans les 10jours, nous nous verrons contraints de vous mettre en demeure pour la somme en question.";
            else
                explainationText += " Nous vous remercions de bien vouloir procéder à son règlement dans les meilleurs délais.";
        } else {
            Affaire affaire = customerOrder.getAssoAffaireOrders().get(0).getAffaire();
            explainationText = "Nous avons le plaisir de vous confirmer la finalisation de votre commande n°"
                    + customerOrder.getId() + " concernant la société "
                    + (affaire.getDenomination() != null ? affaire.getDenomination()
                            : (affaire.getFirstname() + " " + affaire.getLastname()))
                    + ". Vous trouverez en pièces-jointes les éléments suivants : ";

            mail.setExplainationElements(String.join("!#",
                    attachments.stream().map(Attachment::getDescription).collect(Collectors.toList())));
        }

        mail.setExplaination(explainationText);

        if (remainingToPay > 0 && !isPaymentTypePrelevement) {
            mail.setPaymentExplaination(
                    "Vous pouvez régler cette facture d'un montant de " + remainingToPay
                            + " € par virement à l'aide des informations suivantes");

            mail.setPaymentExplaination2("IBAN / BIC : " + ibanJss + " / " + bicJss);

            mail.setCbExplanation(
                    "Vous avez aussi la possibilité de payer par carte bancaire en flashant le QR Code ci-dessous ou en cliquant ");

            if (!sendToMe)
                mail.setCbLink(
                        paymentCbEntryPoint + "/order/invoice?customerOrderId=" + customerOrder.getId() + "&mail="
                                + mailComputeResult.getRecipientsMailTo().get(0).getMail());
            else
                mail.setCbLink(
                        paymentCbEntryPoint + "/order/invoice?customerOrderId=" + customerOrder.getId() + "&mail="
                                + employeeService.getCurrentEmployee().getMail());

            mail.setPaymentExplainationWarning(
                    "Référence à indiquer abolument dans le libellé de votre virement : " + customerOrder.getId());

        } else if (remainingToPay < 0) {
            mail.setExplaination3(
                    "Un remboursement de " + (Math.abs(remainingToPay))
                            + " € sera bientôt réalisé vers votre compte bancaire.");
        }

        mail.setGreetings("En vous remerciant pour votre confiance !");

        mail.setReplyTo(tiers.getSalesEmployee());
        mail.setSendToMe(sendToMe);
        mail.setMailComputeResult(mailComputeResult);

        if (isReminder)
            mail.setSubject("Votre commande n°" + customerOrder.getId() + " est en attente de paiement");
        else
            mail.setSubject("Votre commande n°" + customerOrder.getId() + " est terminée");

        mailService.addMailToQueue(mail);
    }

    private List<Attachment> findAttachmentForCustomerOrder(CustomerOrder customerOrder, boolean isReminder)
            throws OsirisException, OsirisClientMessageException {
        ArrayList<Attachment> attachments = new ArrayList<Attachment>();
        boolean updateCustomerOrder = false;

        if (customerOrder != null && customerOrder.getAttachments() != null) {
            customerOrder.getAttachments().sort(new Comparator<Attachment>() {
                @Override
                public int compare(Attachment o1, Attachment o2) {
                    if (o2.getCreatDateTime() == null && o1.getCreatDateTime() != null)
                        return -1;
                    if (o2.getCreatDateTime() != null && o1.getCreatDateTime() == null)
                        return 1;
                    if (o1.getCreatDateTime() == null && o2.getCreatDateTime() == null)
                        return 0;
                    return o2.getCreatDateTime().isAfter(o1.getCreatDateTime()) ? 1 : -1;
                }
            });
            for (Attachment attachment : customerOrder.getAttachments()) {
                if (attachment.getAttachmentType().getId().equals(constantService.getAttachmentTypeInvoice().getId()))
                    attachments.add(attachment);
                break;
            }
        }

        if (!isReminder && customerOrder != null && customerOrder.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                if (asso.getProvisions() != null)
                    for (Provision provision : asso.getProvisions())
                        if (provision.getAnnouncement() != null) {
                            if (provision.getAnnouncement().getAttachments() != null)
                                for (Attachment attachment : provision.getAnnouncement().getAttachments())
                                    if (attachment.getAttachmentType().getId()
                                            .equals(constantService.getAttachmentTypePublicationFlag()
                                                    .getId())
                                            && provision.getAnnouncement()
                                                    .getIsPublicationFlagAlreadySent() == null) {
                                        attachments.add(attachment);
                                        provision.getAnnouncement().setIsPublicationFlagAlreadySent(true);
                                        updateCustomerOrder = true;
                                    } else if (attachment.getAttachmentType().getId()
                                            .equals(constantService.getAttachmentTypePublicationReceipt()
                                                    .getId())
                                            && provision.getAnnouncement()
                                                    .getIsPublicationReciptAlreadySent() == null) {
                                        attachments.add(attachment);
                                        provision.getAnnouncement().setIsPublicationReciptAlreadySent(true);
                                        updateCustomerOrder = true;
                                    } else if (attachment.getAttachmentType().getId()
                                            .equals(constantService.getAttachmentTypePublicationProof()
                                                    .getId())) {
                                        attachments.add(attachment);
                                    }
                        } else if (provision.getFormalite() != null) {
                            if (provision.getFormalite().getAttachments() != null)
                                for (Attachment attachment : provision.getFormalite().getAttachments())
                                    if (attachment.getAttachmentType().getId()
                                            .equals(constantService.getAttachmentTypeKbisUpdated().getId()))
                                        attachments.add(attachment);
                        } else if (provision.getBodacc() != null) {
                            if (provision.getBodacc().getAttachments() != null)
                                for (Attachment attachment : provision.getBodacc().getAttachments())
                                    if (attachment.getAttachmentType().getId()
                                            .equals(constantService.getAttachmentTypeKbisUpdated().getId()))
                                        attachments.add(attachment);
                        }

        if (updateCustomerOrder)
            customerOrderService.addOrUpdateCustomerOrder(customerOrder);

        return attachments;
    }

    public void sendPublicationReceiptToCustomer(CustomerOrder customerOrder, boolean sendToMe)
            throws OsirisException, OsirisClientMessageException {

        CustomerMail mail = new CustomerMail();
        mail.setCustomerOrder(customerOrder);

        mail.setHeaderPicture("images/receipt-header.png");
        mail.setTitle("Votre attestation de parution");
        mail.setLabel("Commande n°" + customerOrder.getId());
        String explainationText = "Vous trouverez ci-joint l'attestation de parution ";

        if (customerOrder.getAssoAffaireOrders().size() == 1) {
            Affaire affaire = customerOrder.getAssoAffaireOrders().get(0).getAffaire();
            mail.setExplaination(explainationText + " concernant la société " +
                    (affaire.getDenomination() != null ? affaire.getDenomination()
                            : (affaire.getFirstname() + " " + affaire.getLastname()))
                    + ".");
        } else {
            mail.setExplaination(explainationText + " concernant les sociétés indiquées ci-dessous.");
            ArrayList<String> details = new ArrayList<String>();
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                details.add(asso.getAffaire().getDenomination() != null ? asso.getAffaire().getDenomination()
                        : (asso.getAffaire().getFirstname() + " " + asso.getAffaire().getLastname()));
            mail.setExplainationElements(String.join("!#", details));
        }

        List<Attachment> attachments = new ArrayList<Attachment>();
        for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
            if (asso.getProvisions() != null)
                for (Provision provision : asso.getProvisions())
                    if (provision.getAnnouncement() != null)
                        if (provision.getAnnouncement().getAttachments() != null)
                            for (Attachment attachment : provision.getAnnouncement().getAttachments())
                                if (attachment.getAttachmentType().getId()
                                        .equals(constantService.getAttachmentTypePublicationReceipt()
                                                .getId())
                                        && provision.getAnnouncement().getIsPublicationReciptAlreadySent() == null) {
                                    attachments.add(attachment);
                                    provision.getAnnouncement().setIsPublicationReciptAlreadySent(true);
                                }

        if (attachments.size() == 0)
            return;

        customerOrderService.addOrUpdateCustomerOrder(customerOrder);

        mail.setAttachments(attachments);

        mail.setGreetings("En vous remerciant pour votre confiance !");

        mail.setReplyTo(quotationService.getCustomerOrderOfQuotation(customerOrder).getSalesEmployee());
        mail.setSendToMe(sendToMe);
        mail.setMailComputeResult(mailComputeHelper.computeMailForPublicationReceipt(customerOrder));

        mail.setSubject("Votre attestation de parution");

        mailService.addMailToQueue(mail);
    }

    public void sendPublicationFlagToCustomer(CustomerOrder customerOrder, boolean sendToMe)
            throws OsirisException, OsirisClientMessageException {

        CustomerMail mail = new CustomerMail();
        mail.setCustomerOrder(customerOrder);

        mail.setHeaderPicture("images/receipt-header.png");
        mail.setTitle("Votre témoin de publication");
        mail.setLabel("Commande n°" + customerOrder.getId());
        String explainationText = "Vous trouverez ci-joint le témoin de publication ";

        if (customerOrder.getAssoAffaireOrders().size() == 1) {
            Affaire affaire = customerOrder.getAssoAffaireOrders().get(0).getAffaire();
            mail.setExplaination(explainationText + " concernant la société " +
                    (affaire.getDenomination() != null ? affaire.getDenomination()
                            : (affaire.getFirstname() + " " + affaire.getLastname()))
                    + ".");
        } else {
            mail.setExplaination(explainationText + " concernant les sociétés indiquées ci-dessous.");
            ArrayList<String> details = new ArrayList<String>();
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                details.add(asso.getAffaire().getDenomination() != null ? asso.getAffaire().getDenomination()
                        : (asso.getAffaire().getFirstname() + " " + asso.getAffaire().getLastname()));
            mail.setExplainationElements(String.join("!#", details));
        }

        List<Attachment> attachments = new ArrayList<Attachment>();
        for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
            if (asso.getProvisions() != null)
                for (Provision provision : asso.getProvisions())
                    if (provision.getAnnouncement() != null)
                        if (provision.getAnnouncement().getAttachments() != null)
                            for (Attachment attachment : provision.getAnnouncement().getAttachments())
                                if (attachment.getAttachmentType().getId()
                                        .equals(constantService.getAttachmentTypePublicationFlag()
                                                .getId())
                                        && provision.getAnnouncement().getIsPublicationFlagAlreadySent() == null) {
                                    attachments.add(attachment);
                                    provision.getAnnouncement().setIsPublicationFlagAlreadySent(true);
                                }

        if (attachments.size() == 0)
            return;

        customerOrderService.addOrUpdateCustomerOrder(customerOrder);

        mail.setAttachments(attachments);

        mail.setGreetings("En vous remerciant pour votre confiance !");

        mail.setReplyTo(quotationService.getCustomerOrderOfQuotation(customerOrder).getSalesEmployee());
        mail.setSendToMe(sendToMe);
        mail.setMailComputeResult(mailComputeHelper.computeMailForPublicationFlag(customerOrder));

        mail.setSubject("Votre témoin de publication");

        mailService.addMailToQueue(mail);
    }

    public void sendProofReadingToCustomer(CustomerOrder customerOrder, boolean sendToMe)
            throws OsirisException, OsirisClientMessageException {

        CustomerMail mail = new CustomerMail();
        mail.setCustomerOrder(customerOrder);

        mail.setHeaderPicture("images/reading-proof-header.png");
        mail.setTitle("Votre épreuve de relecture de publication");
        mail.setLabel("Commande n°" + customerOrder.getId());
        String explainationText = "Vous trouverez ci-joint votre épreuve de relecture pour validation ";

        if (customerOrder.getAssoAffaireOrders().size() == 1) {
            Affaire affaire = customerOrder.getAssoAffaireOrders().get(0).getAffaire();
            mail.setExplaination(explainationText + " concernant la société " +
                    (affaire.getDenomination() != null ? affaire.getDenomination()
                            : (affaire.getFirstname() + " " + affaire.getLastname()))
                    + ".");
        } else {
            mail.setExplaination(explainationText + " concernant les sociétés indiquées ci-dessous.");
            ArrayList<String> details = new ArrayList<String>();
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                details.add(asso.getAffaire().getDenomination() != null ? asso.getAffaire().getDenomination()
                        : (asso.getAffaire().getFirstname() + " " + asso.getAffaire().getLastname()));
            mail.setExplainationElements(String.join("!#", details));
        }

        List<Attachment> attachments = new ArrayList<Attachment>();
        for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
            if (asso.getProvisions() != null)
                for (Provision provision : asso.getProvisions())
                    if (provision.getAnnouncement() != null)
                        if (provision.getAnnouncement().getAttachments() != null)
                            for (Attachment attachment : provision.getAnnouncement().getAttachments())
                                if (attachment.getAttachmentType().getId()
                                        .equals(constantService.getAttachmentTypeProofReading()
                                                .getId())) {
                                    attachments.add(attachment);
                                }

        if (attachments.size() == 0)
            throw new OsirisException(null,
                    "Unable to find reading proof PDF for CustomerOrder n°" + customerOrder.getId());

        mail.setAttachments(attachments);

        mail.setGreetings("En vous remerciant pour votre confiance !");

        Employee currentAssignee = null;
        if (customerOrder != null && customerOrder.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                if (asso.getProvisions() != null)
                    for (Provision provision : asso.getProvisions())
                        if (provision.getAnnouncement() != null && provision.getAssignedTo() != null)
                            currentAssignee = provision.getAssignedTo();

        mail.setReplyTo(currentAssignee != null ? currentAssignee
                : quotationService.getCustomerOrderOfQuotation(customerOrder).getSalesEmployee());
        mail.setSendToMe(sendToMe);
        mail.setMailComputeResult(mailComputeHelper.computeMailForReadingProof(customerOrder));

        mail.setSubject("Votre épreuve de relecture");

        mailService.addMailToQueue(mail);
    }

    public void sendBillingClosureToCustomer(List<Attachment> attachments, ITiers tiers, boolean sendToMe)
            throws OsirisException, OsirisClientMessageException {

        CustomerMail mail = new CustomerMail();
        if (tiers instanceof Tiers)
            mail.setTiers((Tiers) tiers);
        if (tiers instanceof Responsable)
            mail.setResponsable((Responsable) tiers);
        if (tiers instanceof Confrere)
            mail.setConfrere((Confrere) tiers);

        mail.setHeaderPicture("images/billing-receipt-header.png");
        mail.setTitle("Votre relevé de compte");
        String explainationText = "Vous trouverez ci-joint votre relevé de compte à date.";
        mail.setExplaination(explainationText);

        if (attachments.size() == 0)
            return;

        mail.setAttachments(attachments);

        mail.setGreetings("En vous remerciant pour votre confiance !");

        mail.setReplyToMail(constantService.getStringAccountingSharedMaiblox() + "");
        mail.setSendToMe(sendToMe);
        mail.setMailComputeResult(mailComputeHelper.computeMailForBillingClosure(tiers));

        mail.setSubject("Votre relevé de compte");

        mailService.addMailToQueue(mail);
    }

    public void sendNewPasswordMail(Responsable responsable, String password)
            throws OsirisException {

        CustomerMail mail = new CustomerMail();

        mail.setHeaderPicture("images/password-header.png");
        mail.setTitle("Votre mot de passe");
        String explainationText = "Votre nouveau mot de passe de connexion à JSS.fr est : " + password;
        mail.setExplaination(explainationText);

        mail.setGreetings("En vous remerciant pour votre confiance");

        mail.setReplyToMail(constantService.getStringSalesSharedMailbox() + "");
        mail.setSendToMe(false);
        MailComputeResult mailComputeResult = new MailComputeResult();
        mailComputeResult.setRecipientsMailTo(new ArrayList<Mail>());

        for (Mail mailResponsable : responsable.getMails()) {
            mailComputeResult.getRecipientsMailTo().add(mailResponsable);
        }
        mail.setMailComputeResult(mailComputeResult);

        mail.setSubject("Votre nouveau mot de passe");

        mailService.addMailToQueue(mail);
    }

}
