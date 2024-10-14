package com.jss.osiris.modules.osiris.quotation.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule.Feature;
import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.PrintDelegate;
import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.jackson.JacksonLocalDateDeserializer;
import com.jss.osiris.libs.jackson.JacksonLocalDateSerializer;
import com.jss.osiris.libs.jackson.JacksonLocalDateTimeSerializer;
import com.jss.osiris.libs.jackson.JacksonTimestampMillisecondDeserializer;
import com.jss.osiris.libs.mail.GeneratePdfDelegate;
import com.jss.osiris.libs.mail.MailComputeHelper;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.modules.myjss.profile.service.UserScopeService;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.osiris.invoicing.model.Payment;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceHelper;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceItemService;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceService;
import com.jss.osiris.modules.osiris.invoicing.service.PaymentService;
import com.jss.osiris.modules.osiris.miscellaneous.model.ActiveDirectoryGroup;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.model.InvoicingSummary;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.MailService;
import com.jss.osiris.modules.osiris.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.osiris.miscellaneous.service.PhoneService;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.controller.QuotationValidationHelper;
import com.jss.osiris.modules.osiris.quotation.model.Announcement;
import com.jss.osiris.modules.osiris.quotation.model.AnnouncementStatus;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceDocument;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceFieldType;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderComment;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.DomiciliationStatus;
import com.jss.osiris.modules.osiris.quotation.model.FormaliteStatus;
import com.jss.osiris.modules.osiris.quotation.model.IOrderingSearchTaggedResult;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;
import com.jss.osiris.modules.osiris.quotation.model.OrderingSearch;
import com.jss.osiris.modules.osiris.quotation.model.OrderingSearchResult;
import com.jss.osiris.modules.osiris.quotation.model.OrderingSearchTagged;
import com.jss.osiris.modules.osiris.quotation.model.PaperSet;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.model.SimpleProvision;
import com.jss.osiris.modules.osiris.quotation.model.SimpleProvisionStatus;
import com.jss.osiris.modules.osiris.quotation.model.centralPay.CentralPayPaymentRequest;
import com.jss.osiris.modules.osiris.quotation.repository.CustomerOrderRepository;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@org.springframework.stereotype.Service
public class CustomerOrderServiceImpl implements CustomerOrderService {

    private BigDecimal oneHundredValue = new BigDecimal(100);
    private BigDecimal zeroValue = new BigDecimal(0);

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
    FormaliteStatusService formaliteStatusService;

    @Autowired
    DomiciliationStatusService domiciliationStatusService;

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

    @Autowired
    SimpleProvisionService simpleProvisionService;

    @Autowired
    SimpleProvisionStatusService simpleProvisionStatusService;

    @Autowired
    CustomerOrderCommentService customerOrderCommentService;

    @Autowired
    UserScopeService userScopeService;

