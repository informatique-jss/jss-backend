package com.jss.osiris.libs.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.itextpdf.text.DocumentException;
import com.jss.osiris.libs.mail.model.MailComputeResult;
import com.jss.osiris.libs.mail.model.VatMail;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.service.InvoiceHelper;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.Mail;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.IQuotation;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.Quotation;
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

    private JavaMailSender javaMailSender;

    public static final String EMAIL_TEMPLATE_ENCODING = "UTF-8";

    private static final String PNG_MIME = "image/png";

    private Queue<MimeMessage> mailQueue = new LinkedList<MimeMessage>();

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

    public void sendNextMail() throws MailException, IOException, MessagingException {
        // TODO : persist queue to be safe for restart ..
        if (mailQueue != null && mailQueue.size() > 0) {
            boolean canSend = true;
            if (mailDomainFilter != null && !mailDomainFilter.equals("")) {
                Address[] recipients = mailQueue.peek().getRecipients(Message.RecipientType.TO);
                if (recipients != null)
                    for (Address address : recipients) {
                        String[] chunk = address.toString().split("@");
                        if (chunk.length != 2 || !chunk[1].equals(mailDomainFilter))
                            canSend = false;
                    }
                recipients = mailQueue.peek().getRecipients(Message.RecipientType.CC);
                if (recipients != null)
                    for (Address address : recipients) {
                        String[] chunk = address.toString().split("@");
                        if (chunk.length != 2 || !chunk[1].equals(mailDomainFilter))
                            canSend = false;
                    }
            }

            if (canSend)
                getMailSender().send(mailQueue.poll());
        }
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

    public void generateQuotationMail(Quotation quotation) throws Exception {
        sendQuotationToCustomer(quotation, true);
    }

    private void computeQuotationPrice(Context ctx, IQuotation quotation) {
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
                            vats.add(vatmail);
                        }
                    }
                }
            }
        }

        if (discountTotal != null)
            preTaxPriceTotalWithDicount = preTaxPriceTotal - discountTotal;

        priceTotal = preTaxPriceTotal - (discountTotal != null ? discountTotal : 0) + (vats != null ? vatTotal : 0);

        ctx.setVariable("preTaxPriceTotal", preTaxPriceTotal);
        ctx.setVariable("discountTotal", discountTotal);
        ctx.setVariable("preTaxPriceTotalWithDicount", preTaxPriceTotalWithDicount);
        ctx.setVariable("vats", vats);
        ctx.setVariable("priceTotal", priceTotal);
    }

    public void sendQuotationToCustomer(Quotation quotation, boolean sendToMe) throws Exception {
        // Prepare the evaluation context
        final Context ctx = new Context();
        ctx.setVariable("instagram", "instagram");
        ctx.setVariable("facebook", "facebook");
        ctx.setVariable("linkedin", "linkedin");
        ctx.setVariable("twitter", "twitter");
        ctx.setVariable("jssHeaderPicture", "jssHeaderPicture");
        ctx.setVariable("quotationId", quotation.getId());
        ctx.setVariable("quotationHeaderPicture", "quotationHeaderPicture");
        ctx.setVariable("assos", quotation.getAssoAffaireOrders());

        computeQuotationPrice(ctx, quotation);

        LocalDate localDate = LocalDate.now();
        localDate = localDate.plusMonths(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        ctx.setVariable("quotationDueDate", localDate.format(formatter));

        // Prepare message using a Spring helper
        MimeMessage mimeMessage;
        mimeMessage = getMailSender().createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        message.setFrom(new InternetAddress("no-reply@jss.fr", "Journal Spécial des Sociétés"));

        ITiers customerOrder = quotationService.getCustomerOrderOfQuotation(quotation);
        Employee salesEmployee = customerOrder.getSalesEmployee();
        message.setReplyTo(new InternetAddress(salesEmployee.getMail(),
                salesEmployee.getFirstname() + " " + salesEmployee.getLastname()));

        if (sendToMe)
            message.setTo(employeeService.getCurrentEmployee().getMail());
        else {
            MailComputeResult mailComputeResult = computeMailForQuotationDocument(quotation);
            if (mailComputeResult.getRecipientsMailTo() == null || mailComputeResult.getRecipientsMailTo().size() == 0)
                throw new Exception("No recipient found");

            for (Mail mail : mailComputeResult.getRecipientsMailTo())
                message.setTo(mail.getMail());

            if (mailComputeResult.getRecipientsMailCc() != null && mailComputeResult.getRecipientsMailTo().size() > 0)
                for (Mail mail : mailComputeResult.getRecipientsMailCc())
                    message.setCc(mail.getMail());
        }

        message.setSubject("Votre devis n°" + quotation.getId());

        // Create the HTML body using Thymeleaf
        final String htmlContent = emailTemplateEngine().process("devis", ctx);
        message.setText(htmlContent, true);

        // quotation-header.png
        final InputStreamSource imageSourceQuotationHeader = new ByteArrayResource(
                Files.readAllBytes(ResourceUtils.getFile("classpath:images/quotation-header.png").toPath()));
        message.addInline("quotationHeaderPicture", imageSourceQuotationHeader, PNG_MIME);

        // jss-header.png
        final InputStreamSource imageSourceJssHeader = new ByteArrayResource(
                Files.readAllBytes(ResourceUtils.getFile("classpath:images/jss-header.png").toPath()));
        message.addInline("jssHeaderPicture", imageSourceJssHeader, PNG_MIME);

        // facebook
        final InputStreamSource imageSourceFacebook = new ByteArrayResource(
                Files.readAllBytes(ResourceUtils.getFile("classpath:images/facebook.png").toPath()));
        message.addInline("facebook", imageSourceFacebook, PNG_MIME);

        // linkedin
        final InputStreamSource imageSourceLinkedin = new ByteArrayResource(
                Files.readAllBytes(ResourceUtils.getFile("classpath:images/linkedin.png").toPath()));
        message.addInline("linkedin", imageSourceLinkedin, PNG_MIME);

        // instagram
        final InputStreamSource imageSourceInstagram = new ByteArrayResource(
                Files.readAllBytes(ResourceUtils.getFile("classpath:images/instagram.png").toPath()));
        message.addInline("instagram", imageSourceInstagram, PNG_MIME);

        // twitter
        final InputStreamSource imageSourceTwitter = new ByteArrayResource(
                Files.readAllBytes(ResourceUtils.getFile("classpath:images/twitter.png").toPath()));
        message.addInline("twitter", imageSourceTwitter, PNG_MIME);

        // Send email
        mailQueue.offer(mimeMessage);
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

    public void generateWaintingDepositMail(CustomerOrder customerOrder) throws Exception {
        sendCustomerOrderDepositWaitingToCustomer(customerOrder, true);
    }

    public void sendCustomerOrderDepositWaitingToCustomer(CustomerOrder customerOrder, boolean sendToMe)
            throws Exception {
        // Prepare the evaluation context
        final Context ctx = new Context();
        ctx.setVariable("instagram", "instagram");
        ctx.setVariable("facebook", "facebook");
        ctx.setVariable("linkedin", "linkedin");
        ctx.setVariable("twitter", "twitter");
        ctx.setVariable("jssHeaderPicture", "jssHeaderPicture");
        ctx.setVariable("customerOrderId", customerOrder.getId());
        ctx.setVariable("customerOrderTotalPrice", customerOrder.getId());
        ctx.setVariable("waitingDepositHeaderPicture", "waitingDepositHeaderPicture");

        computeQuotationPrice(ctx, customerOrder);

        // Prepare message using a Spring helper
        MimeMessage mimeMessage;
        mimeMessage = getMailSender().createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        message.setFrom(new InternetAddress("no-reply@jss.fr", "Journal Spécial des Sociétés"));

        ITiers customerOrderTiers = quotationService.getCustomerOrderOfQuotation(customerOrder);
        Employee salesEmployee = customerOrderTiers.getSalesEmployee();
        message.setReplyTo(new InternetAddress(salesEmployee.getMail(),
                salesEmployee.getFirstname() + " " + salesEmployee.getLastname()));

        if (sendToMe)
            message.setTo(employeeService.getCurrentEmployee().getMail());
        else {

            MailComputeResult mailComputeResult = computeMailForBillingDocument(customerOrder);
            if (mailComputeResult.getRecipientsMailTo() == null ||
                    mailComputeResult.getRecipientsMailTo().size() == 0)
                throw new Exception("No recipient found");

            for (Mail mail : mailComputeResult.getRecipientsMailTo())
                message.setTo(mail.getMail());

            if (mailComputeResult.getRecipientsMailCc() != null &&
                    mailComputeResult.getRecipientsMailTo().size() > 0)
                for (Mail mail : mailComputeResult.getRecipientsMailCc())
                    message.setCc(mail.getMail());
        }

        message.setSubject("Votre commande n°" + customerOrder.getId() + " est en attente de provision");

        // Create the HTML body using Thymeleaf
        final String htmlContent = emailTemplateEngine().process("waiting-deposit", ctx);
        message.setText(htmlContent, true);

        // quotation-header.png
        final InputStreamSource imageSourceQuotationHeader = new ByteArrayResource(
                Files.readAllBytes(ResourceUtils.getFile("classpath:images/waiting-deposit-header.png").toPath()));
        message.addInline("waitingDepositHeaderPicture", imageSourceQuotationHeader, PNG_MIME);

        // jss-header.png
        final InputStreamSource imageSourceJssHeader = new ByteArrayResource(
                Files.readAllBytes(ResourceUtils.getFile("classpath:images/jss-header.png").toPath()));
        message.addInline("jssHeaderPicture", imageSourceJssHeader, PNG_MIME);

        // facebook
        final InputStreamSource imageSourceFacebook = new ByteArrayResource(
                Files.readAllBytes(ResourceUtils.getFile("classpath:images/facebook.png").toPath()));
        message.addInline("facebook", imageSourceFacebook, PNG_MIME);

        // linkedin
        final InputStreamSource imageSourceLinkedin = new ByteArrayResource(
                Files.readAllBytes(ResourceUtils.getFile("classpath:images/linkedin.png").toPath()));
        message.addInline("linkedin", imageSourceLinkedin, PNG_MIME);

        // instagram
        final InputStreamSource imageSourceInstagram = new ByteArrayResource(
                Files.readAllBytes(ResourceUtils.getFile("classpath:images/instagram.png").toPath()));
        message.addInline("instagram", imageSourceInstagram, PNG_MIME);

        // twitter
        final InputStreamSource imageSourceTwitter = new ByteArrayResource(
                Files.readAllBytes(ResourceUtils.getFile("classpath:images/twitter.png").toPath()));
        message.addInline("twitter", imageSourceTwitter, PNG_MIME);

        // Send email
        mailQueue.offer(mimeMessage);
    }

    public void sendCustomerOrderInvoiceToCustomer(CustomerOrder customerOrder, Invoice invoice, boolean sendToMe)
            throws Exception {
        // Prepare the evaluation context
        final Context ctx = new Context();
        ctx.setVariable("instagram", "instagram");
        ctx.setVariable("facebook", "facebook");
        ctx.setVariable("linkedin", "linkedin");
        ctx.setVariable("twitter", "twitter");
        ctx.setVariable("jssHeaderPicture", "jssHeaderPicture");
        ctx.setVariable("customerOrderId", customerOrder.getId());
        ctx.setVariable("remainingPrice",
                accountingRecordService.getRemainingAmountToPayForCustomerOrder(customerOrder));
        ctx.setVariable("invoiceHeaderPicture", "invoiceHeaderPicture");

        // Prepare message using a Spring helper
        MimeMessage mimeMessage;
        mimeMessage = getMailSender().createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        message.setFrom(new InternetAddress("no-reply@jss.fr", "Journal Spécial des Sociétés"));

        ITiers customerOrderTiers = quotationService.getCustomerOrderOfQuotation(customerOrder);
        Employee salesEmployee = customerOrderTiers.getSalesEmployee();
        message.setReplyTo(new InternetAddress(salesEmployee.getMail(),
                salesEmployee.getFirstname() + " " + salesEmployee.getLastname()));

        if (sendToMe)
            message.setTo(employeeService.getCurrentEmployee().getMail());
        else {

            MailComputeResult mailComputeResult = computeMailForBillingDocument(customerOrder);
            if (mailComputeResult.getRecipientsMailTo() == null ||
                    mailComputeResult.getRecipientsMailTo().size() == 0)
                throw new Exception("No recipient found");

            for (Mail mail : mailComputeResult.getRecipientsMailTo())
                message.setTo(mail.getMail());

            if (mailComputeResult.getRecipientsMailCc() != null &&
                    mailComputeResult.getRecipientsMailTo().size() > 0)
                for (Mail mail : mailComputeResult.getRecipientsMailCc())
                    message.setCc(mail.getMail());
        }

        message.setSubject("Votre facture pour votre commande n°" + customerOrder.getId());

        // Create the HTML body using Thymeleaf
        final String htmlContent = emailTemplateEngine().process("invoice-mail", ctx);
        message.setText(htmlContent, true);

        // quotation-header.png
        final InputStreamSource imageSourceQuotationHeader = new ByteArrayResource(
                Files.readAllBytes(ResourceUtils.getFile("classpath:images/invoice-header.png").toPath()));
        message.addInline("invoiceHeaderPicture", imageSourceQuotationHeader, PNG_MIME);

        // jss-header.png
        final InputStreamSource imageSourceJssHeader = new ByteArrayResource(
                Files.readAllBytes(ResourceUtils.getFile("classpath:images/jss-header.png").toPath()));
        message.addInline("jssHeaderPicture", imageSourceJssHeader, PNG_MIME);

        // facebook
        final InputStreamSource imageSourceFacebook = new ByteArrayResource(
                Files.readAllBytes(ResourceUtils.getFile("classpath:images/facebook.png").toPath()));
        message.addInline("facebook", imageSourceFacebook, PNG_MIME);

        // linkedin
        final InputStreamSource imageSourceLinkedin = new ByteArrayResource(
                Files.readAllBytes(ResourceUtils.getFile("classpath:images/linkedin.png").toPath()));
        message.addInline("linkedin", imageSourceLinkedin, PNG_MIME);

        // instagram
        final InputStreamSource imageSourceInstagram = new ByteArrayResource(
                Files.readAllBytes(ResourceUtils.getFile("classpath:images/instagram.png").toPath()));
        message.addInline("instagram", imageSourceInstagram, PNG_MIME);

        // twitter
        final InputStreamSource imageSourceTwitter = new ByteArrayResource(
                Files.readAllBytes(ResourceUtils.getFile("classpath:images/twitter.png").toPath()));
        message.addInline("twitter", imageSourceTwitter, PNG_MIME);

        message.addAttachment("Facture n°" + ".pdf", generateInvoicePdf(customerOrder, invoice));

        // Send email
        mailQueue.offer(mimeMessage);
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

        // TODO : add deposit display

        LocalDateTime localDate = invoice.getCreatedDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        ctx.setVariable("invoiceCreatedDate", localDate.format(formatter));

        // Create the HTML body using Thymeleaf
        final String htmlContent = emailTemplateEngine().process("invoice-page1", ctx);

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
