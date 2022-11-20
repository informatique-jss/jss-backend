package com.jss.osiris.libs.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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
import com.jss.osiris.libs.mail.model.CustomerMail;
import com.jss.osiris.libs.mail.model.CustomerMailAssoAffaireOrder;
import com.jss.osiris.libs.mail.model.MailComputeResult;
import com.jss.osiris.libs.mail.model.VatMail;
import com.jss.osiris.libs.mail.repository.CustomerMailRepository;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.Deposit;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.service.InvoiceHelper;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.Mail;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.IQuotation;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.service.AssoAffaireOrderService;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.quotation.service.QuotationService;
import com.jss.osiris.modules.tiers.model.ITiers;
import com.jss.osiris.modules.tiers.model.Responsable;

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

    @Value("${mail.domain.filter}")
    private String mailDomainFilter;

    @Value("${payment.cb.entry.point}")
    private String paymentCbEntryPoint;

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
    CustomerMailRepository customerMailRepository;

    @Autowired
    AssoAffaireOrderService assoAffaireOrderService;

    @Autowired
    QrCodeHelper qrCodeHelper;

    private JavaMailSender getMailSender() throws IOException {
        if (javaMailSender != null)
            return javaMailSender;

        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(mailHost);
        mailSender.setPort(Integer.parseInt(mailPort));
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);
        final Properties javaMailProperties = new Properties();
        javaMailProperties
                .load(this.applicationContext.getResource("classpath:application.properties").getInputStream());
        mailSender.setJavaMailProperties(javaMailProperties);

        javaMailSender = mailSender;

        return mailSender;
    }

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

    private void addMailToQueue(CustomerMail mail) {
        if (mail.getSendInvoiceAttachment() == null)
            mail.setSendInvoiceAttachment(false);
        mail.setCreatedDateTime(LocalDateTime.now());
        customerMailRepository.save(mail);
    }

    @Transactional(rollbackFor = Exception.class)
    public void sendNextMail() throws Exception {
        List<CustomerMail> mails = customerMailRepository.findAllByOrderByCreatedDateTimeAsc();

        if (mails != null && mails.size() > 0) {
            CustomerMail mail = mails.get(0);
            prepareAndSendMail(mail);

            customerMailRepository.delete(mail);
        }
    }

    private void prepareAndSendMail(CustomerMail mail) throws Exception {
        boolean canSend = true;
        MimeMessage message = generateGenericMail(mail);

        if (mailDomainFilter != null && !mailDomainFilter.equals("")) {
            Address[] recipients = message.getRecipients(Message.RecipientType.TO);
            if (recipients != null)
                for (Address address : recipients) {
                    String[] chunk = address.toString().split("@");
                    if (chunk.length != 2 || !chunk[1].equals(mailDomainFilter))
                        canSend = false;
                }
            recipients = message.getRecipients(Message.RecipientType.CC);
            if (recipients != null)
                for (Address address : recipients) {
                    String[] chunk = address.toString().split("@");
                    if (chunk.length != 2
                            || !chunk[1].toLowerCase().trim().equals(mailDomainFilter.toLowerCase().trim()))
                        canSend = false;
                }
        }

        if (canSend)
            getMailSender().send(message);
    }

    private MimeMessage generateGenericMail(CustomerMail mail) throws Exception {
        // Prepare the evaluation context
        final Context ctx = new Context();
        ctx.setVariable("instagram", "instagram");
        ctx.setVariable("facebook", "facebook");
        ctx.setVariable("linkedin", "linkedin");
        ctx.setVariable("twitter", "twitter");
        ctx.setVariable("jssHeaderPicture", "jssHeaderPicture");
        ctx.setVariable("headerPicture", mail.getHeaderPicture() != null ? "headerPicture" : null);
        ctx.setVariable("title", mail.getTitle());
        ctx.setVariable("subtitle", mail.getSubtitle());
        ctx.setVariable("label", mail.getLabel());
        ctx.setVariable("explaination", mail.getExplaination());
        ctx.setVariable("explainationElements",
                mail.getExplainationElements() != null ? mail.getExplainationElements().split("!#") : null);
        ctx.setVariable("explaination2", mail.getExplaination2());
        ctx.setVariable("explainationWarning", mail.getExplainationWarning());

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

        // Prepare message using a Spring helper
        MimeMessage mimeMessage;
        mimeMessage = getMailSender().createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        message.setFrom(new InternetAddress("no-reply@jss.fr", "Journal Spécial des Sociétés"));

        if (mail.getReplyTo() != null)
            message.setReplyTo(
                    new InternetAddress(mail.getReplyTo().getMail(),
                            mail.getReplyTo().getFirstname() + " " + mail.getReplyTo().getLastname()));

        if (mail.getSendToMe() != null && mail.getSendToMe())
            message.setTo(employeeService.getCurrentEmployee().getMail());
        else {
            if (mail.getMailComputeResult().getRecipientsMailTo() == null
                    || mail.getMailComputeResult().getRecipientsMailTo().size() == 0)
                throw new Exception("No recipient found");

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
        final InputStreamSource imageSourceQuotationHeader = new ByteArrayResource(
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

        if (mail.getCustomerOrder() != null && mail.getSendInvoiceAttachment()) {
            message.addAttachment("Facture n°" + ".pdf",
                    generateInvoicePdf(mail.getCustomerOrder(), mail.getInvoice()));
        }

        return mimeMessage;
    }

    public String generateGenericHtmlConfirmation(String title, String subtitle, String label, String explaination,
            String explainationWarning, String greetings) throws Exception {
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

    public MailComputeResult computeMailForQuotationDocument(IQuotation quotation) throws Exception {
        // Compute recipients
        MailComputeResult mailComputeResult = new MailComputeResult();
        mailComputeResult.setRecipientsMailTo(new ArrayList<Mail>());
        mailComputeResult.setRecipientsMailCc(new ArrayList<Mail>());
        mailComputeResult.setIsSendToClient(false);
        mailComputeResult.setIsSendToAffaire(false);

        Document quotationDocument = documentService.getQuotationDocument(quotation.getDocuments());
        if (quotationDocument != null && (quotation.getTiers() != null || quotation.getResponsable() != null
                || quotation.getConfrere() != null)) {
            ITiers customerOrder = quotationService.getCustomerOrderOfQuotation(quotation);
            if (quotationDocument.getIsRecipientAffaire()) {
                mailComputeResult.setIsSendToAffaire(true);
                if (quotationDocument.getMailsAffaire() != null && quotationDocument.getMailsAffaire().size() > 0) {
                    mailComputeResult.getRecipientsMailTo().addAll(quotationDocument.getMailsAffaire());
                    mailComputeResult.setMailToAffaireOrigin("mails indiqués dans le devis/commande");
                } else if (quotation.getAssoAffaireOrders().get(0).getAffaire().getMails() != null
                        && quotation.getAssoAffaireOrders().get(0).getAffaire().getMails().size() > 0) {
                    mailComputeResult.getRecipientsMailTo()
                            .addAll(quotation.getAssoAffaireOrders().get(0).getAffaire().getMails());
                    mailComputeResult.setMailToAffaireOrigin("mails indiqués sur l'affaire");
                } else
                    return mailComputeResult;

                if (quotationDocument.getMailsCCResponsableAffaire() != null
                        && quotationDocument.getMailsCCResponsableAffaire().size() > 0) {
                    for (Responsable responsable : quotationDocument.getMailsCCResponsableAffaire())
                        if (responsable.getMailsCreationAffaire() != null
                                && responsable.getMailsCreationAffaire().size() > 0) {
                            for (Responsable responsable2 : responsable.getMailsCreationAffaire())
                                mailComputeResult.getRecipientsMailCc().addAll(responsable2.getMails());
                            mailComputeResult.setMailCcAffaireOrigin(
                                    "mails indiqués en AR pour création d'une affaire pour les responsables choisis");
                        } else if (responsable.getMails() != null
                                && responsable.getMails().size() > 0) {
                            mailComputeResult.getRecipientsMailCc().addAll(responsable.getMails());
                            mailComputeResult.setMailCcAffaireOrigin("mails des responsables choisis");
                        }
                }
            }

            if (quotationDocument.getIsRecipientClient()
                    || !quotationDocument.getIsRecipientClient() && !quotationDocument.getIsRecipientAffaire()) {
                mailComputeResult.setIsSendToClient(true);
                if (quotationDocument.getMailsClient() != null
                        && quotationDocument.getMailsClient().size() > 0) {
                    mailComputeResult.getRecipientsMailTo().addAll(quotationDocument.getMailsClient());
                    mailComputeResult.setMailToClientOrigin("mails indiqués dans le devis/commande");
                } else if (customerOrder instanceof Responsable
                        && ((Responsable) customerOrder).getMailsCreationAffaire() != null
                        && ((Responsable) customerOrder).getMailsCreationAffaire().size() > 0) {
                    for (Responsable responsable2 : ((Responsable) customerOrder).getMailsCreationAffaire())
                        mailComputeResult.getRecipientsMailTo()
                                .addAll(responsable2.getMails());
                    mailComputeResult.setMailToClientOrigin(
                            "mails indiqués en AR pour création d'une affaire pour le responsable");
                } else if (customerOrder instanceof Responsable
                        && ((Responsable) customerOrder).getMails() != null
                        && ((Responsable) customerOrder).getMails().size() > 0) {
                    mailComputeResult.getRecipientsMailTo().addAll(((Responsable) customerOrder).getMails());
                    mailComputeResult.setMailToClientOrigin("mails du responsable");
                } else if (customerOrder instanceof Responsable
                        && ((Responsable) customerOrder).getTiers().getMails() != null
                        && ((Responsable) customerOrder).getTiers().getMails().size() > 0) {
                    mailComputeResult.getRecipientsMailTo().addAll(((Responsable) customerOrder).getTiers().getMails());
                    mailComputeResult.setMailToClientOrigin("mails du tiers associé au responsable");
                } else if (customerOrder.getMails() != null
                        && customerOrder.getMails().size() > 0) {
                    mailComputeResult.getRecipientsMailTo().addAll(customerOrder.getMails());
                    mailComputeResult.setMailToClientOrigin("mails du tiers/confrère");
                } else
                    return mailComputeResult;

                if (quotationDocument.getMailsCCResponsableClient() != null
                        && quotationDocument.getMailsCCResponsableClient().size() > 0) {
                    for (Responsable responsable : quotationDocument.getMailsCCResponsableClient())
                        if (responsable.getMailsCreationAffaire() != null
                                && responsable.getMailsCreationAffaire().size() > 0) {
                            for (Responsable responsable2 : ((Responsable) customerOrder).getMailsCreationAffaire())
                                mailComputeResult.getRecipientsMailCc().addAll(responsable2.getMails());
                            mailComputeResult.setMailCcClientOrigin(
                                    "mails indiqués en AR pour création d'une affaire pour les responsables choisis");
                        } else if (responsable.getMails() != null
                                && responsable.getMails().size() > 0) {
                            mailComputeResult.getRecipientsMailCc().addAll(responsable.getMails());
                            mailComputeResult.setMailCcClientOrigin("mails des responsables choisis");
                        }
                }
            }
        }
        return mailComputeResult;
    }

    public MailComputeResult computeMailForBillingDocument(IQuotation quotation) throws Exception {
        // Compute recipients
        MailComputeResult mailComputeResult = new MailComputeResult();
        mailComputeResult.setRecipientsMailTo(new ArrayList<Mail>());
        mailComputeResult.setRecipientsMailCc(new ArrayList<Mail>());
        mailComputeResult.setIsSendToClient(false);
        mailComputeResult.setIsSendToAffaire(false);

        Document billingDocument = documentService.getBillingDocument(quotation.getDocuments());
        if (billingDocument != null && (quotation.getTiers() != null || quotation.getResponsable() != null
                || quotation.getConfrere() != null)) {
            ITiers customerOrder = quotationService.getCustomerOrderOfQuotation(quotation);
            if (billingDocument.getIsRecipientAffaire()) {
                mailComputeResult.setIsSendToAffaire(true);
                if (billingDocument.getMailsAffaire() != null && billingDocument.getMailsAffaire().size() > 0) {
                    mailComputeResult.getRecipientsMailTo().addAll(billingDocument.getMailsAffaire());
                    mailComputeResult.setMailToAffaireOrigin("mails indiqués dans la commande");
                } else if (quotation.getAssoAffaireOrders().get(0).getAffaire().getMails() != null
                        && quotation.getAssoAffaireOrders().get(0).getAffaire().getMails().size() > 0) {
                    mailComputeResult.getRecipientsMailTo()
                            .addAll(quotation.getAssoAffaireOrders().get(0).getAffaire().getMails());
                    mailComputeResult.setMailToAffaireOrigin("mails indiqués sur l'affaire");
                } else
                    return mailComputeResult;

                if (billingDocument.getMailsCCResponsableAffaire() != null
                        && billingDocument.getMailsCCResponsableAffaire().size() > 0) {
                    for (Responsable responsable : billingDocument.getMailsCCResponsableAffaire())
                        if (responsable.getMails() != null
                                && responsable.getMails().size() > 0) {
                            mailComputeResult.getRecipientsMailCc().addAll(responsable.getMails());
                            mailComputeResult.setMailCcAffaireOrigin("mails des responsables choisis");
                        }
                }
            }

            if (billingDocument.getIsRecipientClient()
                    || !billingDocument.getIsRecipientClient() && !billingDocument.getIsRecipientAffaire()) {
                mailComputeResult.setIsSendToClient(true);
                if (billingDocument.getMailsClient() != null
                        && billingDocument.getMailsClient().size() > 0) {
                    mailComputeResult.getRecipientsMailTo().addAll(billingDocument.getMailsClient());
                    mailComputeResult.setMailToClientOrigin("mails indiqués dans la commande");
                } else if (customerOrder instanceof Responsable
                        && ((Responsable) customerOrder).getMails() != null
                        && ((Responsable) customerOrder).getMails().size() > 0) {
                    mailComputeResult.getRecipientsMailTo().addAll(((Responsable) customerOrder).getMails());
                    mailComputeResult.setMailToClientOrigin("mails du responsable");
                } else if (customerOrder instanceof Responsable
                        && ((Responsable) customerOrder).getTiers().getMails() != null
                        && ((Responsable) customerOrder).getTiers().getMails().size() > 0) {
                    mailComputeResult.getRecipientsMailTo().addAll(((Responsable) customerOrder).getTiers().getMails());
                    mailComputeResult.setMailToClientOrigin("mails du tiers associé au responsable");
                } else if (customerOrder instanceof Confrere
                        && ((Confrere) customerOrder).getRegie() != null
                        && ((Confrere) customerOrder).getRegie().getMails() != null
                        && ((Confrere) customerOrder).getRegie().getMails().size() > 0) {
                    mailComputeResult.getRecipientsMailTo().addAll(((Confrere) customerOrder).getRegie().getMails());
                    mailComputeResult.setMailToClientOrigin("mails de la régie du confrère");
                } else if (customerOrder.getMails() != null
                        && customerOrder.getMails().size() > 0) {
                    mailComputeResult.getRecipientsMailTo().addAll(customerOrder.getMails());
                    mailComputeResult.setMailToClientOrigin("mails du tiers/confrère");
                } else
                    return mailComputeResult;

                if (billingDocument.getMailsCCResponsableClient() != null
                        && billingDocument.getMailsCCResponsableClient().size() > 0) {
                    for (Responsable responsable : billingDocument.getMailsCCResponsableClient())
                        if (responsable.getMails() != null
                                && responsable.getMails().size() > 0) {
                            mailComputeResult.getRecipientsMailCc().addAll(responsable.getMails());
                            mailComputeResult.setMailCcClientOrigin("mails des responsables choisis");
                        }
                }
            }
        }
        return mailComputeResult;
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

        mail.setPreTaxPriceTotal(preTaxPriceTotal);
        mail.setDiscountTotal(discountTotal);
        mail.setPreTaxPriceTotalWithDicount(preTaxPriceTotalWithDicount);
        mail.setVatMails(vats);
        mail.setPriceTotal(priceTotal);
    }

    @Transactional(rollbackFor = Exception.class)
    public void generateQuotationMail(Quotation quotation) throws Exception {
        sendQuotationToCustomer(quotation, true);
    }

    public void sendQuotationToCustomer(Quotation quotation, boolean sendToMe) throws Exception {
        CustomerMail mail = new CustomerMail();
        mail.setHeaderPicture("images/quotation-header.png");
        mail.setTitle("Votre nouveau devis est prêt !");
        mail.setSubtitle("Il n'attend plus que votre validation.");
        mail.setLabel("Devis n°" + quotation.getId());

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

        LocalDate localDate = LocalDate.now();
        localDate = localDate.plusMonths(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        mail.setTotalSubtitle("Ce devis est valable jusqu'au " + localDate.format(formatter));
        mail.setGreetings("Bonne journée !");

        ITiers customerOrder = quotationService.getCustomerOrderOfQuotation(quotation);
        mail.setReplyTo(customerOrder.getSalesEmployee());
        mail.setSendToMe(sendToMe);
        mail.setMailComputeResult(computeMailForQuotationDocument(quotation));

        mail.setSubject("Votre devis n°" + quotation.getId());

        addMailToQueue(mail);
    }

    public void generateWaintingDepositMail(CustomerOrder customerOrder) throws Exception {
        sendCustomerOrderDepositWaitingToCustomer(customerOrder, true);
    }

    public void sendCustomerOrderDepositWaitingToCustomer(CustomerOrder customerOrder, boolean sendToMe)
            throws Exception {

        CustomerMail mail = new CustomerMail();
        computeQuotationPrice(mail, customerOrder);
        mail.setHeaderPicture("images/waiting-deposit-header.png");
        mail.setTitle("Votre commande est en attente de provision");
        mail.setLabel("Commande n°" + customerOrder.getId());
        mail.setExplaination("Votre commande est en attente de provision. Effectuez dès maintenant un virement de "
                + mail.getPreTaxPriceTotal() + " € sur le compte ci-dessous pour la débloquer.");

        mail.setExplaination2("IBAN : FR76 3007 6021 6310 7548 0020 040 \r\n BIC : NORDFRPP");

        mail.setCbExplanation(
                "Vous avez aussi la possibilité de payer par carte bancaire en flashant le QR Code ci-dessous ou en cliquant ");

        MailComputeResult mailComputeResult = computeMailForBillingDocument(customerOrder);

        mail.setCbLink(paymentCbEntryPoint + "/order/deposit?customerOrderId=" + customerOrder.getId() + "&mail="
                + mailComputeResult.getRecipientsMailTo().get(0).getMail());

        mail.setExplainationWarning(
                "Référence à indiquer abolument dans le libellé de votre virement : " + customerOrder.getId());

        mail.setGreetings("Bonne journée !");

        ITiers customerOrderTiers = quotationService.getCustomerOrderOfQuotation(customerOrder);
        mail.setReplyTo(customerOrderTiers.getSalesEmployee());
        mail.setSendToMe(sendToMe);
        mail.setMailComputeResult(computeMailForQuotationDocument(customerOrder));

        mail.setSubject("Votre commande n°" + customerOrder.getId() + " est en attente de provision");

        addMailToQueue(mail);

    }

    public void sendCustomerOrderInvoiceToCustomer(CustomerOrder customerOrder, Invoice invoice, boolean sendToMe)
            throws Exception {

        CustomerMail mail = new CustomerMail();
        mail.setHeaderPicture("images/invoice-header.png");
        mail.setTitle("Votre facture est disponible");
        mail.setLabel("Commande n°" + customerOrder.getId());

        computeQuotationPrice(mail, customerOrder);

        Float remainingToPay = accountingRecordService.getRemainingAmountToPayForCustomerOrder(customerOrder);
        if (remainingToPay > 0) {
            MailComputeResult mailComputeResult = computeMailForBillingDocument(customerOrder);

            mail.setExplaination(
                    "Vous trouverez ci-jointe votre facture pour votre commande. Nous vous remercions de bien vouloir effectuez dès maintenant le réglement par un virement de "
                            + remainingToPay + " € sur le compte ci-dessous.");
            mail.setExplaination2("IBAN : FR76 3007 6021 6310 7548 0020 040 \r\n BIC : NORDFRPP");
            mail.setExplainationWarning(
                    "Référence à indiquer abolument dans le libellé de votre virement : " + customerOrder.getId());

            mail.setCbExplanation(
                    "Vous avez aussi la possibilité de payer par carte bancaire en flashant le QR Code ci-dessous ou en cliquant ");
            mail.setCbLink(paymentCbEntryPoint + "/order/invoice?customerOrderId=" + customerOrder.getId() + "&mail="
                    + mailComputeResult.getRecipientsMailTo().get(0).getMail());
        } else
            mail.setExplaination("Vous trouverez ci-jointe votre facture pour votre commande.");

        mail.setGreetings("Nous vous remercions pour votre confiance");

        ITiers customerOrderTiers = quotationService.getCustomerOrderOfQuotation(customerOrder);
        mail.setReplyTo(customerOrderTiers.getSalesEmployee());
        mail.setSendToMe(sendToMe);
        mail.setMailComputeResult(computeMailForQuotationDocument(customerOrder));

        mail.setSubject("Votre facture pour votre commande n°" + customerOrder.getId());

        mail.setCustomerOrder(customerOrder);
        mail.setSendInvoiceAttachment(true);
        mail.setInvoice(invoice);

        if (sendToMe) {
            prepareAndSendMail(mail);
        } else
            addMailToQueue(mail);
    }

    public File generateInvoicePdf(CustomerOrder customerOrder, Invoice invoice) throws DocumentException, IOException {
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
                    vats.add(vatmail);
                }
            }
        }

        ctx.setVariable("vats", vats);
        ctx.setVariable("priceTotal", invoiceHelper.getPriceTotal(invoice));
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

        File tempFile = File.createTempFile("invoice", "pdf");
        OutputStream outputStream = new FileOutputStream(tempFile);
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();

        return tempFile;
    }

}