    @Override
    public CustomerOrder getCustomerOrder(Integer id) {
        Optional<CustomerOrder> customerOrder = customerOrderRepository.findById(id);
        if (customerOrder.isPresent()) {
            CustomerOrder customerOrderOut = customerOrder.get();
            if (customerOrderOut.getCustomerOrderParentRecurring() != null)
                customerOrderOut.setHasCustomerOrderParentRecurring(true);
            return customerOrderOut;
        }
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

        boolean isNewCustomerOrder = customerOrder.getId() == null;

        if (customerOrder.getCustomerOrderOrigin() == null)
            customerOrder.setCustomerOrderOrigin(constantService.getCustomerOrderOriginOsiris());

        // Set default customer order assignation to sales employee if not set
        if (customerOrder.getAssignedTo() == null)
            if (customerOrder.getResponsable().getDefaultCustomerOrderEmployee() != null)
                customerOrder.setAssignedTo(customerOrder.getResponsable().getDefaultCustomerOrderEmployee());
            else
                customerOrder
                        .setAssignedTo(customerOrder.getResponsable().getTiers().getDefaultCustomerOrderEmployee());

        if (customerOrder.getIsGifted() == null)
            customerOrder.setIsGifted(false);

        customerOrder.setIsQuotation(false);

        if (customerOrder.getDocuments() != null)
            for (Document document : customerOrder.getDocuments()) {
                mailService.populateMailIds(document.getMailsAffaire());
                mailService.populateMailIds(document.getMailsClient());
                document.setCustomerOrder(customerOrder);
            }

        // If from recurring, reset parent customerOrder, because field @JsonIgnore in
        // customerOrder entity
        if (customerOrder.getRecurringStartDate() != null && !isNewCustomerOrder) {
            CustomerOrder currentCustomerOrder = getCustomerOrder(customerOrder.getId());
            customerOrder.setCustomerOrderParentRecurring(currentCustomerOrder.getCustomerOrderParentRecurring());
        }

        // Complete provisions
        boolean oneNewProvision = false;
        if (customerOrder.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders()) {
                assoAffaireOrder.setCustomerOrder(customerOrder);
                assoAffaireOrderService.completeAssoAffaireOrder(assoAffaireOrder, customerOrder, isFromUser);
                if (assoAffaireOrder.getId() != null)
                    batchService.declareNewBatch(Batch.REINDEX_ASSO_AFFAIRE_ORDER, assoAffaireOrder.getId());
                for (Service service : assoAffaireOrder.getServices())
                    for (Provision provision : service.getProvisions())
                        if (provision.getId() == null)
                            oneNewProvision = true;
            }

        if (oneNewProvision)
            customerOrder = customerOrderRepository.save(customerOrder);

        pricingHelper.getAndSetInvoiceItemsForQuotation(customerOrder, true);

        customerOrder = customerOrderRepository.save(customerOrder);

        // Generate first comment
        if (isNewCustomerOrder && isFromUser) {
            String comment = "";
            Tiers tiers = customerOrder.getResponsable().getTiers();
            if (tiers != null && (tiers.getInstructions() != null || tiers.getObservations() != null)) {
                comment = "<p>Intructions du tiers : "
                        + (tiers.getInstructions() != null ? tiers.getInstructions() : "") + "</p>" +
                        "<p>Observations du tiers : " + (tiers.getObservations() != null ? tiers.getObservations() : "")
                        + "</p>" +
                        "<p>Description de la demande : "
                        + (customerOrder.getDescription() != null ? customerOrder.getDescription() : "") + "</p>";
                customerOrderCommentService.createCustomerOrderComment(customerOrder, comment);
            }
        }

        customerOrder = getCustomerOrder(customerOrder.getId());

        if (checkAllProvisionEnded)
            checkAllProvisionEnded(customerOrder);

        // Generate recurring
        if (customerOrder.getIsRecurring() != null && customerOrder.getIsRecurring()
                && customerOrder.getRecurringStartDate() == null) {
            customerOrder.setRecurringStartDate(customerOrder.getRecurringPeriodStartDate());
            customerOrder.setRecurringEndDate(customerOrder.getRecurringPeriodStartDate()
                    .plusMonths(customerOrder.getCustomerOrderFrequency().getMonthNumber()).minusDays(1));
            customerOrderRepository.save(customerOrder);
        }

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
                for (Service service : assoAffaireOrder.getServices())
                    for (Provision provision : service.getProvisions()) {
                        if (provision.getAnnouncement() != null
                                && !provision.getAnnouncement().getAnnouncementStatus().getIsCloseState())
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
                for (Service service : asso.getServices())
                    for (Provision provision : service.getProvisions())
                        if (provision.getAnnouncement() == null)
                            return false;
        return true;
    }

    private boolean isOnlyJssAnnouncement(CustomerOrder customerOrder) throws OsirisException {
        if (!isOnlyAnnouncement(customerOrder))
            return false;

        if (customerOrder != null && customerOrder.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                for (Service service : asso.getServices())
                    for (Provision provision : service.getProvisions())
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
        if ((customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.OPEN)
                || customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BEING_PROCESSED))
                && (targetStatusCode.equals(CustomerOrderStatus.BEING_PROCESSED)
                        || targetStatusCode.equals(CustomerOrderStatus.WAITING_DEPOSIT))) {
            BigDecimal remainingToPay = getRemainingAmountToPayForCustomerOrder(customerOrder);

            Tiers tiers = customerOrder.getResponsable().getTiers();
            boolean isDepositMandatory = false;
            boolean isPaymentTypePrelevement = false;
            isDepositMandatory = tiers.getIsProvisionalPaymentMandatory();

            if (tiers instanceof Tiers)
                isPaymentTypePrelevement = ((Tiers) tiers).getPaymentType().getId()
                        .equals(constantService.getPaymentTypePrelevement().getId());

            if (!isDepositMandatory && !targetStatusCode.equals(CustomerOrderStatus.WAITING_DEPOSIT)
                    || remainingToPay.compareTo(zeroValue) <= 0 || isPaymentTypePrelevement) {
                targetStatusCode = CustomerOrderStatus.BEING_PROCESSED;
                mailHelper.sendCustomerOrderInProgressToCustomer(customerOrder, false);
            } else {
                targetStatusCode = CustomerOrderStatus.WAITING_DEPOSIT;
                mailHelper.sendCustomerOrderDepositMailToCustomer(customerOrder, false, false);
            }

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
            if (!customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.TO_BILLED)
                    && !customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.ABANDONED)) {
                if (customerOrder.getCustomerOrderStatus().getCode()
                        .equals(CustomerOrderStatus.WAITING_DEPOSIT)) {
                    mailHelper.sendCustomerOrderInProgressToCustomer(customerOrder, false);
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
                    && getRemainingAmountToPayForCustomerOrder(customerOrder).compareTo(zeroValue) >= 0) {
                targetStatusCode = CustomerOrderStatus.BILLED;
            }
        }

