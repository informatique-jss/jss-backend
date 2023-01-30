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
import com.jss.osiris.libs.PrintDelegate;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.mail.MailComputeHelper;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.Deposit;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.service.DepositService;
import com.jss.osiris.modules.invoicing.service.InvoiceHelper;
import com.jss.osiris.modules.invoicing.service.InvoiceItemService;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.invoicing.service.PaymentService;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.PaymentType;
import com.jss.osiris.modules.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.miscellaneous.service.MailService;
import com.jss.osiris.modules.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.miscellaneous.service.PhoneService;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.quotation.controller.QuotationController;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.Announcement;
import com.jss.osiris.modules.quotation.model.AnnouncementStatus;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.model.Debour;
import com.jss.osiris.modules.quotation.model.IQuotation;
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

    @Autowired
    AnnouncementStatusService announcementStatusService;

    @Autowired
    QuotationController quotationController;

    @Autowired
    PaymentService paymentService;

    @Autowired
    PricingHelper pricingHelper;

    @Autowired
    PrintDelegate printDelegate;

    @Autowired
    MailComputeHelper mailComputeHelper;

    @Autowired
    DirectDebitTransfertService debitTransfertService;

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
    public CustomerOrder getCustomerOrderForAnnouncement(Announcement announcement) {
        Optional<CustomerOrder> customerOrder = customerOrderRepository
                .findCustomerOrderForAnnouncement(announcement.getId());
        if (customerOrder.isPresent())
            return customerOrder.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerOrder addOrUpdateCustomerOrderFromUser(CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException {
        return addOrUpdateCustomerOrder(customerOrder, true);
    }

    @Override
    public CustomerOrder addOrUpdateCustomerOrder(CustomerOrder customerOrder, boolean isFromUser)
            throws OsirisException, OsirisClientMessageException {
        if (customerOrder.getId() == null) {
            customerOrder.setCreatedDate(LocalDateTime.now());
        }

        if (customerOrder.getIsCreatedFromWebSite() == null)
            customerOrder.setIsCreatedFromWebSite(false);

        customerOrder.setIsQuotation(false);

        if (customerOrder.getDocuments() != null)
            for (Document document : customerOrder.getDocuments()) {
                mailService.populateMailIds(document.getMailsAffaire());
                mailService.populateMailIds(document.getMailsClient());
                document.setCustomerOrder(customerOrder);
            }

        // Complete provisions
        if (customerOrder.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders()) {
                assoAffaireOrder.setCustomerOrder(customerOrder);
                assoAffaireOrderService.completeAssoAffaireOrder(assoAffaireOrder, customerOrder);
            }

        boolean isNewCustomerOrder = customerOrder.getId() == null;

        customerOrder = customerOrderRepository.save(customerOrder);

        pricingHelper.getAndSetInvoiceItemsForQuotation(customerOrder, true);

        customerOrder = getCustomerOrder(customerOrder.getId());

        indexEntityService.indexEntity(customerOrder, customerOrder.getId());
        if (customerOrder.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders()) {
                indexEntityService.indexEntity(assoAffaireOrder, assoAffaireOrder.getId());
                if (assoAffaireOrder.getProvisions() != null)
                    for (Provision provision : assoAffaireOrder.getProvisions())
                        if (provision.getDebours() != null)
                            for (Debour debour : provision.getDebours())
                                if (debour.getId() != null)
                                    indexEntityService.indexEntity(debour, debour.getId());
            }

        if (isNewCustomerOrder)
            notificationService.notifyNewCustomerOrderQuotation(customerOrder);

        checkAllProvisionEnded(customerOrder);

        // Trigger move forward for announcement created in website
        if (!isFromUser && customerOrder.getAssoAffaireOrders() != null
                && customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.OPEN)
                && customerOrder.getIsCreatedFromWebSite() != null && customerOrder.getIsCreatedFromWebSite()
                && isOnlyAnnouncement(customerOrder))
            addOrUpdateCustomerOrderStatus(customerOrder, CustomerOrderStatus.OPEN, isFromUser);
        return customerOrder;
    }

    @Override
    public CustomerOrder checkAllProvisionEnded(CustomerOrder customerOrderIn)
            throws OsirisException, OsirisClientMessageException {
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
            throws OsirisException, OsirisClientMessageException {
        return addOrUpdateCustomerOrderStatus(customerOrder, targetStatusCode, true);
    }

    private boolean isOnlyAnnouncement(CustomerOrder customerOrder) {
        if (customerOrder != null && customerOrder.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                if (asso.getProvisions() != null)
                    for (Provision provision : asso.getProvisions())
                        if (provision.getAnnouncement() == null)
                            return false;
        return true;
    }

    @Override
    public CustomerOrder addOrUpdateCustomerOrderStatus(CustomerOrder customerOrder, String targetStatusCode,
            boolean isFromUser)
            throws OsirisException, OsirisClientMessageException {
        // Handle automatic workflow for Announcement created from website
        boolean onlyAnnonceLegale = isOnlyAnnouncement(customerOrder);
        boolean isFromWebsite = (customerOrder.getIsCreatedFromWebSite() != null
                && customerOrder.getIsCreatedFromWebSite()) ? true : false;

        // Determine if deposit is mandatory or not
        if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.OPEN)
                && (targetStatusCode.equals(CustomerOrderStatus.BEING_PROCESSED)
                        || targetStatusCode.equals(CustomerOrderStatus.WAITING_DEPOSIT))) {
            Float remainingToPay = getRemainingAmountToPayForCustomerOrder(customerOrder);

            ITiers tiers = quotationService.getCustomerOrderOfQuotation(customerOrder);
            boolean isDepositMandatory = false;
            if (tiers instanceof Tiers)
                isDepositMandatory = ((Tiers) tiers).getIsProvisionalPaymentMandatory();
            if (tiers instanceof Responsable)
                isDepositMandatory = ((Responsable) tiers).getTiers().getIsProvisionalPaymentMandatory();
            if (tiers instanceof Confrere)
                isDepositMandatory = ((Confrere) tiers).getIsProvisionalPaymentMandatory();

            if (!isDepositMandatory || remainingToPay <= 0)
                targetStatusCode = CustomerOrderStatus.BEING_PROCESSED;
            else
                targetStatusCode = CustomerOrderStatus.WAITING_DEPOSIT;

            // Confirm customer order to cutomser with or without deposit
            mailHelper.sendCustomerOrderCreationConfirmationToCustomer(customerOrder, false, false);
        }

        // Handle automatic workflow for Announcement created from website
        if (isFromWebsite && onlyAnnonceLegale && !isFromUser) {
            // First round : move forward customerOrder
            if (targetStatusCode.equals(CustomerOrderStatus.OPEN)) {
                boolean hasError = false;
                try {
                    quotationController.validateQuotationAndCustomerOrder(customerOrder,
                            CustomerOrderStatus.BEING_PROCESSED);
                } catch (Exception e) {
                    hasError = true;
                }
                if (!hasError)
                    targetStatusCode = CustomerOrderStatus.BEING_PROCESSED;
            }
        }

        // Target : BEING PROCESSED => notify customer
        if (targetStatusCode.equals(CustomerOrderStatus.BEING_PROCESSED)) {
            resetDeboursManuelAmount(customerOrder);
            // Confirm deposit taken into account or customer order starting
            if (!isFromUser
                    && customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.WAITING_DEPOSIT)) {
                mailHelper.sendCustomerOrderDepositConfirmationToCustomer(customerOrder, false);
            } else
                notificationService.notifyCustomerOrderToBeingProcessed(customerOrder, true);
        }

        // Handle automatic workflow for Announcement created from website
        if (isFromWebsite && onlyAnnonceLegale && !isFromUser) {
            // Second round : move forward announcements. Final check checkAllProvisionEnded
            // on save will put it to TO BILLED if necessary
            if (targetStatusCode.equals(CustomerOrderStatus.BEING_PROCESSED))
                moveForwardAnnouncementFromWebsite(customerOrder);
        }

        // Target : TO BILLED => notify
        if (targetStatusCode.equals(CustomerOrderStatus.TO_BILLED))
            notificationService.notifyCustomerOrderToBeingToBilled(customerOrder);

        // Target : BILLED => generate invoice
        if (targetStatusCode.equals(CustomerOrderStatus.BILLED)) {
            // save once customer order to recompute invoice item before set it in stone...
            this.addOrUpdateCustomerOrder(customerOrder, true);
            Invoice invoice = generateInvoice(customerOrder);
            accountingRecordService.generateAccountingRecordsForSaleOnInvoiceGeneration(
                    getInvoice(customerOrder));
            // If deposit already set, associate them to invoice
            moveCustomerOrderDepositToInvoiceDeposit(customerOrder, invoice);

            // If customer order is on direct debit, generate it
            ITiers tiers = invoiceHelper.getCustomerOrder(invoice);
            PaymentType paymentType = null;
            if (tiers instanceof Responsable)
                paymentType = ((Responsable) tiers).getTiers().getPaymentType();
            if (tiers instanceof Tiers)
                paymentType = ((Tiers) tiers).getPaymentType();
            if (tiers instanceof Confrere)
                paymentType = ((Confrere) tiers).getPaymentType();

            if (paymentType != null
                    && paymentType.getId().equals(constantService.getPaymentTypePrelevement().getId())) {
                debitTransfertService.generateDirectDebitTransfertForOutboundInvoice(invoice);
            }

            // Check invoice payed
            Float remainingToPayForCurrentInvoice = invoiceService.getRemainingAmountToPayForInvoice(invoice);
            if (remainingToPayForCurrentInvoice < 0) {
                throw new OsirisException(null, "Impossible to billed, too much money on customerOrder !");
            }
            accountingRecordService.checkInvoiceForLettrage(invoice);

            mailHelper.sendCustomerOrderFinalisationToCustomer(customerOrder, false, false, false);
        }

        // Target : going back to TO BILLED => cancel invoice
        if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BILLED)
                && targetStatusCode.equals(CustomerOrderStatus.TO_BILLED)) {
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

        CustomerOrderStatus customerOrderStatus = customerOrderStatusService
                .getCustomerOrderStatusByCode(
                        targetStatusCode);
        if (customerOrderStatus == null)
            throw new OsirisException(null, "Quotation status not found for code " + targetStatusCode);

        customerOrder.setCustomerOrderStatus(customerOrderStatus);
        customerOrder.setLastStatusUpdate(LocalDateTime.now());
        return this.addOrUpdateCustomerOrder(customerOrder, false);
    }

    private void resetDeboursManuelAmount(CustomerOrder customerOrder) {
        if (customerOrder.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders()) {
                indexEntityService.indexEntity(assoAffaireOrder, assoAffaireOrder.getId());
                if (assoAffaireOrder.getProvisions() != null)
                    for (Provision provision : assoAffaireOrder.getProvisions())
                        if (provision.getInvoiceItems() != null)
                            for (InvoiceItem invoiceItem : provision.getInvoiceItems())
                                if (invoiceItem.getBillingItem().getBillingType().getIsDebour()) {
                                    invoiceItem.setPreTaxPrice(0f);
                                    invoiceItem.setDiscountAmount(0f);
                                    invoiceItem.setIsOverridePrice(false);
                                    invoiceItem.setVatPrice(null);
                                    invoiceItem.setVat(null);
                                    invoiceItem.setIsGifted(false);
                                    invoiceItemService.addOrUpdateInvoiceItem(invoiceItem);
                                }
            }
    }

    /**
     * Return true if announcement is done at the end, false otherwise
     * 
     * @throws OsirisClientMessageException
     */
    private boolean moveForwardAnnouncementFromWebsite(CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException {
        boolean allDone = true;
        if (customerOrder != null && customerOrder.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                if (asso.getProvisions() != null)
                    for (Provision provision : asso.getProvisions())
                        if (provision.getAnnouncement() != null) {
                            Announcement announcement = provision.getAnnouncement();
                            announcement.setAnnouncementStatus(announcementStatusService
                                    .getAnnouncementStatusByCode(AnnouncementStatus.ANNOUNCEMENT_IN_PROGRESS));
                            assoAffaireOrderService.addOrUpdateAssoAffaireOrder(asso);
                            if (announcement.getIsProofReadingDocument() == false) {
                                announcement.setAnnouncementStatus(announcementStatusService
                                        .getAnnouncementStatusByCode(AnnouncementStatus.ANNOUNCEMENT_PUBLISHED));
                                assoAffaireOrderService.addOrUpdateAssoAffaireOrder(asso);
                                announcement.setAnnouncementStatus(announcementStatusService
                                        .getAnnouncementStatusByCode(AnnouncementStatus.ANNOUNCEMENT_DONE));
                                assoAffaireOrderService.addOrUpdateAssoAffaireOrder(asso);
                            } else {
                                allDone = false;
                                announcement.setAnnouncementStatus(announcementStatusService
                                        .getAnnouncementStatusByCode(
                                                AnnouncementStatus.ANNOUNCEMENT_WAITING_READ_CUSTOMER));
                            }
                        }
        return allDone;
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
    public CustomerOrder unlockCustomerOrderFromDeposit(CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException {
        if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.WAITING_DEPOSIT)) {
            addOrUpdateCustomerOrderStatus(customerOrder, CustomerOrderStatus.BEING_PROCESSED, false);
            notificationService.notifyCustomerOrderToBeingProcessed(customerOrder, false);
        }

        return customerOrder;
    }

    private Invoice generateInvoice(CustomerOrder customerOrder) throws OsirisException, OsirisClientMessageException {
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
                if (customerOrderStatus != null)
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

        if (orderingSearch.getStartDate() == null)
            orderingSearch.setStartDate(LocalDateTime.now().minusYears(100));

        if (orderingSearch.getEndDate() == null)
            orderingSearch.setEndDate(LocalDateTime.now().plusYears(100));

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
            throws OsirisException, OsirisClientMessageException {
        CustomerOrderStatus statusOpen = customerOrderStatusService
                .getCustomerOrderStatusByCode(CustomerOrderStatus.OPEN);
        CustomerOrder customerOrder = new CustomerOrder(quotation.getTiers(), quotation.getResponsable(),
                quotation.getConfrere(), quotation.getSpecialOffers(), LocalDateTime.now(), statusOpen,
                quotation.getObservations(), quotation.getDescription(), null,
                quotation.getDocuments(), quotation.getAssoAffaireOrders(), null,
                quotation.getOverrideSpecialOffer(), quotation.getQuotationLabel(), false, null, null, null);

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
                        }
                        if (provision.getFormalite() != null) {
                            provision.getFormalite().setId(null);
                            provision.getFormalite().setAttachments(null);
                        }
                        if (provision.getSimpleProvision() != null) {
                            provision.getSimpleProvision().setId(null);
                            provision.getSimpleProvision().setAttachments(null);
                        }
                        if (provision.getDomiciliation() != null) {
                            provision.getDomiciliation().setId(null);
                            provision.getDomiciliation().setAttachments(null);
                        }
                        if (provision.getInvoiceItems() != null)
                            for (InvoiceItem invoiceItem : provision.getInvoiceItems())
                                invoiceItem.setId(null);
                        provision.setAttachments(null);
                    }
            }
        addOrUpdateCustomerOrder(customerOrder2, false);

        return customerOrder2;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateInvoiceMail(CustomerOrder customerOrder) throws OsirisException, OsirisClientMessageException {
        mailHelper.sendCustomerOrderFinalisationToCustomer(customerOrder, true, false, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String getCardPaymentLinkForCustomerOrderDeposit(CustomerOrder customerOrder, String mail, String subject)
            throws OsirisException, OsirisClientMessageException {
        return getCardPaymentLinkForCustomerOrderPayment(customerOrder, mail, subject, paymentCbRedirectDeposit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String getCardPaymentLinkForPaymentInvoice(CustomerOrder customerOrder, String mail, String subject)
            throws OsirisException, OsirisClientMessageException {
        return getCardPaymentLinkForCustomerOrderPayment(customerOrder, mail, subject, paymentCbRedirectInvoice);
    }

    private String getCardPaymentLinkForCustomerOrderPayment(CustomerOrder customerOrder, String mail, String subject,
            String redirectEntrypoint)
            throws OsirisException, OsirisClientMessageException {

        if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.ABANDONED))
            throw new OsirisException(null, "Impossible to pay an cancelled customer order n°" + customerOrder.getId());

        if (customerOrder.getCentralPayPaymentRequestId() != null) {
            CentralPayPaymentRequest centralPayPaymentRequest = centralPayDelegateService
                    .getPaymentRequest(customerOrder.getCentralPayPaymentRequestId());

            if (centralPayPaymentRequest != null) {
                if (centralPayPaymentRequest.getPaymentRequestStatus().equals(CentralPayPaymentRequest.ACTIVE))
                    centralPayDelegateService.cancelPaymentRequest(customerOrder.getCentralPayPaymentRequestId());

                if (centralPayPaymentRequest.getPaymentRequestStatus().equals(CentralPayPaymentRequest.CLOSED)
                        && centralPayPaymentRequest.getPaymentStatus().equals(CentralPayPaymentRequest.PAID)) {
                    validateCardPaymentLinkForCustomerOrder(customerOrder);
                    return "ok";
                }
            }
        }

        Float remainingToPay = 0f;

        if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BILLED)) {
            if (customerOrder.getInvoices() != null)
                for (Invoice invoice : customerOrder.getInvoices())
                    if (invoice.getInvoiceStatus().getId()
                            .equals(constantService.getInvoiceStatusSend().getId())) {
                        remainingToPay = invoiceService.getRemainingAmountToPayForInvoice(invoice);
                        break;
                    }
        } else {
            remainingToPay = getRemainingAmountToPayForCustomerOrder(customerOrder);
        }

        if (remainingToPay > 0) {
            CentralPayPaymentRequest paymentRequest = centralPayDelegateService.generatePayPaymentRequest(
                    getRemainingAmountToPayForCustomerOrder(customerOrder), mail,
                    customerOrder.getId() + "", subject);

            customerOrder.setCentralPayPaymentRequestId(paymentRequest.getPaymentRequestId());
            addOrUpdateCustomerOrder(customerOrder, false);
            return paymentRequest.getBreakdowns().get(0).getEndpoint()
                    + "?urlRedirect=" + redirectEntrypoint + "?customerOrderId=" + customerOrder.getId() + "&delay=0";
        }
        return "ok";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean validateCardPaymentLinkForCustomerOrder(CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException {
        if (customerOrder.getCentralPayPaymentRequestId() != null) {
            CentralPayPaymentRequest centralPayPaymentRequest = centralPayDelegateService
                    .getPaymentRequest(customerOrder.getCentralPayPaymentRequestId());

            if (centralPayPaymentRequest != null) {
                if (centralPayPaymentRequest.getPaymentRequestStatus().equals(CentralPayPaymentRequest.CLOSED)
                        && centralPayPaymentRequest.getPaymentStatus().equals(CentralPayPaymentRequest.PAID)) {

                    if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BILLED)) {
                        if (customerOrder.getInvoices() != null)
                            for (Invoice invoice : customerOrder.getInvoices())
                                if (invoice.getInvoiceStatus().getId()
                                        .equals(constantService.getInvoiceStatusSend().getId())) {
                                    generatePaymentOnInvoiceForCbPayment(invoice, centralPayPaymentRequest);
                                    break;
                                }
                    } else {
                        generateDepositOnCustomerOrderForCbPayment(customerOrder, centralPayPaymentRequest);
                        customerOrder.setCentralPayPaymentRequestId(null);
                        addOrUpdateCustomerOrder(customerOrder, false);
                        unlockCustomerOrderFromDeposit(customerOrder);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void generateDepositOnCustomerOrderForCbPayment(CustomerOrder customerOrder,
            CentralPayPaymentRequest centralPayPaymentRequest) throws OsirisException, OsirisClientMessageException {
        // Generate payment to materialize CB payment
        Payment payment = getCentralPayPayment(centralPayPaymentRequest);

        Deposit deposit = depositService.getNewDepositForCustomerOrder(payment.getPaymentAmount(), LocalDateTime.now(),
                customerOrder, null, payment, true);

        deposit.setCustomerOrder(customerOrder);
        depositService.addOrUpdateDeposit(deposit);

        addOrUpdateCustomerOrder(customerOrder, false);

        accountingRecordService.generateAccountingRecordsForCentralPayPayment(centralPayPaymentRequest, payment,
                deposit, customerOrder, null);
    }

    private void generatePaymentOnInvoiceForCbPayment(Invoice invoice,
            CentralPayPaymentRequest centralPayPaymentRequest) throws OsirisException, OsirisClientMessageException {
        // Generate payment to materialize CB payment
        Payment payment = getCentralPayPayment(centralPayPaymentRequest);

        accountingRecordService.generateAccountingRecordsForSaleOnInvoicePayment(invoice, payment);
        accountingRecordService.generateAccountingRecordsForCentralPayPayment(centralPayPaymentRequest, payment,
                null, invoice.getCustomerOrder(), invoice);
    }

    private Payment getCentralPayPayment(CentralPayPaymentRequest centralPayPaymentRequest) throws OsirisException {
        Payment payment = new Payment();
        payment.setExternallyAssociated(false);
        payment.setLabel(centralPayPaymentRequest.getDescription());
        payment.setPaymentAmount(centralPayPaymentRequest.getTotalAmount() / 100f);
        payment.setPaymentDate(centralPayPaymentRequest.getCreationDate());
        payment.setPaymentWay(constantService.getPaymentWayInbound());
        payment.setPaymentType(constantService.getPaymentTypeCB());
        paymentService.addOrUpdatePayment(payment);
        return payment;
    }

    @Override
    @Transactional
    public void sendRemindersForCustomerOrderDeposit() throws OsirisException, OsirisClientMessageException {
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
                    addOrUpdateCustomerOrder(customerOrder, false);
                }
            }
    }

    @Override
    public Float getTotalForCustomerOrder(IQuotation customerOrder) {
        Float total = 0f;
        if (customerOrder != null) {
            if (customerOrder.getAssoAffaireOrders() != null) {
                for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders()) {
                    if (assoAffaireOrder.getProvisions() != null) {
                        for (Provision provision : assoAffaireOrder.getProvisions()) {
                            if (provision.getInvoiceItems() != null && provision.getInvoiceItems().size() > 0) {
                                for (InvoiceItem invoiceItem : provision.getInvoiceItems()) {
                                    total += (invoiceItem.getPreTaxPrice() != null ? invoiceItem.getPreTaxPrice() : 0f)
                                            + (invoiceItem.getVatPrice() != null ? invoiceItem.getVatPrice() : 0f)
                                            - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount()
                                                    : 0f);
                                }
                            }
                        }
                    }
                }
            }
        }
        return total;
    }

    @Override
    public Float getRemainingAmountToPayForCustomerOrder(CustomerOrder customerOrder) {
        customerOrder = getCustomerOrder(customerOrder.getId());
        if (customerOrder != null) {
            Float total = getTotalForCustomerOrder(customerOrder);

            if (customerOrder.getDeposits() != null && customerOrder.getDeposits().size() > 0)
                for (Deposit deposit : customerOrder.getDeposits())
                    if (!deposit.getIsCancelled())
                        total -= deposit.getDepositAmount();

            return Math.round(total * 100f) / 100f;
        }
        return 0f;
    }

    @Override
    public void printMailingLabel(List<String> customerOrders)
            throws OsirisException, OsirisClientMessageException {
        if (customerOrders != null && customerOrders.size() > 0)
            for (String id : customerOrders) {
                try {
                    printDelegate.printMailingLabel(
                            mailComputeHelper.computePaperLabelResult(getCustomerOrder(Integer.parseInt(id))));
                } catch (NumberFormatException e) {
                } catch (Exception e) {
                    throw new OsirisException(e, "Error when printing label");
                }
            }
    }

}
