package com.jss.osiris.modules.quotation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.JacksonLocalDateDeserializer;
import com.jss.osiris.libs.JacksonLocalDateSerializer;
import com.jss.osiris.libs.JacksonLocalDateTimeDeserializer;
import com.jss.osiris.libs.JacksonLocalDateTimeSerializer;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.Deposit;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.service.DepositService;
import com.jss.osiris.modules.invoicing.service.InvoiceHelper;
import com.jss.osiris.modules.invoicing.service.InvoiceItemService;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.MailService;
import com.jss.osiris.modules.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.miscellaneous.service.PhoneService;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.model.OrderingSearch;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.repository.CustomerOrderRepository;

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
    ActiveDirectoryHelper activeDirectoryHelper;

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

    @Override
    public CustomerOrder getCustomerOrder(Integer id) {
        Optional<CustomerOrder> customerOrder = customerOrderRepository.findById(id);
        if (customerOrder.isPresent())
            return customerOrder.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerOrder addOrUpdateCustomerOrderFromUser(CustomerOrder customerOrder) throws Exception {
        return addOrUpdateCustomerOrder(customerOrder);
    }

    @Override
    public CustomerOrder addOrUpdateCustomerOrder(CustomerOrder customerOrder) throws Exception {
        if (customerOrder.getId() == null)
            customerOrder.setCreatedDate(LocalDateTime.now());

        customerOrder.setIsQuotation(false);

        if (customerOrder.getDocuments() != null)
            for (Document document : customerOrder.getDocuments())
                document.setCustomerOrder(customerOrder);

        // Complete domiciliation end date
        for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders()) {
            assoAffaireOrderService.completeAssoAffaireOrder(assoAffaireOrder, customerOrder);
        }

        // If invoice has not been generated yet, recompute billing items
        if (!hasBeenBilled(customerOrder)) {
            quotationService.getAndSetInvoiceItemsForQuotation(customerOrder);
        }

        // Save invoice item
        for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders()) {
            for (Provision provision : assoAffaireOrder.getProvisions()) {
                if (provision.getInvoiceItems() != null)
                    for (InvoiceItem invoiceItem : provision.getInvoiceItems())
                        invoiceItemService.addOrUpdateInvoiceItem(invoiceItem);
            }
        }

        boolean isNewCustomerOrder = customerOrder.getId() == null;
        customerOrder = customerOrderRepository.save(customerOrder);
        indexEntityService.indexEntity(customerOrder, customerOrder.getId());
        for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders())
            indexEntityService.indexEntity(assoAffaireOrder, assoAffaireOrder.getId());

        if (isNewCustomerOrder)
            notificationService.notifyNewCustomerOrderQuotation(customerOrder);

        return checkAllProvisionEnded(customerOrder);
    }

    @Override
    public CustomerOrder checkAllProvisionEnded(CustomerOrder customerOrderIn) throws Exception {
        if (customerOrderIn != null
                && customerOrderIn.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BEING_PROCESSED)) {
            CustomerOrder customerOrder = getCustomerOrder(customerOrderIn.getId());
            if (customerOrder != null && customerOrder.getAssoAffaireOrders() != null
                    && customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BEING_PROCESSED))
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
                            if (provision.getDomiciliation() != null
                                    && !provision.getDomiciliation().getDomiciliationStatus().getIsCloseState())
                                return customerOrderIn;
                        }
            return addOrUpdateCustomerOrderStatus(customerOrder, CustomerOrderStatus.TO_BILLED);
        }
        return customerOrderIn;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerOrder addOrUpdateCustomerOrderStatusFromUser(CustomerOrder customerOrder, String targetStatusCode)
            throws Exception {
        return addOrUpdateCustomerOrderStatus(customerOrder, targetStatusCode);
    }

    @Override
    public CustomerOrder addOrUpdateCustomerOrderStatus(CustomerOrder customerOrder, String targetStatusCode)
            throws Exception {
        CustomerOrderStatus targetCustomerStatus = customerOrderStatusService
                .getCustomerOrderStatusByCode(targetStatusCode);
        if (targetCustomerStatus == null)
            throw new Exception("Customer order status not found for code " + targetStatusCode);
        if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.OPEN)
                && targetCustomerStatus.getCode().equals(CustomerOrderStatus.TO_VERIFY))
            notificationService.notifyCustomerOrderToVerify(customerOrder);
        if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.TO_VERIFY)
                && targetCustomerStatus.getCode().equals(CustomerOrderStatus.WAITING_DEPOSIT)) {
            mailHelper.sendCustomerOrderDepositWaitingToCustomer(customerOrder, true);
        }
        if (targetCustomerStatus.getCode().equals(CustomerOrderStatus.BEING_PROCESSED))
            notificationService.notifyCustomerOrderToBeingProcessed(customerOrder, true);
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
            invoiceService.cancelInvoice(invoiceToCancel);
        }

        if (targetStatusCode.equals(CustomerOrderStatus.BILLED)) {
            Invoice invoice = generateInvoice(customerOrder);
            accountingRecordService.generateAccountingRecordsForSaleOnInvoiceGeneration(
                    getInvoice(customerOrder));
            // If deposit already set, associate them to invoice
            if (customerOrder.getDeposits() != null && customerOrder.getDeposits().size() > 0)
                for (Deposit deposit : customerOrder.getDeposits()) {
                    deposit.setInvoice(invoice);
                    depositService.addOrUpdateDeposit(deposit);
                    for (AccountingRecord accountingRecord : deposit.getAccountingRecords()) {
                        accountingRecord.setInvoice(invoice);
                        accountingRecordService.addOrUpdateAccountingRecord(accountingRecord);
                    }

                    // TODO : changer de compte en contrepasse (attente => client) + check lettrage
                }
            mailHelper.sendCustomerOrderInvoiceToCustomer(customerOrder, invoiceService.getInvoice(invoice.getId()),
                    true);
        }

        if (targetStatusCode.equals(CustomerOrderStatus.WAITING_DEPOSIT)) {
            // TODO : add rule to check if deposit required + check if a deposit already
            // exists to check if we can go further
            targetStatusCode = CustomerOrderStatus.WAITING_DEPOSIT;
        }

        CustomerOrderStatus customerOrderStatus = customerOrderStatusService
                .getCustomerOrderStatusByCode(targetStatusCode);
        if (customerOrderStatus == null)
            throw new Exception("Quotation status not found for code " + targetStatusCode);
        customerOrder.setCustomerOrderStatus(customerOrderStatus);
        return this.addOrUpdateCustomerOrder(customerOrder);
    }

    private Invoice generateInvoice(CustomerOrder customerOrder) throws Exception {
        if (!hasAtLeastOneInvoiceItemNotNull(customerOrder))
            throw new Exception("No invoice item found on customer order " + customerOrder.getId());

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
        return invoiceService.addOrUpdateInvoice(invoice);
    }

    private boolean hasAtLeastOneInvoiceItemNotNull(CustomerOrder customerOrder) {
        for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders()) {
            if (customerOrder != null && assoAffaireOrder.getProvisions() != null
                    && assoAffaireOrder.getProvisions().size() > 0)
                for (Provision provision : assoAffaireOrder.getProvisions()) {
                    if (provision.getInvoiceItems() != null && provision.getInvoiceItems().size() > 0)
                        for (InvoiceItem invoiceItem : provision.getInvoiceItems())
                            if (invoiceItem.getPreTaxPrice() > 0) {
                                return true;
                            }
                }
        }
        return false;
    }

    private boolean hasBeenBilled(CustomerOrder customerOrder) {
        if (customerOrder == null || customerOrder.getAssoAffaireOrders() == null
                || customerOrder.getAssoAffaireOrders().get(0).getProvisions() == null
                || customerOrder.getAssoAffaireOrders().get(0).getProvisions().size() == 0
                || customerOrder.getAssoAffaireOrders().get(0).getProvisions().get(0) == null
                || customerOrder.getAssoAffaireOrders().get(0).getProvisions().get(0).getInvoiceItems() == null
                || customerOrder.getAssoAffaireOrders().get(0).getProvisions().get(0).getInvoiceItems().size() == 0
                || customerOrder.getAssoAffaireOrders().get(0).getProvisions().get(0).getInvoiceItems().get(0) == null)
            return false;

        return customerOrder.getAssoAffaireOrders().get(0).getProvisions().get(0).getInvoiceItems().get(0)
                .getInvoice() != null;
    }

    /**
     * Assert that provided customerOrder has been billed and throw exception if no
     * invoice is found
     * You can check if customerOrder has been billed with hasBeenBilled method of
     * current class
     * 
     * @param customerOrder
     * @return Invoice
     * @throws Exception
     */
    private Invoice getInvoice(CustomerOrder customerOrder) throws Exception {
        if (customerOrder == null || customerOrder.getAssoAffaireOrders() == null
                || customerOrder.getAssoAffaireOrders().get(0).getProvisions() == null
                || customerOrder.getAssoAffaireOrders().get(0).getProvisions().size() == 0
                || customerOrder.getAssoAffaireOrders().get(0).getProvisions().get(0) == null
                || customerOrder.getAssoAffaireOrders().get(0).getProvisions().get(0).getInvoiceItems() == null
                || customerOrder.getAssoAffaireOrders().get(0).getProvisions().get(0).getInvoiceItems().size() == 0
                || customerOrder.getAssoAffaireOrders().get(0).getProvisions().get(0).getInvoiceItems().get(0) == null)
            throw new Exception("No invoice found");

        return customerOrder.getAssoAffaireOrders().get(0).getProvisions().get(0).getInvoiceItems().get(0).getInvoice();
    }

    @Override
    public List<CustomerOrder> searchOrders(OrderingSearch orderingSearch) {
        List<CustomerOrder> customerOrders = customerOrderRepository.findCustomerOrders(
                activeDirectoryHelper.getMyHolidaymaker(orderingSearch.getSalesEmployee()),
                orderingSearch.getCustomerOrderStatus(),
                orderingSearch.getStartDate(),
                orderingSearch.getEndDate());
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
    public CustomerOrder createNewCustomerOrderFromQuotation(Quotation quotation) throws Exception {
        CustomerOrderStatus statusOpen = customerOrderStatusService
                .getCustomerOrderStatusByCode(CustomerOrderStatus.OPEN);
        CustomerOrder customerOrder = new CustomerOrder(quotation.getTiers(), quotation.getResponsable(),
                quotation.getConfrere(), quotation.getSpecialOffers(), LocalDateTime.now(), statusOpen,
                quotation.getObservations(), quotation.getDescription(), null,
                quotation.getDocuments(), quotation.getLabelType(), quotation.getCustomLabelResponsable(),
                quotation.getCustomLabelTiers(), quotation.getRecordType(), quotation.getAssoAffaireOrders(),
                quotation.getMails(), quotation.getPhones(), null,
                quotation.getOverrideSpecialOffer(), quotation.getQuotationLabel(), false, null, null, null, null);

        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule("SimpleModule");
        simpleModule.addSerializer(LocalDateTime.class, new JacksonLocalDateTimeSerializer());
        simpleModule.addSerializer(LocalDate.class, new JacksonLocalDateSerializer());
        simpleModule.addDeserializer(LocalDateTime.class, new JacksonLocalDateTimeDeserializer());
        simpleModule.addDeserializer(LocalDate.class, new JacksonLocalDateDeserializer());
        objectMapper.registerModule(simpleModule);
        String customerOrderString = objectMapper.writeValueAsString(customerOrder);

        CustomerOrder customerOrder2 = objectMapper.readValue(customerOrderString, CustomerOrder.class);

        if (customerOrder2.getDocuments() != null)
            for (Document document : customerOrder2.getDocuments())
                document.setId(null);

        if (customerOrder2.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder asso : customerOrder2.getAssoAffaireOrders()) {
                asso.setId(null);
                asso.setCustomerOrder(null);
                asso.setQuotation(null);
                if (asso.getProvisions() != null)
                    for (Provision provision : asso.getProvisions()) {
                        provision.setId(null);
                        if (provision.getAnnouncement() != null)
                            provision.getAnnouncement().setId(null);
                        if (provision.getBodacc() != null)
                            provision.getBodacc().setId(null);
                        if (provision.getFormalite() != null)
                            provision.getFormalite().setId(null);
                        if (provision.getDomiciliation() != null)
                            provision.getDomiciliation().setId(null);
                        if (provision.getInvoiceItems() != null)
                            for (InvoiceItem invoiceItem : provision.getInvoiceItems())
                                invoiceItem.setId(null);
                        provision.setAttachments(null);
                    }
            }
        return addOrUpdateCustomerOrder(customerOrder2);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateInvoiceMail(CustomerOrder customerOrder) throws Exception {
        Invoice invoice = generateInvoice(customerOrder);
        mailHelper.sendCustomerOrderInvoiceToCustomer(customerOrder, invoice, true);
        if (invoice != null)
            throw new Exception();
    }

}
