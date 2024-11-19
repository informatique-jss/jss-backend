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
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.util.XRLog;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfImage;
import com.itextpdf.text.pdf.PdfName;
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
import com.jss.osiris.modules.osiris.invoicing.service.PaymentService;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.VatService;
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
    VatService vatService;

    @Autowired
    ProvisionService provisionService;

    @Autowired
    PaymentService paymentService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    InvoiceItemService invoiceItemService;

    @Autowired
    TranslationService translationService;

    @Transactional(rollbackFor = Exception.class)
    public File generatePublicationForAnnouncement(Announcement announcement, Provision provision,
            boolean isPublicationFlag,
            boolean isPublicationReceipt, boolean isProofReading) throws OsirisException {
        // To avoid proxy error
        provision = provisionService.getProvision(provision.getId());

        File tempFile;
        if (!announcement.getIsComplexAnnouncement()) {
            // Generate announcement PDF
            final Context ctx = new Context();

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
            final String htmlContent = StringEscapeUtils
                    .unescapeHtml4(mailHelper.emailTemplateEngine().process("publication-flag", ctx));

            OutputStream outputStream;
            File tempFile2;
            try {
                tempFile2 = File.createTempFile("Témoin de publication", "pdf");
                outputStream = new FileOutputStream(tempFile2);
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
                throw new OsirisException(e,
                        "Unable to create publication flag PDF file for announcement " + announcement.getId());
            }

            if (isPublicationFlag)
                tempFile = addHeaderAndFooterOnPublicationFlag(tempFile2, announcement);
            else
                tempFile = addHeaderOnPublicationReceipt(tempFile2, announcement, isPublicationReceipt);

            tempFile2.delete();

        } else {
            // Get announcement PDF
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

            if (isPublicationFlag)
                tempFile = addHeaderAndFooterOnPublicationFlag(complexePdf, announcement);
            else
                tempFile = addHeaderOnPublicationReceipt(complexePdf, announcement, isPublicationReceipt);
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
                    signatureEmployee = asso.getAssignedTo();
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
        String htmlContent = StringEscapeUtils
                .unescapeHtml4(mailHelper.emailTemplateEngine().process("letter-page", ctx));

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
        renderer.setDocumentFromString(
                htmlContent.replaceAll("\\p{C}", " ").replaceAll("&", "<![CDATA[&]]>").replaceAll("<col (.*?)>", "")
                        .replaceAll("line-height: normal",
                                "line-height: normal;padding:0;margin:0"));
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
        } else if (responsable != null) {
            ctx.setVariable("denomination",
                    (responsable.getFirstname() + " " + responsable.getLastname()));
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
        final String htmlContent = mailHelper.emailTemplateEngine().process("billing-closure-receipt", ctx);

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
        renderer.setDocumentFromString(htmlContent.replaceAll("\\p{C}", " "));
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
        ctx.setVariable("quotation", quotation);
        ctx.setVariable("quotationCreatedDate", quotation.getCreatedDate().format(DateTimeFormatter
                .ofPattern("dd/MM/yyyy")));
        ctx.setVariable("endOfYearDateString",
                LocalDate.now().withMonth(12).withDayOfMonth(31).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        mailHelper.setQuotationPrice(quotation, ctx);

        final String htmlContent = StringEscapeUtils
                .unescapeHtml4(mailHelper.emailTemplateEngine().process("quotation-page", ctx));
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
        renderer.setDocumentFromString(htmlContent.replaceAll("\\p{C}", " ").replaceAll("&", "<![CDATA[&]]>"));
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

        // Group debouts for asso invoice item debours
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

        ctx.setVariable("preTaxPriceTotalWithDicount",
                invoiceHelper.getPreTaxPriceTotal(invoice).subtract(invoiceHelper.getDiscountTotal(invoice)) != null
                        && invoiceHelper.getDiscountTotal(invoice).multiply(oneHundredValue).setScale(0)
                                .divide(oneHundredValue).compareTo(zeroValue) > 0
                                        ? invoiceHelper.getDiscountTotal(invoice)
                                        : zeroValue);
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
                invoiceHelper.getPriceTotal(invoice).multiply(oneHundredValue).setScale(0, RoundingMode.HALF_UP)
                        .divide(oneHundredValue).setScale(2, RoundingMode.HALF_UP));
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
            Document billingDocument = documentService.getDocumentByDocumentType(customerOrder.getDocuments(),
                    constantService.getDocumentTypeBilling());
            if (billingDocument != null) {
                if (billingDocument.getExternalReference() != null)
                    ctx.setVariable("externalReference", billingDocument.getExternalReference());

                // Responsable on billing
                if (billingDocument.getIsResponsableOnBilling() != null && billingDocument.getIsResponsableOnBilling()
                        && customerOrder.getResponsable() != null)
                    ctx.setVariable("responsableOnBilling", customerOrder.getResponsable().getFirstname() + " "
                            + customerOrder.getResponsable().getLastname());

            }

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
        if (customerOrder != null) {
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
        // Create the HTML body using Thymeleaf
        final String htmlContent = StringEscapeUtils
                .unescapeHtml4(mailHelper.emailTemplateEngine().process("invoice-page", ctx));

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
        renderer.setDocumentFromString(htmlContent.replaceAll("\\p{C}", " ").replaceAll("&", "<![CDATA[&]]>"));
        renderer.layout();
        try {
            renderer.createPDF(outputStream);
            outputStream.close();
        } catch (DocumentException | IOException e) {
            throw new OsirisException(e, "Unable to create PDF file for invoice " + invoice.getId());
        }
        return tempFile;
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

    private File addHeaderAndFooterOnPublicationFlag(File pdfFile, Announcement announcement) throws OsirisException {
        String pdfPath = pdfFile.getAbsolutePath();
        File tempPdfFile;
        try {
            tempPdfFile = File.createTempFile("pdfFooterHeader", "Add");
        } catch (IOException e) {
            throw new OsirisException(e, "Impossible to create temp file");
        }
        String pdfPathOut = tempPdfFile.getAbsolutePath();
        float headerPositionX = 60;
        float headerPositionY = PageSize.A4.getHeight() - 25;
        float footerPositionX = 60;
        float footerPositionY = 10;

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

        Font blueFont = new Font(FontFamily.TIMES_ROMAN);
        blueFont.setColor(0, 32, 96);
        blueFont.setSize(10);

        for (int i = 1; i <= n; i++) {

            if (i == 1) {
                PdfPTable table = new PdfPTable(1);
                table.setTotalWidth(PageSize.A4.getWidth() - (headerPositionX * 2));
                table.setLockedWidth(true);
                table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                table.getDefaultCell().setBorderColor(new BaseColor(0, 32, 96));

                // Title
                Font blueFontTitle = new Font(FontFamily.TIMES_ROMAN);
                blueFontTitle.setColor(0, 32, 96);
                blueFontTitle.setSize(30);

                final PdfPCell titleCell = new PdfPCell(new Phrase("www.JSS.fr", blueFontTitle));
                titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                titleCell.setBorderWidth(0);
                table.addCell(titleCell);

                // Subtitle
                Font blueFontSubTitle = new Font(FontFamily.TIMES_ROMAN);
                blueFontSubTitle.setColor(0, 32, 96);
                blueFontSubTitle.setSize(8);

                final PdfPCell subtitleCell = new PdfPCell(new Phrase(
                        "Service de Presse En Ligne d'informations Générales, Juridiques, Judiciaires et Techniques, habilité à publier les annonces légales",
                        blueFontSubTitle));
                subtitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                subtitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                subtitleCell.setBorderWidth(0);
                table.addCell(subtitleCell);

                final PdfPCell subtitleCell2 = new PdfPCell(
                        new Phrase("dans les départements 75, 78, 91, 92, 93, 94 et 95", blueFontSubTitle));
                subtitleCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                subtitleCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
                subtitleCell2.setBorderWidth(0);
                table.addCell(subtitleCell2);

                // Type
                Font blueFontType = new Font(FontFamily.TIMES_ROMAN);
                blueFontType.setColor(0, 32, 96);
                blueFontType.setSize(12);

                final PdfPCell typeCell = new PdfPCell(new Phrase("Témoin de Publication", blueFontType));
                typeCell.setPaddingTop(8);
                typeCell.setPaddingBottom(8);
                typeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                typeCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                typeCell.setBorderWidth(0);
                table.addCell(typeCell);

                // Nested table informations
                PdfPTable tableInformations = new PdfPTable(3);
                tableInformations.setTotalWidth(PageSize.A4.getWidth() - (headerPositionX * 2));
                tableInformations.setLockedWidth(true);
                tableInformations.getDefaultCell().setBorder(Rectangle.BOX);
                tableInformations.getDefaultCell().setBorderColor(new BaseColor(0, 32, 96));

                final PdfPCell dateCell = new PdfPCell(new Phrase(announcementDate, blueFont));
                dateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                dateCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                dateCell.setBorderWidthRight(0);
                dateCell.setBorderColor(new BaseColor(0, 32, 96));
                tableInformations.addCell(dateCell);

                final PdfPCell dummyCell = new PdfPCell(new Phrase("", blueFont));
                dummyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                dummyCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                dummyCell.setBorderWidthRight(0);
                dummyCell.setBorderWidthLeft(0);
                dummyCell.setBorderColor(new BaseColor(0, 32, 96));
                tableInformations.addCell(dummyCell);

                final PdfPCell departmentCell = new PdfPCell(new Phrase(announcementDepartment, blueFont));
                departmentCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                departmentCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                departmentCell.setBorderWidthLeft(0);
                departmentCell.setBorderColor(new BaseColor(0, 32, 96));
                tableInformations.addCell(departmentCell);

                table.addCell(tableInformations);

                // Notice type
                final PdfPCell noticeTypeCell = new PdfPCell(new Phrase(announcementNoticeType, blueFont));
                noticeTypeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                noticeTypeCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                noticeTypeCell.setBorderWidth(0);
                noticeTypeCell.setPaddingBottom(8);
                table.addCell(noticeTypeCell);

                // Separator
                final PdfPCell separatorCell = new PdfPCell(new Phrase("", blueFont));
                separatorCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                separatorCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                separatorCell.setBorderWidthBottom(1);
                separatorCell.setBorderColor(new BaseColor(0, 32, 96));
                table.addCell(separatorCell);

                table.writeSelectedRows(0, -1, headerPositionX, headerPositionY, stamper.getOverContent(i));

                Image image;
                // Add QR code
                try {
                    image = Image
                            .getInstance(qrCodeHelper
                                    .getQrCode("https://www.jss.fr/Annonce-publiee.awp?P1=" + announcement.getId() + "",
                                            60));
                    PdfImage stream = new PdfImage(image, "", null);
                    stream.put(new PdfName("QRCode"), new PdfName("P1"));
                    image.setDirectReference(stamper.getWriter().addToBody(stream).getIndirectReference());
                    image.setAbsolutePosition(PageSize.A4.getWidth() - 60 - 10, headerPositionY - 60);
                    stamper.getOverContent(i).addImage(image);
                } catch (Exception e) {
                    throw new OsirisException(e, "Impossible to add QR Code on PDF");
                }
            }
            // add footer
            PdfPTable tableFooter = new PdfPTable(2);
            try {
                tableFooter.setWidths(new int[] { 90, 10 });
            } catch (DocumentException e) {
                throw new OsirisException(e, "Wrong columns sizes for PDF");
            }
            tableFooter.setTotalWidth(PageSize.A4.getWidth() - (footerPositionX * 2));
            tableFooter.setLockedWidth(true);
            tableFooter.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            // Separator
            final PdfPCell separatorCell = new PdfPCell(new Phrase("", blueFont));
            separatorCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            separatorCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            separatorCell.setBorderWidthTop(1);
            separatorCell.setBorderColor(new BaseColor(0, 32, 96));
            tableFooter.addCell(separatorCell);

            final PdfPCell separatorCell2 = new PdfPCell(new Phrase("", blueFont));
            separatorCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            separatorCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            separatorCell2.setBorderWidthTop(1);
            separatorCell2.setBorderColor(new BaseColor(0, 32, 96));
            tableFooter.addCell(separatorCell2);

            // Details 1

            Font footerFont = new Font(FontFamily.TIMES_ROMAN);
            footerFont.setColor(0, 32, 96);
            footerFont.setSize(8);

            final PdfPCell detailsCell1 = new PdfPCell(new Phrase(
                    "https://www.jss.fr - Service de Presse En Ligne habilité pour les départements de 75, 78, 91, 92, 93, 94 et 95",
                    footerFont));
            detailsCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            detailsCell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            detailsCell1.setBorderWidth(0);
            tableFooter.addCell(detailsCell1);

            final PdfPCell pageCell = new PdfPCell(new Phrase(String.format("%d / %d", i, n), footerFont));
            pageCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pageCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            pageCell.setBorderWidth(0);
            tableFooter.addCell(pageCell);

            final PdfPCell detailsCell2 = new PdfPCell(new Phrase(
                    "8 rue Saint Augustin - 75002 PARIS - Téléphone : 01 47 03 10 10 - E-mail : annonces@jss.fr",
                    footerFont));
            detailsCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            detailsCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            detailsCell2.setBorderWidth(0);
            tableFooter.addCell(detailsCell2);

            final PdfPCell pageCell2 = new PdfPCell(new Phrase("", footerFont));
            pageCell2.setBorderWidth(0);
            tableFooter.addCell(pageCell2);

            tableFooter.writeSelectedRows(0, -1, footerPositionX, footerPositionY * 4, stamper.getOverContent(i));
        }

        // Close the stamper
        try {
            stamper.close();
        } catch (DocumentException | IOException e) {
            throw new OsirisException(e, "Impossible to close PDF File stamper");
        }
        reader.close();

        return tempPdfFile;
    }

    private File addHeaderOnPublicationReceipt(File pdfFile, Announcement announcement, boolean displayStamp)
            throws OsirisException {
        String pdfPath = pdfFile.getAbsolutePath();
        File tempPdfFile;
        try {
            tempPdfFile = File.createTempFile("pdfFooterHeader", "Add");
        } catch (IOException e) {
            throw new OsirisException(e, "Impossible to create temp file");
        }
        String pdfPathOut = tempPdfFile.getAbsolutePath();
        float headerPositionX = 60;
        float headerPositionY = PageSize.A4.getHeight() - 25;

        String announcementDate = "";

        if (announcement.getPublicationDate() != null) {
            LocalDate localDate = announcement.getPublicationDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
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

        if (displayStamp) {
            // Loop over the pages and add a header to each page
            int n = reader.getNumberOfPages();

            Font blueFont = new Font(FontFamily.TIMES_ROMAN);
            blueFont.setColor(0, 32, 96);
            blueFont.setSize(10);

            for (int i = 1; i <= n; i++) {

                if (i == 1) {
                    PdfPTable table = new PdfPTable(1);
                    table.setTotalWidth(PageSize.A4.getWidth() / 2 - 30);
                    table.setLockedWidth(true);
                    table.getDefaultCell().setBorder(Rectangle.BOX);
                    table.getDefaultCell().setBorderWidth(1);
                    table.getDefaultCell().setBorderColor(new BaseColor(0, 0, 0));

                    // Title
                    Font blackFontTitle = new Font(FontFamily.TIMES_ROMAN, 18, Font.BOLD);
                    blackFontTitle.setColor(0, 0, 0);

                    final PdfPCell titleCell = new PdfPCell(new Phrase("ATTESTATION DE PARUTION", blackFontTitle));
                    titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    titleCell.setBorderWidth(1);
                    titleCell.setPaddingBottom(8);
                    table.addCell(titleCell);

                    // Subtitle
                    Font blackFontSubTitle = new Font(FontFamily.TIMES_ROMAN);
                    blackFontSubTitle.setColor(0, 0, 0);
                    blackFontSubTitle.setSize(11);

                    final PdfPCell subtitleCell = new PdfPCell(
                            new Phrase("Pour le " + announcementDate, blackFontSubTitle));
                    subtitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    subtitleCell.setPaddingTop(6);
                    subtitleCell.setPaddingBottom(5);
                    subtitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    subtitleCell.setBorderWidth(1);
                    table.addCell(subtitleCell);

                    // Website
                    Font blueFontType = new Font(FontFamily.TIMES_ROMAN, 20, Font.BOLD);
                    blueFontType.setColor(38, 61, 83);

                    final PdfPCell typeCell = new PdfPCell(new Phrase("www.jss.fr", blueFontType));
                    typeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    typeCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    typeCell.setBorderWidth(1);
                    typeCell.setPaddingBottom(6);
                    typeCell.setPaddingTop(0);
                    table.addCell(typeCell);

                    // Departments
                    Font departmentFontType = new Font(FontFamily.TIMES_ROMAN);
                    departmentFontType.setColor(0, 0, 0);
                    departmentFontType.setSize(7);

                    final PdfPCell typeCellDepartments = new PdfPCell(
                            new Phrase("Habitilité sur le 75, 78, 91, 92, 93, 94, 95", departmentFontType));
                    typeCellDepartments.setHorizontalAlignment(Element.ALIGN_CENTER);
                    typeCellDepartments.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    typeCellDepartments.setBorderWidth(1);
                    typeCellDepartments.setPaddingBottom(2);
                    table.addCell(typeCellDepartments);

                    table.writeSelectedRows(0, -1, headerPositionX, headerPositionY, stamper.getOverContent(i));
                }
            }
        }

        // Close the stamper
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
        String htmlContent = StringEscapeUtils
                .unescapeHtml4(mailHelper.emailTemplateEngine().process(template, ctx));

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
        renderer.setDocumentFromString(
                htmlContent.replaceAll("\\p{C}", " ").replaceAll("&", "<![CDATA[&]]>").replaceAll("<col (.*?)>", "")
                        .replaceAll("line-height: normal",
                                "line-height: normal;padding:0;margin:0"));
        renderer.layout();
        try {
            renderer.createPDF(outputStream);
            outputStream.close();
        } catch (DocumentException | IOException e) {
            throw new OsirisException(e, "Unable to create PDF file for domiciliation contracts");
        }
        return addFooterOnDomiciliationContract(tempFile);
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

    private File addFooterOnDomiciliationContract(File pdfFile) throws OsirisException {
        String pdfPath = pdfFile.getAbsolutePath();
        File tempPdfFile;
        try {
            tempPdfFile = File.createTempFile("pdfDomiciliationFooter", "Add");
        } catch (IOException e) {
            throw new OsirisException(e, "Impossible to create temp file");
        }
        String pdfPathOut = tempPdfFile.getAbsolutePath();
        float footerPositionX = 60;
        float footerPositionY = 6;

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

        for (int i = 1; i <= n; i++) {

            // add footer
            PdfPTable tableFooter = new PdfPTable(1);
            try {
                tableFooter.setWidths(new int[] { 100 });
            } catch (DocumentException e) {
                throw new OsirisException(e, "Wrong columns sizes for PDF");
            }
            tableFooter.setTotalWidth(PageSize.A4.getWidth() - (footerPositionX * 2));
            tableFooter.setLockedWidth(true);
            tableFooter.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            // Details 1

            Font footerFont = new Font(FontFamily.TIMES_ROMAN);
            footerFont.setSize(10);

            final PdfPCell detailsCell1 = new PdfPCell(new Phrase(
                    "Agrément n°" + jssDomiciliationAgreementNumber + " obtenu le " + jssDomiciliationAgreementDate
                            + " auprès de la Préfecture de Paris",
                    footerFont));
            detailsCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            detailsCell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            detailsCell1.setBorderWidth(0);
            tableFooter.addCell(detailsCell1);

            tableFooter.writeSelectedRows(0, -1, footerPositionX, footerPositionY * 4, stamper.getOverContent(i));
        }

        // Close the stamper
        try {
            stamper.close();
        } catch (DocumentException | IOException e) {
            throw new OsirisException(e, "Impossible to close PDF File stamper");
        }
        reader.close();

        return tempPdfFile;
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
                .unescapeHtml4(mailHelper.emailTemplateEngine().process("registration-act", ctx));

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
                .unescapeHtml4(mailHelper.emailTemplateEngine().process("tracking-sheet", ctx));

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
}
