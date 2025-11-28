package com.jss.osiris.libs.mail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.poi.util.IOUtils;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.util.XRLog;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.jss.osiris.libs.PictureHelper;
import com.jss.osiris.libs.QrCodeHelper;
import com.jss.osiris.libs.azure.TranslationService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.model.LetterModel;
import com.jss.osiris.libs.mail.model.VatMail;
import com.jss.osiris.modules.osiris.accounting.model.BillingClosureReceiptValue;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.osiris.invoicing.model.Payment;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceHelper;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceItemService;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceService;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.quotation.model.Announcement;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.Domiciliation;
import com.jss.osiris.modules.osiris.quotation.model.NoticeType;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.service.ProvisionService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;

import ch.digitalfondue.mjml4j.Mjml4j;

@org.springframework.stereotype.Service
public class GeneratePdfDelegate {

    private BigDecimal oneHundredValue = new BigDecimal(100);
    private BigDecimal zeroValue = new BigDecimal(0);

    @Autowired
    PictureHelper pictureHelper;

    @Autowired
    ConstantService constantService;

    @Autowired
    QrCodeHelper qrCodeHelper;

    @Autowired
    MailHelper mailHelper;

    @Autowired
    MailComputeHelper mailComputeHelper;

    @Autowired
    DocumentService documentService;

    @Value("${jss.iban}")
    private String ibanJss;

    @Value("${jss.bic}")
    private String bicJss;

    @Value("${jss.domiciliation.agreement.number}")
    private String jssDomiciliationAgreementNumber;

    @Value("${jss.domiciliation.agreement.date}")
    private String jssDomiciliationAgreementDate;

    @Value("${invoicing.payment.limit.refund.euros}")
    private String payementLimitRefundInEuros;

    @Autowired
    InvoiceHelper invoiceHelper;

    @Autowired
    ProvisionService provisionService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    InvoiceItemService invoiceItemService;

    @Autowired
    TranslationService translationService;

    public static final String EMAIL_TEMPLATE_ENCODING = "UTF-8";
    public static final String HEADER_PDF_TEMPLATE = "header-pdf";
    public static final String FOOTER_DOMICILIATION = "footer-domiciliation";
    public Boolean isContent = true;

    public TemplateEngine pdfTemplateEngine(Boolean isContent) {
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(htmlTemplateResolver(isContent));
        // Message source, internationalization specific to emails
        return templateEngine;
    }

    private ITemplateResolver htmlTemplateResolver(Boolean isContent) {
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setOrder(Integer.valueOf(1));
        if (!isContent) {
            templateResolver.setPrefix("mails/mjml/");
            templateResolver.setSuffix(".mjml");
        } else {
            templateResolver.setPrefix("mails/templates/");
            templateResolver.setSuffix(".html");
        }
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(EMAIL_TEMPLATE_ENCODING);
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    private String parseMjmlFile(String mjmlString) {
        org.jsoup.nodes.Document xhtmlDoc = Jsoup.parse(Mjml4j.render(mjmlString), "UTF-8");
        xhtmlDoc.outputSettings()
                .syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml)
                .escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml)
                .charset("UTF-8");

