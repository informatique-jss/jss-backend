package com.jss.osiris.modules.quotation.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module.Feature;
import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.DateHelper;
import com.jss.osiris.libs.JacksonLocalDateDeserializer;
import com.jss.osiris.libs.JacksonLocalDateSerializer;
import com.jss.osiris.libs.JacksonLocalDateTimeDeserializer;
import com.jss.osiris.libs.JacksonLocalDateTimeSerializer;
import com.jss.osiris.libs.PrintDelegate;
import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.GeneratePdfDelegate;
import com.jss.osiris.libs.mail.MailComputeHelper;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.service.InvoiceHelper;
import com.jss.osiris.modules.invoicing.service.InvoiceItemService;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.invoicing.service.PaymentService;
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
import com.jss.osiris.modules.quotation.controller.QuotationValidationHelper;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.Announcement;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
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

    @Value("${invoicing.payment.limit.refund.euros}")
    private String payementLimitRefundInEuros;

    @Autowired
    CustomerOrderRepository customerOrderRepository;

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
    GeneratePdfDelegate generatePdfDelegate;

    @Autowired
    CustomerOrderStatusService customerOrderStatusService;

    @Autowired
    EmployeeService employeeService;

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
    PaymentService paymentService;

    @Autowired
    PricingHelper pricingHelper;

    @Autowired
    PrintDelegate printDelegate;

    @Autowired
    MailComputeHelper mailComputeHelper;

    @Autowired
    QuotationValidationHelper quotationValidationHelper;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    CentralPayPaymentRequestService centralPayPaymentRequestService;

    @Autowired
    ActiveDirectoryHelper activeDirectoryHelper;

    @Autowired
    InvoiceItemService invoiceItemService;

    @Autowired
    BatchService batchService;

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
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        return addOrUpdateCustomerOrder(customerOrder, true, true);
    }

    @Override
    public CustomerOrder addOrUpdateCustomerOrder(CustomerOrder customerOrder, boolean isFromUser,
            boolean checkAllProvisionEnded)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {

        if (customerOrder.getCustomerOrderOrigin() == null)
            customerOrder.setCustomerOrderOrigin(constantService.getCustomerOrderOriginOsiris());

        // Set default customer order assignation to sales employee if not set
        if (customerOrder.getAssignedTo() == null)
            customerOrder.setAssignedTo(
                    quotationService.getCustomerOrderOfQuotation(customerOrder).getDefaultCustomerOrderEmployee());

        if (customerOrder.getIsGifted() == null)
            customerOrder.setIsGifted(false);

        customerOrder.setIsQuotation(false);

        findDuplicatesForCustomerOrder(customerOrder);

        if (customerOrder.getDocuments() != null)
            for (Document document : customerOrder.getDocuments()) {
                mailService.populateMailIds(document.getMailsAffaire());
                mailService.populateMailIds(document.getMailsClient());
                document.setCustomerOrder(customerOrder);
            }

        // Complete provisions
        boolean oneNewProvision = false;
        if (customerOrder.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders()) {
                assoAffaireOrder.setCustomerOrder(customerOrder);
                assoAffaireOrderService.completeAssoAffaireOrder(assoAffaireOrder, customerOrder, isFromUser);
                if (assoAffaireOrder.getProvisions() != null)
                    for (Provision provision : assoAffaireOrder.getProvisions())
                        if (provision.getId() == null)
                            oneNewProvision = true;
            }

        if (oneNewProvision)
            customerOrder = customerOrderRepository.save(customerOrder);

        pricingHelper.getAndSetInvoiceItemsForQuotation(customerOrder, true);

        customerOrder = customerOrderRepository.save(customerOrder);

        customerOrder = getCustomerOrder(customerOrder.getId());

        if (customerOrder.getId() == null)
            notificationService.notifyNewCustomerOrderQuotation(customerOrder);

        if (checkAllProvisionEnded)
            checkAllProvisionEnded(customerOrder);

        batchService.declareNewBatch(Batch.REINDEX_CUSTOMER_ORDER, customerOrder.getId());
        return customerOrder;
    }

    @Override
    public CustomerOrder checkAllProvisionEnded(CustomerOrder customerOrderIn)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
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
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
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

    private boolean isOnlyJssAnnouncement(CustomerOrder customerOrder) throws OsirisException {
        if (!isOnlyAnnouncement(customerOrder))
            return false;

        if (customerOrder != null && customerOrder.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                if (asso.getProvisions() != null)
                    for (Provision provision : asso.getProvisions())
                        if (provision.getAnnouncement() != null && !provision.getAnnouncement().getConfrere().getId()
                                .equals(constantService.getConfrereJssSpel().getId()))
                            return false;
        return true;
    }

    @Override
    public CustomerOrder addOrUpdateCustomerOrderStatus(CustomerOrder customerOrder, String targetStatusCode,
            boolean isFromUser)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        // Handle automatic workflow for Announcement created from website
        boolean checkAllProvisionEnded = false;

        // Determine if deposit is mandatory or not
        if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.OPEN)
                && (targetStatusCode.equals(CustomerOrderStatus.BEING_PROCESSED)
                        || targetStatusCode.equals(CustomerOrderStatus.WAITING_DEPOSIT))) {
            Float remainingToPay = getRemainingAmountToPayForCustomerOrder(customerOrder);

            ITiers tiers = quotationService.getCustomerOrderOfQuotation(customerOrder);
            boolean isDepositMandatory = false;
            if (tiers instanceof Responsable)
                tiers = ((Responsable) tiers).getTiers();
            isDepositMandatory = tiers.getIsProvisionalPaymentMandatory();

            if (!isDepositMandatory || remainingToPay <= 0)
                targetStatusCode = CustomerOrderStatus.BEING_PROCESSED;
            else
                targetStatusCode = CustomerOrderStatus.WAITING_DEPOSIT;

            // Confirm customer order to cutomser with or without deposit
            mailHelper.sendCustomerOrderCreationConfirmationToCustomer(customerOrder, false, false);
        }

        // Target : CANCELLED => vérifiy there is no more deposit
        if (targetStatusCode.equals(CustomerOrderStatus.ABANDONED)) {
            if (customerOrder.getPayments() != null && customerOrder.getPayments().size() > 0)
                for (Payment payment : customerOrder.getPayments())
                    if (!payment.getIsCancelled())
                        throw new OsirisClientMessageException(
                                "Impossible d'abandonner cette commande, elle possède encore des acomptes associés");
        }

        // Target : BEING PROCESSED => notify customer
        if (targetStatusCode.equals(CustomerOrderStatus.BEING_PROCESSED)) {
            resetDeboursInvoiceItems(customerOrder);
            // Confirm deposit taken into account or customer order starting and only if not
            // from to billed
            if (!customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.TO_BILLED)) {
                if (customerOrder.getCustomerOrderStatus().getCode()
                        .equals(CustomerOrderStatus.WAITING_DEPOSIT)) {
                    if (!isFromUser)
                        mailHelper.sendCustomerOrderDepositConfirmationToCustomer(customerOrder, false);
                    notificationService.notifyCustomerOrderToBeingProcessedFromDeposit(customerOrder, isFromUser);
                } else
                    notificationService.notifyCustomerOrderToBeingProcessed(customerOrder, true);
            }
        }

        // Target : TO BILLED => notify
        if (targetStatusCode.equals(CustomerOrderStatus.TO_BILLED)) {
            notificationService.notifyCustomerOrderToBeingToBilled(customerOrder);

            // Auto billed for JSS Announcement only customer order
            if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BEING_PROCESSED)
                    && isOnlyJssAnnouncement(customerOrder)
                    && getRemainingAmountToPayForCustomerOrder(customerOrder) >= 0) {
                targetStatusCode = CustomerOrderStatus.BILLED;
            }
        }

        // Target : BILLED => generate invoice
        if (targetStatusCode.equals(CustomerOrderStatus.BILLED)) {
            // save once customer order to recompute invoice item before set it in stone...
            this.addOrUpdateCustomerOrder(customerOrder, true, checkAllProvisionEnded);

            // Protection : if we already have an invoice not cancelled, abord...
            if (customerOrder.getInvoices() != null) {
                for (Invoice invoice : customerOrder.getInvoices())
                    if (invoice.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusSend().getId())
                            || invoice.getInvoiceStatus().getId()
                                    .equals(constantService.getInvoiceStatusPayed().getId()))
                        throw new OsirisClientMessageException("Une facture existe déjà pour cette commande !");
            }

            Float remainingToPayForCurrentCustomerOrder = Math
                    .round(getRemainingAmountToPayForCustomerOrder(customerOrder) * 100f) / 100f;

            if (remainingToPayForCurrentCustomerOrder < 0
                    && Math.abs(remainingToPayForCurrentCustomerOrder) > Float.parseFloat(payementLimitRefundInEuros))
                throw new OsirisException(null, "Impossible to billed, too much money on customerOrder !");

            Invoice invoice = generateInvoice(customerOrder);

            // If deposit already set, associate them to invoice
            if (customerOrder.getPayments() != null)
                for (Payment payment : customerOrder.getPayments())
                    if (!payment.getIsCancelled())
                        paymentService.movePaymentFromCustomerOrderToInvoice(payment, customerOrder, invoice);

            entityManager.flush();
            entityManager.clear();

            mailHelper.sendCustomerOrderFinalisationToCustomer(getCustomerOrder(customerOrder.getId()), false, false,
                    false);
            mailHelper.sendCustomerOrderAttachmentOnFinalisationToCustomer(getCustomerOrder(customerOrder.getId()),
                    false);
        }

        // Target : going back to TO BILLED
        if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BILLED)
                && targetStatusCode.equals(CustomerOrderStatus.TO_BILLED) && customerOrder.getIsGifted() == false)
            if (customerOrder.getInvoices() != null) {
                for (Invoice invoice : customerOrder.getInvoices())
                    if (!invoice.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusCancelled().getId())
                            && !invoice.getInvoiceStatus().getId()
                                    .equals(constantService.getInvoiceStatusCreditNoteEmited().getId())) {
                        invoiceService.cancelInvoice(invoice);
                        break;
                    }
                // Flush to take invoice item break link with provision into account
                entityManager.flush();
                entityManager.clear();
                customerOrder = getCustomerOrder(customerOrder.getId());
            }
        CustomerOrderStatus customerOrderStatus = customerOrderStatusService
                .getCustomerOrderStatusByCode(targetStatusCode);
        if (customerOrderStatus == null)
            throw new OsirisException(null, "Customer order status not found for code " + targetStatusCode);

        customerOrder.setCustomerOrderStatus(customerOrderStatus);
        customerOrder.setLastStatusUpdate(LocalDateTime.now());
        return this.addOrUpdateCustomerOrder(customerOrder, false, checkAllProvisionEnded);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateCreditNoteForCustomerOrderInvoice(CustomerOrder customerOrder, Invoice invoiceToRefund)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        customerOrder = getCustomerOrder(customerOrder.getId());
        invoiceToRefund = invoiceService.getInvoice(invoiceToRefund.getId());

        if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BILLED))
            if (customerOrder.getInvoices() != null) {
                for (Invoice invoice : customerOrder.getInvoices())
                    if (!invoice.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusCancelled().getId())
                            && !invoice.getInvoiceStatus().getId()
                                    .equals(constantService.getInvoiceStatusCreditNoteEmited().getId())
                            && invoice.getId().equals(invoiceToRefund.getId())) {
                        invoiceService.cancelInvoice(invoice);
                        break;
                    }
            }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reinitInvoicing(CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        customerOrder = getCustomerOrder(customerOrder.getId());
        if (customerOrder.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                if (asso.getProvisions() != null)
                    for (Provision provision : asso.getProvisions())
                        if (provision.getInvoiceItems() != null)
                            for (InvoiceItem invoiceItem : provision.getInvoiceItems())
                                invoiceItemService.deleteInvoiceItem(invoiceItem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void offerCustomerOrder(CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BILLED))
            if (customerOrder.getInvoices() != null) {
                for (Invoice invoice : customerOrder.getInvoices())
                    if (!invoice.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusCancelled().getId())
                            && !invoice.getInvoiceStatus().getId()
                                    .equals(constantService.getInvoiceStatusCreditNoteEmited().getId())) {
                        invoiceService.cancelInvoice(invoice);
                        break;
                    }
                // Flush to take invoice item break link with provision into account
                entityManager.flush();
                entityManager.clear();
                customerOrder = getCustomerOrder(customerOrder.getId());
            }

        customerOrder.setLastStatusUpdate(LocalDateTime.now());
        customerOrder.setIsGifted(true);
        this.addOrUpdateCustomerOrder(customerOrder, false, false);
    }

    private void resetDeboursInvoiceItems(CustomerOrder customerOrder) {
        List<InvoiceItem> invoiceItemToDelete = new ArrayList<InvoiceItem>();
        if (customerOrder.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders())
                if (assoAffaireOrder.getProvisions() != null)
                    for (Provision provision : assoAffaireOrder.getProvisions())
                        if (provision.getInvoiceItems() != null) {
                            invoiceItemToDelete = new ArrayList<InvoiceItem>();
                            for (InvoiceItem invoiceItem : provision.getInvoiceItems())
                                if (invoiceItem.getBillingItem().getBillingType().getIsDebour())
                                    invoiceItemToDelete.add(invoiceItem);

                            if (invoiceItemToDelete.size() > 0)
                                provision.getInvoiceItems().removeAll(invoiceItemToDelete);
                        }
    }

    @Override
    public CustomerOrder unlockCustomerOrderFromDeposit(CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.WAITING_DEPOSIT)) {
            addOrUpdateCustomerOrderStatus(customerOrder, CustomerOrderStatus.BEING_PROCESSED, false);
            notificationService.notifyCustomerOrderToBeingProcessed(customerOrder, false);
        }

        return customerOrder;
    }

    private Invoice generateInvoice(CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        // Generate blank invoice
        ITiers orderingCustomer = quotationService.getCustomerOrderOfQuotation(customerOrder);
        Invoice invoice = new Invoice();

        invoice.setIsCreditNote(false);
        invoice.setIsProviderCreditNote(false);
        invoice.setIsInvoiceFromProvider(false);

        if (orderingCustomer instanceof Tiers)
            invoice.setTiers((Tiers) orderingCustomer);

        if (orderingCustomer instanceof Responsable)
            invoice.setResponsable((Responsable) orderingCustomer);

        if (orderingCustomer instanceof Confrere)
            invoice.setConfrere((Confrere) orderingCustomer);

        invoice.setCustomerOrder(customerOrder);

        invoice.setInvoiceItems(new ArrayList<InvoiceItem>());
        // Associate invoice to invoice item
        for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders()) {
            for (Provision provision : assoAffaireOrder.getProvisions()) {
                if (provision.getInvoiceItems() != null && provision.getInvoiceItems().size() > 0)
                    for (InvoiceItem invoiceItem : provision.getInvoiceItems()) {
                        invoice.getInvoiceItems().add(invoiceItem);
                    }
            }
        }
        invoiceService.addOrUpdateInvoiceFromUser(invoice);

        // Create invoice PDF and attach it to customerOrder and invoice
        File invoicePdf = generatePdfDelegate.generateInvoicePdf(customerOrder, invoice, null);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmm");
        try {
            List<Attachment> attachments = attachmentService.addAttachment(new FileInputStream(invoicePdf),
                    customerOrder.getId(),
                    CustomerOrder.class.getSimpleName(),
                    constantService.getAttachmentTypeInvoice(),
                    "Invoice_" + invoice.getId() + "_" + formatter.format(LocalDateTime.now()) + ".pdf",
                    false, "Facture n°" + invoice.getId(), null);

            for (Attachment attachment : attachments)
                if (attachment.getDescription().contains(invoice.getId() + "")) {
                    attachment.setInvoice(invoice);
                    attachmentService.addOrUpdateAttachment(attachment);
                }
        } catch (FileNotFoundException e) {
            throw new OsirisException(e, "Impossible to read invoice PDF temp file");
        } finally {
            invoicePdf.delete();
        }

        return invoice;
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

        ArrayList<Integer> assignedToEmployeeId = new ArrayList<Integer>();
        if (orderingSearch.getAssignedToEmployee() != null) {
            for (Employee employee : employeeService.getMyHolidaymaker(orderingSearch.getAssignedToEmployee()))
                assignedToEmployeeId.add(employee.getId());
        } else {
            assignedToEmployeeId.add(0);
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

        if (orderingSearch.getIdCustomerOrder() == null)
            orderingSearch.setIdCustomerOrder(0);

        List<OrderingSearchResult> customerOrders = customerOrderRepository.findCustomerOrders(
                salesEmployeeId, assignedToEmployeeId,
                statusId,
                orderingSearch.getStartDate().withHour(0).withMinute(0),
                orderingSearch.getEndDate().withHour(23).withMinute(59), customerOrderId, affaireId, 0,
                orderingSearch.getIdCustomerOrder());
        return customerOrders;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reindexCustomerOrder() throws OsirisException {
        List<CustomerOrder> customerOrders = IterableUtils.toList(customerOrderRepository.findAll());
        if (customerOrders != null)
            for (CustomerOrder customerOrder : customerOrders)
                batchService.declareNewBatch(Batch.REINDEX_CUSTOMER_ORDER, customerOrder.getId());
    }

    @Override
    public CustomerOrder createNewCustomerOrderFromQuotation(Quotation quotation)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        CustomerOrderStatus statusOpen = customerOrderStatusService
                .getCustomerOrderStatusByCode(CustomerOrderStatus.OPEN);
        CustomerOrder customerOrder = new CustomerOrder(quotation.getAssignedTo(), quotation.getTiers(),
                quotation.getResponsable(),
                quotation.getConfrere(), quotation.getSpecialOffers(), LocalDateTime.now(), statusOpen,
                quotation.getObservations(), quotation.getDescription(), quotation.getInstructions(), null,
                quotation.getDocuments(), quotation.getAssoAffaireOrders(), null, quotation.getQuotationLabel(), false,
                null, null);

        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule("SimpleModule");
        simpleModule.addSerializer(LocalDateTime.class, new JacksonLocalDateTimeSerializer());
        simpleModule.addSerializer(LocalDate.class, new JacksonLocalDateSerializer());
        simpleModule.addDeserializer(LocalDateTime.class, new JacksonLocalDateTimeDeserializer());
        simpleModule.addDeserializer(LocalDate.class, new JacksonLocalDateDeserializer());
        objectMapper.registerModule(simpleModule);
        Hibernate5Module module = new Hibernate5Module();
        module.enable(Feature.FORCE_LAZY_LOADING);
        objectMapper.registerModule(module);
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
                            if (provision.getAnnouncement().getDocuments() != null)
                                for (Document document : provision.getAnnouncement().getDocuments())
                                    document.setId(null);
                        }
                        if (provision.getBodacc() != null) {
                            provision.getBodacc().setId(null);
                        }
                        if (provision.getFormalite() != null) {
                            provision.getFormalite().setId(null);
                        }
                        if (provision.getSimpleProvision() != null) {
                            provision.getSimpleProvision().setId(null);
                        }
                        if (provision.getDomiciliation() != null) {
                            provision.getDomiciliation().setId(null);
                        }
                        if (provision.getInvoiceItems() != null)
                            for (InvoiceItem invoiceItem : provision.getInvoiceItems())
                                invoiceItem.setId(null);
                        provision.setAttachments(null);
                    }
            }
        addOrUpdateCustomerOrder(customerOrder2, false, true);

        // Attach attachments
        if (quotation.getAttachments() != null) {
            ArrayList<Attachment> attachments = new ArrayList<Attachment>();
            for (Attachment attachment : quotation.getAttachments()) {
                attachment.setCustomerOrder(customerOrder2);
                attachments.add(attachment);
                attachmentService.addOrUpdateAttachment(attachment);
            }
            customerOrder2.setAttachments(attachments);
        }

        if (quotation.getAssoAffaireOrders() != null)
            for (int assoIndex = 0; assoIndex < quotation.getAssoAffaireOrders().size(); assoIndex++) {
                AssoAffaireOrder quotationAsso = quotation.getAssoAffaireOrders().get(assoIndex);
                if (quotationAsso.getProvisions() != null)
                    for (int provisionIndex = 0; provisionIndex < quotationAsso.getProvisions()
                            .size(); provisionIndex++) {
                        Provision quotationProvision = quotationAsso.getProvisions().get(provisionIndex);
                        if (quotationProvision.getAttachments() != null
                                && quotationProvision.getAttachments().size() > 0)
                            for (Attachment attachment : quotationProvision.getAttachments()) {
                                Attachment newAttachment = attachmentService.cloneAttachment(attachment);
                                newAttachment.setQuotation(null);
                                newAttachment.setProvision(customerOrder2.getAssoAffaireOrders().get(assoIndex)
                                        .getProvisions().get(provisionIndex));
                                attachmentService.addOrUpdateAttachment(newAttachment);
                            }
                    }
            }
        return customerOrder2;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateInvoiceMail(CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        mailHelper.sendCustomerOrderFinalisationToCustomer(customerOrder, true, false, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendInvoiceMail(CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        mailHelper.sendCustomerOrderFinalisationToCustomer(customerOrder, false, true, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String getCardPaymentLinkForCustomerOrderDeposit(CustomerOrder customerOrder, String mail, String subject)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        customerOrder = getCustomerOrder(customerOrder.getId());
        return getCardPaymentLinkForCustomerOrderPayment(customerOrder, mail, subject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String getCardPaymentLinkForPaymentInvoice(CustomerOrder customerOrder, String mail, String subject)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        customerOrder = getCustomerOrder(customerOrder.getId());
        return getCardPaymentLinkForCustomerOrderPayment(customerOrder, mail, subject);
    }

    private String getCardPaymentLinkForCustomerOrderPayment(CustomerOrder customerOrder, String mail, String subject)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {

        if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.ABANDONED))
            throw new OsirisException(null, "Impossible to pay an cancelled customer order n°" + customerOrder.getId());

        com.jss.osiris.modules.quotation.model.CentralPayPaymentRequest request = centralPayPaymentRequestService
                .getCentralPayPaymentRequestByCustomerOrder(customerOrder);

        if (request != null) {
            CentralPayPaymentRequest centralPayPaymentRequest = centralPayDelegateService
                    .getPaymentRequest(request.getPaymentRequestId());

            if (centralPayPaymentRequest != null) {
                if (centralPayPaymentRequest.getPaymentRequestStatus().equals(CentralPayPaymentRequest.ACTIVE)) {
                    centralPayDelegateService.cancelPaymentRequest(request.getPaymentRequestId());
                    centralPayPaymentRequestService.deleteCentralPayPaymentRequest(request);
                }

                if (centralPayPaymentRequest.getPaymentRequestStatus().equals(CentralPayPaymentRequest.CLOSED)
                        && centralPayPaymentRequest.getPaymentStatus().equals(CentralPayPaymentRequest.PAID)) {
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
                    remainingToPay, mail,
                    customerOrder.getId() + "", subject);

            centralPayPaymentRequestService.declareNewCentralPayPaymentRequest(paymentRequest.getPaymentRequestId(),
                    customerOrder, null);
            return paymentRequest.getBreakdowns().get(0).getEndpoint();
        } else {
            throw new OsirisException(null, "Nothing to pay on customer order n°" + customerOrder.getId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean validateCardPaymentLinkForCustomerOrder(CustomerOrder customerOrder,
            com.jss.osiris.modules.quotation.model.CentralPayPaymentRequest request)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        customerOrder = getCustomerOrder(customerOrder.getId());

        if (request != null) {
            CentralPayPaymentRequest centralPayPaymentRequest = centralPayDelegateService
                    .getPaymentRequest(request.getPaymentRequestId());

            if (centralPayPaymentRequest != null) {
                if (centralPayPaymentRequest.getPaymentRequestStatus().equals(CentralPayPaymentRequest.CLOSED)
                        && centralPayPaymentRequest.getPaymentStatus().equals(CentralPayPaymentRequest.PAID)) {

                    if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BILLED)) {
                        if (customerOrder.getInvoices() != null)
                            for (Invoice invoice : customerOrder.getInvoices())
                                if (invoice.getInvoiceStatus().getId()
                                        .equals(constantService.getInvoiceStatusSend().getId())) {
                                    paymentService.generatePaymentOnInvoiceForCbPayment(invoice,
                                            centralPayPaymentRequest);
                                    break;
                                }
                    } else {
                        paymentService.generateDepositOnCustomerOrderForCbPayment(customerOrder,
                                centralPayPaymentRequest);
                        unlockCustomerOrderFromDeposit(customerOrder);
                    }
                }

                if (centralPayPaymentRequest.getCreationDate().isBefore(LocalDateTime.now().minusMinutes(5))) {
                    if (centralPayPaymentRequest.getPaymentRequestStatus().equals(CentralPayPaymentRequest.ACTIVE))
                        centralPayDelegateService.cancelPaymentRequest(centralPayPaymentRequest.getPaymentRequestId());
                    return true;
                }

                return centralPayPaymentRequest.getPaymentRequestStatus().equals(CentralPayPaymentRequest.CLOSED)
                        || centralPayPaymentRequest.getPaymentRequestStatus()
                                .equals(CentralPayPaymentRequest.CANCELED);
            }
        }
        return true;
    }

    @Override
    @Transactional
    public void sendRemindersForCustomerOrderDeposit() throws OsirisException {
        List<CustomerOrder> customerOrders = customerOrderRepository.findCustomerOrderForReminder(
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.WAITING_DEPOSIT));

        if (customerOrders != null && customerOrders.size() > 0)
            for (CustomerOrder customerOrder : customerOrders) {
                batchService.declareNewBatch(Batch.SEND_REMINDER_FOR_CUSTOMER_ORDER_DEPOSITS, customerOrder.getId());
            }
    }

    @Override
    @Transactional
    public void sendReminderForCustomerOrderDeposit(CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        if (customerOrder != null) {
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
                addOrUpdateCustomerOrder(customerOrder, false, true);
            }
        }
    }

    @Override
    public Float getTotalForCustomerOrder(IQuotation customerOrder) {
        Float total = 0f;
        if (customerOrder != null)
            if (customerOrder.getAssoAffaireOrders() != null)
                for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders())
                    if (assoAffaireOrder.getProvisions() != null)
                        for (Provision provision : assoAffaireOrder.getProvisions())
                            if (provision.getInvoiceItems() != null && provision.getInvoiceItems().size() > 0)
                                for (InvoiceItem invoiceItem : provision.getInvoiceItems())
                                    total += invoiceHelper.getTotalForInvoiceItem(invoiceItem);
        return total;
    }

    @Override
    public Float getRemainingAmountToPayForCustomerOrder(CustomerOrder customerOrder) {
        customerOrder = getCustomerOrder(customerOrder.getId());
        if (customerOrder != null) {
            Float total = getTotalForCustomerOrder(customerOrder);
            if (customerOrder.getPayments() != null)
                for (Payment payment : customerOrder.getPayments())
                    if (!payment.getIsCancelled())
                        total -= payment.getPaymentAmount();

            return Math.round(total * 100f) / 100f;
        }
        return 0f;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<byte[]> printMailingLabel(List<String> customerOrdersIn, boolean printLabel,
            boolean printLetters)
            throws OsirisException, OsirisClientMessageException {
        ArrayList<CustomerOrder> customerOrders = new ArrayList<CustomerOrder>();
        for (String id : customerOrdersIn) {
            customerOrders.add(getCustomerOrder(Integer.parseInt(id)));
        }

        if (printLabel)
            for (CustomerOrder customerOrder : customerOrders) {
                try {
                    printDelegate.printMailingLabel(mailComputeHelper.computePaperLabelResult(customerOrder),
                            customerOrder);
                } catch (NumberFormatException e) {
                } catch (Exception e) {
                    throw new OsirisException(e, "Error when printing label");
                }
            }
        if (printLetters) {

            byte[] data = null;
            HttpHeaders headers = null;
            File file = generatePdfDelegate.generateLetterPdf(customerOrders);

            if (file != null) {
                try {
                    data = Files.readAllBytes(file.toPath());
                } catch (IOException e) {
                    throw new OsirisException(e, "Unable to read file " + file.getAbsolutePath());
                }

                headers = new HttpHeaders();
                headers.setContentLength(data.length);
                headers.add("filename", "Postal_letters_"
                        + DateTimeFormatter.ofPattern("yyyyMMdd HHmm").format(LocalDateTime.now()) + ".pdf");
                headers.setAccessControlExposeHeaders(Arrays.asList("filename"));

                // Compute content type
                String mimeType = null;
                try {
                    mimeType = Files.probeContentType(file.toPath());
                } catch (IOException e) {
                    throw new OsirisException(e, "Unable to read file " + file.getAbsolutePath());
                }
                if (mimeType == null)
                    mimeType = "application/pdf";
                headers.set("content-type", mimeType);
                file.delete();
            }
            return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
        }
        return new ResponseEntity<byte[]>(null, new HttpHeaders(), HttpStatus.OK);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAssignedToForCustomerOrder(CustomerOrder customerOrder, Employee employee)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        customerOrder.setAssignedTo(employee);
        addOrUpdateCustomerOrder(customerOrder, true, false);
    }

    @Override
    public List<OrderingSearchResult> searchByQuotationId(Integer idQuotation) {
        return customerOrderRepository.findCustomerOrders(
                Arrays.asList(0), Arrays.asList(0), Arrays.asList(0), LocalDateTime.now().minusYears(100),
                LocalDateTime.now().plusYears(100), Arrays.asList(0), Arrays.asList(0), idQuotation, 0);
    }

    private void findDuplicatesForCustomerOrder(CustomerOrder customerOrder) throws OsirisDuplicateException {
        // Check duplicate
        // Find first affaire customer order
        if (customerOrder.getId() == null && customerOrder.getAssoAffaireOrders() != null
                && customerOrder.getAssoAffaireOrders().size() > 0) {
            OrderingSearch orderingSearch = new OrderingSearch();
            orderingSearch.setAffaires(Arrays.asList(customerOrder.getAssoAffaireOrders().get(0).getAffaire()));
            orderingSearch.setCustomerOrders(new ArrayList<Tiers>());
            if (customerOrder.getResponsable() != null) {
                Tiers tiers = new Tiers();
                tiers.setId(customerOrder.getResponsable().getId());
                orderingSearch.getCustomerOrders().add(tiers);
            } else if (customerOrder.getTiers() != null) {
                orderingSearch.getCustomerOrders().add(customerOrder.getTiers());
            } else if (customerOrder.getConfrere() != null) {
                Tiers tiers = new Tiers();
                tiers.setId(customerOrder.getConfrere().getId());
                orderingSearch.getCustomerOrders().add(tiers);
            }

            // Only last 3 days
            orderingSearch.setStartDate(DateHelper.subtractDaysSkippingWeekends(LocalDateTime.now(), 3));
            List<OrderingSearchResult> duplicatedCustomerOrders = searchOrders(orderingSearch);
            List<CustomerOrder> duplicatedFound = new ArrayList<CustomerOrder>();

            if (duplicatedCustomerOrders != null && duplicatedCustomerOrders.size() > 0) {
                outerloop: for (OrderingSearchResult potentialCustomerOrderResult : duplicatedCustomerOrders) {
                    CustomerOrder potentialCustomerOrder = getCustomerOrder(
                            potentialCustomerOrderResult.getCustomerOrderId());
                    if (!potentialCustomerOrder.getCustomerOrderStatus().getCode()
                            .equals(CustomerOrderStatus.ABANDONED)) {
                        for (AssoAffaireOrder currentAsso : customerOrder.getAssoAffaireOrders()) {
                            boolean foundAsso = false;
                            for (AssoAffaireOrder duplicateAsso : potentialCustomerOrder.getAssoAffaireOrders()) {
                                if (currentAsso.getAffaire().getId().equals(duplicateAsso.getAffaire().getId())) {
                                    foundAsso = true;
                                    for (Provision currentProvision : currentAsso.getProvisions()) {
                                        boolean foundProvision = false;
                                        for (Provision duplicateProvision : duplicateAsso.getProvisions()) {
                                            if (duplicateProvision.getProvisionType().getId()
                                                    .equals(currentProvision.getProvisionType().getId())) {
                                                foundProvision = true;
                                            }
                                        }
                                        if (!foundProvision)
                                            break outerloop;
                                    }
                                }
                            }
                            if (!foundAsso)
                                break outerloop;
                        }
                        duplicatedFound.add(potentialCustomerOrder);
                    }
                }

                if (duplicatedFound.size() > 0) {
                    throw new OsirisDuplicateException(duplicatedFound.stream().map(CustomerOrder::getId).toList());
                }
            }
        }
    }
}
