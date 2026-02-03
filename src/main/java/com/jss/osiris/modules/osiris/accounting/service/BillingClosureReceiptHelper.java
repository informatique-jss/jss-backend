package com.jss.osiris.modules.osiris.accounting.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.GlobalExceptionHandler;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisLog;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.GeneratePdfDelegate;
import com.jss.osiris.libs.mail.MailComputeHelper;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.libs.mail.model.MailComputeResult;
import com.jss.osiris.modules.osiris.accounting.model.AccountingRecordSearch;
import com.jss.osiris.modules.osiris.accounting.model.AccountingRecordSearchResult;
import com.jss.osiris.modules.osiris.accounting.model.BillingClosureReceiptValue;
import com.jss.osiris.modules.osiris.invoicing.model.ICreatedDate;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.Payment;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceService;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderStatusService;
import com.jss.osiris.modules.osiris.quotation.service.ServiceService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;
import com.jss.osiris.modules.osiris.tiers.service.TiersService;

@org.springframework.stereotype.Service
public class BillingClosureReceiptHelper {

    @Value("${payment.cb.entry.point}")
    private String paymentCbEntryPoint;

    @Autowired
    TiersService tiersService;

    @Autowired
    ResponsableService responsableService;

    @Autowired
    DocumentService documentService;

    @Autowired
    ConstantService constantService;

    @Autowired
    GeneratePdfDelegate generatePdfDelegate;

    @Autowired
    GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    CustomerOrderStatusService customerOrderStatusService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    MailComputeHelper mailComputeHelper;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    MailHelper mailHelper;

    @Autowired
    ServiceService serviceService;

    @Autowired
    AccountingRecordService accountingRecordService;

    public File getBillingClosureReceiptFile(Integer tiersId, Integer responsableId, boolean downloadFile)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        Tiers tier = null;
        Responsable responsable = null;
        if (tiersId != null)
            tier = tiersService.getTiers(tiersId);

        if (responsableId != null)
            responsable = responsableService.getResponsable(responsableId);

        Document billingClosureDocument = null;
        if (responsable != null) {
            tier = responsable.getTiers();
            billingClosureDocument = documentService
                    .getBillingClosureDocument(responsable.getTiers().getDocuments());
        } else
            billingClosureDocument = documentService.getBillingClosureDocument(tier.getDocuments());

