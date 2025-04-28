package com.jss.osiris.modules.osiris.accounting.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

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
                        isOrderingByEventDate, false, false);

                return generatePdfDelegate.getBillingClosureReceiptFile(tier, responsable, values);
            }

            // Send all to tiers
            if (billingClosureDocument.getBillingClosureRecipientType() != null
                    && (billingClosureDocument.getBillingClosureRecipientType().getId()
                            .equals(constantService.getBillingClosureRecipientTypeClient().getId())
                            || billingClosureDocument.getBillingClosureRecipientType().getId()
                                    .equals(constantService.getBillingClosureRecipientTypeOther().getId()))) {

                List<BillingClosureReceiptValue> values = generateBillingClosureValuesForITiers(tier, responsable,
                        isOrderingByEventDate, false, false);
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

                        List<BillingClosureReceiptValue> values = generateBillingClosureValuesForITiers(null,
                                tiersResponsable, isOrderingByEventDate, false, false);
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
        return null;
    }

    public List<BillingClosureReceiptValue> generateBillingClosureValuesForITiers(Tiers tiers, Responsable responsable,
            boolean isOrderingByEventDate, boolean hideAffaires, boolean hideServices)
            throws OsirisException, OsirisClientMessageException {

        // Find all elements
        ArrayList<BillingClosureReceiptValue> values = new ArrayList<BillingClosureReceiptValue>();

        // Find customer orders
        ArrayList<Responsable> responsableList = new ArrayList<Responsable>();

        if (tiers != null && tiers.getResponsables() != null) {
            values.add(new BillingClosureReceiptValue(
                    tiers.getDenomination() != null ? tiers.getDenomination()
                            : (tiers.getFirstname() + " " + tiers.getLastname())));
            for (Responsable responsableOfTiers : tiers.getResponsables())
                responsableList.add(responsableOfTiers);
        }
        if (responsable != null) {
            responsableList.add(responsable);
        }

        List<CustomerOrder> customerOrders = customerOrderService
                .searchOrders(customerOrderStatusService.getCustomerOrderStatus().stream()
                        .filter(status -> !status.getCode().equals(CustomerOrderStatus.BILLED) &&
                                !status.getCode().equals(CustomerOrderStatus.ABANDONED))
                        .collect(Collectors.toList()), null);

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
        return values;
    }

    private BillingClosureReceiptValue getBillingClosureReceiptValueForInvoice(Invoice invoice, boolean hideAffaires,
            boolean hideServices)
            throws OsirisException, OsirisClientMessageException {
        BillingClosureReceiptValue value = new BillingClosureReceiptValue();
        value.setDisplayBottomBorder(true);
        value.setDebitAmount(invoice.getTotalPrice());
        value.setCreditAmount(null);
        value.setEventDateTime(invoice.getCreatedDate());
        value.setResponsable(invoice.getResponsable());
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
                    .computeMailForCustomerOrderFinalizationAndInvoice(invoice.getCustomerOrder());
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

    private BillingClosureReceiptValue getBillingClosureReceiptValueForPayment(Payment payment,
            boolean displayBottomBorder) {

        BillingClosureReceiptValue value = new BillingClosureReceiptValue();
        value.setDisplayBottomBorder(displayBottomBorder);
        value.setDebitAmount(null);
        if (payment.getInvoice() != null)
            value.setResponsable(payment.getInvoice().getResponsable());
        else if (payment.getCustomerOrder() != null)
            value.setResponsable(payment.getCustomerOrder().getResponsable());
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
                    serviceLabels.add(service.getCustomLabel());

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
}