        // Target : BILLED => generate invoice
        if (targetStatusCode.equals(CustomerOrderStatus.BILLED)) {

            // If no closed paper set
            if (customerOrder.getPaperSets() != null) {
                for (PaperSet paperSet : customerOrder.getPaperSets()) {
                    if ((paperSet.getIsCancelled() == null || paperSet.getIsCancelled() == false)
                            && (paperSet.getIsValidated() == null || paperSet.getIsValidated() == false)) {
                        throw new OsirisClientMessageException(
                                "Impossible de facturer la commande, des actions documentaires sont encore en cours");
                    }
                }
            }
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

            BigDecimal remainingToPayForCurrentCustomerOrder = getRemainingAmountToPayForCustomerOrder(customerOrder)
                    .multiply(oneHundredValue).setScale(0, RoundingMode.HALF_UP).divide(oneHundredValue);

            if (remainingToPayForCurrentCustomerOrder.compareTo(zeroValue) < 0
                    && remainingToPayForCurrentCustomerOrder.abs()
                            .compareTo(BigDecimal.valueOf(Float.parseFloat(payementLimitRefundInEuros))) > 0)
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
                for (Service service : asso.getServices())
                    for (Provision provision : service.getProvisions())
                        if (provision.getInvoiceItems() != null) {
                            for (InvoiceItem invoiceItem : provision.getInvoiceItems())
                                invoiceItemService.deleteInvoiceItem(invoiceItem);
                            provision.setInvoiceItems(null);
                        }

        addOrUpdateCustomerOrder(getCustomerOrder(customerOrder.getId()), true, false);
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
                for (Service service : assoAffaireOrder.getServices())
                    for (Provision provision : service.getProvisions())
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
        Invoice invoice = new Invoice();

        invoice.setIsCreditNote(false);
        invoice.setResponsable(customerOrder.getResponsable());
        invoice.setCustomerOrder(customerOrder);

        invoice.setInvoiceItems(new ArrayList<InvoiceItem>());
        // Associate invoice to invoice item
        for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders()) {
            for (Service service : assoAffaireOrder.getServices())
                for (Provision provision : service.getProvisions()) {
                    if (provision.getInvoiceItems() != null && provision.getInvoiceItems().size() > 0)
                        for (InvoiceItem invoiceItem : provision.getInvoiceItems()) {
                            invoice.getInvoiceItems().add(invoiceItem);
                        }
                }
        }
        invoiceService.addOrUpdateInvoiceFromUser(invoice);
        return invoice;
    }

    @Override
    public List<IOrderingSearchTaggedResult> searchOrdersTagged(OrderingSearchTagged orderingSearchTagged) {
        ArrayList<Integer> statusId = new ArrayList<Integer>();
        if (orderingSearchTagged.getCustomerOrderStatus() != null
                && orderingSearchTagged.getCustomerOrderStatus().size() > 0) {
            for (CustomerOrderStatus customerOrderStatus : orderingSearchTagged.getCustomerOrderStatus())
                if (customerOrderStatus != null)
                    statusId.add(customerOrderStatus.getId());
        } else {
            statusId.add(0);
        }

        ArrayList<Integer> salesEmployeeId = new ArrayList<Integer>();
        if (orderingSearchTagged.getSalesEmployee() != null) {
            for (Employee employee : employeeService.getMyHolidaymaker(orderingSearchTagged.getSalesEmployee()))
                salesEmployeeId.add(employee.getId());
        } else {
            salesEmployeeId.add(0);
        }

        ArrayList<Integer> assignedToEmployeeId = new ArrayList<Integer>();
        if (orderingSearchTagged.getAssignedToEmployee() != null) {
            for (Employee employee : employeeService.getMyHolidaymaker(orderingSearchTagged.getAssignedToEmployee()))
                assignedToEmployeeId.add(employee.getId());
        } else {
            assignedToEmployeeId.add(0);
        }

        if (orderingSearchTagged.getActiveDirectoryGroup() == null) {
            orderingSearchTagged.setActiveDirectoryGroup(new ActiveDirectoryGroup());
            orderingSearchTagged.getActiveDirectoryGroup().setId(0);
        }

        if (orderingSearchTagged.getStartDate() == null)
            orderingSearchTagged.setStartDate(LocalDateTime.now().minusYears(100));

        if (orderingSearchTagged.getEndDate() == null)
            orderingSearchTagged.setEndDate(LocalDateTime.now().plusYears(100));

        List<IOrderingSearchTaggedResult> customerOrders = customerOrderRepository.findTaggedCustomerOrders(statusId,
                salesEmployeeId, assignedToEmployeeId, orderingSearchTagged.getActiveDirectoryGroup().getId(),
                orderingSearchTagged.getIsOnlyDisplayUnread(),
                orderingSearchTagged.getStartDate().withHour(0).withMinute(0),
                orderingSearchTagged.getEndDate().withHour(23).withMinute(59));
        return customerOrders;
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
            for (Tiers tiers : orderingSearch.getCustomerOrders())
                customerOrderId.add(tiers.getId());
        } else {
            customerOrderId.add(0);
        }

        Integer affaireId = 0;
        if (orderingSearch.getAffaire() != null)
            affaireId = orderingSearch.getAffaire().getId();

        if (orderingSearch.getStartDate() == null)
            orderingSearch.setStartDate(LocalDateTime.now().minusYears(100));

        if (orderingSearch.getEndDate() == null)
            orderingSearch.setEndDate(LocalDateTime.now().plusYears(100));

        if (orderingSearch.getIdCustomerOrder() == null)
            orderingSearch.setIdCustomerOrder(0);

        if (orderingSearch.getIdQuotation() == null)
            orderingSearch.setIdQuotation(0);

        if (orderingSearch.getIdCustomerOrderParentRecurring() == null)
            orderingSearch.setIdCustomerOrderParentRecurring(0);

        if (orderingSearch.getIdCustomerOrderChildRecurring() == null)
            orderingSearch.setIdCustomerOrderChildRecurring(0);

        if (orderingSearch.getIsDisplayOnlyParentRecurringCustomerOrder() == null)
            orderingSearch.setIsDisplayOnlyParentRecurringCustomerOrder(false);

        if (orderingSearch.getIsDisplayOnlyRecurringCustomerOrder() == null)
            orderingSearch.setIsDisplayOnlyRecurringCustomerOrder(false);

        if (orderingSearch.getRecurringValidityDate() == null)
            orderingSearch.setRecurringValidityDate(LocalDate.of(1949, 1, 1));

        List<OrderingSearchResult> customerOrders = customerOrderRepository.findCustomerOrders(
                salesEmployeeId, assignedToEmployeeId,
                statusId,
                orderingSearch.getStartDate().withHour(0).withMinute(0),
                orderingSearch.getEndDate().withHour(23).withMinute(59), customerOrderId, affaireId,
                orderingSearch.getIdQuotation(),
                orderingSearch.getIdCustomerOrder(), orderingSearch.getIdCustomerOrderParentRecurring(),
                orderingSearch.getIdCustomerOrderChildRecurring(),
                orderingSearch.getIsDisplayOnlyRecurringCustomerOrder(),
                orderingSearch.getIsDisplayOnlyParentRecurringCustomerOrder(),
                orderingSearch.getRecurringValidityDate());
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
        CustomerOrder customerOrder = new CustomerOrder(quotation.getAssignedTo(),
                quotation.getResponsable().getTiers(),
                quotation.getResponsable(),
                /* quotation.getConfrere(), */ quotation.getSpecialOffers(), LocalDateTime.now(), statusOpen,
                quotation.getDescription(), null,
                quotation.getDocuments(), quotation.getAssoAffaireOrders(), null, false,
                null, quotation.getCustomerOrderComments());

        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule("SimpleModule");
        simpleModule.addSerializer(LocalDateTime.class, new JacksonLocalDateTimeSerializer());
        simpleModule.addSerializer(LocalDate.class, new JacksonLocalDateSerializer());
        simpleModule.addDeserializer(LocalDateTime.class, new JacksonTimestampMillisecondDeserializer());
        simpleModule.addDeserializer(LocalDate.class, new JacksonLocalDateDeserializer());
        objectMapper.registerModule(simpleModule);
        Hibernate5JakartaModule module = new Hibernate5JakartaModule();
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

        if (customerOrder2.getCustomerOrderComments() != null) {
            ArrayList<CustomerOrderComment> customerOrderComments = new ArrayList<CustomerOrderComment>();
            for (CustomerOrderComment customerOrderComment : customerOrder2.getCustomerOrderComments()) {
                customerOrderComment.setId(null);
                customerOrderComment.setCustomerOrder(customerOrder2);
                customerOrderComment.setQuotation(null);
                customerOrderComments.add(customerOrderComment);
            }
            customerOrder2.setCustomerOrderComments(customerOrderComments);
        }

        if (customerOrder2.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder asso : customerOrder2.getAssoAffaireOrders()) {
                asso.setId(null);
                asso.setCustomerOrder(null);
                asso.setQuotation(null);
                for (Service service : asso.getServices()) {
                    service.setId(null);
                    if (service.getAssoServiceDocuments() != null)
                        for (AssoServiceDocument assoServiceDocument : service.getAssoServiceDocuments()) {
                            assoServiceDocument.setId(null);
                            assoServiceDocument.setAttachments(null); // TODO AGN ...
                        }
                    if (service.getAssoServiceFieldTypes() != null && service.getAssoServiceFieldTypes().size() > 0)
                        for (AssoServiceFieldType assoServiceFieldType : service.getAssoServiceFieldTypes()) {
                            assoServiceFieldType.setId(null);
                        }
                    for (Provision provision : service.getProvisions()) {
                        provision.setId(null);
                        if (provision.getAnnouncement() != null) {
                            provision.getAnnouncement().setId(null);
                            if (provision.getAnnouncement().getDocuments() != null)
                                for (Document document : provision.getAnnouncement().getDocuments())
                                    document.setId(null);
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
                for (int serviceIndex = 0; serviceIndex < quotationAsso.getServices().size(); serviceIndex++) {
                    // Service attachments
                    Service quotationService = quotation.getAssoAffaireOrders().get(assoIndex)
                            .getServices().get(serviceIndex);
                    if (quotationService.getAssoServiceDocuments() != null
                            && quotationService.getAssoServiceDocuments().size() > 0) {
                        for (int assoServiceDocumentIndex = 0; assoServiceDocumentIndex < quotationService
                                .getAssoServiceDocuments().size(); assoServiceDocumentIndex++) {
                            AssoServiceDocument quotationServiceDocument = quotationService
                                    .getAssoServiceDocuments().get(assoServiceDocumentIndex);
                            if (quotationServiceDocument.getAttachments() != null
                                    && quotationServiceDocument.getAttachments().size() > 0) {
                                for (Attachment attachment : quotationServiceDocument.getAttachments()) {
                                    Attachment newAttachment = attachmentService.cloneAttachment(attachment);
                                    newAttachment
                                            .setAssoServiceDocument(customerOrder2.getAssoAffaireOrders().get(assoIndex)
                                                    .getServices().get(serviceIndex).getAssoServiceDocuments()
                                                    .get(assoServiceDocumentIndex));
                                    attachmentService.addOrUpdateAttachment(newAttachment);
                                }
                            }
                        }
                    }

                    // Provision attachments
                    for (int provisionIndex = 0; provisionIndex < quotation.getAssoAffaireOrders().get(assoIndex)
                            .getServices().get(serviceIndex).getProvisions()
                            .size(); provisionIndex++) {
                        Provision quotationProvision = quotation.getAssoAffaireOrders().get(assoIndex)
                                .getServices().get(serviceIndex).getProvisions().get(provisionIndex);
                        if (quotationProvision.getAttachments() != null
                                && quotationProvision.getAttachments().size() > 0)
                            for (Attachment attachment : quotationProvision.getAttachments()) {
                                Attachment newAttachment = attachmentService.cloneAttachment(attachment);
                                newAttachment.setQuotation(null);
                                newAttachment.setProvision(customerOrder2.getAssoAffaireOrders().get(assoIndex)
                                        .getServices().get(serviceIndex)
                                        .getProvisions().get(provisionIndex));
                                attachmentService.addOrUpdateAttachment(newAttachment);
                            }
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

        com.jss.osiris.modules.osiris.quotation.model.CentralPayPaymentRequest request = centralPayPaymentRequestService
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

        BigDecimal remainingToPay = new BigDecimal(0);

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

        if (remainingToPay.compareTo(zeroValue) > 0) {
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
            com.jss.osiris.modules.osiris.quotation.model.CentralPayPaymentRequest request)
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
                mailHelper.sendCustomerOrderDepositMailToCustomer(customerOrder, false, true);
                addOrUpdateCustomerOrder(customerOrder, false, true);
            }
        }
    }

    public BigDecimal getTotalForCustomerOrder(IQuotation customerOrder) {
        BigDecimal total = new BigDecimal(0);
        if (customerOrder != null)
            if (customerOrder.getAssoAffaireOrders() != null)
                for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders())
                    for (Service service : assoAffaireOrder.getServices())
                        for (Provision provision : service.getProvisions())
                            if (provision.getInvoiceItems() != null && provision.getInvoiceItems().size() > 0)
                                for (InvoiceItem invoiceItem : provision.getInvoiceItems())
                                    total = total.add(invoiceHelper.getTotalForInvoiceItem(invoiceItem));
        return total;
    }

    @Override
    public BigDecimal getRemainingAmountToPayForCustomerOrder(CustomerOrder customerOrder) {
        customerOrder = getCustomerOrder(customerOrder.getId());

        if (customerOrder != null) {
            BigDecimal total = getTotalForCustomerOrder(customerOrder);
            if (customerOrder.getPayments() != null)
                for (Payment payment : customerOrder.getPayments())
                    if (!payment.getIsCancelled())
                        total = total.subtract(payment.getPaymentAmount());

            return total.multiply(oneHundredValue).setScale(0, RoundingMode.HALF_UP).divide(oneHundredValue);
        }
        return zeroValue;
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
        OrderingSearch search = new OrderingSearch();
        search.setIdQuotation(idQuotation);
        return this.searchOrders(search);
    }

    @Override
    public List<OrderingSearchResult> searchByCustomerOrderParentRecurringId(Integer idCustomerOrder) {
        OrderingSearch search = new OrderingSearch();
        search.setIdCustomerOrderParentRecurring(idCustomerOrder);
        return this.searchOrders(search);
    }

    @Override
    public List<OrderingSearchResult> searchByCustomerOrderParentRecurringByCustomerOrderId(Integer idCustomerOrder) {
        OrderingSearch search = new OrderingSearch();
        search.setIdCustomerOrderChildRecurring(idCustomerOrder);
        return this.searchOrders(search);
    }

    // Recuring
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void generateRecurringCustomerOrders()
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        List<CustomerOrder> allActiveRecuringCustomerOrders = customerOrderRepository
                .findAllActiveRecurringCustomerOrders(
                        customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.OPEN),
                        customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.WAITING_DEPOSIT),
                        customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.ABANDONED));

        if (allActiveRecuringCustomerOrders != null) {
            for (CustomerOrder customerOrder : allActiveRecuringCustomerOrders) {
                List<CustomerOrder> childCustomerOrders = customerOrderRepository
                        .findByCustomerOrderParentRecurringOrderByRecurringEndDateDesc(customerOrder);

                CustomerOrder newChild = null;
                if (childCustomerOrders == null || childCustomerOrders.size() == 0) {
                    newChild = createNewCustomerOrderFromRecurringCustomerOrder(customerOrder);
                    newChild.setRecurringStartDate(customerOrder.getRecurringPeriodStartDate());
                    newChild.setRecurringEndDate(customerOrder.getRecurringPeriodStartDate()
                            .plusMonths(customerOrder.getCustomerOrderFrequency().getMonthNumber()).minusDays(1));
                } else {
                    for (CustomerOrder childCustomerOrder : childCustomerOrders) {
                        if (customerOrder.getRecurringPeriodEndDate().isAfter(LocalDate.now())) {
                            if (childCustomerOrder.getRecurringEndDate().isBefore(LocalDate.now())) {
                                newChild = createNewCustomerOrderFromRecurringCustomerOrder(customerOrder);
                                newChild.setRecurringStartDate(childCustomerOrder.getRecurringEndDate().plusDays(1));
                                newChild.setRecurringEndDate(newChild.getRecurringStartDate()
                                        .plusMonths(customerOrder.getCustomerOrderFrequency().getMonthNumber())
                                        .minusDays(1));
                            }
                        }
                        break;
                    }
                }

                if (newChild != null) {
                    newChild.setCustomerOrderParentRecurring(customerOrder);
                    addOrUpdateCustomerOrder(newChild, false, false);
                    addOrUpdateCustomerOrderStatus(newChild, CustomerOrderStatus.BEING_PROCESSED, false);

                    if (customerOrder.getIsRecurringAutomaticallyBilled()) {
                        newChild = getCustomerOrder(newChild.getId());
                        if (newChild.getCustomerOrderStatus() != null && newChild.getCustomerOrderStatus().getCode()
                                .equals(CustomerOrderStatus.BEING_PROCESSED)) {
                            if (newChild.getAssoAffaireOrders() != null) {
                                for (AssoAffaireOrder asso : newChild.getAssoAffaireOrders()) {
                                    if (asso.getServices() != null) {
                                        for (Service service : asso.getServices()) {
                                            if (service.getProvisions() != null) {
                                                for (Provision provision : service.getProvisions()) {
                                                    if (provision.getSimpleProvision() != null) {
                                                        SimpleProvision simpleProvision = provision
                                                                .getSimpleProvision();
                                                        simpleProvision
                                                                .setSimpleProvisionStatus(simpleProvisionStatusService
                                                                        .getSimpleProvisionStatusByCode(
                                                                                SimpleProvisionStatus.SIMPLE_PROVISION_DONE));
                                                        simpleProvisionService
                                                                .addOrUpdateSimpleProvision(simpleProvision);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            newChild = getCustomerOrder(newChild.getId());
                            try {
                                addOrUpdateCustomerOrder(newChild, false, true); // Put to billed if all ended
                            } catch (Exception e) {
                                if (!(e instanceof OsirisClientMessageException
                                        || e instanceof OsirisValidationException))
                                    throw e;
                            }
                        }
                    }
                }
            }
        }
    }

    private CustomerOrder createNewCustomerOrderFromRecurringCustomerOrder(CustomerOrder customerOrderRecurring)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        CustomerOrderStatus statusOpen = customerOrderStatusService
                .getCustomerOrderStatusByCode(CustomerOrderStatus.OPEN);
        CustomerOrder customerOrder = new CustomerOrder(customerOrderRecurring.getAssignedTo(),
                customerOrderRecurring.getResponsable().getTiers(),
                customerOrderRecurring.getResponsable(),
                /* customerOrderRecurring.getConfrere(), */customerOrderRecurring.getSpecialOffers(),
                LocalDateTime.now(),
                statusOpen, customerOrderRecurring.getDescription(), null,
                customerOrderRecurring.getDocuments(), customerOrderRecurring.getAssoAffaireOrders(), null, false,
                null, null);

        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule("SimpleModule");
        simpleModule.addSerializer(LocalDateTime.class, new JacksonLocalDateTimeSerializer());
        simpleModule.addSerializer(LocalDate.class, new JacksonLocalDateSerializer());
        simpleModule.addDeserializer(LocalDateTime.class, new JacksonTimestampMillisecondDeserializer());
        simpleModule.addDeserializer(LocalDate.class, new JacksonLocalDateDeserializer());
        objectMapper.registerModule(simpleModule);
        Hibernate5JakartaModule module = new Hibernate5JakartaModule();
        module.enable(Feature.FORCE_LAZY_LOADING);
        objectMapper.registerModule(module);
        String customerOrderString;
        try {
            customerOrderString = objectMapper.writeValueAsString(customerOrder);
        } catch (JsonProcessingException e) {
            throw new OsirisException(e,
                    "Error when cloning quotation to customer order for customerOrderRecurring "
                            + customerOrderRecurring.getId());
        }

        CustomerOrder customerOrder2;
        try {
            customerOrder2 = objectMapper.readValue(customerOrderString, CustomerOrder.class);
        } catch (JsonProcessingException e) {
            throw new OsirisException(e,
                    "Error when reading clone of customerOrder for customerOrderRecurring "
                            + customerOrderRecurring.getId());
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
                for (Service service : asso.getServices()) {
                    List<Provision> nonRecuringProvisionToDelete = new ArrayList<Provision>();
                    service.setId(null);
                    if (service.getAssoServiceDocuments() != null)
                        for (AssoServiceDocument assoServiceDocument : service.getAssoServiceDocuments()) {
                            assoServiceDocument.setId(null);
                            assoServiceDocument.setAttachments(null);
                        }
                    for (Provision provision : service.getProvisions()) {
                        provision.setId(null);
                        if (provision.getAnnouncement() != null) {
                            provision.getAnnouncement().setId(null);
                            provision.getAnnouncement().setAnnouncementStatus(announcementStatusService
                                    .getAnnouncementStatusByCode(AnnouncementStatus.ANNOUNCEMENT_NEW));
                            if (provision.getAnnouncement().getDocuments() != null)
                                for (Document document : provision.getAnnouncement().getDocuments())
                                    document.setId(null);
                        }
                        if (provision.getFormalite() != null) {
                            provision.getFormalite().setFormaliteStatus(
                                    formaliteStatusService.getFormaliteStatusByCode(FormaliteStatus.FORMALITE_NEW));
                            provision.getFormalite().setId(null);
                        }
                        if (provision.getSimpleProvision() != null) {
                            provision.getSimpleProvision().setSimpleProvisionStatus(simpleProvisionStatusService
                                    .getSimpleProvisionStatusByCode(SimpleProvisionStatus.SIMPLE_PROVISION_NEW));
                            provision.getSimpleProvision().setId(null);
                        }
                        if (provision.getDomiciliation() != null) {
                            provision.getDomiciliation().setDomiciliationStatus(domiciliationStatusService
                                    .getDomiciliationStatusByCode(DomiciliationStatus.DOMICILIATION_NEW));
                            provision.getDomiciliation().setId(null);
                        }
                        if (provision.getInvoiceItems() != null)
                            for (InvoiceItem invoiceItem : provision.getInvoiceItems())
                                invoiceItem.setId(null);
                        provision.setAttachments(null);

                        if (provision.getProvisionType().getIsRecurring() == null
                                || !provision.getProvisionType().getIsRecurring()) {
                            nonRecuringProvisionToDelete.add(provision);
                        }
                    }
                    if (nonRecuringProvisionToDelete.size() > 0)
                        service.getProvisions().removeAll(nonRecuringProvisionToDelete);
                }
            }
        addOrUpdateCustomerOrder(customerOrder2, false, true);

        return customerOrder2;
    }

    public List<CustomerOrder> searchOrdersForCurrentUser(List<String> customerOrderStatus, Integer page,
            String sortBy) {
        List<CustomerOrderStatus> customerOrderStatusToFilter = new ArrayList<CustomerOrderStatus>();
        boolean displayPayed = false;

        if (customerOrderStatus != null && customerOrderStatus.size() > 0) {
            for (String customerOrderStatusCode : customerOrderStatus) {
                if (customerOrderStatusCode.equals(CustomerOrderStatus.PAYED)) {
                    displayPayed = true;
                    continue;
                }
                CustomerOrderStatus customerOrderStatusFetched = customerOrderStatusService
                        .getCustomerOrderStatusByCode(customerOrderStatusCode);
                if (customerOrderStatusFetched != null)
                    customerOrderStatusToFilter.add(customerOrderStatusFetched);
            }

            CustomerOrderStatus customerOrderStatusBilled = customerOrderStatusService
                    .getCustomerOrderStatusByCode(CustomerOrderStatus.BILLED);

            List<Responsable> responsablesToFilter = userScopeService.getUserScopeResponsables();

            if (customerOrderStatusToFilter.size() > 0 && responsablesToFilter != null
                    && responsablesToFilter.size() > 0) {

                Order order = new Order(Direction.DESC, "createdDate");

                if (sortBy.equals("createdDateAsc"))
                    order = new Order(Direction.ASC, "createdDate");

                if (sortBy.equals("statusAsc"))
                    order = new Order(Direction.ASC, "customerOrderStatus");

                Sort sort = Sort.by(Arrays.asList(order));
                Pageable pageableRequest = PageRequest.of(page, 50, sort);
                return populateTransientField(customerOrderRepository.searchOrdersForCurrentUser(responsablesToFilter,
                        customerOrderStatusToFilter, pageableRequest, customerOrderStatusBilled, displayPayed));
            }
        }

        return null;
    }

    private List<CustomerOrder> populateTransientField(List<CustomerOrder> customerOrders) {
        if (customerOrders != null && customerOrders.size() > 0)
            for (CustomerOrder customerOrder : customerOrders) {
                List<String> affaireLabels = new ArrayList<String>();
                List<String> serviceLabels = new ArrayList<String>();
                customerOrder.setHasMissingInformations(false);
                if (customerOrder.getAssoAffaireOrders() != null)
                    for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders()) {
                        String affaireDenomination = assoAffaireOrder.getAffaire().getDenomination();
                        if (affaireDenomination == null || affaireDenomination.length() == 0)
                            affaireDenomination = assoAffaireOrder.getAffaire().getFirstname() + " "
                                    + assoAffaireOrder.getAffaire().getLastname();
                        if (affaireLabels.indexOf(affaireDenomination) < 0)
                            affaireLabels.add(affaireDenomination);

                        if (assoAffaireOrder.getServices() != null && assoAffaireOrder.getServices().size() > 0)
                            for (Service service : assoAffaireOrder.getServices()) {
                                String serviceLabel = service.getCustomLabel();
                                if (serviceLabel == null || serviceLabel.length() == 0)
                                    serviceLabel = service.getServiceType().getLabel();
                                if (serviceLabels.indexOf(serviceLabel) < 0)
                                    serviceLabels.add(serviceLabel);
                            }

                        if (assoAffaireOrder.getServices() != null)
                            for (Service service : assoAffaireOrder.getServices()) {
                                if (assoAffaireOrderService.isServiceHasMissingInformations(service)) {
                                    customerOrder.setHasMissingInformations(true);
                                }
                            }
                    }

                if (affaireLabels.size() > 0)
                    customerOrder.setAffairesList(String.join(" / ", affaireLabels));
                customerOrder.setServicesList(String.join(" / ", serviceLabels));
            }

        return customerOrders;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Payment> getApplicablePaymentsForCustomerOrder(CustomerOrder customerOrder) throws OsirisException {
        customerOrder = getCustomerOrder(customerOrder.getId());
        List<Payment> payments = new ArrayList<Payment>();

        if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BILLED)
                && customerOrder.getInvoices() != null) {
            for (Invoice invoice : customerOrder.getInvoices())
                if (invoice.getResponsable() != null
                        && (invoice.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusSend().getId())
                                || invoice.getInvoiceStatus().getId()
                                        .equals(constantService.getInvoiceStatusPayed().getId()))) {
                    if (invoice.getPayments() != null) {
                        for (Payment payment : invoice.getPayments()) {
                            // We hide appoint from customer
                            if ((payment.getIsCancelled() == null || payment.getIsCancelled() == false)
                                    && (payment.getIsAppoint() == null || payment.getIsAppoint() == false))
                                payments.add(payment);
                        }
                    }
                }
        }
        // If not billed, all payments are on the customer order,
        // If billed, took only refund
        if (customerOrder.getPayments() != null) {
            for (Payment payment : customerOrder.getPayments()) {
                if ((payment.getIsCancelled() == null || payment.getIsCancelled() == false)
                        && (payment.getIsAppoint() == null || payment.getIsAppoint() == false))
                    if (payment.getRefund() != null
                            || !customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BILLED))
                        payments.add(payment);
            }
        }
        return payments;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InvoicingSummary getInvoicingSummaryForCustomerOrder(CustomerOrder customerOrder) throws OsirisException {
        customerOrder = getCustomerOrder(customerOrder.getId());
        InvoicingSummary invoicingSummary = new InvoicingSummary();

        if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BILLED)
                && customerOrder.getInvoices() != null) {
            for (Invoice invoice : customerOrder.getInvoices())
                if (invoice.getResponsable() != null
                        && (invoice.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusSend().getId())
                                || invoice.getInvoiceStatus().getId()
                                        .equals(constantService.getInvoiceStatusPayed().getId()))) {
                    if (invoice.getPayments() != null) {
                        for (Payment payment : invoice.getPayments()) {
                            // We hide appoint from customer
                            if ((payment.getIsCancelled() == null || payment.getIsCancelled() == false)
                                    && (payment.getIsAppoint() == null || payment.getIsAppoint() == false)) {

                            }

                        }
                    }
                }
        } else {
            BigDecimal totalPrice = new BigDecimal(0);
            BigDecimal discountTotal = new BigDecimal(0);
            BigDecimal preTaxPriceTotal = new BigDecimal(0);
            BigDecimal vatTotal = new BigDecimal(0);
            if (customerOrder != null)
                if (customerOrder.getAssoAffaireOrders() != null)
                    for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders())
                        for (Service service : assoAffaireOrder.getServices())
                            for (Provision provision : service.getProvisions())
                                if (provision.getInvoiceItems() != null && provision.getInvoiceItems().size() > 0)
                                    // for (InvoiceItem invoiceItem : provision.getInvoiceItems()) {
                                    // if (invoiceItem.getPreTaxPriceReinvoiced() != null) {
                                    // preTaxPriceTotal
                                    // .add(new BigDecimal(invoiceItem.getPreTaxPriceReinvoiced()));
                                    // } else if (invoiceItem.getPreTaxPrice() != null
                                    // && invoiceItem.getPreTaxPrice() > 0) {
                                    // preTaxPriceTotal
                                    // .add(new BigDecimal(invoiceItem.getPreTaxPrice()));
                                    //
                                    // }
                                    //
                                    // if (invoiceItem.getVatPrice() != null) {
                                    // // vatTotal.add(new BigDecimal(invoiceItem.get))
                                    // }
                                    // }
                                    return null;
        }
        return null;
    }

}