        if (billingClosureDocument != null) {
            boolean isOrderingByEventDate = billingClosureDocument.getBillingClosureType() != null
                    && !billingClosureDocument
                            .getBillingClosureType().getId()
                            .equals(constantService.getBillingClosureTypeAffaire().getId());

            if (downloadFile) {
                List<BillingClosureReceiptValue> values = generateBillingClosureValuesForITiers(tier, responsable,
                        isOrderingByEventDate, false, false, false);

                return generatePdfDelegate.getBillingClosureReceiptFile(tier, responsable, values);
            }

            // Send all to tiers
            if (billingClosureDocument.getBillingClosureRecipientType() != null
                    && (billingClosureDocument.getBillingClosureRecipientType().getId()
                            .equals(constantService.getBillingClosureRecipientTypeClient().getId())
                            || billingClosureDocument.getBillingClosureRecipientType().getId()
                                    .equals(constantService.getBillingClosureRecipientTypeOther().getId()))) {

                List<BillingClosureReceiptValue> values = generateBillingClosureValuesForITiers(tier, responsable,
                        isOrderingByEventDate, false, false, false);
                if (values.size() > 0) {
                    try {
                        sendBillingClosureReceiptFile(
                                generatePdfDelegate.getBillingClosureReceiptFile(tier, responsable, values),
                                tier, responsable);
                    } catch (Exception e) {
                        globalExceptionHandler.persistLog(
                                new OsirisException(e, "Impossible to generate billing closure for Tiers " +
                                        tiersId),
                                OsirisLog.UNHANDLED_LOG);
                    }
                }

            } else if (billingClosureDocument.getBillingClosureRecipientType() != null
                    && billingClosureDocument.getBillingClosureRecipientType().getId()
                            .equals(constantService.getBillingClosureRecipientTypeResponsable().getId())) {
                // Send to each responsable
                if (tier instanceof Tiers && ((Tiers) tier).getResponsables() != null)
                    for (Responsable tiersResponsable : tier.getResponsables()) {
                        if (tiersResponsable.getIsActive()) {
                            List<BillingClosureReceiptValue> values = generateBillingClosureValuesForITiers(null,
                                    tiersResponsable, isOrderingByEventDate, false, false, false);
                            if (values.size() > 0) {
                                try {
                                    sendBillingClosureReceiptFile(
                                            generatePdfDelegate.getBillingClosureReceiptFile(null, tiersResponsable,
                                                    values),
                                            null, tiersResponsable);
                                } catch (Exception e) {
                                    globalExceptionHandler.persistLog(
                                            new OsirisException(e,
                                                    "Impossible to generate billing closure for Tiers " + tiersId),
                                            OsirisLog.UNHANDLED_LOG);
                                }
                            }
                        }
                    }
            }
        }
        return null;
    }

    public List<BillingClosureReceiptValue> generateBillingClosureValuesForITiers(Tiers tiers, Responsable responsable,
            boolean isOrderingByEventDate, boolean hideAffaires, boolean hideServices, boolean seeAllScope)
            throws OsirisException, OsirisClientMessageException {

        // Find all elements
        ArrayList<BillingClosureReceiptValue> values = new ArrayList<BillingClosureReceiptValue>();

        // Find customer orders
        List<Responsable> responsableList = new ArrayList<Responsable>();

        if (responsable != null) {
            responsableList.add(responsable);
        } else if (tiers != null && tiers.getResponsables() != null) {
            values.add(new BillingClosureReceiptValue(
                    tiers.getDenomination() != null ? tiers.getDenomination()
                            : (tiers.getFirstname() + " " + tiers.getLastname())));
            for (Responsable responsableOfTiers : tiers.getResponsables())
                responsableList.add(responsableOfTiers);
        }

        if (seeAllScope && Boolean.TRUE.equals(responsable.getCanViewAllTiersInWeb()) && tiers != null
                && tiers.getResponsables() != null)
            responsableList = tiers.getResponsables();

        List<CustomerOrder> customerOrders = customerOrderService
                .searchOrders(customerOrderStatusService.getCustomerOrderStatus().stream()
                        .filter(status -> !status.getCode().equals(CustomerOrderStatus.BILLED) &&
                                !status.getCode().equals(CustomerOrderStatus.ABANDONED))
                        .collect(Collectors.toList()), false, null);

        // Find invoices
        for (Responsable responsableToCheck : responsableList) {
            boolean hadSomeValues = false;
            values.add(new BillingClosureReceiptValue(
                    (responsableToCheck.getFirstname() + " " + responsableToCheck.getLastname())));
            List<Invoice> invoices = invoiceService.searchInvoices(
                    Arrays.asList(constantService.getInvoiceStatusSend()),
                    Arrays.asList(responsableToCheck));

            if (isOrderingByEventDate) {
                ArrayList<ICreatedDate> allInputs = new ArrayList<ICreatedDate>();
                if (customerOrders != null && customerOrders.size() > 0) {
                    for (CustomerOrder customerOrder : customerOrders) {
                        allInputs.add(customerOrder);
                        if (customerOrder.getPayments() != null && customerOrder.getPayments().size() > 0)
                            for (Payment payment : customerOrder.getPayments())
                                if (!payment.getIsCancelled())
                                    allInputs.add(payment);
                    }
                }

                if (invoices != null && invoices.size() > 0) {
                    for (Invoice invoice : invoices) {
                        allInputs.add(invoice);
                        if (invoice.getPayments() != null && invoice.getPayments().size() > 0)
                            for (Payment payment : invoice.getPayments())
                                if (!payment.getIsCancelled())
                                    allInputs.add(payment);
                    }
                }

                if (allInputs.size() > 0) {
                    hadSomeValues = true;
                    allInputs.sort(new Comparator<ICreatedDate>() {
                        @Override
                        public int compare(ICreatedDate o1, ICreatedDate o2) {
                            return o1.getCreatedDate().compareTo(o2.getCreatedDate());
                        }
                    });

                    for (ICreatedDate input : allInputs) {
                        if (input instanceof Invoice)
                            values.add(
                                    getBillingClosureReceiptValueForInvoice((Invoice) input, hideAffaires,
                                            hideServices));
                        if (input instanceof Payment) {
                            CustomerOrder customerOrder = null;
                            Payment payment = (Payment) input;
                            if (payment.getIsDeposit()) {
                                if (payment.getCustomerOrder() != null)
                                    customerOrder = payment.getCustomerOrder();
                                else if (payment.getInvoice() != null)
                                    customerOrder = payment.getInvoice().getCustomerOrder();
                                values.add(getBillingClosureReceiptValueForDeposit(payment, customerOrder, true));
                            } else {
                                values.add(getBillingClosureReceiptValueForPayment(payment, true));
                            }
                        }
                    }
                }
            } else {
                // Order by affaire
                ArrayList<ICreatedDate> allInputs = new ArrayList<ICreatedDate>();
                if (customerOrders != null && customerOrders.size() > 0)
                    allInputs.addAll(customerOrders);
                if (invoices != null && invoices.size() > 0)
                    allInputs.addAll(invoices);

                if (allInputs.size() > 0) {
                    hadSomeValues = true;
                    allInputs.sort(new Comparator<ICreatedDate>() {

                        @Override
                        public int compare(ICreatedDate o1, ICreatedDate o2) {
                            if (o1 != null && o2 == null)
                                return 1;
                            if (o1 == null && o2 != null)
                                return -1;
                            if (o1 == null && o2 == null)
                                return 0;
                            if (o1 != null && o2 != null)
                                return o1.getCreatedDate().compareTo(o2.getCreatedDate());
                            return 0;
                        }
                    });
                }

                for (Object input : allInputs) {
                    if (input instanceof CustomerOrder) {
                        CustomerOrder customerOrder = (CustomerOrder) input;
                        if (customerOrder.getPayments() != null && customerOrder.getPayments().size() > 0) {
                            for (Payment payment : customerOrder.getPayments())
                                if (!payment.getIsCancelled())
                                    values.add(getBillingClosureReceiptValueForDeposit(payment, customerOrder,
                                            customerOrder.getPayments()
                                                    .indexOf(payment) == customerOrder.getPayments().size() - 1));
                        }
                    }
                    if (input instanceof Invoice) {
                        Invoice invoice = (Invoice) input;
                        BillingClosureReceiptValue valueInvoice = getBillingClosureReceiptValueForInvoice(invoice,
                                hideAffaires, hideServices);
                        values.add(valueInvoice);
                        if (invoice.getPayments() != null && invoice.getPayments().size() > 0) {
                            valueInvoice.setDisplayBottomBorder(false);
                            for (Payment payment : invoice.getPayments())
                                if (!payment.getIsCancelled() && payment.getIsDeposit())
                                    values.add(getBillingClosureReceiptValueForDeposit(payment,
                                            invoice.getCustomerOrder(),
                                            invoice.getCustomerOrder().getPayments()
                                                    .indexOf(payment) == invoice.getCustomerOrder().getPayments().size()
                                                            - 1));
                        }
                        if (invoice.getPayments() != null && invoice.getPayments().size() > 0) {
                            valueInvoice.setDisplayBottomBorder(false);
                            for (Payment payment : invoice.getPayments())
                                if (!payment.getIsCancelled() && !payment.getIsDeposit())
                                    values.add(getBillingClosureReceiptValueForPayment(payment,
                                            invoice.getPayments().indexOf(payment) == invoice.getPayments().size()
                                                    - 1));
                        }

                    }
                }
            }

            if (!hadSomeValues)
                values.remove(values.size() - 1);
        }

        // Add as400 values
        if (tiers != null) {
            AccountingRecordSearch search = new AccountingRecordSearch();
            search.setAccountingAccount(tiers.getAccountingAccountCustomer());
            search.setIsFromAs400(true);
            search.setHideLettered(true);
            search.setStartDate(
                    LocalDateTime.of(constantService.getDateAccountingClosureForAccountant().getYear(), 1, 1, 0, 1, 0));

            List<AccountingRecordSearchResult> results = accountingRecordService.searchAccountingRecords(search, false);
            if (results != null && !results.isEmpty())
                for (AccountingRecordSearchResult result : results)
                    values.add(getBillingClosureReceiptValueForAs400(result));
            else
                values.remove(values.size() - 1);
        }

        return values;
    }

    private BillingClosureReceiptValue getBillingClosureReceiptValueForInvoice(Invoice invoice, boolean hideAffaires,
            boolean hideServices)
            throws OsirisException, OsirisClientMessageException {
        BillingClosureReceiptValue value = new BillingClosureReceiptValue();
        value.setDisplayBottomBorder(true);
        value.setDebitAmount(invoice.getTotalPrice());
        value.setRemainingDebitAmount(invoiceService.getRemainingAmountToPayForInvoice(invoice));
        value.setCreditAmount(null);
        value.setEventDateTime(invoice.getCreatedDate());
        value.setResponsable(invoice.getResponsable());
        value.setIdInvoice(invoice.getId());
        if (invoice.getCustomerOrder() != null)
            value.setIdCustomerOrder(invoice.getCustomerOrder().getId());
        value.setEventDateString(invoice.getCreatedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        value.setEventDescription("Facture n°" + invoice.getId());
        if (invoice.getManualPaymentType() != null
                && invoice.getManualPaymentType().getId().equals(constantService.getPaymentTypePrelevement().getId())
                && invoice.getDirectDebitTransfert() != null)
            value.setDirectDebitTransfertDateTime(invoice.getDirectDebitTransfert()
                    .getTransfertDateTime().toLocalDate());

        if (invoice.getCustomerOrder() != null) {
            String customerOrderValue = invoice.getCustomerOrder().getId() != null
                    ? " / Commande n°" + invoice.getCustomerOrder().getId()
                    : "";

            List<String> eventDescriptions = new ArrayList<String>();
            eventDescriptions.add("Facture n°" + invoice.getId() + customerOrderValue);
            if (!hideAffaires)
                eventDescriptions.add("<br/><strong>"
                        + String.join("<br/>",
                                getAllAffairesLabelForCustomerOrder(invoice.getCustomerOrder()))
                                .replaceAll("&",
                                        "<![CDATA[&]]>")
                        + "</strong>");
            else
                value.setAffaireLists(
                        String.join(" / ", getAllAffairesLabelForCustomerOrder(invoice.getCustomerOrder())));
            if (!hideServices)
                eventDescriptions.add("<br/>"
                        + String.join("<br/>", getAllServiceLabelsForCustomerOrder(invoice.getCustomerOrder()))
                                .replaceAll("&",
                                        "<![CDATA[&]]>"));
            else
                value.setServiceLists(
                        String.join(" / ", getAllServiceLabelsForCustomerOrder(invoice.getCustomerOrder())));
            value.setEventDescription(String.join("", eventDescriptions));

            MailComputeResult mailComputeResult = mailComputeHelper
                    .computeMailForCustomerOrderFinalizationAndInvoice(invoice.getCustomerOrder(), false);
            value.setEventCbLink(paymentCbEntryPoint + "/order/invoice?mail="
                    + mailComputeResult.getRecipientsMailTo().get(0).getMail() + "&customerOrderId="
                    + invoice.getCustomerOrder().getId());
        }

        return value;
    }

    private BillingClosureReceiptValue getBillingClosureReceiptValueForDeposit(Payment payment,
            CustomerOrder customerOrder, boolean displayBottomBorder) {
        BillingClosureReceiptValue value = new BillingClosureReceiptValue();
        value.setDisplayBottomBorder(displayBottomBorder);
        value.setDebitAmount(null);
        value.setCreditAmount(payment.getPaymentAmount());
        value.setResponsable(customerOrder.getResponsable());
        value.setIdCustomerOrder(customerOrder.getId());
        value.setEventDateTime(payment.getPaymentDate());
        value.setEventDateString(payment.getPaymentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        String description = "Acompte n°" + payment.getId() + " - "
                + payment.getLabel().replaceAll("&", "<![CDATA[&]]>");
        // if (customerOrder != null) {
        // description += "<br/>"
        // + String.join("<br/>",
        // getAllAffairesLabelForCustomerOrder(customerOrder)).replaceAll("&",
        // "<![CDATA[&]]>");
        // }
        value.setEventDescription(description);

        return value;
    }

    private BillingClosureReceiptValue getBillingClosureReceiptValueForAs400(
            AccountingRecordSearchResult accountingRecord) {
        BillingClosureReceiptValue value = new BillingClosureReceiptValue();
        value.setDisplayBottomBorder(false);
        value.setDebitAmount(accountingRecord.getDebitAmount());
        value.setCreditAmount(accountingRecord.getCreditAmount());
        value.setResponsable(null);
        value.setIdCustomerOrder(null);
        value.setEventDateTime(accountingRecord.getOperationDateTime());
        value.setEventDateString(
                accountingRecord.getOperationDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        String description = accountingRecord.getLabel().replaceAll("&", "<![CDATA[&]]>").replaceAll("AN 2025 - ", "")
                .replaceAll("A-Nouveau 2024 - ", "");
        value.setEventDescription(description);

        return value;
    }

    private BillingClosureReceiptValue getBillingClosureReceiptValueForPayment(Payment payment,
            boolean displayBottomBorder) {

        BillingClosureReceiptValue value = new BillingClosureReceiptValue();
        value.setDisplayBottomBorder(displayBottomBorder);
        value.setDebitAmount(null);
        if (payment.getInvoice() != null) {
            value.setResponsable(payment.getInvoice().getResponsable());
            value.setIdInvoice(payment.getInvoice().getId());
            if (payment.getInvoice().getCustomerOrder() != null)
                value.setIdCustomerOrder(payment.getInvoice().getCustomerOrder().getId());
        } else if (payment.getCustomerOrder() != null) {
            value.setResponsable(payment.getCustomerOrder().getResponsable());
            value.setIdCustomerOrder(payment.getCustomerOrder().getId());
        }
        value.setCreditAmount(payment.getPaymentAmount());
        value.setEventDateTime(payment.getPaymentDate());
        value.setEventDateString(payment.getPaymentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        value.setEventDescription(payment.getLabel().replaceAll("&", "<![CDATA[&]]>"));

        return value;
    }

    private List<String> getAllAffairesLabelForCustomerOrder(CustomerOrder customerOrder) {
        ArrayList<String> affaires = new ArrayList<String>();
        if (customerOrder != null && customerOrder.getAssoAffaireOrders() != null
                && customerOrder.getAssoAffaireOrders().size() > 0)
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders()) {
                Affaire affaire = asso.getAffaire();
                affaires.add((affaire.getDenomination() != null ? affaire.getDenomination()
                        : (affaire.getFirstname() + " " + affaire.getLastname())) + ", " + affaire.getAddress() + ", "
                        + (affaire.getCity() != null ? affaire.getCity().getLabel() : ""));
            }
        return affaires;
    }

    private List<String> getAllServiceLabelsForCustomerOrder(CustomerOrder customerOrder) throws OsirisException {
        ArrayList<String> serviceLabels = new ArrayList<String>();
        if (customerOrder != null && customerOrder.getAssoAffaireOrders() != null
                && customerOrder.getAssoAffaireOrders().size() > 0)
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders()) {
                for (Service service : asso.getServices())
                    serviceLabels.add(service.getServiceLabelToDisplay());

                Document billingDocument = documentService.getBillingDocument(customerOrder.getDocuments());
                // Add annual reference if defined
                if (billingDocument != null && billingDocument.getIsCommandNumberMandatory() != null
                        && billingDocument.getIsCommandNumberMandatory() && billingDocument.getCommandNumber() != null
                        && billingDocument.getCommandNumber().length() > 0)
                    serviceLabels.add("Référence annuelle " + billingDocument.getCommandNumber());

                // Add if defined
                if (billingDocument != null && billingDocument.getExternalReference() != null
                        && billingDocument.getExternalReference().length() > 0)
                    serviceLabels.add("Référence " + billingDocument.getExternalReference());
            }
        return serviceLabels;
    }

    private void sendBillingClosureReceiptFile(File billingClosureReceipt, Tiers tiers, Responsable responsable)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        List<Attachment> attachments = new ArrayList<Attachment>();
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            List<Attachment> attachmentsList = attachmentService.addAttachment(
                    new FileInputStream(billingClosureReceipt), tiers != null ? tiers.getId() : responsable.getId(),
                    null,
                    tiers != null ? Tiers.class.getSimpleName() : Responsable.class.getSimpleName(),
                    constantService.getAttachmentTypeBillingClosure(),
                    "Relevé de compte du " + LocalDateTime.now().format(formatter) + ".pdf", false,
                    "Relevé de compte du " + LocalDateTime.now().format(formatter), null, null, null);

            for (Attachment attachment : attachmentsList)
                if (attachment.getUploadedFile().getFilename()
                        .equals("Relevé de compte du " + LocalDateTime.now().format(formatter) + ".pdf")) {
                    attachments.add(attachment);
                    break;
                }
        } catch (FileNotFoundException e) {
            throw new OsirisException(e,
                    "Impossible to read excel of billing closure for ITiers " + tiers.getId());
        }

        try {
            if (tiers == null)
                mailHelper.sendBillingClosureToCustomer(attachments, responsable.getTiers(), responsable, false);
            else
                mailHelper.sendBillingClosureToCustomer(attachments, tiers, responsable, false);
        } catch (Exception e) {
            globalExceptionHandler.persistLog(
                    new OsirisException(e,
                            "Impossible to send billing closure mail for Tiers/Responsable "
                                    + (tiers != null ? tiers.getId() : responsable.getId())),
                    OsirisLog.UNHANDLED_LOG);
        }
    }

    private void cleanBeforeMergeOnValidCells(XSSFSheet sheet, CellRangeAddress region, XSSFCellStyle cellStyle) {
        for (int rowNum = region.getFirstRow(); rowNum <= region.getLastRow(); rowNum++) {
            XSSFRow row = sheet.getRow(rowNum);
            if (row == null) {
                row = sheet.createRow(rowNum);
            }
            for (int colNum = region.getFirstColumn(); colNum <= region.getLastColumn(); colNum++) {
                XSSFCell currentCell = row.getCell(colNum);
                if (currentCell == null) {
                    currentCell = row.createCell(colNum);
                }
                currentCell.setCellStyle(cellStyle);
            }
        }
    }

    @Transactional
    public File getReceiptExport(Responsable responsable) throws OsirisException {
        responsable = responsableService.getResponsable(responsable.getId());

        List<BillingClosureReceiptValue> values = generateBillingClosureValuesForITiers(responsable.getTiers(),
                responsable, true, true, true, false);

        values.sort(new Comparator<BillingClosureReceiptValue>() {
            @Override
            public int compare(BillingClosureReceiptValue o1, BillingClosureReceiptValue o2) {
                if (o2.getEventDateTime() == null && o1.getEventDateTime() != null)
                    return -1;
                if (o2.getEventDateTime() != null && o1.getEventDateTime() == null)
                    return 1;
                if (o1.getEventDateTime() == null && o2.getEventDateTime() == null)
                    return 0;
                return o1.getEventDateTime().isAfter(o2.getEventDateTime()) ? -1 : 1;
            }
        });

        return getReceiptExport(values, responsable);
    }

    private File getReceiptExport(List<BillingClosureReceiptValue> receiptValues, Responsable responsable)
            throws OsirisException {
        XSSFWorkbook wb = new XSSFWorkbook();

        // Define style
        // Title
        XSSFCellStyle titleCellStyle = wb.createCellStyle();
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont titleFont = wb.createFont();
        titleFont.setBold(true);
        XSSFColor titleColor = new XSSFColor();
        titleColor.setARGBHex("0000FF");
        titleFont.setColor(titleColor);
        titleFont.setFontHeight(14);
        titleCellStyle.setFont(titleFont);
        titleCellStyle.setBorderBottom(BorderStyle.THIN);
        titleCellStyle.setBorderTop(BorderStyle.THIN);
        titleCellStyle.setBorderRight(BorderStyle.THIN);
        titleCellStyle.setBorderLeft(BorderStyle.THIN);

        // Header
        XSSFCellStyle headerCellStyle = wb.createCellStyle();
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
        headerCellStyle.setBorderBottom(BorderStyle.THIN);
        headerCellStyle.setBorderTop(BorderStyle.THIN);
        headerCellStyle.setBorderRight(BorderStyle.THIN);
        headerCellStyle.setBorderLeft(BorderStyle.THIN);
        String rgbS = "FFFF99";
        byte[] rgbB;
        try {
            rgbB = Hex.decodeHex(rgbS);
        } catch (DecoderException e) {
            try {
                wb.close();
            } catch (IOException e2) {
                throw new OsirisException(e, "Unable to close workbook");
            }
            throw new OsirisException(e, "Unable to decode color " + rgbS);
        }
        XSSFColor color = new XSSFColor(rgbB, null);
        headerCellStyle.setFillForegroundColor(color);
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Record line
        XSSFCellStyle recordCellStyle = wb.createCellStyle();
        recordCellStyle.setBorderBottom(BorderStyle.THIN);
        recordCellStyle.setBorderTop(BorderStyle.THIN);
        recordCellStyle.setBorderRight(BorderStyle.THIN);
        recordCellStyle.setBorderLeft(BorderStyle.THIN);

        // Debit / credit cells
        XSSFCellStyle styleCurrency = wb.createCellStyle();
        styleCurrency.setBorderBottom(BorderStyle.THIN);
        styleCurrency.setBorderTop(BorderStyle.THIN);
        styleCurrency.setBorderRight(BorderStyle.THIN);
        styleCurrency.setBorderLeft(BorderStyle.THIN);
        styleCurrency.setDataFormat((short) 8);

        // Date cells
        XSSFCellStyle styleDate = wb.createCellStyle();
        styleDate.setBorderBottom(BorderStyle.THIN);
        styleDate.setBorderTop(BorderStyle.THIN);
        styleDate.setBorderRight(BorderStyle.THIN);
        styleDate.setBorderLeft(BorderStyle.THIN);
        CreationHelper createHelper = wb.getCreationHelper();
        styleDate.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));

        XSSFSheet currentSheet = wb.createSheet("Relevé");

        // Title
        int currentLine = 0;

        XSSFRow currentRow = currentSheet.createRow(currentLine++);
        XSSFCell currentCell = currentRow.createCell(0);
        currentCell.setCellValue(responsable.getTiers().getDenomination() + " - " + responsable.getFirstname() + " "
                + responsable.getLastname() + " - "
                + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        CellRangeAddress region = new CellRangeAddress(0, 1, 0, 7);
        cleanBeforeMergeOnValidCells(currentSheet, region, titleCellStyle);
        currentSheet.addMergedRegion(region);
        currentLine++;

        // Header
        currentRow = currentSheet.createRow(currentLine++);
        int currentColumn = 0;

        currentCell = currentRow.createCell(currentColumn++);
        currentCell.setCellValue("Date");
        currentCell.setCellStyle(headerCellStyle);
        currentCell = currentRow.createCell(currentColumn++);
        currentCell.setCellValue("Affaire");
        currentCell.setCellStyle(headerCellStyle);
        currentCell = currentRow.createCell(currentColumn++);
        currentCell.setCellValue("Description");
        currentCell.setCellStyle(headerCellStyle);
        currentCell = currentRow.createCell(currentColumn++);
        currentCell.setCellValue("Services");
        currentCell.setCellStyle(headerCellStyle);
        currentCell = currentRow.createCell(currentColumn++);
        currentCell.setCellValue("Facture");
        currentCell.setCellStyle(headerCellStyle);
        currentCell = currentRow.createCell(currentColumn++);
        currentCell.setCellValue("Commande");
        currentCell.setCellStyle(headerCellStyle);
        currentCell = currentRow.createCell(currentColumn++);
        currentCell.setCellValue("Débit");
        currentCell.setCellStyle(headerCellStyle);
        currentCell = currentRow.createCell(currentColumn++);
        currentCell.setCellValue("Crédit");
        currentCell.setCellStyle(headerCellStyle);
        currentCell = currentRow.createCell(currentColumn++);

        // Each record

        BigDecimal debit = new BigDecimal(0f);
        BigDecimal credit = new BigDecimal(0f);
        if (receiptValues != null) {
            for (BillingClosureReceiptValue receiptValue : receiptValues) {

                if (receiptValue.getEventDateTime() == null)
                    continue;

                currentRow = currentSheet.createRow(currentLine++);
                currentColumn = 0;
                currentCell = currentRow.createCell(currentColumn++);
                if (receiptValue.getEventDateTime() != null)
                    currentCell.setCellValue(
                            receiptValue.getEventDateTime().toLocalDate());
                currentCell.setCellStyle(styleDate);
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue(receiptValue.getAffaireLists());
                currentCell.setCellStyle(recordCellStyle);
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue(receiptValue.getEventDescription());
                currentCell.setCellStyle(recordCellStyle);
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue(receiptValue.getServiceLists());
                currentCell.setCellStyle(recordCellStyle);
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue(receiptValue.getIdInvoice() + "");
                currentCell.setCellStyle(recordCellStyle);
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue(receiptValue.getIdCustomerOrder() + "");
                currentCell.setCellStyle(recordCellStyle);
                currentCell = currentRow.createCell(currentColumn++);
                if (receiptValue.getDebitAmount() != null) {
                    currentCell.setCellValue(receiptValue.getDebitAmount()
                            .setScale(2, RoundingMode.HALF_EVEN).doubleValue());
                    debit = debit.add(receiptValue.getDebitAmount());
                }
                currentCell.setCellStyle(styleCurrency);
                currentCell = currentRow.createCell(currentColumn++);
                if (receiptValue.getCreditAmount() != null) {
                    credit = credit.add(receiptValue.getCreditAmount());
                    currentCell.setCellValue(receiptValue.getCreditAmount()
                            .setScale(2, RoundingMode.HALF_EVEN).doubleValue());
                }
                currentCell.setCellStyle(styleCurrency);
            }
        }

        // Accumulation
        currentRow = currentSheet.createRow(currentLine++);
        currentColumn = 5;
        currentCell = currentRow.createCell(currentColumn++);
        currentCell.setCellValue("Total");
        currentCell.setCellStyle(recordCellStyle);
        currentCell = currentRow.createCell(currentColumn++);
        currentCell.setCellValue(debit.setScale(2, RoundingMode.HALF_EVEN).doubleValue());
        currentCell.setCellStyle(styleCurrency);
        currentCell = currentRow.createCell(currentColumn++);
        currentCell.setCellValue(credit.setScale(2, RoundingMode.HALF_EVEN).doubleValue());
        currentCell.setCellStyle(styleCurrency);

        // Balance
        currentRow = currentSheet.createRow(currentLine++);
        currentColumn = 5;
        currentCell = currentRow.createCell(currentColumn++);
        currentCell.setCellValue("Solde");
        currentCell.setCellStyle(recordCellStyle);
        currentCell = currentRow.createCell(currentColumn++);
        if ((credit.subtract(debit)).compareTo(new BigDecimal(0)) < 0)
            currentCell.setCellValue(
                    credit.subtract(debit).setScale(2, RoundingMode.HALF_EVEN).abs()
                            .doubleValue());
        else
            currentCell.setCellValue("");
        currentCell.setCellStyle(styleCurrency);
        currentCell = currentRow.createCell(currentColumn++);
        if ((credit.subtract(debit).compareTo(new BigDecimal(0))) > 0)
            currentCell.setCellValue(
                    credit.subtract(debit).setScale(2, RoundingMode.HALF_EVEN).abs()
                            .doubleValue());
        else
            currentCell.setCellValue("");
        currentCell.setCellStyle(styleCurrency);

        // autosize
        for (int i = 0; i < 11; i++)
            currentSheet.autoSizeColumn(i, true);

        File file;
        FileOutputStream outputStream;
        try {
            file = File.createTempFile("Relevé de compte - " + createHelper.createDataFormat().getFormat("dd-mm-yyyy"),
                    "xlsx");
            outputStream = new FileOutputStream(file);
        } catch (IOException e) {
            try {
                wb.close();
            } catch (IOException e2) {
                throw new OsirisException(e, "Unable to close excel file");
            }
            throw new OsirisException(e, "Unable to create temp file");
        }

        try {
            wb.write(outputStream);
            wb.close();
            outputStream.close();
        } catch (IOException e) {
            throw new OsirisException(e, "Unable to save excel file");
        }

        return file;
    }
}
