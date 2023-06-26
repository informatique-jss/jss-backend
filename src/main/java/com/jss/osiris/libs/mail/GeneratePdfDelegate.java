package com.jss.osiris.libs.mail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

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
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.model.LetterModel;
import com.jss.osiris.libs.mail.model.VatMail;
import com.jss.osiris.modules.accounting.model.BillingClosureReceiptValue;
import com.jss.osiris.modules.invoicing.model.Deposit;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.service.InvoiceHelper;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.Vat;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.miscellaneous.service.VatService;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.quotation.model.Announcement;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Debour;
import com.jss.osiris.modules.quotation.model.NoticeType;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.service.ProvisionService;
import com.jss.osiris.modules.tiers.model.ITiers;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

@Service
public class GeneratePdfDelegate {
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

    @Value("${invoicing.payment.limit.refund.euros}")
    private String payementLimitRefundInEuros;

    @Autowired
    InvoiceHelper invoiceHelper;

    @Autowired
    VatService vatService;

    @Autowired
    ProvisionService provisionService;

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
                                    .replaceAll("<br>", "<br/>").replaceAll("&nbsp;", " ")
                            : null);
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

            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders()) {
                signatureEmployee = asso.getAssignedTo();
                affaireLabels.add((asso.getAffaire().getDenomination() != null ? asso.getAffaire().getDenomination()
                        : (asso.getAffaire().getCivility().getLabel() + " " + asso.getAffaire().getFirstname() + " "
                                + asso.getAffaire().getLastname()))
                        + "<br/>"
                        + asso.getAffaire().getAddress() + "<br/>"
                        + asso.getAffaire().getPostalCode() + " "
                        + (asso.getAffaire().getCity() != null ? asso.getAffaire().getCity().getLabel() : ""));
                for (Provision provision : asso.getProvisions()) {
                    eventLabels.add(provision.getProvisionType().getLabel());
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

    public File getBillingClosureReceiptFile(ITiers tier, List<BillingClosureReceiptValue> billingClosureValues)
            throws OsirisException, OsirisClientMessageException {
        final Context ctx = new Context();

        Document billingDocument = documentService.getBillingDocument(tier.getDocuments());

        ctx.setVariable("commandNumber", null);
        if (billingDocument != null && billingDocument.getIsCommandNumberMandatory() != null
                && billingDocument.getIsCommandNumberMandatory()
                && billingDocument.getCommandNumber() != null)
            ctx.setVariable("commandNumber", billingDocument.getCommandNumber());
        ctx.setVariable("currentDate", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        if (tier instanceof Tiers) {
            ctx.setVariable("denomination",
                    ((Tiers) tier).getDenomination() != null
                            ? ((Tiers) tier).getDenomination()
                            : (((Tiers) tier).getFirstname() + " " + ((Tiers) tier).getLastname()));
            ctx.setVariable("address", ((Tiers) tier).getAddress());
            ctx.setVariable("postalCode", ((Tiers) tier).getPostalCode());
            ctx.setVariable("city", ((Tiers) tier).getCity() != null ? ((Tiers) tier).getCity().getLabel() : "");
        } else if (tier instanceof Responsable) {
            ctx.setVariable("denomination",
                    (((Responsable) tier).getFirstname() + " " + ((Responsable) tier).getLastname()));
            ctx.setVariable("address", ((Responsable) tier).getTiers().getAddress());
            ctx.setVariable("postalCode", ((Responsable) tier).getTiers().getPostalCode());
            ctx.setVariable("city",
                    ((Responsable) tier).getTiers().getCity() != null
                            ? ((Responsable) tier).getTiers().getCity().getLabel()
                            : "");
        } else if (tier instanceof Confrere) {
            ctx.setVariable("denomination", (((Confrere) tier).getLabel()));
            ctx.setVariable("address", ((Confrere) tier).getAddress());
            ctx.setVariable("postalCode", ((Confrere) tier).getPostalCode());
            ctx.setVariable("city", ((Confrere) tier).getCity() != null ? ((Confrere) tier).getCity().getLabel() : "");
        }

        ctx.setVariable("billingClosureValues", billingClosureValues);
        ctx.setVariable("ibanJss", ibanJss);
        ctx.setVariable("bicJss", bicJss);

        Tiers billingTiers = null;
        if (tier instanceof Tiers)
            billingTiers = (Tiers) tier;
        if (tier instanceof Responsable)
            billingTiers = ((Responsable) tier).getTiers();

        ctx.setVariable("tiersReference", null);
        if (billingTiers != null)
            ctx.setVariable("tiersReference", billingTiers.getId()
                    + (billingTiers.getIdAs400() != null ? ("/" + billingTiers.getIdAs400()) : ""));

        Float balance = 0f;
        Float creditBalance = 0f;
        Float debitBalance = 0f;

        if (billingClosureValues != null && billingClosureValues.size() > 0)
            for (BillingClosureReceiptValue billingClosureValue : billingClosureValues) {
                balance -= billingClosureValue.getCreditAmount() != null ? billingClosureValue.getCreditAmount() : 0;
                creditBalance += billingClosureValue.getCreditAmount() != null ? billingClosureValue.getCreditAmount()
                        : 0;
                balance += billingClosureValue.getDebitAmount() != null ? billingClosureValue.getDebitAmount() : 0;
                debitBalance += billingClosureValue.getDebitAmount() != null ? billingClosureValue.getDebitAmount() : 0;
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
        renderer.setDocumentFromString(htmlContent.replaceAll("\\p{C}", " "));
        renderer.layout();
        try {
            renderer.createPDF(outputStream);
            outputStream.close();
        } catch (DocumentException | IOException e) {
            throw new OsirisException(e,
                    "Unable to create PDF file for biling closure receipt, tiers n°" + tier.getId());
        }
        return tempFile;
    }

    public File generateInvoicePdf(CustomerOrder customerOrder, Invoice invoice, Invoice originalInvoice)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        final Context ctx = new Context();

        if (originalInvoice != null)
            ctx.setVariable("reverseCreditNote", originalInvoice);

        ctx.setVariable("preTaxPriceTotal", invoiceHelper.getPreTaxPriceTotal(invoice));
        if (Math.round(invoiceHelper.getDiscountTotal(invoice) * 100f) / 100f > 0)
            ctx.setVariable("discountTotal", invoiceHelper.getDiscountTotal(invoice));
        ctx.setVariable("assos", customerOrder.getAssoAffaireOrders());
        ctx.setVariable("preTaxPriceTotalWithDicount", invoiceHelper.getPreTaxPriceTotal(invoice)
                - (invoiceHelper.getDiscountTotal(invoice) != null
                        && Math.round(invoiceHelper.getDiscountTotal(invoice) * 100f) / 100f > 0
                                ? invoiceHelper.getDiscountTotal(invoice)
                                : 0f));
        ArrayList<VatMail> vats = null;
        Float vatTotal = 0f;
        for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
            if (invoiceItem.getVat() != null && invoiceItem.getVatPrice() != null
                    && invoiceItem.getVatPrice() > 0 && !invoiceItem.getBillingItem().getBillingType().getIsDebour()
                    && !invoiceItem.getBillingItem().getBillingType().getIsFee()) {
                vatTotal += invoiceItem.getVatPrice();
                if (vats == null)
                    vats = new ArrayList<VatMail>();
                boolean vatFound = false;
                for (VatMail vatMail : vats) {
                    if (vatMail.getLabel().equals(invoiceItem.getVat().getLabel())) {
                        vatFound = true;
                        if (vatMail.getTotal() == null && invoiceItem.getVatPrice() != null
                                && invoiceItem.getVatPrice() > 0) {
                            vatMail.setTotal(invoiceItem.getVatPrice());
                            vatMail.setBase(invoiceItem.getPreTaxPrice()
                                    - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : 0f));
                        } else if (invoiceItem.getVatPrice() != null && invoiceItem.getVatPrice() > 0) {
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
                    vatmail.setBase(invoiceItem.getPreTaxPrice()
                            - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : 0f));
                    vats.add(vatmail);
                }
            }
        }

        // Compute base for debours
        ctx.setVariable("vatDebour", null);
        if (vats == null)
            vats = new ArrayList<VatMail>();
        for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders()) {
            for (Provision provision : asso.getProvisions()) {
                if (provision.getDebours() != null && provision.getDebours().size() > 0) {
                    for (Debour debour : provision.getDebours()) {
                        Vat vatDebour = vatService
                                .getGeographicalApplicableVatForSales(customerOrder,
                                        constantService.getVatDeductible());

                        Vat competentAuthorityVatPurschase = vatService.getGeographicalApplicableVatForPurshases(
                                debour.getCompetentAuthority(),
                                constantService.getVatDeductible());

                        if (vatDebour != null && competentAuthorityVatPurschase.getRate() < vatDebour.getRate())
                            vatDebour = competentAuthorityVatPurschase;

                        Float debourAmount = debour.getInvoicedAmount() != null ? debour.getInvoicedAmount()
                                : debour.getDebourAmount();
                        if (!debour.getBillingType().getIsNonTaxable() && vatDebour != null) {
                            ctx.setVariable("vatDebour", vatDebour);

                            boolean vatFound = false;
                            for (VatMail vatMail : vats) {
                                if (vatMail.getLabel().equals(vatDebour.getLabel())) {
                                    vatFound = true;
                                    if (vatMail.getTotal() == null) {
                                        vatMail.setTotal(
                                                (debourAmount / (1f + (vatDebour.getRate() / 100f)))
                                                        * vatDebour.getRate() / 100f);
                                        vatMail.setBase(
                                                debourAmount / (1 + (vatDebour.getRate() / 100)));
                                    } else {
                                        vatMail.setTotal(vatMail.getTotal()
                                                + (debourAmount / (1f + (vatDebour.getRate() / 100f)))
                                                        * vatDebour.getRate() / 100f);
                                        vatMail.setBase(vatMail.getBase()
                                                + debourAmount / (1 + (vatDebour.getRate() / 100)));
                                    }
                                }
                            }
                            if (!vatFound) {
                                VatMail vatmail = new VatMail();
                                vatmail.setTotal((debourAmount / (1f + (vatDebour.getRate() / 100f)))
                                        * vatDebour.getRate() / 100f);
                                vatmail.setLabel(vatDebour.getLabel());
                                vatmail.setBase(debourAmount / (1 + (vatDebour.getRate() / 100)));
                                vats.add(vatmail);
                            }
                        }
                    }
                }
            }
        }

        ctx.setVariable("vats", vats);
        ctx.setVariable("priceTotal", Math.round(invoiceHelper.getPriceTotal(invoice) * 100f) / 100f);
        ctx.setVariable("invoice", invoice);

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

        Tiers invoiceTiers = null;
        if (customerOrder.getResponsable() != null)
            invoiceTiers = customerOrder.getResponsable().getTiers();
        if (customerOrder.getTiers() != null)
            invoiceTiers = customerOrder.getTiers();

        ctx.setVariable("tiersReference", null);
        if (invoiceTiers != null)
            ctx.setVariable("tiersReference", invoiceTiers.getId()
                    + (invoiceTiers.getIdAs400() != null ? ("/" + invoiceTiers.getIdAs400()) : ""));

        ctx.setVariable("quotation",
                customerOrder.getQuotations() != null && customerOrder.getQuotations().size() > 0
                        ? customerOrder.getQuotations().get(0)
                        : null);

        // Exclude deposits generated after invoice
        ArrayList<Deposit> deposits = new ArrayList<Deposit>();
        Float depositTotal = 0f;
        Float payementTotal = 0f;
        if (customerOrder.getDeposits() != null)
            for (Deposit deposit : customerOrder.getDeposits())
                if (deposit.getDepositDate().isBefore(invoice.getCreatedDate())) {
                    deposits.add(deposit);
                    depositTotal += deposit.getDepositAmount();
                    if (deposit.getOriginPayment() != null)
                        payementTotal += deposit.getOriginPayment().getPaymentAmount();
                }

        ctx.setVariable("deposits", deposits);
        ctx.setVariable("remainingToPay",
                Math.round((invoiceHelper.getPriceTotal(invoice) - depositTotal) * 100f) / 100f);
        ctx.setVariable("hasAppoint",
                Math.abs(Math.round((invoiceHelper.getPriceTotal(invoice) - payementTotal) * 100f) / 100f) <= Float
                        .parseFloat(payementLimitRefundInEuros));
        ctx.setVariable("tooMuchPerceived", null);
        Float amountPerceived = payementTotal - Math.round((invoiceHelper.getPriceTotal(invoice)) * 100f) / 100f;
        if (Math.round(amountPerceived * 100f) / 100f > 0
                && (invoice.getAppoints() == null || invoice.getAppoints().size() == 0))
            ctx.setVariable("tooMuchPerceived", amountPerceived);

        LocalDateTime localDate = invoice.getCreatedDate();
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("dd/MM/yyyy");
        ctx.setVariable("invoiceCreatedDate", localDate.format(formatter));
        ctx.setVariable("invoiceDueDate", invoice.getDueDate().format(formatter));

        // Create the HTML body using Thymeleaf
        final String htmlContent = StringEscapeUtils
                .unescapeHtml4(mailHelper.emailTemplateEngine().process("invoice-page", ctx));

        File tempFile;
        OutputStream outputStream;
        try {
            tempFile = File.createTempFile("invoice", "pdf");
            outputStream = new FileOutputStream(tempFile);
        } catch (IOException e) {
            throw new OsirisException(e, "Unable to create temp file");
        }
        ITextRenderer renderer = new ITextRenderer();
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
}