        return xhtmlDoc.html();
    }

    private String composeHtml(String headerTemplateName, String footerTemplateName, String htmlContent, Context ctx) {
        String headerTemplate = "";
        String footerTemplate = "";
        if (headerTemplateName != null && headerTemplateName.length() > 1)
            headerTemplate = parseMjmlFile(pdfTemplateEngine(false).process(headerTemplateName, ctx));
        if (footerTemplateName != null && footerTemplateName.length() > 1)
            footerTemplate = parseMjmlFile(pdfTemplateEngine(false).process(footerTemplateName, ctx));

        org.jsoup.nodes.Document headerDoc = Jsoup.parse(headerTemplate, "UTF-8");
        org.jsoup.nodes.Document footerDoc = Jsoup.parse(footerTemplate, "UTF-8");
        org.jsoup.nodes.Document mainDoc = Jsoup.parse(htmlContent, "UTF-8");
        org.jsoup.nodes.Document finalDoc = Jsoup.parse("<html><head></head><body></body></html>", "UTF-8");

        finalDoc.head()
                .append(headerDoc.head().html())
                .append(mainDoc.head().html())
                .append(footerDoc.head().html())
                .append("""
                            <style>
                                @page {
                                    size: A4;
                                    margin: 40px 0px 80px 0px;
                                }

                                @font-face {
                                    font-family: 'Roboto';
                                    src: url('fonts/Roboto/Roboto-Regular.ttf') format('truetype');
                                    font-weight: normal;
                                }

                                @font-face {
                                    font-family: 'Roboto';
                                    src: url('fonts/Roboto/Roboto-Bold.ttf') format('truetype');
                                    font-weight: bold;
                                }

                                body {
                                    font-family: 'Roboto', sans-serif !important;
                                }

                                strong, b {
                                    font-family: 'Roboto';
                                    font-weight: bold;
                                }

                                a, a * {
                                    font-family: 'Roboto', sans-serif !important;
                                }

                                /* --- Ajustement du header MJML --- */
                                 .mj-column-per-20,
                                .mj-column-per-30,
                                .mj-column-per-40,
                                .mj-column-per-50,
                                .mj-column-per-80 {
                                    display: inline-block !important;
                                    vertical-align: top !important;
                                    }
                                [class*="mj-column"] div {
                                    font-family: 'Roboto', sans-serif !important;
                                }

                                    /* Ajustement de largeur : éviter retour à la ligne */
                                .mj-column-per-20 { width: 20% !important; max-width: 20% !important; }
                                .mj-column-per-30 { width: 30% !important; max-width: 30% !important; }
                                .mj-column-per-40 { width: 40% !important; max-width: 40% !important; }
                                .mj-column-per-50 { width: 50% !important; max-width: 50% !important; }
                                .mj-column-per-70 { width: 70% !important; max-width: 70% !important; }

                                /* Empêcher la ligne de se casser entre les colonnes */
                                td[style*="direction:ltr"] {
                                    white-space: nowrap !important;
                                }
                            </style>
                        """);

        // add Background image in base64 format
        byte[] imageBytes;
        String base64 = null;
        try {
            imageBytes = IOUtils.toByteArray(new ClassPathResource("images/birdBackground.png").getInputStream());
            base64 = Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String backgroundDiv = String.format("""
                <div style="position: fixed;
                            top: 160px;
                            left: 0;
                            width: 595px;
                            height: 842px;
                            background-image: url('data:image/png;base64,%s');
                            background-repeat: no-repeat;
                            background-size: contain;
                            z-index: -1;">
                </div>
                """, base64);

        // delete margin to put background image stuck to the edge with no space on the
        // left
        finalDoc.select("html").attr("style", "margin: 0; padding: 0;");
        finalDoc.body().attr("style", "margin: 0; padding: 0;");
        finalDoc.body().prepend(backgroundDiv);

        // adding the deleted margin for the rest of the document
        StringBuilder wrappedContent = new StringBuilder();
        wrappedContent
                .append(headerDoc.body().html())
                .append("<div style='margin: 40px 40px 40px 40px;'>")
                .append(mainDoc.body().html())
                .append("</div>");
        if (footerTemplateName == FOOTER_DOMICILIATION) {
            // add css to correct and fix the footer display
            wrappedContent.append("""
                    <div style="
                        position: fixed;
                        bottom: -16px;
                        left: 140px;
                        font-size: 14px;
                        color: #303B4D;
                        text-align: center;
                        white-space: nowrap;

                    ">
                        """ + footerDoc.body().html() + """
                        </div>
                    """);
        } else {
            // add css to correct and fix the footer display
            wrappedContent.append("""
                    <div style="
                        position: fixed;
                        bottom: -30px;
                        left: 60px;
                        width: 100%;
                        padding: 0 40px;
                    ">
                    """ + footerDoc.body().html() + """
                    </div>
                    """);
        }

        finalDoc.body().append(wrappedContent.toString());

        finalDoc.outputSettings()
                .syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml)
                .escapeMode(org.jsoup.nodes.Entities.EscapeMode.extended)
                .charset("UTF-8");

        return finalDoc.html();
    }

    @Transactional(rollbackFor = Exception.class)
    public File generatePublicationForAnnouncement(Announcement announcement, Provision provision,
            boolean isPublicationFlag,
            boolean isPublicationReceipt, boolean isProofReading) throws OsirisException {
        // To avoid proxy error
        provision = provisionService.getProvision(provision.getId());
        String publicationDate = announcement.getPublicationDate().format(
                DateTimeFormatter
                        .ofPattern("EEEE d MMMM yyyy")
                        .withLocale(Locale.FRENCH));
        if (publicationDate != null && !publicationDate.isEmpty())
            publicationDate = publicationDate.substring(0, 1).toUpperCase(Locale.FRENCH) + publicationDate.substring(1);

        final Context ctx = new Context();

        ctx.setVariable("publicationDate", publicationDate);
        ctx.setVariable("department",
                announcement.getDepartment().getCode() + " - " + announcement.getDepartment().getLabel());
        ctx.setVariable("serviceLabel", provision.getService().getServiceLabelToDisplay());

        File tempFile;
        if (!announcement.getIsComplexAnnouncement()) {
            // Generate announcement PDF

            if (provision.getAttachments() != null && provision.getAttachments().size() > 0)
                for (Attachment attachment : provision.getAttachments())
                    if (attachment.getAttachmentType().getId().equals(constantService.getAttachmentTypeLogo().getId()))
                        ctx.setVariable("logo", pictureHelper
                                .getPictureFileAsBase64String(new File(attachment.getUploadedFile().getPath())));

            ctx.setVariable("noticeHeader",
                    (announcement.getNoticeHeader() != null && !announcement.getNoticeHeader().equals(""))
                            ? announcement.getNoticeHeader()
                                    .replaceAll("<br style=\"mso-special-character: line-break;\">", "<br/>")
                                    .replaceAll("<br>", "<br/>").replaceAll("&nbsp;", " ").replaceAll("<wbr>", " ")
                                    .replaceAll("</wbr>", " ")
                            : null);
            if (announcement.getNotice() != null && !announcement.getNotice().equals(""))
                ctx.setVariable("notice",
                        StringEscapeUtils.unescapeHtml4(announcement.getNotice()
                                .replaceAll("<br style=\"mso-special-character: line-break;\">", "<br/>")
                                .replaceAll("<br>", "<br/>").replaceAll("&nbsp;", " ")));

            // Create the HTML body using Thymeleaf
            if (isPublicationFlag) {
                ctx.setVariable("publicationTitle", "Témoin de publication sur www.JSS.fr");
            } else {
                ctx.setVariable("publicationTitle", "Attestation de parution sur www.JSS.fr");
            }
            final String htmlContent = composeHtml("header-publication-flag", "footer-pdf", StringEscapeUtils
                    .unescapeHtml4(pdfTemplateEngine(isContent).process("publication-flag", ctx)), ctx);

            OutputStream outputStream;
            try {
                tempFile = File.createTempFile("Témoin de publication", "pdf");
                outputStream = new FileOutputStream(tempFile);
            } catch (IOException e) {
                throw new OsirisException(e, "Unable to create temp file");
            }
            ITextRenderer renderer = new ITextRenderer();
            XRLog.setLevel(XRLog.CSS_PARSE, Level.SEVERE);
            renderer = fixFontSizeHeaderRendererDisplay(renderer);
            renderer.setDocumentFromString(
                    htmlContent.replaceAll("\\p{C}", " ").replaceAll("<col (.*?)>", "")
                            .replaceAll("line-height: normal",
                                    "line-height: normal;padding:0;margin:0")
                            .replaceAll("&nbsp;", "&#160;")
                            .replaceAll("font-size:\\s*0(\\.0+)?(px|pt|em|rem|%)?;", "font-size:1px;"));
            renderer.layout();

            try {
                renderer.createPDF(outputStream);
                outputStream.close();
            } catch (DocumentException | IOException e) {
                throw new OsirisException(e,
                        "Unable to create publication flag PDF file for announcement " + announcement.getId());
            }

        } else {
            File complexePdf = null;
            if (provision.getAttachments() != null && provision.getAttachments().size() > 0)
                for (Attachment attachment : provision.getAttachments())
                    if (attachment.getAttachmentType().getId()
                            .equals(constantService.getAttachmentTypeComplexAnnouncement().getId())) {
                        complexePdf = new File(attachment.getUploadedFile().getPath());
                        break;
                    }

            if (complexePdf == null)
                throw new OsirisException(null, "No announncement PDF found");

            tempFile = addHeaderAndFooterOnPublicationFlag(complexePdf, announcement, isPublicationFlag);
        }
        return tempFile;
    }

    public File generateLetterPdf(ArrayList<CustomerOrder> customerOrders)
            throws OsirisException, OsirisClientMessageException {
        final Context ctx = new Context();
        ctx.setVariable("localDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        ArrayList<LetterModel> letterModels = new ArrayList<LetterModel>();

        for (CustomerOrder customerOrder : customerOrders) {
            Document billingDocument = documentService.getDocumentByDocumentType(customerOrder.getDocuments(),
                    constantService.getDocumentTypeBilling());

            LetterModel letterModel = new LetterModel();
            letterModel.setCustomerOrder(customerOrder);
            letterModel.setInvoiceLabelResult(mailComputeHelper.computePaperLabelResult(customerOrder));
            letterModel.setCommandNumber(billingDocument.getCommandNumber());
            letterModel.setCustomerReference(billingDocument.getExternalReference());

            ArrayList<String> affaireLabels = new ArrayList<String>();
            ArrayList<String> eventLabels = new ArrayList<String>();
            Employee signatureEmployee = null;

            if (customerOrder.getAssoAffaireOrders() != null)
                for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders()) {
                    signatureEmployee = customerOrder.getResponsable().getSalesEmployee();
                    affaireLabels.add((asso.getAffaire().getDenomination() != null ? asso.getAffaire().getDenomination()
                            : (asso.getAffaire().getCivility().getLabel() + " " + asso.getAffaire().getFirstname() + " "
                                    + asso.getAffaire().getLastname()))
                            + "<br/>"
                            + asso.getAffaire().getAddress() + "<br/>"
                            + asso.getAffaire().getPostalCode() + " "
                            + (asso.getAffaire().getCity() != null ? asso.getAffaire().getCity().getLabel() : ""));
                    for (Service service : asso.getServices()) {
                        for (Provision provision : service.getProvisions()) {
                            eventLabels.add(provision.getProvisionType().getLabel());
                        }
                    }
                }

            letterModel.setAffaireLabel(String.join("<br/>", affaireLabels).replaceAll("&", " "));
            letterModel.setEventLabel(String.join(" / ", eventLabels).replaceAll("&", " "));
            if (signatureEmployee != null)
                letterModel.setSignatureLabel(signatureEmployee.getFirstname() + " " + signatureEmployee.getLastname()
                        + "<br/>" + signatureEmployee.getTitle() + "<br/>" + signatureEmployee.getMail());
            letterModels.add(letterModel);
        }

        ctx.setVariable("letterModels", letterModels);

        // Create the HTML body using Thymeleaf
        String htmlBody = StringEscapeUtils
                .unescapeHtml4(pdfTemplateEngine(true).process("letter-page", ctx));
        final String htmlContent = composeHtml(HEADER_PDF_TEMPLATE, null, htmlBody, new Context());

        File tempFile;
        OutputStream outputStream;
        try {
            tempFile = File.createTempFile("invoice", "pdf");
            outputStream = new FileOutputStream(tempFile);
        } catch (IOException e) {
            throw new OsirisException(e, "Unable to create temp file");
        }
        ITextRenderer renderer = new ITextRenderer();
        XRLog.setLevel(XRLog.CSS_PARSE, Level.SEVERE);
        renderer = fixFontSizeHeaderRendererDisplay(renderer);
        renderer.setDocumentFromString(
                htmlContent.replaceAll("\\p{C}", " ").replaceAll("&", "<![CDATA[&]]>").replaceAll("<col (.*?)>", "")
                        .replaceAll("line-height: normal",
                                "line-height: normal;padding:0;margin:0")
                        .replaceAll("&nbsp;", "&#160;")
                        .replaceAll("font-size:\\s*0(\\.0+)?(px|pt|em|rem|%)?;", "font-size:1px;"));
        renderer.layout();
        try {
            renderer.createPDF(outputStream);
            outputStream.close();
        } catch (DocumentException | IOException e) {
            throw new OsirisException(e, "Unable to create PDF file for letters");
        }
        return tempFile;
    }

    public File getBillingClosureReceiptFile(Tiers tier, Responsable responsable,
            List<BillingClosureReceiptValue> billingClosureValues)
            throws OsirisException, OsirisClientMessageException {
        final Context ctx = new Context();

        Document billingDocument = null;

        if (tier != null)
            billingDocument = documentService.getBillingDocument(tier.getDocuments());
        else if (responsable != null)
            billingDocument = documentService.getBillingDocument(responsable.getDocuments());

        ctx.setVariable("commandNumber", null);
        if (billingDocument != null && billingDocument.getIsCommandNumberMandatory() != null
                && billingDocument.getIsCommandNumberMandatory()
                && billingDocument.getCommandNumber() != null)
            ctx.setVariable("commandNumber", billingDocument.getCommandNumber());
        ctx.setVariable("currentDate", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        if (billingDocument != null && billingDocument.getExternalReference() != null)
            ctx.setVariable("clientReference", billingDocument.getExternalReference());

        if (tier != null) {
            ctx.setVariable("denomination",
                    tier.getDenomination() != null
                            ? tier.getDenomination()
                            : (tier.getFirstname() + " " + tier.getLastname()));
            ctx.setVariable("address", tier.getAddress());
            ctx.setVariable("postalCode", tier.getPostalCode());
            ctx.setVariable("city", tier.getCity() != null ? tier.getCity().getLabel() : "");
        }
        if (responsable != null) {
            ctx.setVariable("denomination",
                    (responsable.getFirstname() + " " + responsable.getLastname()));
            ctx.setVariable("tiersName",
                    responsable.getTiers().getDenomination() != null ? responsable.getTiers().getDenomination()
                            : (responsable.getTiers().getFirstname() + " " + responsable.getTiers().getLastname()));
            ctx.setVariable("address", responsable.getTiers().getAddress());
            ctx.setVariable("postalCode", responsable.getTiers().getPostalCode());
            ctx.setVariable("city",
                    responsable.getTiers().getCity() != null
                            ? responsable.getTiers().getCity().getLabel()
                            : "");
        }

        ctx.setVariable("billingClosureValues", billingClosureValues);
        ctx.setVariable("ibanJss", ibanJss);
        ctx.setVariable("bicJss", bicJss);

        Tiers billingTiers = null;
        billingTiers = tier;
        if (responsable != null)
            billingTiers = responsable.getTiers();

        ctx.setVariable("tiersReference", null);
        if (billingTiers != null)
            ctx.setVariable("tiersReference", billingTiers.getId()
                    + (billingTiers.getIdAs400() != null ? ("/" + billingTiers.getIdAs400()) : ""));

        Double balance = 0.0;
        Double creditBalance = 0.0;
        Double debitBalance = 0.0;

        if (billingClosureValues != null && billingClosureValues.size() > 0)
            for (BillingClosureReceiptValue billingClosureValue : billingClosureValues) {
                balance += billingClosureValue.getCreditAmount() != null
                        ? billingClosureValue.getCreditAmount().doubleValue()
                        : 0;
                creditBalance += billingClosureValue.getCreditAmount() != null
                        ? billingClosureValue.getCreditAmount().doubleValue()
                        : 0;
                balance -= billingClosureValue.getDebitAmount() != null
                        ? billingClosureValue.getDebitAmount().doubleValue()
                        : 0;
                debitBalance += billingClosureValue.getDebitAmount() != null
                        ? billingClosureValue.getDebitAmount().doubleValue()
                        : 0;
            }
        ctx.setVariable("balance", balance);
        ctx.setVariable("creditBalance", creditBalance);
        ctx.setVariable("debitBalance", debitBalance);

        // Create the HTML body using Thymeleaf
        String htmlBody = StringEscapeUtils
                .unescapeHtml4(pdfTemplateEngine(true).process("billing-closure-receipt", ctx));
        final String htmlContent = composeHtml(HEADER_PDF_TEMPLATE, null, htmlBody, new Context());

        File tempFile;
        OutputStream outputStream;
        try {
            tempFile = File.createTempFile("billing-closure-receipt", "pdf");
            outputStream = new FileOutputStream(tempFile);
        } catch (IOException e) {
            throw new OsirisException(e, "Unable to create temp file");
        }
        ITextRenderer renderer = new ITextRenderer();
        XRLog.setLevel(XRLog.CSS_PARSE, Level.SEVERE);
        renderer = fixFontSizeHeaderRendererDisplay(renderer);
        renderer.setDocumentFromString(htmlContent.replaceAll("\\p{C}", " ")
                .replaceAll("&(?!(amp|lt|gt|quot|apos|#\\d+|#x[\\da-fA-F]+);)", "&amp;")
                .replaceAll("&nbsp;", "&#160;")
                .replaceAll("font-size:\\s*0(\\.0+)?(px|pt|em|rem|%)?;", "font-size:1px;"));
        renderer.layout();
        try {
            renderer.createPDF(outputStream);
            outputStream.close();
        } catch (DocumentException | IOException e) {
            throw new OsirisException(e,
                    "Unable to create PDF file for biling closure receipt, tiers/responsable n°"
                            + (tier != null ? tier.getId() : (responsable != null ? responsable.getId() : "")));
        }
        return tempFile;
    }

    public File generateQuotationPdf(Quotation quotation) throws OsirisException {
        final Context ctx = new Context();

        ctx.setVariable("tiersReference", quotation.getResponsable().getTiers().getId()
                + (quotation.getResponsable().getTiers().getIdAs400() != null
                        ? ("/" + quotation.getResponsable().getTiers().getIdAs400())
                        : ""));
        ctx.setVariable("responsableOnBilling", quotation.getResponsable().getFirstname() + " "
                + quotation.getResponsable().getLastname());
        ctx.setVariable("assos", quotation.getAssoAffaireOrders());

        Boolean hasDocuments = null;
        if (!quotation.getAssoAffaireOrders().isEmpty())
            hasDocuments = quotation.getAssoAffaireOrders().stream()
                    .filter(asso -> asso.getServices() != null)
                    .flatMap(asso -> asso.getServices().stream())
                    .anyMatch(service -> service.getAssoServiceDocuments() != null
                            && !service.getAssoServiceDocuments().isEmpty());
        ctx.setVariable("hasDocuments", hasDocuments);

        ctx.setVariable("quotation", quotation);
        ctx.setVariable("quotationCreatedDate",
                quotation.getEffectiveDate() != null ? quotation.getEffectiveDate().format(DateTimeFormatter
                        .ofPattern("dd/MM/yyyy"))
                        : quotation.getCreatedDate().format(DateTimeFormatter
                                .ofPattern("dd/MM/yyyy")));
        ctx.setVariable("endOfYearDateString",
                LocalDate.now().withMonth(12).withDayOfMonth(31).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        mailHelper.setQuotationPrice(quotation, ctx);

        String htmlBody = StringEscapeUtils
                .unescapeHtml4(pdfTemplateEngine(true).process("quotation-page", ctx));
        String htmlContent = composeHtml(HEADER_PDF_TEMPLATE, null, htmlBody, new Context());

        File tempFile;
        OutputStream outputStream;
        try {
            tempFile = File.createTempFile("quotation", "pdf");
            outputStream = new FileOutputStream(tempFile);
        } catch (IOException e) {
            throw new OsirisException(e, "Unable to create temp file");
        }
        ITextRenderer renderer = new ITextRenderer();
        XRLog.setLevel(XRLog.CSS_PARSE, Level.SEVERE);
        renderer = fixFontSizeHeaderRendererDisplay(renderer);

        renderer.setDocumentFromString(htmlContent.replaceAll("[\\u0000-\\u001F&&[^\\n\\r\\t]]", " ")
                .replaceAll("&nbsp;", "&#160;")
                .replaceAll("font-size:\\s*0(\\.0+)?(px|pt|em|rem|%)?;", "font-size:1px;"));
        renderer.layout();

        try {
            renderer.createPDF(outputStream);
            outputStream.close();
        } catch (DocumentException | IOException e) {
            throw new OsirisException(e, "Unable to create PDF file for quotation " + quotation.getId());
        }
        return tempFile;
    }

    public File generateInvoicePdf(CustomerOrder customerOrder, Invoice invoice, Invoice originalInvoice)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        final Context ctx = new Context();

        if (originalInvoice != null)
            ctx.setVariable("reverseCreditNote", originalInvoice);

        if (invoice == null)
            throw new OsirisException("null invoice");

        ctx.setVariable("preTaxPriceTotal", invoiceHelper.getPreTaxPriceTotal(invoice));
        if (invoiceHelper.getDiscountTotal(invoice).multiply(oneHundredValue).setScale(0).divide(oneHundredValue)
                .compareTo(zeroValue) > 0)
            ctx.setVariable("discountTotal", invoiceHelper.getDiscountTotal(invoice));

        // Group debours for asso invoice item debours
        if (customerOrder != null) {
            List<AssoAffaireOrder> assos = new ArrayList<AssoAffaireOrder>();
            if (customerOrder.getAssoAffaireOrders() != null && customerOrder.getAssoAffaireOrders().size() > 0)
                for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders()) {
                    if (asso.getServices() != null)
                        for (Service service : asso.getServices()) {
                            if (service.getProvisions() != null) {
                                ArrayList<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
                                for (Provision provision : service.getProvisions()) {
                                    invoiceItems.addAll(provision.getInvoiceItems());
                                }
                                service.getProvisions().get(0)
                                        .setInvoiceItemsGrouped(getGroupedInvoiceItemsForDebours(invoiceItems));
                            }
                        }
                    assos.add(asso);
                }
            ctx.setVariable("assos", assos);
        } else {
            ctx.setVariable("invoiceItems", invoice.getInvoiceItems());
        }

        if (invoiceHelper.getDiscountTotal(invoice) != null)
            ctx.setVariable("preTaxPriceTotalWithDicount",
                    invoiceHelper.getPreTaxPriceTotal(invoice).subtract(invoiceHelper.getDiscountTotal(invoice)));

        ArrayList<VatMail> vats = null;
        Double vatTotal = 0.0;
        for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
            if (invoiceItem.getVat() != null && invoiceItem.getVatPrice() != null) {
                vatTotal += invoiceItem.getVatPrice().doubleValue();
                if (vats == null)
                    vats = new ArrayList<VatMail>();
                boolean vatFound = false;
                for (VatMail vatMail : vats) {
                    if (vatMail.getLabel().equals(invoiceItem.getVat().getLabel())) {
                        vatFound = true;
                        if (vatMail.getTotal() == null && invoiceItem.getVatPrice() != null) {
                            vatMail.setTotal(invoiceItem.getVatPrice());
                            vatMail.setBase(invoiceItem.getPreTaxPrice()
                                    .subtract(invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount()
                                            : zeroValue));
                        } else if (invoiceItem.getVatPrice() != null) {
                            vatMail.setTotal(vatMail.getTotal().add(invoiceItem.getVatPrice()));
                            vatMail.setBase(vatMail.getBase().add(invoiceItem.getPreTaxPrice())
                                    .subtract((invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount()
                                            : zeroValue)));
                        }
                    }
                }
                if (!vatFound) {
                    VatMail vatmail = new VatMail();
                    vatmail.setTotal(invoiceItem.getVatPrice());
                    vatmail.setLabel(invoiceItem.getVat().getLabel());
                    vatmail.setBase(invoiceItem.getPreTaxPrice()
                            .subtract(invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount()
                                    : zeroValue));
                    vats.add(vatmail);
                }
            }
        }

        ctx.setVariable("vats", vats);
        ctx.setVariable("priceTotal",
                invoiceHelper.getPriceTotal(invoice).multiply(oneHundredValue).setScale(0, RoundingMode.HALF_EVEN)
                        .divide(oneHundredValue).setScale(2, RoundingMode.HALF_EVEN));
        ctx.setVariable("invoice", invoice);
        ctx.setVariable("isPrelevementType", false);
        if (invoice.getManualPaymentType().getId().equals(constantService.getPaymentTypePrelevement().getId()))
            ctx.setVariable("isPrelevementType", true);

        // Group debours invoice items for credit note
        if (originalInvoice != null && invoice != null && invoice.getInvoiceItems() != null) {
            ctx.setVariable("reverseInvoiceItems", getGroupedInvoiceItemsForDebours(invoice.getInvoiceItems()));
        }

        ctx.setVariable("tiersReference", null);
        ctx.setVariable("tiersReference", invoice.getResponsable().getTiers().getId()
                + (invoice.getResponsable().getTiers().getIdAs400() != null
                        ? ("/" + invoice.getResponsable().getTiers().getIdAs400())
                        : ""));

        if (customerOrder != null) {
            List<String> externalReferences = new ArrayList<String>();
            Document billingDocument = documentService.getDocumentByDocumentType(customerOrder.getDocuments(),
                    constantService.getDocumentTypeBilling());
            if (billingDocument != null) {
                if (billingDocument.getExternalReference() != null)
                    externalReferences.add(billingDocument.getExternalReference());
                // Responsable on billing
                if (billingDocument.getIsResponsableOnBilling() != null
                        && billingDocument.getIsResponsableOnBilling()
                        && customerOrder.getResponsable() != null)
                    ctx.setVariable("responsableOnBilling", customerOrder.getResponsable().getFirstname() + " "
                            + customerOrder.getResponsable().getLastname());
            }

            if (customerOrder.getAssoAffaireOrders() != null)
                for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                    if (asso.getAffaire() != null && asso.getAffaire().getExternalReference() != null
                            && asso.getAffaire().getExternalReference().length() > 0)
                        externalReferences.add(asso.getAffaire().getExternalReference());

            if (externalReferences.size() > 0)
                ctx.setVariable("externalReference", String.join(" / ", externalReferences));

            ctx.setVariable("customerOrder", customerOrder);

            BigDecimal remainingToPay = invoiceService.getRemainingAmountToPayForInvoice(invoice);
            ArrayList<Payment> invoicePayment = new ArrayList<Payment>();
            if (invoice.getPayments() != null)
                for (Payment payment : invoice.getPayments()) {
                    if (!payment.getIsCancelled())
                        invoicePayment.add(payment);
                }

            if (customerOrder.getPayments() != null)
                for (Payment payment : customerOrder.getPayments())
                    if (!payment.getIsCancelled()) {
                        invoicePayment.add(payment);
                        remainingToPay = remainingToPay.subtract(payment.getPaymentAmount());
                    }

            if (invoicePayment.size() > 0)
                ctx.setVariable("payments", invoicePayment);

            if (invoice.getCustomerOrder() != null && invoice.getCustomerOrder().getRefunds() != null)
                ctx.setVariable("refunds", invoice.getCustomerOrder().getRefunds());

            if (remainingToPay != null && remainingToPay.compareTo(zeroValue) >= 0
                    && remainingToPay.compareTo(BigDecimal.valueOf(Float.parseFloat(payementLimitRefundInEuros))) >= 0)
                ctx.setVariable("remainingToPay", remainingToPay);

            ctx.setVariable("quotation",
                    customerOrder.getQuotations() != null && customerOrder.getQuotations().size() > 0
                            ? customerOrder.getQuotations().get(0)
                            : null);
        } else {
            BigDecimal remainingToPay = invoiceService.getRemainingAmountToPayForInvoice(invoice);
            if (remainingToPay != null && remainingToPay.compareTo(zeroValue) >= 0
                    && remainingToPay.compareTo(BigDecimal.valueOf(Float.parseFloat(payementLimitRefundInEuros))) > 0)
                ctx.setVariable("remainingToPay", remainingToPay);
        }

        LocalDateTime localDate = invoice.getCreatedDate();
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("dd/MM/yyyy");
        ctx.setVariable("invoiceCreatedDate", localDate.format(formatter));
        ctx.setVariable("invoiceDueDate", invoice.getDueDate().format(formatter));

        // Recurring
        if (customerOrder != null)

        {
            if (customerOrder.getRecurringStartDate() != null) {
                ctx.setVariable("recurringStartDate",
                        customerOrder.getRecurringStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                ctx.setVariable("recurringEndDate",
                        customerOrder.getRecurringEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                if (customerOrder.getCustomerOrderParentRecurring() != null)
                    ctx.setVariable("recurringParentId",
                            " - " + customerOrder.getCustomerOrderParentRecurring().getId());
            }
        }
        // Create the HTML body using Thymeleaf and MJML
        String htmlBody = StringEscapeUtils
                .unescapeHtml4(pdfTemplateEngine(true).process("invoice-page", ctx));
        String htmlContent = composeHtml(HEADER_PDF_TEMPLATE, null, htmlBody, new Context());

        try {
            PrintWriter out = new PrintWriter("C:\\uploads\\html.txt");
            out.println(htmlContent);
            out.close();
        } catch (Exception e) {

        }
        File tempFile;
        OutputStream outputStream;
        try {
            tempFile = File.createTempFile("invoice", "pdf");

            outputStream = new FileOutputStream(tempFile);
        } catch (IOException e) {
            throw new OsirisException(e, "Unable to create temp file");
        }
        ITextRenderer renderer = new ITextRenderer();
        XRLog.setLevel(XRLog.CSS_PARSE, Level.SEVERE);

        renderer = fixFontSizeHeaderRendererDisplay(renderer);
        renderer.setDocumentFromString(htmlContent.replaceAll("[\\u0000-\\u001F&&[^\\n\\r\\t]]", " ")
                .replaceAll("&nbsp;", "&#160;")
                .replaceAll("font-size:\\s*0(\\.0+)?(px|pt|em|rem|%)?;", "font-size:1px;"));
        renderer.layout();
        try {
            renderer.createPDF(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (DocumentException | IOException e) {
            throw new OsirisException(e, "Unable to create PDF file for invoice " + invoice.getId());
        }
        return tempFile;
    }

    private ITextRenderer fixFontSizeHeaderRendererDisplay(ITextRenderer renderer) {
        try {
            renderer.getFontResolver().addFont(
                    Objects.requireNonNull(getClass().getResource("/fonts/Roboto/Roboto-Regular.ttf")).getPath(),
                    BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            renderer.getFontResolver().addFont(
                    Objects.requireNonNull(getClass().getResource("/fonts/Roboto/Roboto-Bold.ttf")).getPath(),
                    BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return renderer;
    }

    private List<InvoiceItem> getGroupedInvoiceItemsForDebours(List<InvoiceItem> inInvoiceItems) {
        ArrayList<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
        InvoiceItem invoiceItemTaxable = null;
        InvoiceItem invoiceItemNonTaxable = null;

        if (inInvoiceItems != null)
            for (InvoiceItem invoiceItem : inInvoiceItems) {
                if ((invoiceItem.getIsGifted() == null || invoiceItem.getIsGifted() == false)
                        && invoiceItem.getPreTaxPrice() != null) {
                    if (invoiceItem.getBillingItem().getBillingType().getIsDebour()) {
                        if (invoiceItem.getBillingItem().getBillingType().getIsNonTaxable()) {
                            if (invoiceItemNonTaxable == null) {
                                invoiceItemNonTaxable = invoiceItemService.cloneInvoiceItem(invoiceItem);
                                if (invoiceItemNonTaxable.getDiscountAmount() == null)
                                    invoiceItemNonTaxable.setDiscountAmount(zeroValue);
                            } else {
                                invoiceItemNonTaxable.setPreTaxPrice(
                                        invoiceItemNonTaxable.getPreTaxPrice().add(invoiceItem.getPreTaxPrice()));
                                if (invoiceItem.getDiscountAmount() != null)
                                    invoiceItemNonTaxable.setDiscountAmount(invoiceItemNonTaxable.getDiscountAmount()
                                            .add(invoiceItem.getDiscountAmount()));
                            }
                        } else {
                            if (invoiceItemTaxable == null) {
                                invoiceItemTaxable = invoiceItemService.cloneInvoiceItem(invoiceItem);
                                ;
                                if (invoiceItemTaxable.getDiscountAmount() == null)
                                    invoiceItemTaxable.setDiscountAmount(zeroValue);
                            } else {
                                invoiceItemTaxable.setPreTaxPrice(
                                        invoiceItemTaxable.getPreTaxPrice().add(invoiceItem.getPreTaxPrice()));
                                if (invoiceItem.getDiscountAmount() != null)
                                    invoiceItemTaxable.setDiscountAmount(invoiceItemTaxable.getDiscountAmount()
                                            .add(invoiceItem.getDiscountAmount()));
                            }
                        }
                    } else {
                        invoiceItems.add(invoiceItem);
                    }
                } else {
                    invoiceItems.add(invoiceItem);
                }
            }

        if (invoiceItemTaxable != null) {
            invoiceItemTaxable.setLabel(invoiceItemTaxable.getBillingItem().getBillingType().getLabel());
            invoiceItems.add(invoiceItemTaxable);
        }
        if (invoiceItemNonTaxable != null) {
            invoiceItemNonTaxable.setLabel(invoiceItemNonTaxable.getBillingItem().getBillingType().getLabel());
            invoiceItems.add(invoiceItemNonTaxable);
        }

        return invoiceItems;
    }

    private File addHeaderAndFooterOnPublicationFlag(File pdfFile, Announcement announcement, Boolean isPublicationFlag)
            throws OsirisException {
        String pdfPath = pdfFile.getAbsolutePath();
        File tempPdfFile;
        try {
            tempPdfFile = File.createTempFile("pdfFooterHeader", "Add");
        } catch (IOException e) {
            throw new OsirisException(e, "Impossible to create temp file");
        }
        String pdfPathOut = tempPdfFile.getAbsolutePath();
        float headerPositionX = 5;
        float footerPositionX = 60;

        String announcementDate = "";
        String announcementDepartment = "";
        String announcementNoticeType = "";

        if (announcement.getDepartment() != null)
            announcementDepartment = announcement.getDepartment().getCode() + " - "
                    + announcement.getDepartment().getLabel();

        if (announcement.getNoticeTypeFamily() != null)
            announcementNoticeType += announcement.getNoticeTypeFamily().getLabel();

        if (announcement.getNoticeTypes() != null && announcement.getNoticeTypes().size() > 0)
            announcementNoticeType += " / " + announcement.getNoticeTypes().stream().map(NoticeType::getLabel)
                    .collect(Collectors.joining(" - "));

        if (announcement.getPublicationDate() != null) {
            LocalDate localDate = announcement.getPublicationDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy");
            announcementDate = StringUtils.capitalize(localDate.format(formatter));
        }

        FileInputStream in;
        PdfReader reader;
        try {
            in = new FileInputStream(pdfPath);
            reader = new PdfReader(in);
        } catch (IOException e) {
            throw new OsirisException(e, "Impossible to read input PDF file");
        }

        // Create output PDF
        FileOutputStream out;
        PdfStamper stamper;
        try {
            out = new FileOutputStream(pdfPathOut);
            stamper = new PdfStamper(reader, out);
            XRLog.setLevel(XRLog.CSS_PARSE, Level.SEVERE);
        } catch (DocumentException | IOException e2) {
            throw new OsirisException(e2, "Impossible to create output PDF file");
        }

        // Loop over the pages and add a header to each page
        int n = reader.getNumberOfPages();

        BaseFont baseFontRobotoRegular;
        BaseFont baseFontRobotoBold;

        try {
            baseFontRobotoRegular = BaseFont.createFont("fonts/Roboto/Roboto-Regular.ttf", BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED);
            baseFontRobotoBold = BaseFont.createFont("fonts/Roboto/Roboto-Bold.ttf", BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED);
        } catch (Exception e) {
            throw new OsirisException(e, "Impossible de charger les polices Roboto");
        }

        final BaseColor MJML_COLOR = new BaseColor(48, 59, 77);

        for (int i = 1; i <= n; i++) {
            if (i == 1) {

                Font blueFontTitle = new Font(baseFontRobotoBold, 20, Font.NORMAL, MJML_COLOR);
                Font blueFontDescription = new Font(baseFontRobotoRegular, 10, Font.NORMAL, MJML_COLOR);
                Font blueFontDateDept = new Font(baseFontRobotoRegular, 12, Font.NORMAL, MJML_COLOR);
                Font blueFontNoticeType = new Font(baseFontRobotoBold, 14, Font.NORMAL, MJML_COLOR);

                PdfPTable tableHeader = new PdfPTable(2);
                try {
                    tableHeader.setWidths(new float[] { 5f, 95f });
                } catch (DocumentException e) {
                    throw new OsirisException(e, "Wrong columns sizes for PDF header");
                }
                tableHeader.setTotalWidth((PageSize.A4.getWidth() - (headerPositionX * 2)) * 0.98f);
                tableHeader.setLockedWidth(true);

                // --- Column 1 : Logo ---
                final PdfPCell logoCell = new PdfPCell();
                logoCell.setBorder(Rectangle.NO_BORDER);
                logoCell.setPadding(0);
                logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

                Image logoImage;
                try {
                    byte[] imageBytes;
                    imageBytes = IOUtils.toByteArray(new ClassPathResource("images/logo.png").getInputStream());
                    logoImage = Image.getInstance(imageBytes);

                    // resizing logo image
                    float desiredWidth = 60f;
                    float scaleFactor = desiredWidth / logoImage.getWidth();
                    logoImage.scalePercent(scaleFactor * 100);
                    logoCell.addElement(logoImage);

                } catch (Exception e) {
                    throw new OsirisException(e, "Impossible d'ajouter le logo au PDF");
                }
                tableHeader.addCell(logoCell);

                // --- Column 2 : Text and title ---
                // New interlocked Table inside first Table
                PdfPTable textTable = new PdfPTable(1);
                textTable.setWidthPercentage(100);
                textTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                textTable.getDefaultCell().setPadding(1);

                final PdfPCell titleCell = new PdfPCell(
                        new Phrase("Attestation de parution sur www.JSS.fr", blueFontTitle));
                titleCell.setBorder(Rectangle.NO_BORDER);
                titleCell.setPaddingBottom(3);
                textTable.addCell(titleCell);

                if (isPublicationFlag) {
                    String subtitleText = "Service de Presse En Ligne d'informations Générales, Juridiques, Judiciaires et Techniques,"
                            + " habilité à publier les annonces légales dans les départements 75, 78, 91, 92, 93, 94 et 95";
                    final PdfPCell subtitleCell = new PdfPCell(new Phrase(subtitleText, blueFontDescription));
                    subtitleCell.setBorder(Rectangle.NO_BORDER);
                    subtitleCell.setPaddingBottom(3);
                    textTable.addCell(subtitleCell);

                    final PdfPCell dateDeptCell = new PdfPCell();
                    dateDeptCell.setBorder(Rectangle.NO_BORDER);
                    dateDeptCell.setPaddingBottom(3);
                    Paragraph dateDeptPara = new Paragraph();
                    dateDeptPara.add(new Chunk(announcementDate, blueFontDateDept));
                    dateDeptPara.add(new Chunk("\n", blueFontDateDept));
                    dateDeptPara.add(new Chunk(announcementDepartment, blueFontDateDept));
                    dateDeptPara.add(new Chunk("\n", blueFontDateDept));
                    dateDeptCell.addElement(dateDeptPara);
                    textTable.addCell(dateDeptCell);

                    final PdfPCell noticeTypeCell = new PdfPCell(
                            new Phrase(announcementNoticeType, blueFontNoticeType));
                    noticeTypeCell.setBorder(Rectangle.NO_BORDER);
                    noticeTypeCell.setPaddingBottom(3);
                    textTable.addCell(noticeTypeCell);

                } else {
                    String subtitleText = "Pour le " + announcementDate;
                    final PdfPCell subtitleCell = new PdfPCell(new Phrase(subtitleText, blueFontDateDept));
                    subtitleCell.setBorder(Rectangle.NO_BORDER);
                    subtitleCell.setPaddingBottom(3);
                    textTable.addCell(subtitleCell);

                    String descriptionText = "Service de Presse en ligne habilité à publier les annonces légales dans les "
                            + "départements 75, 78, 91, 92, 93, 94 et 95";
                    final PdfPCell descriptionCell = new PdfPCell(new Phrase(descriptionText, blueFontDescription));
                    descriptionCell.setBorder(Rectangle.NO_BORDER);
                    descriptionCell.setPaddingBottom(3);
                    textTable.addCell(descriptionCell);

                    // 3rd and 4th line to create a fake dateDeptCell and noticeTypeCell with a
                    // minimal height in order to keep display straight and clean
                    // otherwise logo and text are not aligned
                    Paragraph p3 = new Paragraph(11, " "); // 11 points leading pour la police de 11 points
                    final PdfPCell paddingCell3 = new PdfPCell(p3);
                    paddingCell3.setBorder(Rectangle.NO_BORDER);
                    paddingCell3.setPaddingBottom(3);
                    textTable.addCell(paddingCell3);

                    Paragraph p4 = new Paragraph(13, " "); // 13 points leading pour la police de 13 points
                    final PdfPCell paddingCell4 = new PdfPCell(p4);
                    paddingCell4.setBorder(Rectangle.NO_BORDER);
                    paddingCell4.setPaddingBottom(3);
                    textTable.addCell(paddingCell4);
                }

                // Cell to put second text Table
                final PdfPCell textContainerCell = new PdfPCell(textTable);
                textContainerCell.setBorder(Rectangle.NO_BORDER);
                textContainerCell.setBorderWidthLeft(2f);
                textContainerCell.setBorderColorLeft(MJML_COLOR);
                textContainerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                textContainerCell.setPaddingLeft(5f);
                tableHeader.addCell(textContainerCell);

                float startPositionX = headerPositionX;
                tableHeader.writeSelectedRows(0, -1, startPositionX, PageSize.A4.getHeight() - 40,
                        stamper.getOverContent(i));

            }
            // add footer
            PdfPTable tableFooter = new PdfPTable(1);
            try {
                tableFooter.setWidths(new int[] { 100 });
            } catch (DocumentException e) {
                throw new OsirisException(e, "Wrong columns sizes for PDF footer");
            }
            tableFooter.setTotalWidth(PageSize.A4.getWidth() - (footerPositionX * 2));
            tableFooter.setLockedWidth(true);
            tableFooter.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            Font footerFontRegular = new Font(baseFontRobotoRegular, 10, Font.NORMAL, MJML_COLOR);
            Font footerFontBold = new Font(baseFontRobotoBold, 10, Font.NORMAL, MJML_COLOR);

            // --- Only one Cell
            Paragraph detailsParagraph = new Paragraph();
            detailsParagraph.setAlignment(Element.ALIGN_CENTER);
            detailsParagraph.setLeading(14f);
            detailsParagraph
                    .add(new Chunk("SPPS - Société de Publications et de Publicité pour les Sociétés", footerFontBold));
            detailsParagraph.add(new Chunk("\n", footerFontRegular)); // Saut de ligne

            detailsParagraph.add(new Chunk("SAS au capital de 216 000 Euros", footerFontRegular));
            detailsParagraph.add(new Chunk("\n", footerFontRegular));

            detailsParagraph.add(new Chunk("Siret", footerFontBold));
            detailsParagraph.add(new Chunk(" 552 074 627 00043  –  ", footerFontRegular));
            detailsParagraph.add(new Chunk("TVA IC", footerFontBold));
            detailsParagraph.add(new Chunk(" : FR 12552074627", footerFontRegular));
            detailsParagraph.add(new Chunk("\n", footerFontRegular));

            detailsParagraph.add(new Chunk("IBAN", footerFontBold));
            detailsParagraph.add(new Chunk(" : FR76 3000 4007 9900 0257 1438 960  ", footerFontRegular));
            detailsParagraph.add(new Chunk("BIC", footerFontBold));
            detailsParagraph.add(new Chunk(" : BNPAFRPPXXX", footerFontRegular));
            detailsParagraph.add(new Chunk("\n", footerFontRegular));

            detailsParagraph.add(new Chunk("Présidente", footerFontBold));
            detailsParagraph.add(new Chunk(" : Myriam de Montis", footerFontRegular));

            final PdfPCell detailsCell = new PdfPCell(detailsParagraph);
            detailsCell.setBorder(Rectangle.NO_BORDER);
            detailsCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            detailsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableFooter.addCell(detailsCell);

            float shiftedFooterPositionX = footerPositionX - 10f;
            float safeFooterTopY = 80f;
            tableFooter.writeSelectedRows(0, -1, shiftedFooterPositionX, safeFooterTopY, stamper.getOverContent(i));
        }

        try {
            stamper.close();
        } catch (DocumentException | IOException e) {
            throw new OsirisException(e, "Impossible to close PDF File stamper");
        }
        reader.close();

        return tempPdfFile;
    }

    public File generateDomiciliationContract(Provision provision)
            throws OsirisException, OsirisClientMessageException {
        final Context ctx = new Context();

        String template = Domiciliation.DOMICILIATION_CONTRACT_TEMPLATE_BILINGUAL;
        Domiciliation domiciliation = provision.getDomiciliation();
        if (provision.getDomiciliation().getLanguage().getId()
                .equals(constantService.getLanguageFrench().getId()))
            template = Domiciliation.DOMICILIATION_CONTRACT_TEMPLATE_FRENCH;

        ctx.setVariable("currentDate", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        ctx.setVariable("currentDateEnglish",
                LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)));
        ctx.setVariable("jssDomiciliationAgreementNumber", jssDomiciliationAgreementNumber);
        ctx.setVariable("jssDomiciliationAgreementDate", jssDomiciliationAgreementDate);
        ctx.setVariable("provision", provision);
        ctx.setVariable("affaire", provision.getService().getAssoAffaireOrder().getAffaire());
        ctx.setVariable("isRegistered",
                provision.getService().getAssoAffaireOrder().getAffaire().getIsUnregistered() == null
                        || !provision.getService().getAssoAffaireOrder().getAffaire().getIsUnregistered());
        ctx.setVariable("isSuccursale",
                provision.getService().getAssoAffaireOrder().getAffaire().getIsMainOffice() == null
                        || !provision.getService().getAssoAffaireOrder().getAffaire().getIsMainOffice());
        ctx.setVariable("domiciliationStartDate",
                provision.getService().getAssoAffaireOrder().getCustomerOrder().getRecurringPeriodStartDate()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        ctx.setVariable("domiciliationStartDateEnglish", provision.getService().getAssoAffaireOrder().getCustomerOrder()
                .getRecurringPeriodStartDate().format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)));

        ctx.setVariable("domiciliation", domiciliation);
        if (domiciliation.getIsLegalPerson() == null || !domiciliation.getIsLegalPerson()) {
            ctx.setVariable("domiciliationLegalPersonBirthday", domiciliation.getLegalGardianBirthdate()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            ctx.setVariable("domiciliationLegalPersonBirthdayEnglish",
                    domiciliation.getLegalGardianBirthdate()
                            .format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)));
        }

        ctx.setVariable("mailDestinationLabel", getMailDestinationLabel(domiciliation, false));
        ctx.setVariable("mailDestinationLabelEnglish", getMailDestinationLabel(domiciliation, true));

        String activityDescriptionEnglish = "";
        if (domiciliation.getActivityDescription() != null)
            activityDescriptionEnglish = translationService
                    .translateTextToEnglish(domiciliation.getActivityDescription());
        ctx.setVariable("activityDescriptionEnglish", activityDescriptionEnglish);

        String legalGardianJobEnglish = "";
        if (domiciliation.getLegalGardianJob() != null)
            legalGardianJobEnglish = translationService.translateTextToEnglish(domiciliation.getLegalGardianJob());
        ctx.setVariable("legalGardianJobEnglish", legalGardianJobEnglish);

        // Billing item
        String monthlyBillingAmount = "";
        if (provision.getInvoiceItems() != null)
            for (InvoiceItem invoiceItem : provision.getInvoiceItems()) {
                if (invoiceItem.getBillingItem().getBillingType().getId()
                        .equals(constantService.getBillingTypeDomiciliationContractTypeKeepMail().getId())
                        || invoiceItem.getBillingItem().getBillingType().getId()
                                .equals(constantService.getBillingTypeDomiciliationContractTypeRouteEmail().getId())
                        || invoiceItem.getBillingItem().getBillingType().getId()
                                .equals(constantService.getBillingTypeDomiciliationContractTypeRouteEmailAndMail()
                                        .getId())
                        || invoiceItem.getBillingItem().getBillingType().getId()
                                .equals(constantService.getBillingTypeDomiciliationContractTypeRouteMail().getId()))
                    monthlyBillingAmount = invoiceItem.getBillingItem().getPreTaxPrice() + "";
            }
        ctx.setVariable("monthlyBillingAmount", monthlyBillingAmount);

        // Create the HTML body using Thymeleaf
        String htmlBody = StringEscapeUtils
                .unescapeHtml4(pdfTemplateEngine(true).process(template, ctx));
        final String htmlContent = composeHtml(HEADER_PDF_TEMPLATE, FOOTER_DOMICILIATION, htmlBody, ctx);

        File tempFile;
        OutputStream outputStream;
        try {
            tempFile = File.createTempFile("domiciliation-contract", "pdf");
            outputStream = new FileOutputStream(tempFile);
        } catch (IOException e) {
            throw new OsirisException(e, "Unable to create temp file");
        }
        ITextRenderer renderer = new ITextRenderer();
        XRLog.setLevel(XRLog.CSS_PARSE, Level.SEVERE);
        renderer = fixFontSizeHeaderRendererDisplay(renderer);
        renderer.setDocumentFromString(
                htmlContent.replaceAll("\\p{C}", " ").replaceAll("&", "<![CDATA[&]]>").replaceAll("<col (.*?)>", "")
                        .replaceAll("line-height: normal",
                                "line-height: normal;padding:0;margin:0")
                        .replaceAll("font-size:\\s*0(\\.0+)?(px|pt|em|rem|%)?;", "font-size:1px;"));
        renderer.layout();
        try {
            renderer.createPDF(outputStream);
            outputStream.close();
        } catch (DocumentException | IOException e) {
            throw new OsirisException(e, "Unable to create PDF file for domiciliation contracts");
        }
        return tempFile;
    }

    private String getMailDestinationLabel(Domiciliation domiciliation, boolean englishLangage) throws OsirisException {
        String mailDestinationLabel = "";
        if (domiciliation.getDomiciliationContractType().getId()
                .equals(constantService.getDomiciliationContractTypeRouteEmailAndMail().getId())
                || domiciliation.getDomiciliationContractType().getId()
                        .equals(constantService.getDomiciliationContractTypeRouteMail().getId())) {
            // Postal
            mailDestinationLabel = englishLangage ? "; Sending of mail " : "; Envoi du courrier ";
            if (domiciliation.getMailRedirectionType().getId()
                    .equals(constantService.getMailRedirectionTypeActivity().getId())) {
                mailDestinationLabel += englishLangage ? "at " : "à ";
                mailDestinationLabel += domiciliation.getActivityMailRecipient() != null
                        ? domiciliation.getActivityMailRecipient()
                        : "";
                mailDestinationLabel += " ";
                mailDestinationLabel += domiciliation.getActivityAddress() != null ? domiciliation.getActivityAddress()
                        : "";
                mailDestinationLabel += " ";
                mailDestinationLabel += domiciliation.getActivityPostalCode() != null
                        ? domiciliation.getActivityPostalCode()
                        : "";
                mailDestinationLabel += " ";
                mailDestinationLabel += domiciliation.getActivityCity() != null
                        ? domiciliation.getActivityCity().getLabel()
                        : "";
                mailDestinationLabel += " ";
                mailDestinationLabel += domiciliation.getAcitivityCedexComplement() != null
                        ? domiciliation.getAcitivityCedexComplement()
                        : "";
                mailDestinationLabel += " ";
                mailDestinationLabel += domiciliation.getActivityCountry() != null
                        ? domiciliation.getActivityCountry().getLabel()
                        : "";
            } else if (domiciliation.getMailRedirectionType().getId()
                    .equals(constantService.getMailRedirectionTypeOther().getId())) {
                mailDestinationLabel += englishLangage ? "at " : "à ";
                mailDestinationLabel += domiciliation.getMailRecipient() != null ? domiciliation.getMailRecipient()
                        : "";
                mailDestinationLabel += " ";
                mailDestinationLabel += domiciliation.getAddress() != null ? domiciliation.getAddress() : "";
                mailDestinationLabel += " ";
                mailDestinationLabel += domiciliation.getPostalCode() != null ? domiciliation.getPostalCode() : "";
                mailDestinationLabel += " ";
                mailDestinationLabel += domiciliation.getCity() != null ? domiciliation.getCity().getLabel() : "";
                mailDestinationLabel += " ";
                mailDestinationLabel += domiciliation.getCedexComplement() != null ? domiciliation.getCedexComplement()
                        : "";
                mailDestinationLabel += " ";
                mailDestinationLabel += domiciliation.getCountry() != null ? domiciliation.getCountry().getLabel() : "";
            } else if (domiciliation.getMailRedirectionType().getId()
                    .equals(constantService.getMailRedirectionTypeLegalGuardian().getId())) {
                mailDestinationLabel += englishLangage ? "at " : "à ";
                mailDestinationLabel += domiciliation.getLegalGardianDenomination() != null
                        ? domiciliation.getLegalGardianDenomination()
                        : "";
                mailDestinationLabel += " ";
                mailDestinationLabel += domiciliation.getLegalGardianCivility() != null
                        ? domiciliation.getLegalGardianCivility().getLabel()
                        : "";
                mailDestinationLabel += " ";
                mailDestinationLabel += domiciliation.getLegalGardianLastname() != null
                        ? domiciliation.getLegalGardianLastname()
                        : "";
                mailDestinationLabel += " ";
                mailDestinationLabel += domiciliation.getLegalGardianFirstname() != null
                        ? domiciliation.getLegalGardianFirstname()
                        : "";
                mailDestinationLabel += " ";
                mailDestinationLabel += domiciliation.getLegalGardianMailRecipient() != null
                        ? domiciliation.getLegalGardianMailRecipient()
                        : "";
                mailDestinationLabel += " ";
                mailDestinationLabel += domiciliation.getLegalGardianAddress() != null
                        ? domiciliation.getLegalGardianAddress()
                        : "";
                mailDestinationLabel += " ";
                mailDestinationLabel += domiciliation.getLegalGardianPostalCode() != null
                        ? domiciliation.getLegalGardianPostalCode()
                        : "";
                mailDestinationLabel += " ";
                mailDestinationLabel += domiciliation.getLegalGardianCity() != null
                        ? domiciliation.getLegalGardianCity().getLabel()
                        : "";
                mailDestinationLabel += " ";
                mailDestinationLabel += domiciliation.getLegalGardianCedexComplement() != null
                        ? domiciliation.getLegalGardianCedexComplement()
                        : "";
                mailDestinationLabel += " ";
                mailDestinationLabel += domiciliation.getLegalGardianCountry() != null
                        ? domiciliation.getLegalGardianCountry().getLabel()
                        : "";
            }
        }
        if (domiciliation.getDomiciliationContractType().getId()
                .equals(constantService.getDomiciliationContractTypeRouteEmailAndMail().getId())
                || domiciliation.getDomiciliationContractType().getId()
                        .equals(constantService.getDomiciliationContractTypeRouteEmail().getId())) {
            // Mail
            ArrayList<String> mailList = new ArrayList<String>();
            if (domiciliation.getMailRedirectionType().getId()
                    .equals(constantService.getMailRedirectionTypeActivity().getId())) {
                if (domiciliation.getActivityMails() != null)
                    for (Mail mail : domiciliation.getActivityMails())
                        mailList.add(mail.getMail());
            } else if (domiciliation.getMailRedirectionType().getId()
                    .equals(constantService.getMailRedirectionTypeOther().getId())) {
                if (domiciliation.getMails() != null)
                    for (Mail mail : domiciliation.getMails())
                        mailList.add(mail.getMail());
            } else if (domiciliation.getMailRedirectionType().getId()
                    .equals(constantService.getMailRedirectionTypeLegalGuardian().getId())) {
                if (domiciliation.getLegalGardianMails() != null)
                    for (Mail mail : domiciliation.getLegalGardianMails())
                        mailList.add(mail.getMail());
            }
            if (mailList.size() > 0)
                mailDestinationLabel += (englishLangage ? "; Sending of email at " : "; Envoi par mail à ")
                        + String.join(", ", mailList);
        }
        return mailDestinationLabel;
    }

    public File generateRegistrationActPdf(Provision provision) throws OsirisException {
        final Context ctx = new Context();

        ctx.setVariable("currentDate", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        if (provision != null) {
            ctx.setVariable("provision", provision);
            if (provision.getService() != null && provision.getService().getAssoAffaireOrder() != null)
                ctx.setVariable("customerOrder", provision.getService().getAssoAffaireOrder().getCustomerOrder());
        }
        final String htmlContent = StringEscapeUtils
                .unescapeHtml4(pdfTemplateEngine(false).process("registration-act", ctx));

        File tempFile;
        OutputStream outputStream;
        try {
            tempFile = File.createTempFile("Enregistrement d'acte", "pdf");
            outputStream = new FileOutputStream(tempFile);
        } catch (IOException e) {
            throw new OsirisException(e, "Unable to create temp file");
        }
        ITextRenderer renderer = new ITextRenderer();
        XRLog.setLevel(XRLog.CSS_PARSE, Level.SEVERE);
        renderer.setDocumentFromString(
                htmlContent.replaceAll("\\p{C}", " ").replaceAll("&", "<![CDATA[&]]>").replaceAll("<col (.*?)>", "")
                        .replaceAll("line-height: normal",
                                "line-height: normal;padding:0;margin:0"));
        renderer.layout();
        try {
            renderer.createPDF(outputStream);
            outputStream.close();
        } catch (DocumentException | IOException e) {
            throw new OsirisException(e, "Unable to create PDF file for registration act");
        }
        return tempFile;
    }

    public File generateTrackingSheetPdf(Provision provision) throws OsirisException {
        final Context ctx = new Context();

        ctx.setVariable("currentDate", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        if (provision != null) {
            ctx.setVariable("provision", provision);

            if (provision.getAssignedTo() != null)
                ctx.setVariable("employee", provision.getAssignedTo().getFirstname() + ' ' +
                        provision.getAssignedTo().getLastname() + " / " + provision.getAssignedTo().getMail());

            if (provision.getService() != null && provision.getService().getAssoAffaireOrder() != null
                    && provision.getService().getAssoAffaireOrder().getAffaire() != null) {
                if (provision.getService().getAssoAffaireOrder().getAffaire().getDenomination() != null)
                    ctx.setVariable("affaireDenomination",
                            provision.getService().getAssoAffaireOrder().getAffaire().getDenomination());
                else
                    ctx.setVariable("affaireDenomination",
                            provision.getService().getAssoAffaireOrder().getAffaire().getFirstname() + ' '
                                    + provision.getService().getAssoAffaireOrder().getAffaire().getLastname());
            }
            if (provision.getService().getAssoAffaireOrder().getAffaire().getCompetentAuthority() != null)
                ctx.setVariable("affaireCompetentAuthority",
                        provision.getService().getAssoAffaireOrder().getAffaire().getCompetentAuthority().getLabel());
        }

        final String htmlContent = StringEscapeUtils
                .unescapeHtml4(pdfTemplateEngine(false).process("registration-act", ctx));

        File tempFile;
        OutputStream outputStream;
        try {
            tempFile = File.createTempFile("Fiche de suivi", "pdf");
            outputStream = new FileOutputStream(tempFile);
        } catch (IOException e) {
            throw new OsirisException(e, "Unable to create temp file");
        }
        ITextRenderer renderer = new ITextRenderer();
        XRLog.setLevel(XRLog.CSS_PARSE, Level.SEVERE);
        renderer.setDocumentFromString(
                htmlContent.replaceAll("\\p{C}", " ").replaceAll("&", "<![CDATA[&]]>").replaceAll("<col (.*?)>", "")
                        .replaceAll("line-height: normal",
                                "line-height: normal;padding:0;margin:0"));
        renderer.layout();
        try {
            renderer.createPDF(outputStream);
            outputStream.close();
        } catch (DocumentException | IOException e) {
            throw new OsirisException(e, "Unable to create PDF file for tracking sheet document");
        }
        return tempFile;
    }

    public File generateGenericFromHtml(String htmlContent, Integer mailId)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
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
                    htmlContent.replaceAll("\\p{C}", " ").replaceAll("&#160;", " ")
                            .replaceAll("&(?![a-zA-Z#0-9]+;)", "&amp;")
                            .replaceAll("font-size:\\s*0(px|pt|em|rem)?", "font-size:1px"));

            renderer.setScaleToFit(true);
            renderer.layout();
            renderer.createPDF(outputStream);

            outputStream.close();
        } catch (Exception e) {
            throw new OsirisException(e, "Unable to create PDF file for mail " + mailId);
        }
        return tempFile;
    }
}
