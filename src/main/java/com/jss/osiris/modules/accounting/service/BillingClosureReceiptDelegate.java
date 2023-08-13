package com.jss.osiris.modules.accounting.service;

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
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.GlobalExceptionHandler;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisLog;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.GeneratePdfDelegate;
import com.jss.osiris.libs.mail.MailComputeHelper;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.libs.mail.model.MailComputeResult;
import com.jss.osiris.modules.accounting.model.BillingClosureReceiptValue;
import com.jss.osiris.modules.invoicing.model.ICreatedDate;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceSearch;
import com.jss.osiris.modules.invoicing.model.InvoiceSearchResult;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.model.OrderingSearch;
import com.jss.osiris.modules.quotation.model.OrderingSearchResult;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.service.ConfrereService;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.quotation.service.CustomerOrderStatusService;
import com.jss.osiris.modules.tiers.model.ITiers;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;
import com.jss.osiris.modules.tiers.service.ResponsableService;
import com.jss.osiris.modules.tiers.service.TiersService;

@Service
public class BillingClosureReceiptDelegate {

    @Value("${payment.cb.entry.point}")
    private String paymentCbEntryPoint;

    @Autowired
    TiersService tiersService;

    @Autowired
    ResponsableService responsableService;

    @Autowired
    ConfrereService confrereService;

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

