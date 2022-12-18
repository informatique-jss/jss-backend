package com.jss.osiris.modules.quotation.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.jss.osiris.libs.JacksonLocalDateDeserializer;
import com.jss.osiris.libs.JacksonLocalDateSerializer;
import com.jss.osiris.libs.JacksonLocalDateTimeDeserializer;
import com.jss.osiris.libs.JacksonLocalDateTimeSerializer;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.Deposit;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.service.DepositService;
import com.jss.osiris.modules.invoicing.service.InvoiceHelper;
import com.jss.osiris.modules.invoicing.service.InvoiceItemService;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.miscellaneous.service.MailService;
import com.jss.osiris.modules.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.miscellaneous.service.PhoneService;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.Announcement;
import com.jss.osiris.modules.quotation.model.AnnouncementStatus;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.model.OrderingSearch;
import com.jss.osiris.modules.quotation.model.OrderingSearchResult;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.model.centralPay.CentralPayPaymentRequest;
import com.jss.osiris.modules.quotation.repository.CustomerOrderRepository;
import com.jss.osiris.modules.tiers.model.ITiers;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

@Service
public class CustomerOrderServiceImpl implements CustomerOrderService {

    @Autowired
    CustomerOrderRepository customerOrderRepository;

    @Autowired
    IndexEntityService indexEntityService;

    @Autowired
    PhoneService phoneService;

    @Autowired
    MailService mailService;

    @Autowired
    QuotationService quotationService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    InvoiceHelper invoiceHelper;

    @Autowired
    AccountingRecordService accountingRecordService;

    @Autowired
    CustomerOrderStatusService customerOrderStatusService;

    @Autowired
    DepositService depositService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    InvoiceItemService invoiceItemService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    ConstantService constantService;

    @Autowired
    MailHelper mailHelper;

    @Autowired
    AssoAffaireOrderService assoAffaireOrderService;

    @Autowired
    CentralPayDelegateService centralPayDelegateService;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    DocumentService documentService;

    @Value("${payment.cb.redirect.deposit.entry.point}")
    private String paymentCbRedirectDeposit;

    @Value("${payment.cb.redirect.invoice.entry.point}")
    private String paymentCbRedirectInvoice;

    @Override
    public CustomerOrder getCustomerOrder(Integer id) {
        Optional<CustomerOrder> customerOrder = customerOrderRepository.findById(id);
        if (customerOrder.isPresent())
            return customerOrder.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerOrder addOrUpdateCustomerOrderFromUser(CustomerOrder customerOrder)
            throws OsirisException {
        return addOrUpdateCustomerOrder(customerOrder);
    }

    @Override
    public CustomerOrder addOrUpdateCustomerOrder(CustomerOrder customerOrder)
            throws OsirisException {
        if (customerOrder.getId() == null)
            customerOrder.setCreatedDate(LocalDateTime.now());

        customerOrder.setIsQuotation(false);

        if (customerOrder.getDocuments() != null)
            for (Document document : customerOrder.getDocuments()) {
                mailService.populateMailIds(document.getMailsAffaire());
                mailService.populateMailIds(document.getMailsClient());
                document.setCustomerOrder(customerOrder);
            }

        // Complete provisions
        for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders()) {
            assoAffaireOrder.setCustomerOrder(customerOrder);
            assoAffaireOrderService.completeAssoAffaireOrder(assoAffaireOrder, customerOrder);
        }

        boolean isNewCustomerOrder = customerOrder.getId() == null;

        customerOrder = customerOrderRepository.save(customerOrder);

        quotationService.getAndSetInvoiceItemsForQuotation(customerOrder, true);

        customerOrder = getCustomerOrder(customerOrder.getId());

        indexEntityService.indexEntity(customerOrder, customerOrder.getId());
        for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders())
            indexEntityService.indexEntity(assoAffaireOrder, assoAffaireOrder.getId());

        if (isNewCustomerOrder)
            notificationService.notifyNewCustomerOrderQuotation(customerOrder);

