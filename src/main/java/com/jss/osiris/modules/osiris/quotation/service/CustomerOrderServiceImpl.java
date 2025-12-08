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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.libs.search.service.SearchService;
import com.jss.osiris.modules.myjss.profile.controller.MyJssProfileController;
import com.jss.osiris.modules.myjss.profile.service.UserScopeService;
import com.jss.osiris.modules.myjss.quotation.service.MyJssQuotationDelegate;
import com.jss.osiris.modules.myjss.wordpress.model.AssoProvisionPostNewspaper;
import com.jss.osiris.modules.myjss.wordpress.model.Subscription;
import com.jss.osiris.modules.myjss.wordpress.service.AssoProvisionPostNewspaperService;
import com.jss.osiris.modules.myjss.wordpress.service.NewspaperService;
import com.jss.osiris.modules.myjss.wordpress.service.PostService;
import com.jss.osiris.modules.myjss.wordpress.service.SubscriptionService;
import com.jss.osiris.modules.osiris.crm.model.Voucher;
import com.jss.osiris.modules.osiris.crm.service.VoucherService;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceLabelResult;
import com.jss.osiris.modules.osiris.invoicing.model.InvoicingBlockage;
import com.jss.osiris.modules.osiris.invoicing.model.Payment;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceItemService;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceService;
import com.jss.osiris.modules.osiris.invoicing.service.PaymentService;
import com.jss.osiris.modules.osiris.miscellaneous.model.ActiveDirectoryGroup;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.osiris.miscellaneous.model.CustomerOrderOrigin;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.model.InvoicingSummary;
import com.jss.osiris.modules.osiris.miscellaneous.model.Notification;
import com.jss.osiris.modules.osiris.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.CompetentAuthorityService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.MailService;
import com.jss.osiris.modules.osiris.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.controller.QuotationValidationHelper;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.Announcement;
import com.jss.osiris.modules.osiris.quotation.model.AnnouncementStatus;
import com.jss.osiris.modules.osiris.quotation.model.AssignationType;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceDocument;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceFieldType;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderComment;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderTransient;
import com.jss.osiris.modules.osiris.quotation.model.DomiciliationStatus;
import com.jss.osiris.modules.osiris.quotation.model.FormaliteStatus;
import com.jss.osiris.modules.osiris.quotation.model.IOrderingSearchTaggedResult;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;
import com.jss.osiris.modules.osiris.quotation.model.InvoicingStatistics;
import com.jss.osiris.modules.osiris.quotation.model.OrderBlockage;
import com.jss.osiris.modules.osiris.quotation.model.OrderingSearch;
import com.jss.osiris.modules.osiris.quotation.model.OrderingSearchResult;
import com.jss.osiris.modules.osiris.quotation.model.OrderingSearchTagged;
import com.jss.osiris.modules.osiris.quotation.model.PaperSet;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.model.ServiceType;
import com.jss.osiris.modules.osiris.quotation.model.SimpleProvision;
import com.jss.osiris.modules.osiris.quotation.model.SimpleProvisionStatus;
import com.jss.osiris.modules.osiris.quotation.model.ToOrderStatistics;
import com.jss.osiris.modules.osiris.quotation.model.centralPay.CentralPayPaymentRequest;
import com.jss.osiris.modules.osiris.quotation.repository.CustomerOrderRepository;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;
import com.jss.osiris.modules.osiris.tiers.service.TiersService;

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
    MailService mailService;

    @Autowired
    QuotationService quotationService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    CompetentAuthorityService competentAuthorityService;

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

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    CentralPayPaymentRequestService centralPayPaymentRequestService;

    @Autowired
    InvoiceItemService invoiceItemService;

    @Autowired
    BatchService batchService;

    @Autowired
    SimpleProvisionService simpleProvisionService;

    @Autowired
    SimpleProvisionStatusService simpleProvisionStatusService;

    @Autowired
    ServiceTypeService serviceTypeService;

    @Autowired
    ServiceService serviceService;

    @Autowired
    AffaireService affaireService;

    @Autowired
    ResponsableService responsableService;

    @Autowired
    TiersService tiersService;

    @Autowired
    MyJssProfileController myJssProfileController;

    @Autowired
    ProvisionService provisionService;

    @Autowired
    AssoProvisionPostNewspaperService assoProvisionPostNewspaperService;

    @Autowired
    PostService postService;

    @Autowired
    NewspaperService newspaperService;

    @Autowired
    SubscriptionService subscriptionService;

    @Autowired
    QuotationValidationHelper quotationValidationHelper;

    @Autowired
    MyJssQuotationDelegate myJssQuotationDelegate;

    @Autowired
    AnnouncementService announcementService;

    @Autowired
    VoucherService voucherService;

    @Autowired
    UserScopeService userScopeService;

    @Autowired
    CustomerOrderAssignationService customerOrderAssignationService;

    @Autowired
    SearchService searchService;

    private CustomerOrder simpleAddOrUpdate(CustomerOrder customerOrder) {
        return customerOrderRepository.save(customerOrder);
    }

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
    public void markCustomerOrderAsPayed(CustomerOrder customerOrder, boolean isPayed)
            throws OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException, OsirisException {
        customerOrder = getCustomerOrder(customerOrder.getId());
        customerOrder.setIsPayed(isPayed);
        if (isPayed)
            subscriptionService.saveSubscription(customerOrder);

        simpleAddOrUpdate(customerOrder);
    }

    @Override
    public CustomerOrder addOrUpdateCustomerOrder(CustomerOrder customerOrder, boolean isFromUser,
            boolean checkAllProvisionEnded)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {

        boolean isNewCustomerOrder = customerOrder.getId() == null;
        boolean hasNewAsso = false;

        if (isNewCustomerOrder && customerOrder.getCreatedDate() == null)
            customerOrder.setCreatedDate(LocalDateTime.now());

        if (customerOrder.getCustomerOrderOrigin() == null) {
            if (employeeService.getCurrentMyJssUser() != null || employeeService.getCurrentEmployee() == null)
                customerOrder.setCustomerOrderOrigin(constantService.getCustomerOrderOriginMyJss());
            else
                customerOrder.setCustomerOrderOrigin(constantService.getCustomerOrderOriginOsiris());
        }

        if (customerOrder.getIsGifted() == null)
            customerOrder.setIsGifted(false);

        customerOrder.setIsQuotation(false);

        if (customerOrder.getDocuments() != null)
            for (Document document : customerOrder.getDocuments()) {
                mailService.populateMailIds(document.getMailsAffaire());
                mailService.populateMailIds(document.getMailsClient());
                mailService.populateMailId(document.getReminderMail());
                document.setCustomerOrder(customerOrder);
            }

        // If from recurring, reset parent customerOrder, because field @JsonIgnore in
        // customerOrder entity
        if (customerOrder.getRecurringStartDate() != null && !isNewCustomerOrder) {
            CustomerOrder currentCustomerOrder = getCustomerOrder(customerOrder.getId());
            customerOrder.setCustomerOrderParentRecurring(currentCustomerOrder.getCustomerOrderParentRecurring());
        }

        if (customerOrder.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                if (asso.getId() == null) {
                    hasNewAsso = true;
                    break;
                }

        if (isNewCustomerOrder || hasNewAsso)
            customerOrder = simpleAddOrUpdate(customerOrder);

        // Complete provisions
        if (customerOrder.getAssoAffaireOrders() != null) {
            for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders()) {
                assoAffaireOrder.setCustomerOrder(customerOrder);
                if (assoAffaireOrder.getServices() != null && assoAffaireOrder.getServices().size() > 0) {
                    assoAffaireOrderService.completeAssoAffaireOrder(assoAffaireOrder, customerOrder, isFromUser);
                    if (assoAffaireOrder.getId() != null)
                        batchService.declareNewBatch(Batch.REINDEX_ASSO_AFFAIRE_ORDER, assoAffaireOrder.getId());
                    for (Service service : assoAffaireOrder.getServices())
                        if (service.getProvisions() != null)
                            for (Provision provision : service.getProvisions())
                                if (provision.getId() == null && !isNewCustomerOrder) {
                                    provision.setService(service);
                                    provisionService.addOrUpdateProvision(provision);
                                }
                }
            }

            pricingHelper.getAndSetInvoiceItemsForQuotation(customerOrder, true);
        }

        customerOrder = simpleAddOrUpdate(customerOrder);

        customerOrder = getCustomerOrder(customerOrder.getId());

        if (checkAllProvisionEnded)
            checkAllProvisionEnded(customerOrder);

        // Generate recurring
        if (customerOrder.getIsRecurring() != null && customerOrder.getIsRecurring()
                && customerOrder.getRecurringStartDate() == null) {
            customerOrder.setRecurringStartDate(customerOrder.getRecurringPeriodStartDate());
            customerOrder.setRecurringEndDate(customerOrder.getRecurringPeriodStartDate()
                    .plusMonths(customerOrder.getCustomerOrderFrequency().getMonthNumber()).minusDays(1));
            simpleAddOrUpdate(customerOrder);
        }

        customerOrderAssignationService.completeAssignationForCustomerOrder(customerOrder);

        // In progress when created in Osiris
        if (isNewCustomerOrder
                && customerOrder.getCustomerOrderOrigin().getId()
                        .equals(constantService.getCustomerOrderOriginOsiris().getId())
                && customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.DRAFT) && isFromUser)
            addOrUpdateCustomerOrderStatus(customerOrder, CustomerOrderStatus.BEING_PROCESSED, isFromUser);

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

    private boolean isOnlySubscription(CustomerOrder customerOrder) throws OsirisException {
        if (customerOrder != null && customerOrder.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                for (Service service : asso.getServices())
                    if (service.getServiceTypes() != null)
                        for (ServiceType serviceType : service.getServiceTypes())
                            if (!serviceType.getId().equals(constantService.getServiceTypeAnnualSubscription().getId())
                                    && !serviceType.getId().equals(
                                            constantService.getServiceTypeEnterpriseAnnualSubscription().getId())
                                    && !serviceType.getId()
                                            .equals(constantService.getServiceTypeKioskNewspaperBuy().getId())
                                    && !serviceType.getId()
                                            .equals(constantService.getServiceTypeUniqueArticleBuy().getId()))
                                return false;
        return true;
    }

    @Override
    public boolean isOnlyJssAnnouncementOrSubscription(CustomerOrder customerOrder, Boolean isReadyForBilling)
            throws OsirisException {
        if (!isOnlyAnnouncement(customerOrder) && !isOnlySubscription(customerOrder))
            return false;

        if (isOnlySubscription(customerOrder))
            return true;

        if (customerOrder != null && customerOrder.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                for (Service service : asso.getServices())
                    for (Provision provision : service.getProvisions()) {
                        if (provision.getAnnouncement().getConfrere() == null || provision.getAnnouncement() != null
                                && !provision.getAnnouncement().getConfrere().getId()
                                        .equals(constantService.getConfrereJssSpel().getId()))
                            return false;
                        if (isReadyForBilling && provision.getAnnouncement() != null
                                && (provision.getIsRedactedByJss() || provision.getAnnouncement().getNotice() == null
                                        || provision.getAnnouncement().getNotice().length() == 0
                                        || provision.getAnnouncement().getPublicationDate() == null
                                        || provision.getAnnouncement().getNoticeTypeFamily() == null
                                        || provision.getAnnouncement().getNoticeTypes().isEmpty()))
                            return false;
                    }
        return true;
    }

    @Override
    public CustomerOrder addOrUpdateCustomerOrderStatus(CustomerOrder customerOrder, String targetStatusCode,
            boolean isFromUser)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        // Handle automatic workflow for Announcement created from website
        boolean checkAllProvisionEnded = false;

        // Determine if deposit is mandatory or not
        if ((customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.DRAFT)
                || customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BEING_PROCESSED))
                && (targetStatusCode.equals(CustomerOrderStatus.BEING_PROCESSED)
                        || targetStatusCode.equals(CustomerOrderStatus.WAITING_DEPOSIT))) {
            BigDecimal remainingToPay = getRemainingAmountToPayForCustomerOrder(customerOrder);

            Tiers tiers = customerOrder.getResponsable().getTiers();
            boolean isPaymentTypePrelevement = false;

            boolean isDepositMandatory = quotationService.isDepositMandatory(customerOrder);

            if (tiers instanceof Tiers)
                isPaymentTypePrelevement = ((Tiers) tiers).getPaymentType().getId()
                        .equals(constantService.getPaymentTypePrelevement().getId());

            if (!isDepositMandatory && !targetStatusCode.equals(CustomerOrderStatus.WAITING_DEPOSIT)
                    || remainingToPay.compareTo(zeroValue) <= 0 || isPaymentTypePrelevement) {
                targetStatusCode = CustomerOrderStatus.BEING_PROCESSED;
                customerOrder.setProductionEffectiveDateTime(LocalDateTime.now());
                mailHelper.sendCustomerOrderInProgressToCustomer(customerOrder, false);
            } else {
                targetStatusCode = CustomerOrderStatus.WAITING_DEPOSIT;
                try {
                    mailHelper.sendCustomerOrderDepositMailToCustomer(customerOrder, false, false);
                } catch (OsirisClientMessageException e) {
                }
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
            if (customerOrder.getProductionEffectiveDateTime() == null)
                customerOrder.setProductionEffectiveDateTime(LocalDateTime.now());
            resetDeboursInvoiceItems(customerOrder);
            // Confirm deposit taken into account or customer order starting and only if not
            // from to billed
            if (!customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.TO_BILLED)
                    && !customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.ABANDONED)) {
                if (customerOrder.getCustomerOrderStatus().getCode()
                        .equals(CustomerOrderStatus.WAITING_DEPOSIT)) {
                    mailHelper.sendCustomerOrderInProgressToCustomer(customerOrder, false);
                }
            }
        }

        // Target : TO BILLED => notify
        if (targetStatusCode.equals(CustomerOrderStatus.TO_BILLED)) {
            if (customerOrder.getPrincingEffectiveDate() == null)
                customerOrder.setPrincingEffectiveDate(LocalDate.now());
            // Auto billed for JSS Announcement only customer order
            if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BEING_PROCESSED)
                    && isOnlyJssAnnouncementOrSubscription(customerOrder, false)
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
            this.addOrUpdateCustomerOrder(customerOrder, isFromUser, checkAllProvisionEnded);

            // Protection : if we already have an invoice not cancelled, abord...
            if (customerOrder.getInvoices() != null) {
                for (Invoice invoice : customerOrder.getInvoices())
                    if (invoice.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusSend().getId())
                            || invoice.getInvoiceStatus().getId()
                                    .equals(constantService.getInvoiceStatusPayed().getId()))
                        throw new OsirisClientMessageException("Une facture existe déjà pour cette commande !");
            }

            BigDecimal remainingToPayForCurrentCustomerOrder = getRemainingAmountToPayForCustomerOrder(customerOrder)
                    .multiply(oneHundredValue).setScale(0, RoundingMode.HALF_EVEN).divide(oneHundredValue);

            if (remainingToPayForCurrentCustomerOrder.compareTo(zeroValue) < 0
                    && remainingToPayForCurrentCustomerOrder.abs()
                            .compareTo(BigDecimal.valueOf(Float.parseFloat(payementLimitRefundInEuros))) > 0)
                throw new OsirisException(null, "Impossible to billed, too much money on customerOrder !");

            // If deposit already set, associate them to invoice
            List<Payment> paymentToMove = new ArrayList<Payment>();
            List<Payment> customerOrderPayement = paymentService.getPaymentForCustomerOrder(customerOrder);

            if (customerOrderPayement != null)
                for (Payment payment : customerOrderPayement)
                    if (!payment.getIsCancelled())
                        paymentToMove.add(payment);

            generateInvoice(customerOrder, paymentToMove);

            entityManager.flush();
            entityManager.clear();

            mailHelper.sendCustomerOrderFinalisationToCustomer(getCustomerOrder(customerOrder.getId()), false, false,
                    false);
            mailHelper.sendCustomerOrderAttachmentOnFinalisationToCustomer(getCustomerOrder(customerOrder.getId()),
                    false);

            if (customerOrder.getInvoicingEmployee() == null)
                customerOrder.setInvoicingEmployee(employeeService.getCurrentEmployee());
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

        customerOrder.setIsPayed(false);
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

            if (isOnlyJssAnnouncementOrSubscription(customerOrder, true)) {
                quotationValidationHelper.validateQuotationAndCustomerOrder(customerOrder, null);
                autoBilledProvisions(customerOrder);
            }
        }

        return customerOrder;
    }

    private Invoice generateInvoice(CustomerOrder customerOrder, List<Payment> paymentsToUseForInvoice)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        // Generate blank invoice
        Invoice invoice = new Invoice();

        invoice.setIsCreditNote(false);
        invoice.setResponsable(customerOrder.getResponsable());
        invoice.setCustomerOrder(customerOrder);

        invoice.setInvoiceItems(new ArrayList<InvoiceItem>());
        // Associate invoice to invoice item
        if (customerOrder.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders()) {
                for (Service service : assoAffaireOrder.getServices())
                    for (Provision provision : service.getProvisions()) {
                        if (provision.getInvoiceItems() != null && provision.getInvoiceItems().size() > 0)
                            for (InvoiceItem invoiceItem : provision.getInvoiceItems()) {
                                invoice.getInvoiceItems().add(invoiceItem);
                            }
                    }
            }
        invoiceService.addOrUpdateInvoiceFromUser(invoice, paymentsToUseForInvoice);
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
                salesEmployeeId,
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
                .getCustomerOrderStatusByCode(CustomerOrderStatus.DRAFT);
        CustomerOrder customerOrder = new CustomerOrder(quotation.getResponsable().getTiers(),
                quotation.getResponsable(),
                /* quotation.getConfrere(), */ quotation.getSpecialOffers(), LocalDateTime.now(), statusOpen,
                quotation.getDescription(), null,
                quotation.getDocuments(), quotation.getAssoAffaireOrders(), null, false,
                null, quotation.getCustomerOrderComments(), quotation.getCustomerOrderOrigin());

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

        customerOrder2.setPrincingEffectiveDate(quotation.getPrincingEffectiveDate());

        if (customerOrder2.getDocuments() != null) {
            ArrayList<Document> documents = new ArrayList<Document>();
            for (Document document : customerOrder2.getDocuments()) {
                document = documentService.cloneOrMergeDocument(document, null);
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
                            assoServiceDocument.setAttachments(null);
                        }
                    if (service.getAssoServiceFieldTypes() != null && service.getAssoServiceFieldTypes().size() > 0)
                        for (AssoServiceFieldType assoServiceFieldType : service.getAssoServiceFieldTypes()) {
                            assoServiceFieldType.setId(null);
                        }
                    for (Provision provision : service.getProvisions()) {
                        provision.setId(null);
                        if (provision.getAnnouncement() != null) {
                            provision.getAnnouncement().setId(null);
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

        addOrUpdateCustomerOrderStatusFromUser(customerOrder2, CustomerOrderStatus.BEING_PROCESSED);
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
    public String getCardPaymentLinkForCustomerOrderDeposit(List<CustomerOrder> customerOrders, String mail,
            String subject)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        return getCardPaymentLinkForCustomerOrderPayment(customerOrders, mail, subject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String getCardPaymentLinkForPaymentInvoice(List<CustomerOrder> customerOrders, String mail, String subject)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        return getCardPaymentLinkForCustomerOrderPayment(customerOrders, mail, subject);
    }

    private String getCardPaymentLinkForCustomerOrderPayment(List<CustomerOrder> customerOrders, String mail,
            String subject)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        if (customerOrders == null || customerOrders.size() == 0)
            throw new OsirisException("no customer orders provided");

        BigDecimal remainingToPay = new BigDecimal(0);
        for (CustomerOrder customerOrder : customerOrders) {
            customerOrder = getCustomerOrder(customerOrder.getId());
            if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.ABANDONED))
                throw new OsirisException(null,
                        "Impossible to pay an cancelled customer order n°" + customerOrder.getId());

            if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BILLED)) {
                if (customerOrder.getInvoices() != null)
                    for (Invoice invoice : customerOrder.getInvoices())
                        if (invoice.getInvoiceStatus().getId()
                                .equals(constantService.getInvoiceStatusSend().getId())) {
                            remainingToPay = remainingToPay
                                    .add(invoiceService.getRemainingAmountToPayForInvoice(invoice));
                            break;
                        }
            } else {
                remainingToPay = remainingToPay.add(getRemainingAmountToPayForCustomerOrder(customerOrder));
            }
        }

        if (remainingToPay.compareTo(zeroValue) > 0) {
            CentralPayPaymentRequest paymentRequest = centralPayDelegateService.generatePayPaymentRequest(
                    remainingToPay, mail,
                    String.join(", ",
                            customerOrders.stream().map(c -> c.getId().toString()).collect(Collectors.toList())),
                    subject, false);

            centralPayPaymentRequestService.declareNewCentralPayPaymentRequest(paymentRequest.getPaymentRequestId(),
                    customerOrders, null);
            return paymentRequest.getBreakdowns().get(0).getEndpoint();
        } else {
            throw new OsirisException(null, "Nothing to pay on customer order n°" + String.join(", ",
                    customerOrders.stream().map(c -> c.getId().toString()).collect(Collectors.toList())));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean validateCardPaymentLinkForCustomerOrder(List<CustomerOrder> customerOrders,
            com.jss.osiris.modules.osiris.quotation.model.CentralPayPaymentRequest request)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {

        if (request != null) {
            CentralPayPaymentRequest centralPayPaymentRequest = centralPayDelegateService
                    .getPaymentRequest(request.getPaymentRequestId());

            if (centralPayPaymentRequest != null && customerOrders != null) {
                if (centralPayPaymentRequest.getPaymentRequestStatus().equals(CentralPayPaymentRequest.CLOSED)
                        && centralPayPaymentRequest.getPaymentStatus().equals(CentralPayPaymentRequest.PAID)) {

                    List<Invoice> invoicesToPay = new ArrayList<Invoice>();
                    List<CustomerOrder> customerOrdersToPay = new ArrayList<CustomerOrder>();
                    for (CustomerOrder customerOrder : customerOrders) {
                        customerOrder = getCustomerOrder(customerOrder.getId());
                        if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BILLED)) {
                            if (customerOrder.getInvoices() != null)
                                for (Invoice invoice : customerOrder.getInvoices())
                                    if (invoice.getInvoiceStatus().getId()
                                            .equals(constantService.getInvoiceStatusSend().getId())) {
                                        invoicesToPay.add(invoice);
                                        break;
                                    }
                        } else {
                            customerOrdersToPay.add(customerOrder);
                        }

                    }

                    if (invoicesToPay != null && invoicesToPay.size() > 0) {
                        paymentService.generatePaymentOnInvoiceForCbPayment(invoicesToPay, centralPayPaymentRequest);
                    } else if (customerOrdersToPay != null && customerOrdersToPay.size() > 0) {
                        paymentService.generateDepositOnCustomerOrderForCbPayment(customerOrdersToPay,
                                centralPayPaymentRequest);

                        for (CustomerOrder orderToUnlock : customerOrders)
                            unlockCustomerOrderFromDeposit(orderToUnlock);
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
            } else if (customerOrder.getCreatedDate().isBefore(LocalDateTime.now().minusDays(5 * 7))) {
                toSend = true;
                customerOrder.setThirdReminderDateTime(LocalDateTime.now());
            }

            if (toSend) {
                mailHelper.sendCustomerOrderDepositMailToCustomer(customerOrder, false, true);
                addOrUpdateCustomerOrder(customerOrder, false, true);
            }
        }
    }

    @Override
    public BigDecimal getRemainingAmountToPayForCustomerOrder(CustomerOrder customerOrder) throws OsirisException {
        customerOrder = getCustomerOrder(customerOrder.getId());

        if (customerOrder != null) {
            BigDecimal total = getInvoicingSummaryForIQuotation(customerOrder).getTotalPrice();
            if (customerOrder.getPayments() != null)
                for (Payment payment : customerOrder.getPayments())
                    if (!payment.getIsCancelled())
                        total = total.subtract(payment.getPaymentAmount());

            return total.multiply(oneHundredValue).setScale(0, RoundingMode.HALF_EVEN).divide(oneHundredValue);
        }
        return zeroValue;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<byte[]> printMailingLabel(List<String> customerOrdersIn, boolean printLabel,
            Integer competentAuthorityId,
            boolean printLetters, boolean printRegisteredLetter)
            throws OsirisException, OsirisClientMessageException {
        ArrayList<CustomerOrder> customerOrders = new ArrayList<CustomerOrder>();

        for (String id : customerOrdersIn) {
            customerOrders.add(getCustomerOrder(Integer.parseInt(id)));
        }

        InvoiceLabelResult invoiceLabelResult = null;
        CompetentAuthority competentAuthority = null;
        if (competentAuthorityId != null) {
            competentAuthority = competentAuthorityService
                    .getCompetentAuthority(competentAuthorityId);
            if (competentAuthority != null)
                invoiceLabelResult = mailComputeHelper.computeCompetentAuthorityLabelResult(competentAuthority);
        }
        if (printLabel)
            for (CustomerOrder customerOrder : customerOrders) {
                try {
                    if (competentAuthority != null)
                        printDelegate.printMailingLabel(invoiceLabelResult, customerOrder);
                    else
                        printDelegate.printMailingLabel(mailComputeHelper.computePaperLabelResult(customerOrder),
                                customerOrder);
                } catch (NumberFormatException e) {
                } catch (Exception e) {
                    throw new OsirisException(e, "Error when printing label");
                }
            }

        if (printRegisteredLetter) {
            for (CustomerOrder customerOrder : customerOrders) {
                try {
                    if (competentAuthority != null)
                        printDelegate.printRegisteredLabel(invoiceLabelResult, customerOrder);
                    else
                        printDelegate.printRegisteredLabel(mailComputeHelper.computePaperLabelResult(customerOrder),
                                customerOrder);
                } catch (NumberFormatException e) {
                } catch (Exception e) {
                    throw new OsirisException(e, "Error when printing label");
                }
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
                        customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.DRAFT),
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
                    newChild = getCustomerOrder(newChild.getId());

                    if (customerOrder.getIsRecurringAutomaticallyBilled() != null
                            && customerOrder.getIsRecurringAutomaticallyBilled())
                        autoBilledProvisions(newChild);
                }
            }
        }
    }

    private CustomerOrder createNewCustomerOrderFromRecurringCustomerOrder(CustomerOrder customerOrderRecurring)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        CustomerOrderStatus statusOpen = customerOrderStatusService
                .getCustomerOrderStatusByCode(CustomerOrderStatus.DRAFT);
        CustomerOrder customerOrder = new CustomerOrder(customerOrderRecurring.getResponsable().getTiers(),
                customerOrderRecurring.getResponsable(),
                /* customerOrderRecurring.getConfrere(), */customerOrderRecurring.getSpecialOffers(),
                LocalDateTime.now(),
                statusOpen, customerOrderRecurring.getDescription(), null,
                customerOrderRecurring.getDocuments(), customerOrderRecurring.getAssoAffaireOrders(), null, false,
                null, null, customerOrderRecurring.getCustomerOrderOrigin());

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
                document = documentService.cloneOrMergeDocument(document, null);
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

                        provision.setInvoiceItems(null);
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

    public List<CustomerOrder> searchOrdersForCurrentUser(List<String> customerOrderStatus,
            List<Integer> responsableIdToFilter,
            Boolean withMissingAttachment, Integer page,
            String sortBy) throws OsirisException {
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
                if (customerOrderStatusFetched != null
                        && !customerOrderStatusFetched.getCode().equals(CustomerOrderStatus.ABANDONED))
                    customerOrderStatusToFilter.add(customerOrderStatusFetched);
            }

            CustomerOrderStatus customerOrderStatusBilled = customerOrderStatusService
                    .getCustomerOrderStatusByCode(CustomerOrderStatus.BILLED);

            Responsable currentUser = employeeService.getCurrentMyJssUser();
            List<Responsable> responsablesToFilter = new ArrayList<Responsable>();

            if (responsableIdToFilter == null || responsableIdToFilter.contains(currentUser.getId()))
                responsablesToFilter.add(currentUser);

            if (Boolean.TRUE.equals(currentUser.getCanViewAllTiersInWeb())
                    && currentUser.getTiers().getResponsables() != null)
                for (Responsable respo : currentUser.getTiers().getResponsables()) {
                    if (responsableIdToFilter == null || responsableIdToFilter.contains(respo.getId()))
                        responsablesToFilter.add(respo);
                }

            if (responsablesToFilter != null
                    && responsablesToFilter.size() > 0) {

                Order order = new Order(Direction.DESC, "createdDate");

                if (sortBy.equals("createdDateAsc"))
                    order = new Order(Direction.ASC, "createdDate");

                if (sortBy.equals("statusAsc"))
                    order = new Order(Direction.ASC, "customerOrderStatus");

                Sort sort = Sort.by(Arrays.asList(order));
                Pageable pageableRequest = PageRequest.of(page, 10, sort);
                return completeAdditionnalInformationForCustomerOrders(
                        customerOrderRepository.searchOrdersForCurrentUser(responsablesToFilter,
                                customerOrderStatusToFilter, pageableRequest, customerOrderStatusBilled, displayPayed,
                                withMissingAttachment, AnnouncementStatus.ANNOUNCEMENT_WAITING_DOCUMENT,
                                FormaliteStatus.FORMALITE_WAITING_DOCUMENT,
                                SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT),
                        false);
            }
        }

        return new ArrayList<>();
    }

    public List<CustomerOrder> searchOrdersForCurrentUserAndAffaire(Affaire affaire) throws OsirisException {
        Responsable currentUser = employeeService.getCurrentMyJssUser();
        List<Responsable> responsablesToFilter = new ArrayList<Responsable>();
        responsablesToFilter.add(currentUser);

        if (currentUser != null && Boolean.TRUE.equals(currentUser.getCanViewAllTiersInWeb())) {
            responsablesToFilter.addAll(tiersService.getTiers(currentUser.getTiers().getId()).getResponsables());
        }

        if (responsablesToFilter != null && responsablesToFilter.size() > 0) {
            return completeAdditionnalInformationForCustomerOrders(customerOrderRepository
                    .searchOrdersForCurrentUserAndAffaire(responsablesToFilter, affaire), false);
        }
        return null;
    }

    @Override
    public CustomerOrder completeAdditionnalInformationForCustomerOrder(CustomerOrder customerOrder,
            Boolean populationAssoAffaireOrderTransientField)
            throws OsirisException {
        return completeAdditionnalInformationForCustomerOrders(Arrays.asList(customerOrder),
                populationAssoAffaireOrderTransientField).get(0);
    }

    @Override
    public List<CustomerOrder> completeAdditionnalInformationForCustomerOrders(List<CustomerOrder> customerOrders,
            Boolean populationAssoAffaireOrderTransientField)
            throws OsirisException {
        if (customerOrders != null && customerOrders.size() > 0) {
            List<Notification> notifications = notificationService.getNotificationsForCurrentEmployee(true, false, null,
                    false, false);

            if (notifications != null)
                notifications = notifications.stream().filter(n -> n.getCustomerOrder() != null).toList();

            // Prepare indexation usage
            List<IndexEntity> indexEntities = searchService.searchForEntitiesByIds(
                    customerOrders.stream().map(CustomerOrder::getId).toList(), CustomerOrder.class.getSimpleName());

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

            for (CustomerOrder customerOrder : customerOrders) {
                if (notifications != null)
                    notifications.stream().filter(n -> n.getCustomerOrder().getId().equals(customerOrder.getId()))
                            .findFirst()
                            .ifPresent(n -> customerOrder.setIsHasNotifications(true));

                if (populationAssoAffaireOrderTransientField)
                    assoAffaireOrderService.populateTransientField(customerOrder.getAssoAffaireOrders());

                if (indexEntities != null) {
                    indexEntities.stream().filter(n -> n.getEntityId().equals(customerOrder.getId())).findFirst()
                            .ifPresent(c -> {
                                CustomerOrderTransient indexOrder = null;
                                try {
                                    indexOrder = objectMapper.readValue(c.getText(), CustomerOrderTransient.class);
                                } catch (Exception e) {
                                }
                                if (indexOrder != null) {
                                    customerOrder.setAffairesList(indexOrder.getAffairesList());
                                    customerOrder.setServicesList(indexOrder.getServicesList());
                                    customerOrder.setHasMissingInformations(indexOrder.getHasMissingInformations());
                                    customerOrder.setIsPriority(indexOrder.getIsPriority());
                                    customerOrder.setComplexity(indexOrder.getComplexity());
                                }
                            });
                }
            }
        }

        return customerOrders;
    }

    @Override
    public CustomerOrder completeAdditionnalInformationForCustomerOrderWhenIndexing(CustomerOrder customerOrder)
            throws OsirisException {
        List<String> affaireLabels = new ArrayList<String>();
        List<String> serviceLabels = new ArrayList<String>();
        customerOrder.setHasMissingInformations(false);
        if (customerOrder.getAssoAffaireOrders() != null) {
            assoAffaireOrderService.populateTransientField(customerOrder.getAssoAffaireOrders());
            for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders()) {
                if (assoAffaireOrder.getAffaire() != null) {
                    String affaireDenomination = assoAffaireOrder.getAffaire().getDenomination();
                    if (affaireDenomination == null || affaireDenomination.length() == 0)
                        affaireDenomination = assoAffaireOrder.getAffaire().getFirstname() + " "
                                + assoAffaireOrder.getAffaire().getLastname();
                    if (affaireLabels.indexOf(affaireDenomination) < 0)
                        affaireLabels.add(affaireDenomination);
                }

                if (assoAffaireOrder.getServices() != null && assoAffaireOrder.getServices().size() > 0)
                    for (Service service : assoAffaireOrder.getServices()) {
                        String serviceLabel = service.getServiceLabelToDisplay();
                        if (serviceLabels.indexOf(serviceLabel) < 0)
                            serviceLabels.add(serviceLabel);
                    }

                if (assoAffaireOrder.getServices() != null)
                    for (Service service : assoAffaireOrder.getServices()) {
                        if (serviceService.isServiceHasMissingInformations(service)) {
                            customerOrder.setHasMissingInformations(true);
                        }
                    }
            }
        }

        if (affaireLabels.size() > 0)
            customerOrder.setAffairesList(String.join(" / ", affaireLabels));
        customerOrder.setServicesList(String.join(" / ", serviceLabels));
        customerOrder.setIsPriority(customerOrderAssignationService.isPriorityOrder(customerOrder));
        customerOrder.setComplexity(getComplexity(customerOrder));
        return customerOrder;
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
    public InvoicingSummary getInvoicingSummaryForIQuotation(IQuotation customerOrder) throws OsirisException {
        InvoicingSummary invoicingSummary = new InvoicingSummary();
        List<InvoiceItem> invoiceItemsToConsider = new ArrayList<InvoiceItem>();

        if (customerOrder instanceof CustomerOrder
                && ((CustomerOrder) customerOrder).getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BILLED)
                && ((CustomerOrder) customerOrder).getInvoices() != null) {
            customerOrder = getCustomerOrder(customerOrder.getId());
            for (Invoice invoice : ((CustomerOrder) customerOrder).getInvoices())
                if (invoice.getResponsable() != null
                        && (invoice.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusSend().getId())
                                || invoice.getInvoiceStatus().getId()
                                        .equals(constantService.getInvoiceStatusPayed().getId()))) {
                    invoiceItemsToConsider = invoice.getInvoiceItems();
                    invoicingSummary.setBillingLabelType(invoice.getBillingLabelType());
                }
        } else {
            if (customerOrder.getIsQuotation())
                customerOrder = quotationService.getQuotation(customerOrder.getId());
            else
                customerOrder = getCustomerOrder(customerOrder.getId());
            if (customerOrder != null) {
                if (customerOrder.getAssoAffaireOrders() != null)
                    for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders())
                        for (Service service : assoAffaireOrder.getServices())
                            for (Provision provision : service.getProvisions())
                                if (provision.getInvoiceItems() != null && provision.getInvoiceItems().size() > 0)
                                    invoiceItemsToConsider.addAll(provision.getInvoiceItems());
                Document billingDocument = documentService.getBillingDocument(customerOrder.getDocuments());
                if (billingDocument != null)
                    invoicingSummary.setBillingLabelType(billingDocument.getBillingLabelType());
            }
        }

        BigDecimal totalPrice = new BigDecimal(0);
        BigDecimal discountTotal = new BigDecimal(0);
        BigDecimal preTaxPriceTotal = new BigDecimal(0);
        BigDecimal vatTotal = new BigDecimal(0);
        if (invoiceItemsToConsider != null)
            for (InvoiceItem invoiceItem : invoiceItemsToConsider) {
                if (invoiceItem.getPreTaxPriceReinvoiced() != null) {
                    preTaxPriceTotal = preTaxPriceTotal
                            .add(invoiceItem.getPreTaxPriceReinvoiced());
                } else if (invoiceItem.getPreTaxPrice() != null) {
                    preTaxPriceTotal = preTaxPriceTotal.add(invoiceItem.getPreTaxPrice());
                }

                if (invoiceItem.getVatPrice() != null) {
                    vatTotal = vatTotal.add(invoiceItem.getVatPrice());
                }
                if (invoiceItem.getDiscountAmount() != null) {
                    discountTotal = discountTotal.add(invoiceItem.getDiscountAmount());
                }
            }
        totalPrice = preTaxPriceTotal.add(vatTotal).subtract(discountTotal);

        invoicingSummary.setDiscountTotal(discountTotal);
        invoicingSummary.setPreTaxPriceTotal(preTaxPriceTotal);
        invoicingSummary.setVatTotal(vatTotal);
        invoicingSummary.setTotalPrice(totalPrice);
        invoicingSummary.setRemainingToPay(totalPrice);

        if (customerOrder instanceof CustomerOrder) {
            List<Payment> payments = getApplicablePaymentsForCustomerOrder((CustomerOrder) customerOrder);
            if (payments != null && payments.size() > 0)
                for (Payment payment : payments)
                    invoicingSummary
                            .setRemainingToPay(
                                    invoicingSummary.getRemainingToPay().subtract(payment.getPaymentAmount()));
        }

        return invoicingSummary;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CustomerOrderComment> getCustomerOrderCommentsForCustomer(CustomerOrder customerOrder) {
        customerOrder = getCustomerOrder(customerOrder.getId());
        List<CustomerOrderComment> customerOrderComments = new ArrayList<CustomerOrderComment>();
        if (customerOrder.getCustomerOrderComments() != null)
            for (CustomerOrderComment customerOrderComment : customerOrder.getCustomerOrderComments())
                if (customerOrderComment.getIsToDisplayToCustomer() != null
                        && customerOrderComment.getIsToDisplayToCustomer())
                    customerOrderComments.add(customerOrderComment);

        if (customerOrderComments.size() > 0)
            customerOrderComments.sort(new Comparator<CustomerOrderComment>() {
                @Override
                public int compare(CustomerOrderComment c0, CustomerOrderComment c1) {
                    if (c0 == null && c1 == null)
                        return 0;
                    if (c0 != null && c1 == null)
                        return 1;
                    if (c0 == null && c1 != null)
                        return -1;
                    if (c1 != null && c0 != null)
                        return c0.getCreatedDateTime().compareTo(c1.getCreatedDateTime());
                    return 0;
                }
            });
        return customerOrderComments;
    }

    @Override
    public List<CustomerOrder> searchOrders(List<CustomerOrderStatus> customerOrderStatus,
            Boolean withMissingAttachment,
            List<Responsable> responsables) {
        if (customerOrderStatus != null && customerOrderStatus.size() > 0 && customerOrderStatus.size() > 0
                && responsables != null && responsables.size() > 0) {
            return customerOrderRepository.searchOrders(responsables, customerOrderStatus,
                    withMissingAttachment, AnnouncementStatus.ANNOUNCEMENT_WAITING_DOCUMENT,
                    FormaliteStatus.FORMALITE_WAITING_DOCUMENT,
                    SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT);
        }
        return null;
    }

    @Override
    public List<CustomerOrder> findCustomerOrderByResponsable(Responsable responsable) {
        return customerOrderRepository.findByResponsable(responsable);
    }

    @Override
    public List<CustomerOrder> getCustomerOrderByResponsableAndStatusAndDates(Responsable responsable,
            CustomerOrderStatus customerOrderStatus, Boolean isReccuring,
            LocalDateTime startOfDay, LocalDateTime endOfDay) {
        return customerOrderRepository.findByResponsableAndStatusAndCreatedDateBetween(responsable,
                startOfDay, endOfDay, isReccuring, customerOrderStatus);
    }

    @Override
    public List<CustomerOrder> getOrdersByResponsablesAndDates(List<Responsable> responsables,
            LocalDateTime startOfDay, LocalDateTime endOfDay) {
        return customerOrderRepository.findByResponsableInAndCreatedDateBetween(responsables, startOfDay, endOfDay);
    }

    @Override
    public List<CustomerOrder> searchCustomerOrders(List<Employee> commercials,
            List<CustomerOrderStatus> status, List<Employee> invoicingEmployees, List<Employee> orderingEmployees)
            throws OsirisException {

        List<Integer> commercialIds = (commercials != null && commercials.size() > 0)
                ? commercials.stream().map(Employee::getId).collect(Collectors.toList())
                : Arrays.asList(0);

        List<Integer> statusIds = (status != null && status.size() > 0)
                ? status.stream().map(CustomerOrderStatus::getId).collect(Collectors.toList())
                : Arrays.asList(0);

        List<Integer> invoicingEmployeesIds = new ArrayList<Integer>();
        List<Integer> orderingEmployeesIds = new ArrayList<Integer>();
        if (invoicingEmployees == null) {
            invoicingEmployeesIds.add(0);
        } else {
            statusIds = Arrays
                    .asList(customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.TO_BILLED)
                            .getId());
            if (invoicingEmployees.size() == 0) {
                invoicingEmployeesIds.add(0);
                invoicingEmployeesIds.add(1);
            } else {
                invoicingEmployeesIds = invoicingEmployees.stream().map(Employee::getId).collect(Collectors.toList());
            }
        }

        if (orderingEmployees == null) {
            orderingEmployeesIds.add(0);
        } else {
            statusIds = Arrays
                    .asList(customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.DRAFT)
                            .getId());
            if (orderingEmployees.size() == 0) {
                orderingEmployeesIds.add(0);
                orderingEmployeesIds.add(1);
            } else {
                orderingEmployeesIds = orderingEmployees.stream().map(Employee::getId).collect(Collectors.toList());
            }
        }

        List<CustomerOrder> foundOrders = customerOrderRepository.searchCustomerOrders(commercialIds, statusIds,
                invoicingEmployeesIds,
                orderingEmployeesIds);

        if (foundOrders != null && orderingEmployees != null) {
            CustomerOrderOrigin origin = constantService.getCustomerOrderOriginOsiris();
            foundOrders = foundOrders.stream().filter(c -> c.getCustomerOrderOrigin().getId().equals(origin.getId()))
                    .toList();
        }

        return completeAdditionnalInformationForCustomerOrders(foundOrders, false);
    }

    @Override
    public Boolean setEmergencyOnOrder(CustomerOrder customerOrder, Boolean isEnabled)
            throws OsirisClientMessageException, OsirisValidationException, OsirisException {
        if (customerOrder.getAssoAffaireOrders() != null && customerOrder.getAssoAffaireOrders().size() > 0
                && customerOrder.getAssoAffaireOrders().get(0).getServices() != null
                && customerOrder.getAssoAffaireOrders().get(0).getServices().size() > 0
                && customerOrder.getAssoAffaireOrders().get(0).getServices().get(0).getProvisions() != null
                && customerOrder.getAssoAffaireOrders().get(0).getServices().get(0).getProvisions().size() > 0) {
            customerOrder.getAssoAffaireOrders().get(0).getServices().get(0).getProvisions().get(0)
                    .setIsEmergency(isEnabled);
            provisionService.addOrUpdateProvision(
                    customerOrder.getAssoAffaireOrders().get(0).getServices().get(0).getProvisions().get(0));
            customerOrder = getCustomerOrder(customerOrder.getId());
            pricingHelper.getAndSetInvoiceItemsForQuotation(customerOrder, true);
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public void assignInvoicingEmployee(CustomerOrder customerOrder, Employee employee)
            throws OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException, OsirisException {
        customerOrder.setInvoicingEmployee(employee);
        simpleAddOrUpdate(customerOrder);
    }

    @Transactional(rollbackFor = Exception.class)
    public void assignOrderingEmployee(CustomerOrder customerOrder, Employee employee)
            throws OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException, OsirisException {
        customerOrder.setOrderingEmployee(employee);
        simpleAddOrUpdate(customerOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean setDocumentOnOrder(CustomerOrder customerOrder, Document document)
            throws OsirisClientMessageException, OsirisValidationException, OsirisException {
        if (customerOrder.getDocuments() == null)
            customerOrder.setDocuments(new ArrayList<Document>());

        document.setCustomerOrder(customerOrder);
        documentService.addOrUpdateDocument(document);

        customerOrder = getCustomerOrder(customerOrder.getId());
        return true;
    }

    public void modifyInvoicingBlockage(CustomerOrder customerOrder, InvoicingBlockage invoicingBlockage)
            throws OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException, OsirisException {
        customerOrder.setInvoicingBlockage(invoicingBlockage);
        simpleAddOrUpdate(customerOrder);
    }

    public void modifyOrderingBlockage(CustomerOrder customerOrder, OrderBlockage orderingBlockage)
            throws OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException, OsirisException {
        customerOrder.setOrderBlockage(orderingBlockage);
        simpleAddOrUpdate(customerOrder);
    }

    @Override
    public InvoicingStatistics getInvoicingStatistics() throws OsirisException {
        List<CustomerOrder> customerOrders = searchCustomerOrders(null, Arrays.asList(
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.TO_BILLED)), null, null);
        InvoicingStatistics stats = new InvoicingStatistics();
        stats.setOrderAssigned(0);
        stats.setOrderBlocked(0);
        stats.setOrderToInvoiced(0);

        if (customerOrders != null && customerOrders.size() > 0) {
            stats.setOrderToInvoiced(customerOrders.size());
            stats.setOrderAssigned(
                    Math.toIntExact(customerOrders.stream().filter(c -> c.getInvoicingEmployee() != null).count()));
            stats.setOrderBlocked(
                    Math.toIntExact(customerOrders.stream().filter(c -> c.getInvoicingBlockage() != null).count()));
        }
        return stats;
    }

    @Override
    public ToOrderStatistics getToOrderStatistics() throws OsirisException {
        List<CustomerOrder> customerOrders = searchCustomerOrders(null, Arrays.asList(
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.DRAFT)), null, null);
        ToOrderStatistics stats = new ToOrderStatistics();
        stats.setOrderAssigned(0);
        stats.setOrderBlocked(0);
        stats.setOrderToOrder(0);
        CustomerOrderOrigin originOsiris = constantService.getCustomerOrderOriginOsiris();

        if (customerOrders != null && customerOrders.size() > 0) {
            customerOrders = customerOrders.stream().filter(c -> c.getCustomerOrderOrigin().getId()
                    .equals(originOsiris.getId())).toList();
            stats.setOrderToOrder(customerOrders.size());
            stats.setOrderAssigned(
                    Math.toIntExact(customerOrders.stream().filter(c -> c.getInvoicingEmployee() != null).count()));
            stats.setOrderBlocked(
                    Math.toIntExact(customerOrders.stream().filter(c -> c.getInvoicingBlockage() != null).count()));
        }
        return stats;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerOrder assignNewCustomerOrderToBilled() throws OsirisException {
        Order orderSort = new Order(Direction.ASC, "lastStatusUpdate");
        Sort sort = Sort.by(Arrays.asList(orderSort));
        Pageable pageableRequest = PageRequest.of(0, 100000, sort);
        List<CustomerOrder> customerOrders = customerOrderRepository.searchCustomerOrdersWithInvoiceToFill(
                constantService.getAttachmentTypeProviderInvoice(),
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.BEING_PROCESSED),
                pageableRequest);

        Employee currentUser = employeeService.getCurrentEmployee();
        CustomerOrder assignedOrder = null;

        if (customerOrders != null && customerOrders.size() > 0 && Math.random() < 0.3) {
            for (CustomerOrder order : customerOrders) {
                if (order.getInvoicingEmployee() == null
                        || order.getInvoicingEmployee().getId().equals(currentUser.getId())) {
                    assignedOrder = order;
                    break;
                }
            }
        }

        if (assignedOrder == null) {
            customerOrders = customerOrderRepository.findNewCustomerOrderToBilled(
                    customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.TO_BILLED),
                    pageableRequest);

            if (customerOrders != null && customerOrders.size() > 0)
                assignedOrder = customerOrders.get(0);
        }

        if (currentUser != null && assignedOrder != null) {
            assignedOrder.setInvoicingEmployee(currentUser);
            return simpleAddOrUpdate(assignedOrder);
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerOrder assignNewCustomerOrderToOrder() throws OsirisException {
        Order order = new Order(Direction.ASC, "createdDate");
        Sort sort = Sort.by(Arrays.asList(order));
        Pageable pageableRequest = PageRequest.of(0, 1, sort);
        List<CustomerOrder> customerOrders = customerOrderRepository.findNewCustomerOrderToOrder(
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.DRAFT),
                constantService.getCustomerOrderOriginOsiris(),
                pageableRequest);

        if (customerOrders != null && customerOrders.size() > 0 && employeeService.getCurrentEmployee() != null) {
            customerOrders.get(0).setOrderingEmployee(employeeService.getCurrentEmployee());
            return simpleAddOrUpdate(customerOrders.get(0));
        } else {
            return null;
        }
    }

    @Override
    public CustomerOrder getCustomerOrderForSubscription(String subscriptionType,
            Boolean isPriceReductionForSubscription, Integer idArticle) throws OsirisException {

        ServiceType serviceTypeSubscription = null;

        CustomerOrder customerOrder = new CustomerOrder();
        boolean isRecurring = false;
        AssoProvisionPostNewspaper assoProvisionPostNewspaper = new AssoProvisionPostNewspaper();
        customerOrder.setServiceFamilyGroup(constantService.getServiceFamilyGroupOther());

        switch (subscriptionType) {
            case Subscription.ANNUAL_SUBSCRIPTION:
                serviceTypeSubscription = constantService.getServiceTypeAnnualSubscription();
                customerOrder.setRecurringPeriodStartDate(LocalDate.now());
                customerOrder.setRecurringPeriodEndDate(LocalDate.now().plusYears(1));
                customerOrder.setCustomerOrderFrequency(constantService.getCustomerOrderFrequencyAnnual());
                isRecurring = true;
                break;

            case Subscription.ENTERPRISE_ANNUAL_SUBSCRIPTION:
                serviceTypeSubscription = constantService.getServiceTypeEnterpriseAnnualSubscription();
                customerOrder.setRecurringPeriodStartDate(LocalDate.now());
                customerOrder.setRecurringPeriodEndDate(LocalDate.now().plusYears(1));
                customerOrder.setCustomerOrderFrequency(constantService.getCustomerOrderFrequencyAnnual());
                isRecurring = true;
                break;

            case Subscription.MONTHLY_SUBSCRIPTION:
                serviceTypeSubscription = constantService.getServiceTypeMonthlySubscription();
                customerOrder.setRecurringPeriodStartDate(LocalDate.now());
                customerOrder.setRecurringPeriodEndDate(LocalDate.now().plusMonths(1));
                customerOrder.setCustomerOrderFrequency(constantService.getCustomerOrderFrequencyMonthly());
                isRecurring = true;
                break;

            case Subscription.ONE_POST_SUBSCRIPTION:
                serviceTypeSubscription = constantService.getServiceTypeUniqueArticleBuy();
                assoProvisionPostNewspaper.setPost(postService.getPost(idArticle));
                break;

            case Subscription.NEWSPAPER_KIOSK_BUY:
                serviceTypeSubscription = constantService.getServiceTypeKioskNewspaperBuy();
                assoProvisionPostNewspaper.setNewspaper(newspaperService.getNewspaper(idArticle));
                break;

            default:
                return customerOrder;
        }

        if (isRecurring) {
            customerOrder.setIsRecurring(true);
            customerOrder.setRecurringStartDate(LocalDate.now());
            customerOrder.setRecurringEndDate(LocalDate.now().plusYears(200));
            customerOrder.setIsRecurringAutomaticallyBilled(true);
        }

        customerOrder.setResponsable(constantService.getResponsableDummyCustomerFrance());

        List<ServiceType> subscriptionServiceTypeList = new ArrayList<ServiceType>();
        subscriptionServiceTypeList.add(serviceTypeSubscription);
        List<Service> servicesSubscription = serviceService
                .generateServiceInstanceFromMultiServiceTypes(subscriptionServiceTypeList, null, null);

        if (servicesSubscription == null || servicesSubscription.size() == 0)
            return null;

        Service serviceSubscription = servicesSubscription.get(0);

        if (subscriptionType.equals(Subscription.NEWSPAPER_KIOSK_BUY)
                || subscriptionType.equals(Subscription.ONE_POST_SUBSCRIPTION)) {
            assoProvisionPostNewspaper.setProvision(serviceSubscription.getProvisions().get(0));
            ArrayList<AssoProvisionPostNewspaper> assoProvisionPostList = new ArrayList<AssoProvisionPostNewspaper>();
            assoProvisionPostList.add(assoProvisionPostNewspaper);
            serviceSubscription.getProvisions().get(0)
                    .setAssoProvisionPostNewspapers(assoProvisionPostList);
        }

        AssoAffaireOrder assoAffaireOrder = new AssoAffaireOrder();
        assoAffaireOrder.setCustomerOrder(customerOrder);

        if (employeeService.getCurrentMyJssUser() != null) {
            assoAffaireOrder
                    .setAffaire(affaireService.getAffaireFromResponsable(employeeService.getCurrentMyJssUser()));
        } else
            assoAffaireOrder.setAffaire(constantService.getAffaireDummyForSubscription());

        ArrayList<AssoAffaireOrder> assoList = new ArrayList<AssoAffaireOrder>();
        assoList.add(assoAffaireOrder);
        customerOrder.setAssoAffaireOrders(assoList);

        ArrayList<Service> serviceList = new ArrayList<Service>();
        serviceList.add(serviceSubscription);
        assoAffaireOrder.setServices(serviceList);

        List<SpecialOffer> specialOffers = new ArrayList<>();
        if (isPriceReductionForSubscription) {
            specialOffers.add(constantService.getSpecialOfferJssSubscriptionReduction());
        }
        customerOrder.setSpecialOffers(specialOffers);
        pricingHelper.getAndSetInvoiceItemsForQuotation(customerOrder, false);

        customerOrder.setResponsable(null);
        customerOrder.setIsQuotation(false);

        if (employeeService.getCurrentMyJssUser() != null)
            return myJssQuotationDelegate.saveCustomerOrderFromMyJss(customerOrder, false, null, null);
        else
            return customerOrder;
    }

    @Override
    public void autoBilledProvisions(CustomerOrder customerOrder)
            throws OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException, OsirisException {
        if (customerOrder.getCustomerOrderStatus() != null && customerOrder.getCustomerOrderStatus().getCode()
                .equals(CustomerOrderStatus.BEING_PROCESSED)
                && isOnlyJssAnnouncementOrSubscription(customerOrder, true))
            if (customerOrder.getAssoAffaireOrders() != null) {
                for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders()) {
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
                                    if (provision.getAnnouncement() != null) {
                                        provision.getAnnouncement()
                                                .setAnnouncementStatus(announcementStatusService
                                                        .getAnnouncementStatusByCode(
                                                                AnnouncementStatus.ANNOUNCEMENT_DONE));
                                        provisionService.addOrUpdateProvision(provision);
                                        announcementService.generateAndStorePublicationReceipt(
                                                provision.getAnnouncement(),
                                                provision);
                                    }
                                }
                            }
                        }
                        customerOrder = getCustomerOrder(customerOrder.getId());
                        try {
                            addOrUpdateCustomerOrder(customerOrder, false, true); // Put to billed if all
                                                                                  // ended
                        } catch (Exception e) {
                            if (!(e instanceof OsirisClientMessageException
                                    || e instanceof OsirisValidationException))
                                throw e;
                        }
                    }
                }
            }
    }

    @Override
    public List<CustomerOrder> getCustomerOrdersByVoucherAndResponsable(Voucher voucher, Responsable responsable) {
        List<CustomerOrder> voucheredOrders = new ArrayList<>();
        CustomerOrderStatus statusAbandonned = customerOrderStatusService
                .getCustomerOrderStatusByCode(CustomerOrderStatus.ABANDONED);
        voucheredOrders = customerOrderRepository.findByVoucherAndResponsable(voucher, responsable, statusAbandonned);
        return voucheredOrders;
    }

    @Transactional(rollbackFor = Exception.class)
    public void purgeCustomerOrders() throws OsirisException {
        List<CustomerOrder> orders = customerOrderRepository.findCustomerOrderOlderThanDate(
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.DRAFT),
                LocalDateTime.now().minusMonths(3));

        if (orders != null)
            for (CustomerOrder order : orders)
                batchService.declareNewBatch(Batch.PURGE_CUSTOMER_ORDER, order.getId());
    }

    public List<CustomerOrder> findCustomerOrderByFormalisteAssigned(List<Employee> employees,
            CustomerOrderStatus customerOrderStatus, Employee assignedUser, AssignationType assignationType) {
        return customerOrderRepository.findCustomerOrderByFormalisteAndStatusAssigned(employees, customerOrderStatus,
                assignedUser, assignationType);
    }

    @Override
    public List<CustomerOrder> findCustomerOrderByPubliscisteAssigned(List<Employee> employees,
            CustomerOrderStatus customerOrderStatus, Employee assignedUser, AssignationType assignationType) {
        return customerOrderRepository.findCustomerOrderByPubliscisteAndStatusAssigned(employees, customerOrderStatus,
                assignedUser, assignationType);
    }

    @Override
    public List<CustomerOrder> findCustomerOrderByForcedEmployeeAssigned(CustomerOrderStatus customerOrderStatus,
            Employee assignedUser) {
        return customerOrderRepository.findCustomerOrderByForcedEmployeeAndStatusAssigned(customerOrderStatus,
                assignedUser);
    }

    @Override
    @Transactional
    public void switchResponsable(CustomerOrder order, Responsable responsable) {
        order = getCustomerOrder(order.getId());
        List<Responsable> userScope = userScopeService.getPotentialUserScope();

        if (userScope != null)
            for (Responsable scope : userScope)
                if (scope.getId().equals(responsable.getId())) {
                    order.setResponsable(responsable);
                    simpleAddOrUpdate(order);
                }
    }

    @Override
    public Integer getComplexity(CustomerOrder customerOrder) throws OsirisException {
        Integer complexity = 4;
        if (customerOrder.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders())
                if (assoAffaireOrder.getServices() != null)
                    for (Service service : assoAffaireOrder.getServices())
                        if (service.getProvisions() != null)
                            for (Provision provision : service.getProvisions())
                                if (provision.getComplexity() != null && provision.getComplexity() < complexity)
                                    complexity = provision.getComplexity();
        return complexity;
    }
}