    public File getBillingClosureReceiptFile(Integer tiersId, boolean downloadFile)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {

        ITiers tier = null;
        tier = tiersService.getTiers(tiersId);

        if (tier == null)
            tier = responsableService.getResponsable(tiersId);

        if (tier == null)
            tier = confrereService.getConfrere(tiersId);

        Document billingClosureDocument = null;
        if (tier instanceof Responsable)
            billingClosureDocument = documentService
                    .getBillingClosureDocument(((Responsable) tier).getTiers().getDocuments());
        else
            billingClosureDocument = documentService.getBillingClosureDocument(tier.getDocuments());

        if (billingClosureDocument != null) {
            boolean isOrderingByEventDate = billingClosureDocument.getBillingClosureType() != null
                    && !billingClosureDocument
                            .getBillingClosureType().getId()
                            .equals(constantService.getBillingClosureTypeAffaire().getId());

            if (downloadFile) {
                ArrayList<ITiers> tiers = new ArrayList<ITiers>();
                if (tier instanceof Tiers && ((Tiers) tier).getResponsables() != null)
                    tiers.addAll(((Tiers) tier).getResponsables());
                else
                    tiers.add(tier);
                List<BillingClosureReceiptValue> values = generateBillingClosureValuesForITiers(tiers,
                        isOrderingByEventDate);
                return generatePdfDelegate.getBillingClosureReceiptFile(tier, values);
            }

            // Send all to tiers
            if (billingClosureDocument.getBillingClosureRecipientType() != null
                    && (billingClosureDocument.getBillingClosureRecipientType().getId()
                            .equals(constantService.getBillingClosureRecipientTypeClient().getId())
                            || billingClosureDocument.getBillingClosureRecipientType().getId()
                                    .equals(constantService.getBillingClosureRecipientTypeOther().getId()))) {

                ArrayList<ITiers> tiers = new ArrayList<ITiers>();
                if (tier instanceof Tiers && ((Tiers) tier).getResponsables() != null)
                    tiers.addAll(((Tiers) tier).getResponsables());
                else
                    tiers.add(tier);

                List<BillingClosureReceiptValue> values = generateBillingClosureValuesForITiers(tiers,
                        isOrderingByEventDate);
                if (values.size() > 0) {
                    try {
                        sendBillingClosureReceiptFile(
                                generatePdfDelegate.getBillingClosureReceiptFile(tier, values),
                                tier);
                    } catch (Exception e) {
                        globalExceptionHandler.persistLog(
                                new OsirisException(e, "Impossible to generate billing closure for Tiers " + tiersId),
                                OsirisLog.UNHANDLED_LOG);
                    }
                }

            } else if (billingClosureDocument.getBillingClosureRecipientType() != null
                    && billingClosureDocument.getBillingClosureRecipientType().getId()
                            .equals(constantService.getBillingClosureRecipientTypeResponsable().getId())) {
                // Send to each responsable
                if (tier instanceof Tiers && ((Tiers) tier).getResponsables() != null)
                    for (Responsable responsable : ((Tiers) tier).getResponsables()) {

                        ArrayList<ITiers> tiers = new ArrayList<ITiers>();
                        tiers.add(responsable);
                        List<BillingClosureReceiptValue> values = generateBillingClosureValuesForITiers(tiers,
                                isOrderingByEventDate);
                        if (values.size() > 0) {
                            try {
                                sendBillingClosureReceiptFile(
                                        generatePdfDelegate.getBillingClosureReceiptFile(responsable, values),
                                        tier);
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

    private List<BillingClosureReceiptValue> generateBillingClosureValuesForITiers(ArrayList<ITiers> tiers,
            boolean isOrderingByEventDate) throws OsirisException, OsirisClientMessageException {

        // Find all elements
        ArrayList<BillingClosureReceiptValue> values = new ArrayList<BillingClosureReceiptValue>();

        // Find customer orders
        OrderingSearch search = new OrderingSearch();
        ArrayList<Tiers> tiersList = new ArrayList<Tiers>();
        for (ITiers tiersIn : tiers) {
            tiersList = new ArrayList<Tiers>();
            boolean hadSomeValues = false;
            if (tiersIn instanceof Tiers) {
                Tiers t = (Tiers) tiersIn;
                tiersList.add(t);
                values.add(new BillingClosureReceiptValue(
                        t.getDenomination() != null ? t.getDenomination()
                                : (t.getFirstname() + " " + t.getLastname())));
            } else if (tiersIn instanceof Confrere) {
                Tiers fakeTiers = new Tiers();
                fakeTiers.setId(tiersIn.getId());
                tiersList.add(fakeTiers);
                values.add(new BillingClosureReceiptValue(((Confrere) tiersIn).getLabel()));
            } else if (tiersIn instanceof Responsable) {
                Tiers fakeTiers = new Tiers();
                fakeTiers.setId(tiersIn.getId());
                tiersList.add(fakeTiers);
                Responsable t = (Responsable) tiersIn;
                values.add(new BillingClosureReceiptValue((t.getFirstname() + " " + t.getLastname())));
            }

            search.setCustomerOrders(tiersList);

            search.setCustomerOrderStatus(customerOrderStatusService.getCustomerOrderStatus().stream()
                    .filter(status -> !status.getCode().equals(CustomerOrderStatus.BILLED) &&
                            !status.getCode().equals(CustomerOrderStatus.ABANDONED))
                    .collect(Collectors.toList()));

            List<OrderingSearchResult> customerOrdersList = customerOrderService.searchOrders(search);
            ArrayList<CustomerOrder> customerOrders = new ArrayList<CustomerOrder>();

            if (customerOrdersList != null && customerOrdersList.size() > 0) {
                for (OrderingSearchResult customerOrder : customerOrdersList) {
                    if (customerOrder.getDepositTotalAmount() != null && customerOrder.getDepositTotalAmount() > 0) {
                        CustomerOrder completeCustomerOrder = customerOrderService
                                .getCustomerOrder(customerOrder.getCustomerOrderId());
                        customerOrders.add(completeCustomerOrder);
                    }
                }
            }

            // Find invoices
            InvoiceSearch invoiceSearch = new InvoiceSearch();
            invoiceSearch.setCustomerOrders(tiersList);
            invoiceSearch.setInvoiceStatus(Arrays.asList(constantService.getInvoiceStatusSend()));

            List<InvoiceSearchResult> invoiceList = invoiceService.searchInvoices(invoiceSearch);
            ArrayList<Invoice> invoices = new ArrayList<Invoice>();
            if (invoiceList != null && invoiceList.size() > 0) {
                for (InvoiceSearchResult invoice : invoiceList) {
                    Invoice completeInvoice = invoiceService.getInvoice(invoice.getInvoiceId());
                    invoices.add(completeInvoice);
                }
            }

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
                        if (input instanceof CustomerOrder)
                            values.add(getBillingClosureReceiptValueForCustomerOrder((CustomerOrder) input));
                        if (input instanceof Invoice)
                            values.add(getBillingClosureReceiptValueForInvoice((Invoice) input));
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
                ArrayList<Object> allInputs = new ArrayList<Object>();
                if (customerOrders != null && customerOrders.size() > 0)
                    allInputs.addAll(customerOrders);
                if (invoices != null && invoices.size() > 0)
                    allInputs.addAll(invoices);

                if (allInputs.size() > 0) {
                    hadSomeValues = true;
                    allInputs.sort(new Comparator<Object>() {

                        @Override
                        public int compare(Object o1, Object o2) {
                            Affaire affaire1 = null;
                            Affaire affaire2 = null;
                            if (o1 instanceof CustomerOrder)
                                affaire1 = ((CustomerOrder) o1).getAssoAffaireOrders().get(0).getAffaire();
                            if (o1 instanceof Invoice && ((Invoice) o1).getCustomerOrder() != null)
                                affaire1 = ((Invoice) o1).getCustomerOrder().getAssoAffaireOrders().get(0).getAffaire();
                            if (o2 instanceof CustomerOrder)
                                affaire2 = ((CustomerOrder) o2).getAssoAffaireOrders().get(0).getAffaire();
                            if (o2 instanceof Invoice && ((Invoice) o2).getCustomerOrder() != null)
                                affaire2 = ((Invoice) o2).getCustomerOrder().getAssoAffaireOrders().get(0).getAffaire();

                            if (affaire1 != null && affaire2 == null)
                                return 1;
                            if (affaire1 == null && affaire2 != null)
                                return -1;
                            if (affaire1 == null && affaire2 == null)
                                return 0;

                            if (affaire1 != null && affaire2 != null)
                                return (affaire1.getDenomination() != null ? affaire1.getDenomination()
                                        : (affaire1.getFirstname() + affaire1.getLastname()))
                                        .compareTo((affaire2.getDenomination() != null ? affaire2.getDenomination()
                                                : (affaire2.getFirstname() + affaire2.getLastname())));
                            return 0;
                        }
                    });
                }

                for (Object input : allInputs) {
                    if (input instanceof CustomerOrder) {
                        CustomerOrder customerOrder = (CustomerOrder) input;
                        BillingClosureReceiptValue valueCustomerOrder = getBillingClosureReceiptValueForCustomerOrder(
                                customerOrder);
                        values.add(valueCustomerOrder);
                        if (customerOrder.getPayments() != null && customerOrder.getPayments().size() > 0) {
                            valueCustomerOrder.setDisplayBottomBorder(false);
                            for (Payment payment : customerOrder.getPayments())
                                if (!payment.getIsCancelled())
                                    values.add(getBillingClosureReceiptValueForDeposit(payment, customerOrder,
                                            customerOrder.getPayments()
                                                    .indexOf(payment) == customerOrder.getPayments().size() - 1));
                        }
                    }
                    if (input instanceof Invoice) {
                        Invoice invoice = (Invoice) input;
                        BillingClosureReceiptValue valueInvoice = getBillingClosureReceiptValueForInvoice(invoice);
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

    private BillingClosureReceiptValue getBillingClosureReceiptValueForCustomerOrder(CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException {
        BillingClosureReceiptValue value = new BillingClosureReceiptValue();
        value.setDisplayBottomBorder(true);
        value.setDebitAmount(null);
        value.setCreditAmount(null);
        value.setEventDateTime(customerOrder.getCreatedDate());
        value.setEventDateString(customerOrder.getCreatedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        value.setEventDescription(
                "Commande n°" + customerOrder.getId() + " - " + customerOrder.getCustomerOrderStatus().getLabel()
                        + "<br/>"
                        + String.join("<br/>", getAllAffairesLabelForCustomerOrder(customerOrder)).replaceAll("&",
                                "<![CDATA[&]]>")
                        + "<br/>"
                        + String.join("<br/>", getAllProvisionLabelForCustomerOrder(customerOrder)).replaceAll("&",
                                "<![CDATA[&]]>"));

        if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.WAITING_DEPOSIT)) {
            MailComputeResult mailComputeResult = mailComputeHelper
                    .computeMailForCustomerOrderFinalizationAndInvoice(customerOrder);
            value.setEventCbLink(paymentCbEntryPoint + "/order/deposit?mail="
                    + mailComputeResult.getRecipientsMailTo().get(0).getMail() + "&customerOrderId="
                    + customerOrder.getId());
        }

        return value;
    }

    private BillingClosureReceiptValue getBillingClosureReceiptValueForInvoice(Invoice invoice)
            throws OsirisException, OsirisClientMessageException {
        BillingClosureReceiptValue value = new BillingClosureReceiptValue();
        value.setDisplayBottomBorder(true);
        value.setDebitAmount(invoice.getTotalPrice());
        value.setCreditAmount(null);
        value.setEventDateTime(invoice.getCreatedDate());
        value.setEventDateString(invoice.getCreatedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        value.setEventDescription("Facture n°" + invoice.getId());

        if (invoice.getCustomerOrder() != null) {
            String customerOrderValue = invoice.getCustomerOrder().getId() != null
                    ? " / Commande n°" + invoice.getCustomerOrder().getId()
                    : "";
            value.setEventDescription(
                    "Facture n°" + invoice.getId() + customerOrderValue
                            + "<br/>"
                            + String.join("<br/>", getAllAffairesLabelForCustomerOrder(invoice.getCustomerOrder()))
                                    .replaceAll("&",
                                            "<![CDATA[&]]>")
                            + "<br/>"
                            + String.join("<br/>", getAllProvisionLabelForCustomerOrder(invoice.getCustomerOrder()))
                                    .replaceAll("&",
                                            "<![CDATA[&]]>"));

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
        value.setEventDateTime(payment.getPaymentDate());
        value.setEventDateString(payment.getPaymentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        String description = payment.getLabel().replaceAll("&", "<![CDATA[&]]>");
        if (customerOrder != null) {
            description += "<br/>"
                    + String.join("<br/>", getAllAffairesLabelForCustomerOrder(customerOrder)).replaceAll("&",
                            "<![CDATA[&]]>");
        }
        value.setEventDescription(description);

        return value;
    }

    private BillingClosureReceiptValue getBillingClosureReceiptValueForPayment(Payment payment,
            boolean displayBottomBorder) {

        BillingClosureReceiptValue value = new BillingClosureReceiptValue();
        value.setDisplayBottomBorder(displayBottomBorder);
        value.setDebitAmount(null);
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

    private List<String> getAllProvisionLabelForCustomerOrder(CustomerOrder customerOrder) {
        ArrayList<String> provisions = new ArrayList<String>();
        if (customerOrder != null && customerOrder.getAssoAffaireOrders() != null
                && customerOrder.getAssoAffaireOrders().size() > 0)
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders()) {
                if (asso.getProvisions() != null && asso.getProvisions().size() > 0)
                    for (Provision provision : asso.getProvisions())
                        provisions
                                .add(provision.getProvisionFamilyType().getLabel() + " - "
                                        + provision.getProvisionType().getLabel());
            }
        return provisions;
    }

    private void sendBillingClosureReceiptFile(File billingClosureReceipt, ITiers tiers)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        List<Attachment> attachments = new ArrayList<Attachment>();
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            String tiersType = "";
            if (tiers instanceof Tiers)
                tiersType = Tiers.class.getSimpleName();
            if (tiers instanceof Responsable)
                tiersType = Responsable.class.getSimpleName();
            if (tiers instanceof Confrere)
                tiersType = Confrere.class.getSimpleName();

            List<Attachment> attachmentsList = attachmentService.addAttachment(
                    new FileInputStream(billingClosureReceipt), tiers.getId(),
                    tiersType, constantService.getAttachmentTypeBillingClosure(),
                    "Relevé de compte du " + LocalDateTime.now().format(formatter) + ".pdf", false,
                    "Relevé de compte du " + LocalDateTime.now().format(formatter));

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
            mailHelper.sendBillingClosureToCustomer(attachments, tiers, false);
        } catch (Exception e) {
            globalExceptionHandler.persistLog(
                    new OsirisException(e, "Impossible to send billing closure mail for Tiers " + tiers.getId()),
                    OsirisLog.UNHANDLED_LOG);
        }
    }
}