        checkAllProvisionEnded(customerOrder);
        generateStoreAndSendPublicationReceipt(customerOrder);
        generateStoreAndSendPublicationFlag(customerOrder);
        return customerOrder;
    }

    @Override
    public CustomerOrder checkAllProvisionEnded(CustomerOrder customerOrderIn)
            throws OsirisException {
        CustomerOrder customerOrder = getCustomerOrder(customerOrderIn.getId());
        if (customerOrder != null && customerOrder.getAssoAffaireOrders() != null
                && customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BEING_PROCESSED)) {
            for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders())
                if (assoAffaireOrder.getProvisions() != null)
                    for (Provision provision : assoAffaireOrder.getProvisions()) {
                        if (provision.getAnnouncement() != null
                                && !provision.getAnnouncement().getAnnouncementStatus().getIsCloseState())
                            return customerOrderIn;
                        if (provision.getBodacc() != null
                                && !provision.getBodacc().getBodaccStatus().getIsCloseState())
                            return customerOrderIn;
                        if (provision.getFormalite() != null
                                && !provision.getFormalite().getFormaliteStatus().getIsCloseState())
                            return customerOrderIn;
                        if (provision.getSimpleProvision() != null
                                && !provision.getSimpleProvision().getSimpleProvisionStatus().getIsCloseState())
                            return customerOrderIn;
                        if (provision.getDomiciliation() != null
                                && !provision.getDomiciliation().getDomiciliationStatus().getIsCloseState())
                            return customerOrderIn;
                    }
            return addOrUpdateCustomerOrderStatus(customerOrder, CustomerOrderStatus.TO_BILLED, false);
        }
        return customerOrder;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerOrder addOrUpdateCustomerOrderStatusFromUser(CustomerOrder customerOrder, String targetStatusCode)
            throws OsirisException {
        return addOrUpdateCustomerOrderStatus(customerOrder, targetStatusCode, true);
    }

    @Override
    public CustomerOrder addOrUpdateCustomerOrderStatus(CustomerOrder customerOrder, String targetStatusCode,
            boolean isFromUser)
            throws OsirisException {
        CustomerOrderStatus targetCustomerStatus = customerOrderStatusService
                .getCustomerOrderStatusByCode(targetStatusCode);
        if (targetCustomerStatus == null)
            throw new OsirisException(null, "Customer order status not found for code " + targetStatusCode);
        if (targetCustomerStatus.getCode().equals(CustomerOrderStatus.BEING_PROCESSED)) {
            if (!isFromUser
                    && customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.WAITING_DEPOSIT)) {
                mailHelper.sendCustomerOrderDepositConfirmationToCustomer(customerOrder, false);
            }
            notificationService.notifyCustomerOrderToBeingProcessed(customerOrder, true);
        }
        if (targetCustomerStatus.getCode().equals(CustomerOrderStatus.TO_BILLED))
            notificationService.notifyCustomerOrderToBeingToBilled(customerOrder);
        if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BILLED)
                && targetCustomerStatus.getCode().equals(CustomerOrderStatus.TO_BILLED)) {
            Invoice invoiceToCancel = null;
            if (customerOrder.getInvoices() != null)
                for (Invoice invoice : customerOrder.getInvoices())
                    if (!invoice.getInvoiceStatus().getId()
                            .equals(constantService.getInvoiceStatusCancelled().getId())) {
                        invoiceToCancel = invoice;
                        break;
                    }
            moveInvoiceDepositToCustomerOrderDeposit(customerOrder, invoiceToCancel);
            invoiceService.cancelInvoice(invoiceToCancel);
        }

        if (targetStatusCode.equals(CustomerOrderStatus.BILLED)) {
            Invoice invoice = generateInvoice(customerOrder);
            accountingRecordService.generateAccountingRecordsForSaleOnInvoiceGeneration(
                    getInvoice(customerOrder));
            // If deposit already set, associate them to invoice
            moveCustomerOrderDepositToInvoiceDeposit(customerOrder, invoice);

            // Check invoice payed
            Float remainingToPayForCurrentInvoice = accountingRecordService.getRemainingAmountToPayForInvoice(invoice);
            if (remainingToPayForCurrentInvoice >= 0) {
                if (customerOrder.getDeposits() != null && customerOrder.getDeposits().size() > 0) {
                    float totalDeposit = 0f;
                    for (Deposit deposit : customerOrder.getDeposits())
                        totalDeposit += deposit.getDepositAmount();
                    accountingRecordService.generateAccountingRecordsForSaleOnInvoicePayment(invoice, null,
                            customerOrder.getDeposits(), totalDeposit);

                    if (remainingToPayForCurrentInvoice == 0) {
                        invoice.setInvoiceStatus(constantService.getInvoiceStatusPayed());
                        invoiceService.addOrUpdateInvoice(invoice);
                    }
                }
            } else {
                throw new OsirisException(null, "Impossible to billed, too much money on customerOrder !");
            }

            mailHelper.sendCustomerOrderFinalisationToCustomer(customerOrder, false, false, false);
        }

        if (targetStatusCode.equals(CustomerOrderStatus.WAITING_DEPOSIT))

        {
            Float remainingToPay = Math
                    .round(accountingRecordService.getRemainingAmountToPayForCustomerOrder(customerOrder) * 100f)
                    / 100f;

            ITiers tiers = quotationService.getCustomerOrderOfQuotation(customerOrder);
            boolean isDepositMandatory = false;
            if (tiers instanceof Tiers)
                isDepositMandatory = ((Tiers) tiers).getIsProvisionalPaymentMandatory();
            if (tiers instanceof Responsable)
                isDepositMandatory = ((Responsable) tiers).getTiers().getIsProvisionalPaymentMandatory();
            if (tiers instanceof Confrere)
                isDepositMandatory = ((Confrere) tiers).getIsProvisionalPaymentMandatory();

            mailHelper.sendCustomerOrderCreationConfirmationToCustomer(customerOrder, true, false);

            if (!isDepositMandatory || remainingToPay <= 0)
                targetStatusCode = CustomerOrderStatus.BEING_PROCESSED;
        }

        if (targetStatusCode.equals(CustomerOrderStatus.BEING_PROCESSED)
                && customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.OPEN))
            mailHelper.sendCustomerOrderCreationConfirmationToCustomer(customerOrder, true, false);

        CustomerOrderStatus customerOrderStatus = customerOrderStatusService
                .getCustomerOrderStatusByCode(
                        targetStatusCode);
        if (customerOrderStatus == null)
            throw new OsirisException(null, "Quotation status not found for code " + targetStatusCode);

        customerOrder.setCustomerOrderStatus(customerOrderStatus);
        customerOrder.setLastStatusUpdate(LocalDateTime.now());
        return this.addOrUpdateCustomerOrder(customerOrder);
    }

    private void moveCustomerOrderDepositToInvoiceDeposit(CustomerOrder customerOrder, Invoice invoice)
            throws OsirisException {
        if (customerOrder.getDeposits() != null && customerOrder.getDeposits().size() > 0)
            for (Deposit deposit : customerOrder.getDeposits()) {
                depositService.moveDepositFromCustomerOrderToInvoice(deposit, customerOrder, invoice);
            }
    }

    private void moveInvoiceDepositToCustomerOrderDeposit(CustomerOrder customerOrder, Invoice invoice)
            throws OsirisException {
        if (invoice.getDeposits() != null && invoice.getDeposits().size() > 0)
            for (Deposit deposit : invoice.getDeposits()) {
                depositService.moveDepositFromInvoiceToCustomerOrder(deposit, invoice, customerOrder);
            }
    }

    @Override
    public CustomerOrder unlockCustomerOrderFromDeposit(CustomerOrder customerOrder, Float effectivePayment)
            throws OsirisException {
        Float remainingToPay = accountingRecordService
                .getRemainingAmountToPayForCustomerOrder(customerOrder);

        if (customerOrder.getCustomerOrderStatus().getCode()
                .equals(CustomerOrderStatus.WAITING_DEPOSIT)
                && remainingToPay - effectivePayment <= 0) {
            addOrUpdateCustomerOrderStatus(customerOrder, CustomerOrderStatus.BEING_PROCESSED, false);
            notificationService.notifyCustomerOrderToBeingProcessed(customerOrder, false);
        }
        return customerOrder;
    }

    private Invoice generateInvoice(CustomerOrder customerOrder) throws OsirisException {
        if (!hasAtLeastOneInvoiceItemNotNull(customerOrder))
            throw new OsirisException(null, "No invoice item found on customer order " + customerOrder.getId());

        // Generate blank invoice
        Invoice invoice = invoiceService.createInvoice(customerOrder,
                quotationService.getCustomerOrderOfQuotation(customerOrder));
        invoice.setInvoiceItems(new ArrayList<InvoiceItem>());
        // Associate invoice to invoice item
        for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders()) {
            for (Provision provision : assoAffaireOrder.getProvisions()) {
                if (provision.getInvoiceItems() != null && provision.getInvoiceItems().size() > 0)
                    for (InvoiceItem invoiceItem : provision.getInvoiceItems()) {
                        invoiceItem.setInvoice(invoice);
                        invoice.getInvoiceItems().add(invoiceItem);
                    }
            }
        }

        invoice.setCustomerOrder(customerOrder);
        invoiceHelper.setPriceTotal(invoice);
        invoiceService.addOrUpdateInvoice(invoice);

        // Create invoice PDF and attach it to customerOrder
        File invoicePdf = mailHelper.generateInvoicePdf(customerOrder, invoice);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmm");
        try {
            attachmentService.addAttachment(new FileInputStream(invoicePdf), customerOrder.getId(),
                    CustomerOrder.class.getSimpleName(),
                    constantService.getAttachmentTypeInvoice(),
                    "Invoice_" + formatter.format(LocalDateTime.now()) + ".pdf",
                    false, "Facture n°" + invoice.getId());
        } catch (FileNotFoundException e) {
            throw new OsirisException(e, "Impossible to read invoice PDF temp file");
        } finally {
            invoicePdf.delete();
        }

        return invoice;
    }

    private boolean hasAtLeastOneInvoiceItemNotNull(CustomerOrder customerOrder) {
        for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders()) {
            if (customerOrder != null && assoAffaireOrder.getProvisions() != null
                    && assoAffaireOrder.getProvisions().size() > 0)
                for (Provision provision : assoAffaireOrder.getProvisions()) {
                    if (provision.getInvoiceItems() != null && provision.getInvoiceItems().size() > 0)
                        return true;
                }
        }
        return false;
    }

    /**
     * Assert that provided customerOrder has been billed and throw exception if no
     * invoice is found
     * You can check if customerOrder has been billed with hasBeenBilled method of
     * current class
     * 
     * @param customerOrder
     * @return Invoice
     * @throws OsirisException
     * @
     */
    private Invoice getInvoice(CustomerOrder customerOrder) throws OsirisException {
        if (customerOrder == null || customerOrder.getAssoAffaireOrders() == null
                || customerOrder.getAssoAffaireOrders().get(0).getProvisions() == null
                || customerOrder.getAssoAffaireOrders().get(0).getProvisions().size() == 0
                || customerOrder.getAssoAffaireOrders().get(0).getProvisions().get(0) == null
                || customerOrder.getAssoAffaireOrders().get(0).getProvisions().get(0).getInvoiceItems() == null
                || customerOrder.getAssoAffaireOrders().get(0).getProvisions().get(0).getInvoiceItems().size() == 0
                || customerOrder.getAssoAffaireOrders().get(0).getProvisions().get(0).getInvoiceItems().get(0) == null)
            throw new OsirisException(null, "No invoice found");

        return customerOrder.getAssoAffaireOrders().get(0).getProvisions().get(0).getInvoiceItems().get(0).getInvoice();
    }

    @Override
    public List<OrderingSearchResult> searchOrders(OrderingSearch orderingSearch) {
        ArrayList<Integer> statusId = new ArrayList<Integer>();
        if (orderingSearch.getCustomerOrderStatus() != null && orderingSearch.getCustomerOrderStatus().size() > 0) {
            for (CustomerOrderStatus customerOrderStatus : orderingSearch.getCustomerOrderStatus())
                statusId.add(customerOrderStatus.getId());
        } else {
            statusId.add(0);
        }

        ArrayList<Integer> salesEmployeeId = new ArrayList<Integer>();
        if (orderingSearch.getSalesEmployee() != null) {
            for (Employee employee : employeeService.getMyHolidaymaker(orderingSearch.getSalesEmployee()))
                salesEmployeeId.add(employee.getId());
        } else {
            salesEmployeeId.add(0);
        }

        ArrayList<Integer> customerOrderId = new ArrayList<Integer>();
        if (orderingSearch.getCustomerOrders() != null && orderingSearch.getCustomerOrders().size() > 0) {
            for (ITiers tiers : orderingSearch.getCustomerOrders())
                customerOrderId.add(tiers.getId());
        } else {
            customerOrderId.add(0);
        }

        ArrayList<Integer> affaireId = new ArrayList<Integer>();
        if (orderingSearch.getAffaires() != null && orderingSearch.getAffaires().size() > 0) {
            for (Affaire affaire : orderingSearch.getAffaires())
                affaireId.add(affaire.getId());
        } else {
            affaireId.add(0);
        }

        List<OrderingSearchResult> customerOrders = customerOrderRepository.findCustomerOrders(
                salesEmployeeId,
                statusId,
                orderingSearch.getStartDate().withHour(0).withMinute(0),
                orderingSearch.getEndDate().withHour(23).withMinute(59), customerOrderId, affaireId);
        return customerOrders;
    }

    @Override
    public void reindexCustomerOrder() {
        List<CustomerOrder> customerOrders = IterableUtils.toList(customerOrderRepository.findAll());
        if (customerOrders != null)
            for (CustomerOrder customerOrder : customerOrders)
                indexEntityService.indexEntity(customerOrder, customerOrder.getId());
    }

    @Override
    public CustomerOrder createNewCustomerOrderFromQuotation(Quotation quotation)
            throws OsirisException {
        CustomerOrderStatus statusOpen = customerOrderStatusService
                .getCustomerOrderStatusByCode(CustomerOrderStatus.OPEN);
        CustomerOrder customerOrder = new CustomerOrder(quotation.getTiers(), quotation.getResponsable(),
                quotation.getConfrere(), quotation.getSpecialOffers(), LocalDateTime.now(), statusOpen,
                quotation.getObservations(), quotation.getDescription(), null,
                quotation.getDocuments(), quotation.getAssoAffaireOrders(), null,
                quotation.getOverrideSpecialOffer(), quotation.getQuotationLabel(), false, null, null, null, null);

        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule("SimpleModule");
        simpleModule.addSerializer(LocalDateTime.class, new JacksonLocalDateTimeSerializer());
        simpleModule.addSerializer(LocalDate.class, new JacksonLocalDateSerializer());
        simpleModule.addDeserializer(LocalDateTime.class, new JacksonLocalDateTimeDeserializer());
        simpleModule.addDeserializer(LocalDate.class, new JacksonLocalDateDeserializer());
        objectMapper.registerModule(simpleModule);
        String customerOrderString;
        try {
            customerOrderString = objectMapper.writeValueAsString(customerOrder);
        } catch (JsonProcessingException e) {
            throw new OsirisException(e,
                    "Error when cloning quotation to customer order for quotation " + quotation.getId());
        }

        CustomerOrder customerOrder2;
        try {
            customerOrder2 = objectMapper.readValue(customerOrderString, CustomerOrder.class);
        } catch (JsonProcessingException e) {
            throw new OsirisException(e,
                    "Error when reading clone of quotation for quotation " + quotation.getId());
        }

        if (customerOrder2.getDocuments() != null) {
            ArrayList<Document> documents = new ArrayList<Document>();
            for (Document document : customerOrder2.getDocuments()) {
                document = documentService.cloneDocument(document);
                document.setId(null);
                documents.add(document);
            }
            customerOrder2.setDocuments(documents);
        }

        if (customerOrder2.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder asso : customerOrder2.getAssoAffaireOrders()) {
                asso.setId(null);
                asso.setCustomerOrder(null);
                asso.setQuotation(null);
                if (asso.getProvisions() != null)
                    for (Provision provision : asso.getProvisions()) {
                        provision.setId(null);
                        if (provision.getAnnouncement() != null) {
                            provision.getAnnouncement().setId(null);
                            provision.getAnnouncement().setAttachments(null);
                            if (provision.getAnnouncement().getDocuments() != null)
                                for (Document document : provision.getAnnouncement().getDocuments())
                                    document.setId(null);
                        }
                        if (provision.getBodacc() != null) {
                            provision.getBodacc().setId(null);
                            provision.getBodacc().setAttachments(null);
                            if (provision.getBodacc().getDocuments() != null)
                                for (Document document : provision.getBodacc().getDocuments())
                                    document.setId(null);
                        }
                        if (provision.getFormalite() != null) {
                            provision.getFormalite().setId(null);
                            provision.getFormalite().setAttachments(null);
                            if (provision.getFormalite().getDocuments() != null)
                                for (Document document : provision.getFormalite().getDocuments())
                                    document.setId(null);
                        }
                        if (provision.getDomiciliation() != null) {
                            provision.getDomiciliation().setId(null);
                            provision.getDomiciliation().setAttachments(null);
                            if (provision.getDomiciliation().getDocuments() != null)
                                for (Document document : provision.getDomiciliation().getDocuments())
                                    document.setId(null);
                        }
                        if (provision.getInvoiceItems() != null)
                            for (InvoiceItem invoiceItem : provision.getInvoiceItems())
                                invoiceItem.setId(null);
                        provision.setAttachments(null);
                    }
            }
        addOrUpdateCustomerOrder(customerOrder2);

        return customerOrder2;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateInvoiceMail(CustomerOrder customerOrder) throws OsirisException {
        mailHelper.sendCustomerOrderFinalisationToCustomer(customerOrder, true, false, false);
    }

    private String getCardPaymentLinkForCustomerOrderPayment(CustomerOrder customerOrder, String mail, String subject,
            String redirectEntrypoint)
            throws OsirisException {
        if (customerOrder.getCentralPayPaymentRequestId() != null) {
            CentralPayPaymentRequest centralPayPaymentRequest = centralPayDelegateService
                    .getPaymentRequest(customerOrder.getCentralPayPaymentRequestId());

            if (centralPayPaymentRequest != null) {
                if (centralPayPaymentRequest.getPaymentRequestStatus().equals(CentralPayPaymentRequest.ACTIVE))
                    centralPayDelegateService.cancelPaymentRequest(customerOrder.getCentralPayPaymentRequestId());

                if (centralPayPaymentRequest.getPaymentRequestStatus().equals(CentralPayPaymentRequest.CLOSED)
                        && centralPayPaymentRequest.getPaymentStatus().equals(CentralPayPaymentRequest.PAID)
                        && customerOrder.getCustomerOrderStatus().getCode()
                                .equals(CustomerOrderStatus.WAITING_DEPOSIT)) {
                    unlockCustomerOrderFromDeposit(customerOrder, centralPayPaymentRequest.getTotalAmount() / 100f);
                    return "ok";
                }
            }
        }

        Float remainingToPay = accountingRecordService.getRemainingAmountToPayForCustomerOrder(customerOrder);
        if (customerOrder.getCentralPayPendingPaymentAmount() != null)
            remainingToPay += customerOrder.getCentralPayPendingPaymentAmount();

        if (remainingToPay > 0) {
            CentralPayPaymentRequest paymentRequest = centralPayDelegateService.generatePayPaymentRequest(
                    accountingRecordService.getRemainingAmountToPayForCustomerOrder(customerOrder), mail,
                    customerOrder.getId() + "", subject);

            customerOrder.setCentralPayPaymentRequestId(paymentRequest.getPaymentRequestId());
            addOrUpdateCustomerOrder(customerOrder);
            return paymentRequest.getBreakdowns().get(0).getEndpoint()
                    + "?urlRedirect=" + redirectEntrypoint + "?customerOrderId=" + customerOrder.getId() + "&delay=0";
        }
        return "ok";
    }

    @Override
    public String getCardPaymentLinkForCustomerOrderDeposit(CustomerOrder customerOrder, String mail, String subject)
            throws OsirisException {
        return getCardPaymentLinkForCustomerOrderPayment(customerOrder, mail, subject, paymentCbRedirectDeposit);
    }

    @Override
    public Boolean validateCardPaymentLinkForCustomerOrderDeposit(CustomerOrder customerOrder)
            throws OsirisException {
        if (customerOrder.getCentralPayPaymentRequestId() != null) {
            CentralPayPaymentRequest centralPayPaymentRequest = centralPayDelegateService
                    .getPaymentRequest(customerOrder.getCentralPayPaymentRequestId());

            if (centralPayPaymentRequest != null) {
                if (centralPayPaymentRequest.getPaymentRequestStatus().equals(CentralPayPaymentRequest.CLOSED)
                        && centralPayPaymentRequest.getPaymentStatus().equals(CentralPayPaymentRequest.PAID)) {

                    if (customerOrder.getCustomerOrderStatus().getCode()
                            .equals(CustomerOrderStatus.WAITING_DEPOSIT)) {
                        unlockCustomerOrderFromDeposit(customerOrder, centralPayPaymentRequest.getTotalAmount() / 100f);
                    }
                    customerOrder
                            .setCentralPayPendingPaymentAmount(
                                    (customerOrder.getCentralPayPendingPaymentAmount() != null
                                            ? customerOrder.getCentralPayPendingPaymentAmount()
                                            : 0f)
                                            + centralPayPaymentRequest.getTotalAmount() / 100f);
                    addOrUpdateCustomerOrder(customerOrder);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getCardPaymentLinkForPaymentInvoice(CustomerOrder customerOrder, String mail, String subject)
            throws OsirisException {
        return getCardPaymentLinkForCustomerOrderPayment(customerOrder, mail, subject, paymentCbRedirectInvoice);
    }

    @Override
    public Boolean validateCardPaymentLinkForInvoice(CustomerOrder customerOrder)
            throws OsirisException {
        if (customerOrder.getCentralPayPaymentRequestId() != null) {
            CentralPayPaymentRequest centralPayPaymentRequest = centralPayDelegateService
                    .getPaymentRequest(customerOrder.getCentralPayPaymentRequestId());

            if (centralPayPaymentRequest != null) {
                if (centralPayPaymentRequest.getPaymentRequestStatus().equals(CentralPayPaymentRequest.CLOSED)
                        && centralPayPaymentRequest.getPaymentStatus().equals(CentralPayPaymentRequest.PAID)) {
                }
                // Do nothing, wainting for bank credit to letter invoice
                customerOrder
                        .setCentralPayPendingPaymentAmount((customerOrder.getCentralPayPendingPaymentAmount() != null
                                ? customerOrder.getCentralPayPendingPaymentAmount()
                                : 0f)
                                + centralPayPaymentRequest.getTotalAmount() / 100f);
                addOrUpdateCustomerOrder(customerOrder);
                return true;
            }
        }
        return false;
    }

    @Override
    public void sendRemindersForCustomerOrderDeposit() throws OsirisException {
        List<CustomerOrder> customerOrders = customerOrderRepository.findCustomerOrderForReminder(
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.WAITING_DEPOSIT));

        if (customerOrders != null && customerOrders.size() > 0)
            for (CustomerOrder customerOrder : customerOrders) {
                boolean toSend = false;
                if (customerOrder.getFirstReminderDateTime() == null
                        && customerOrder.getCreatedDate().isBefore(LocalDateTime.now().minusDays(1 * 7))) {
                    toSend = true;
                    customerOrder.setFirstReminderDateTime(LocalDateTime.now());
                } else if (customerOrder.getSecondReminderDateTime() == null
                        && customerOrder.getCreatedDate().isBefore(LocalDateTime.now().minusDays(3 * 7))) {
                    toSend = true;
                    customerOrder.setSecondReminderDateTime(LocalDateTime.now());
                } else if (customerOrder.getCreatedDate().isBefore(LocalDateTime.now().minusDays(6 * 7))) {
                    toSend = true;
                    customerOrder.setThirdReminderDateTime(LocalDateTime.now());
                }

                if (toSend) {
                    mailHelper.sendCustomerOrderCreationConfirmationToCustomer(customerOrder, false, true);
                    addOrUpdateCustomerOrder(customerOrder);
                }
            }
    }

    @Override
    public void generateStoreAndSendPublicationReceipt(CustomerOrder customerOrder) throws OsirisException {

        ArrayList<Announcement> allAnnoucement = new ArrayList<Announcement>();
        ArrayList<Announcement> annoucementWaitingForReading = new ArrayList<Announcement>();
        ArrayList<Announcement> annoucementToGenerate = new ArrayList<Announcement>();
        boolean onlyAnnonceLegale = true;

        if (customerOrder != null && customerOrder.getAssoAffaireOrders() != null) {
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                if (asso.getProvisions() != null)
                    for (Provision provision : asso.getProvisions())
                        if (provision.getAnnouncement() == null)
                            onlyAnnonceLegale = false;
                        // Generate only for JSS Paper
                        else if (provision.getAnnouncement().getConfrere().getId()
                                .equals(constantService.getConfrereJssPaper().getId())) {
                            if (provision.getAnnouncement().getAnnouncementStatus().getCode()
                                    .equals(AnnouncementStatus.ANNOUNCEMENT_WAITING_READ))
                                annoucementWaitingForReading.add(provision.getAnnouncement());

                            // If closed and never generated
                            if (provision.getAnnouncement().getAnnouncementStatus().getCode()
                                    .equals(AnnouncementStatus.ANNOUNCEMENT_DONE)
                                    && provision.getAnnouncement().getIsPublicationReciptAlreadySent() == null)
                                annoucementToGenerate.add(provision.getAnnouncement());

                            allAnnoucement.add(provision.getAnnouncement());
                        }
            if (onlyAnnonceLegale) {
                // If customerOrder is created from a validated quotation
                if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.OPEN)
                        && customerOrder.getQuotations() != null && customerOrder.getQuotations().size() > 0)
                    annoucementToGenerate.addAll(allAnnoucement);

                // If customerOrder is created from scratch
                if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BEING_PROCESSED))
                    annoucementToGenerate.addAll(allAnnoucement);
            } else {
                if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BEING_PROCESSED))
                    annoucementToGenerate.addAll(annoucementWaitingForReading);
            }

            if (annoucementToGenerate.size() > 0) {
                for (Announcement announcement : annoucementToGenerate) {
                    // Check if publication receipt already exists
                    boolean publicationReceiptExists = false;
                    if (announcement.getAttachments() != null)
                        for (Attachment attachment : announcement.getAttachments())
                            if (attachment.getAttachmentType().getId()
                                    .equals(constantService.getAttachmentTypePublicationReceipt().getId()))
                                publicationReceiptExists = true;

                    if (!publicationReceiptExists) {
                        File publicationReceiptPdf = mailHelper.generatePublicationReceiptPdf(announcement);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmm");
                        try {
                            announcement.setAttachments(
                                    attachmentService.addAttachment(new FileInputStream(publicationReceiptPdf),
                                            announcement.getId(),
                                            Announcement.class.getSimpleName(),
                                            constantService.getAttachmentTypePublicationReceipt(),
                                            "Publication_receipt_" + formatter.format(LocalDateTime.now()) + ".pdf",
                                            false, "Attestation de parution n°" + announcement.getId()));
                        } catch (FileNotFoundException e) {
                            throw new OsirisException(e, "Impossible to read invoice PDF temp file");
                        } finally {
                            publicationReceiptPdf.delete();
                        }
                    }
                }
            }

            // Try to send whereas it was JSS or not
            mailHelper.sendPublicationReceiptToCustomer(getCustomerOrder(customerOrder.getId()), false);
        }
    }

    private void generateStoreAndSendPublicationFlag(CustomerOrder customerOrder) throws OsirisException {

        ArrayList<Announcement> annoucementToGenerate = new ArrayList<Announcement>();

        if (customerOrder != null && customerOrder.getAssoAffaireOrders() != null) {
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                if (asso.getProvisions() != null)
                    for (Provision provision : asso.getProvisions())
                        // Only for JSS SPEL
                        if (provision.getAnnouncement() != null
                                && provision.getAnnouncement().getIsPublicationFlagAlreadySent() == null
                                && provision.getAnnouncement().getConfrere().getId()
                                        .equals(constantService.getConfrereJssSpel().getId()))
                            annoucementToGenerate.add(provision.getAnnouncement());

            if (annoucementToGenerate.size() > 0) {
                for (Announcement announcement : annoucementToGenerate) {
                    // Check if publication receipt already exists
                    boolean publicationFlagExists = false;
                    if (announcement.getAttachments() != null)
                        for (Attachment attachment : announcement.getAttachments())
                            if (attachment.getAttachmentType().getId()
                                    .equals(constantService.getAttachmentTypePublicationFlag().getId()))
                                publicationFlagExists = true;

                    if (!publicationFlagExists && announcement.getNotice() != null) {
                        File publicationReceiptPdf = mailHelper.generatePublicationFlagPdf(announcement);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmm");
                        try {
                            announcement.setAttachments(
                                    attachmentService.addAttachment(new FileInputStream(publicationReceiptPdf),
                                            announcement.getId(),
                                            Announcement.class.getSimpleName(),
                                            constantService.getAttachmentTypePublicationFlag(),
                                            "Publication_falg_" + formatter.format(LocalDateTime.now()) + ".pdf",
                                            false, "Témoin de publication n°" + announcement.getId()));
                        } catch (FileNotFoundException e) {
                            throw new OsirisException(e, "Impossible to read invoice PDF temp file");
                        } finally {
                            publicationReceiptPdf.delete();
                        }
                    }
                }
            }
        }

        mailHelper.sendPublicationFlagToCustomer(customerOrder, false);
    }

}
