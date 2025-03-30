package com.jss.osiris.modules.osiris.quotation.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.GeneratePdfDelegate;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.modules.myjss.profile.service.UserScopeService;
import com.jss.osiris.modules.osiris.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.osiris.invoicing.service.PaymentService;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.CustomerOrderOrigin;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.CustomerOrderOriginService;
import com.jss.osiris.modules.osiris.miscellaneous.service.MailService;
import com.jss.osiris.modules.osiris.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.osiris.miscellaneous.service.PhoneService;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.Announcement;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.QuotationSearch;
import com.jss.osiris.modules.osiris.quotation.model.QuotationSearchResult;
import com.jss.osiris.modules.osiris.quotation.model.QuotationStatus;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.model.centralPay.CentralPayPaymentRequest;
import com.jss.osiris.modules.osiris.quotation.repository.QuotationRepository;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;

import jakarta.servlet.http.HttpServletRequest;

@org.springframework.stereotype.Service
public class QuotationServiceImpl implements QuotationService {

    @Autowired
    QuotationRepository quotationRepository;

    @Autowired
    PhoneService phoneService;

    @Autowired
    MailService mailService;

    @Autowired
    QuotationStatusService quotationStatusService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    MailHelper mailHelper;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    AssoAffaireOrderService assoAffaireOrderService;

    @Autowired
    CentralPayDelegateService centralPayDelegateService;

    @Autowired
    PaymentService paymentService;

    @Autowired
    AccountingRecordService accountingRecordService;

    @Autowired
    PricingHelper pricingHelper;

    @Autowired
    CentralPayPaymentRequestService centralPayPaymentRequestService;

    @Autowired
    ConstantService constantService;

    @Autowired
    ActiveDirectoryHelper activeDirectoryHelper;

    @Autowired
    CustomerOrderOriginService customerOrderOriginService;

    @Autowired
    BatchService batchService;

    @Autowired
    GeneratePdfDelegate generatePdfDelegate;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    UserScopeService userScopeService;

    @Override
    public Quotation getQuotation(Integer id) {
        Optional<Quotation> quotation = quotationRepository.findById(id);
        if (quotation.isPresent())
            return quotation.get();
        return null;
    }

    @Override
    public Quotation getQuotationForAnnouncement(Announcement announcement) {
        Optional<Quotation> quotation = quotationRepository
                .findQuotationForAnnouncement(announcement.getId());
        if (quotation.isPresent())
            return quotation.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Quotation addOrUpdateQuotationFromUser(Quotation quotation)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        return addOrUpdateQuotation(quotation);
    }

    @Override
    public Quotation addOrUpdateQuotation(Quotation quotation)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        quotation.setIsQuotation(true);

        if (quotation.getDocuments() != null)
            for (Document document : quotation.getDocuments()) {
                document.setQuotation(quotation);
                mailService.populateMailIds(document.getMailsAffaire());
                mailService.populateMailIds(document.getMailsClient());
            }

        // Set default customer order assignation to sales employee if not set
        if (quotation.getAssignedTo() == null && quotation.getResponsable() != null)
            quotation.setAssignedTo(quotation.getResponsable().getDefaultCustomerOrderEmployee());

        // Complete provisions
        boolean oneNewProvision = false;
        boolean computePrice = false;
        if (quotation.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder assoAffaireOrder : quotation.getAssoAffaireOrders()) {
                assoAffaireOrder.setQuotation(quotation);
                if (assoAffaireOrder.getServices() != null && assoAffaireOrder.getServices().size() > 0) {
                    assoAffaireOrderService.completeAssoAffaireOrder(assoAffaireOrder, quotation, true);
                    for (Service service : assoAffaireOrder.getServices())
                        if (service.getProvisions() != null && service.getProvisions().size() > 0) {
                            computePrice = true;
                            for (Provision provision : service.getProvisions())
                                if (provision.getId() == null)
                                    oneNewProvision = true;
                        }
                }
            }

        boolean isNewQuotation = quotation.getId() == null;
        if (isNewQuotation) {
            quotation.setCreatedDate(LocalDateTime.now());
            quotation.setValidationToken(UUID.randomUUID().toString());
            quotation = quotationRepository.save(quotation);
        }

        if (oneNewProvision)
            quotation = quotationRepository.save(quotation);

        if (computePrice) {
            pricingHelper.getAndSetInvoiceItemsForQuotation(quotation, true);
            quotation = quotationRepository.save(quotation);
        }

        quotation = getQuotation(quotation.getId());

        batchService.declareNewBatch(Batch.REINDEX_QUOTATION, quotation.getId());

        if (isNewQuotation) {
            notificationService.notifyNewQuotation(quotation);

            List<CustomerOrderOrigin> origins = customerOrderOriginService
                    .getByUsername(activeDirectoryHelper.getCurrentUsername());
            if (origins != null && origins.size() == 1)
                quotation.setCustomerOrderOrigin(origins.get(0));
            else
                quotation.setCustomerOrderOrigin(constantService.getCustomerOrderOriginOsiris());

            /*
             * if (quotation.getCustomerOrderOrigin().getId()
             * .equals(constantService.getCustomerOrderOriginWebSite().getId()))
             * mailHelper.sendQuotationCreationConfirmationToCustomer(quotation);
             */
        }
        return quotation;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Quotation addOrUpdateQuotationStatus(Quotation quotation, String targetStatusCode)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        quotation = getQuotation(quotation.getId());
        QuotationStatus targetQuotationStatus = quotationStatusService.getQuotationStatusByCode(targetStatusCode);
        if (targetQuotationStatus == null)
            throw new OsirisException(null, "Quotation status not found for code " + targetStatusCode);

        // Target TO VERIFY from OPEN : notify users
        if (quotation.getQuotationStatus().getCode().equals(QuotationStatus.OPEN)
                && targetQuotationStatus.getCode().equals(QuotationStatus.TO_VERIFY))
            notificationService.notifyQuotationToVerify(quotation);

        // Target SENT TO CUSTOMER : notify users and customer
        if (targetQuotationStatus.getCode().equals(QuotationStatus.SENT_TO_CUSTOMER)) {
            // save to recompute invoice item before sent it to customer
            quotation = this.addOrUpdateQuotation(quotation);

            generateQuotationPdf(quotation);

            mailHelper.sendQuotationToCustomer(quotation, false);
            notificationService.notifyQuotationSent(quotation);
        }

        // Target VALIDATED from SENT : generate Customer Order and Publication receipt
        if (quotation.getQuotationStatus().getCode().equals(QuotationStatus.SENT_TO_CUSTOMER)
                && targetQuotationStatus.getCode().equals(QuotationStatus.VALIDATED_BY_CUSTOMER)) {
            CustomerOrder customerOrder = customerOrderService.createNewCustomerOrderFromQuotation(quotation);
            if (customerOrder == null)
                throw new OsirisException(null,
                        "Erreur when createing customer order from quotation " + quotation.getId());
            if (quotation.getCustomerOrders() == null)
                quotation.setCustomerOrders(new ArrayList<CustomerOrder>());
            quotation.getCustomerOrders().add(customerOrder);
            customerOrder.setQuotations(new ArrayList<Quotation>());
            customerOrder.getQuotations().add(quotation);
            mailHelper.sendCustomerOrderCreationConfirmationOnQuotationValidation(quotation, customerOrder);
        }

        // Target REFUSED from SENT TO CUSTOMER : notify user
        if (quotation.getQuotationStatus().getCode().equals(QuotationStatus.SENT_TO_CUSTOMER)
                && targetQuotationStatus.getCode().equals(QuotationStatus.REFUSED_BY_CUSTOMER))
            notificationService.notifyQuotationRefusedByCustomer(quotation);

        quotation.setLastStatusUpdate(LocalDateTime.now());
        quotation.setQuotationStatus(targetQuotationStatus);
        return this.addOrUpdateQuotation(quotation);
    }

    public Quotation generateQuotationPdf(Quotation quotation) throws OsirisClientMessageException,
            OsirisValidationException, OsirisDuplicateException, OsirisException {
        File quotationPdf = generatePdfDelegate.generateQuotationPdf(quotation);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmm");
        try {
            List<Attachment> attachments = new ArrayList<Attachment>();
            if (quotation != null)
                attachments = attachmentService.addAttachment(new FileInputStream(quotationPdf),
                        quotation.getId(), null,
                        Quotation.class.getSimpleName(),
                        constantService.getAttachmentTypeQuotation(),
                        "Quotation_" + quotation.getId() + "_" + formatter.format(LocalDateTime.now()) + ".pdf",
                        false, "Devis n°" + quotation.getId(), null, null, null);

            for (Attachment attachment : attachments)
                if (quotation != null && attachment.getDescription().contains(quotation.getId() + "")) {
                    attachment.setQuotation(quotation);
                    attachmentService.addOrUpdateAttachment(attachment);
                }
        } catch (FileNotFoundException e) {
            throw new OsirisException(e, "Impossible to read quotation PDF temp file");
        } finally {
            quotationPdf.delete();
        }
        return quotation;
    }

    @Override
    public List<QuotationSearchResult> searchQuotations(QuotationSearch quotationSearch) {
        ArrayList<Integer> statusId = new ArrayList<Integer>();
        if (quotationSearch.getQuotationStatus() != null && quotationSearch.getQuotationStatus().size() > 0) {
            for (QuotationStatus quotationStatus : quotationSearch.getQuotationStatus())
                if (quotationStatus != null)
                    statusId.add(quotationStatus.getId());
        } else {
            statusId.add(0);
        }

        ArrayList<Integer> salesEmployeeId = new ArrayList<Integer>();
        if (quotationSearch.getSalesEmployee() != null) {
            for (Employee employee : employeeService.getMyHolidaymaker(quotationSearch.getSalesEmployee()))
                salesEmployeeId.add(employee.getId());
        } else {
            salesEmployeeId.add(0);
        }

        ArrayList<Integer> assignedToEmployeeId = new ArrayList<Integer>();
        if (quotationSearch.getAssignedToEmployee() != null) {
            for (Employee employee : employeeService.getMyHolidaymaker(quotationSearch.getAssignedToEmployee()))
                assignedToEmployeeId.add(employee.getId());
        } else {
            assignedToEmployeeId.add(0);
        }

        ArrayList<Integer> customerOrderId = new ArrayList<Integer>();
        if (quotationSearch.getCustomerOrders() != null && quotationSearch.getCustomerOrders().size() > 0) {
            for (Tiers tiers : quotationSearch.getCustomerOrders())
                customerOrderId.add(tiers.getId());
        } else {
            customerOrderId.add(0);
        }

        ArrayList<Integer> affaireId = new ArrayList<Integer>();
        if (quotationSearch.getAffaires() != null && quotationSearch.getAffaires().size() > 0) {
            for (Affaire affaire : quotationSearch.getAffaires())
                affaireId.add(affaire.getId());
        } else {
            affaireId.add(0);
        }

        if (quotationSearch.getStartDate() == null)
            quotationSearch.setStartDate(LocalDateTime.now().minusYears(100));

        if (quotationSearch.getEndDate() == null)
            quotationSearch.setEndDate(LocalDateTime.now().plusYears(100));

        return quotationRepository.findQuotations(
                salesEmployeeId, assignedToEmployeeId,
                statusId,
                quotationSearch.getStartDate().withHour(0).withMinute(0),
                quotationSearch.getEndDate().withHour(23).withMinute(59), customerOrderId, affaireId, 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reindexQuotation() throws OsirisException {
        List<Quotation> quotations = IterableUtils.toList(quotationRepository.findAll());
        if (quotations != null)
            for (Quotation quotation : quotations)
                batchService.declareNewBatch(Batch.REINDEX_QUOTATION, quotation.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String getCardPaymentLinkForQuotationDeposit(Quotation quotation, String mail, String subject)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {

        if (quotation.getQuotationStatus().getCode().equals(QuotationStatus.ABANDONED))
            throw new OsirisException(null, "Impossible to pay an cancelled quotation n°" + quotation.getId());

        if (!quotation.getQuotationStatus().getCode().equals(QuotationStatus.SENT_TO_CUSTOMER))
            throw new OsirisException(null, "Wrong status to pay for quotation n°" + quotation.getId());

        if (quotation.getQuotationStatus().getCode().equals(QuotationStatus.VALIDATED_BY_CUSTOMER))
            if (quotation.getCustomerOrders() != null && quotation.getCustomerOrders().size() > 0)
                return customerOrderService
                        .getCardPaymentLinkForCustomerOrderDeposit(quotation.getCustomerOrders().get(0), mail, subject);
            else
                return "ok";

        com.jss.osiris.modules.osiris.quotation.model.CentralPayPaymentRequest request = centralPayPaymentRequestService
                .getCentralPayPaymentRequestByQuotation(quotation);
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

        BigDecimal remainingToPay = computeQuotationTotalPrice(quotation);

        if (remainingToPay.compareTo(BigDecimal.ZERO) > 0) {
            CentralPayPaymentRequest paymentRequest = centralPayDelegateService.generatePayPaymentRequest(
                    remainingToPay, mail,
                    quotation.getId() + "", subject);

            centralPayPaymentRequestService.declareNewCentralPayPaymentRequest(paymentRequest.getPaymentRequestId(),
                    null, quotation);
            return paymentRequest.getBreakdowns().get(0).getEndpoint();
        }
        return "ok";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean validateCardPaymentLinkForQuotationDeposit(Quotation quotation,
            com.jss.osiris.modules.osiris.quotation.model.CentralPayPaymentRequest request)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        quotation = getQuotation(quotation.getId());
        if (request != null) {
            CentralPayPaymentRequest centralPayPaymentRequest = centralPayDelegateService
                    .getPaymentRequest(request.getPaymentRequestId());

            if (centralPayPaymentRequest != null) {
                if (centralPayPaymentRequest.getPaymentRequestStatus().equals(CentralPayPaymentRequest.CLOSED)
                        && centralPayPaymentRequest.getPaymentStatus().equals(CentralPayPaymentRequest.PAID)) {

                    if (quotation.getQuotationStatus().getCode()
                            .equals(QuotationStatus.SENT_TO_CUSTOMER)) {
                        unlockQuotationFromDeposit(quotation);
                        paymentService.generateDepositOnCustomerOrderForCbPayment(
                                quotation.getCustomerOrders().get(0), centralPayPaymentRequest);
                    }
                }
                if (centralPayPaymentRequest.getCreationDate().isBefore(LocalDateTime.now().minusMinutes(5))) {
                    if (centralPayPaymentRequest.getPaymentRequestStatus().equals(CentralPayPaymentRequest.ACTIVE))
                        centralPayDelegateService.cancelPaymentRequest(centralPayPaymentRequest.getPaymentRequestId());
                    return true;
                }

                return centralPayPaymentRequest.getPaymentRequestStatus().equals(CentralPayPaymentRequest.CLOSED)
                        || centralPayPaymentRequest.getPaymentRequestStatus().equals(CentralPayPaymentRequest.CANCELED);
            }
        }
        return true;
    }

    @Override
    public Quotation unlockQuotationFromDeposit(Quotation quotation)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {

        if (quotation.getQuotationStatus().getCode().equals(QuotationStatus.SENT_TO_CUSTOMER)) {
            // Generate customer order
            quotation = addOrUpdateQuotationStatus(quotation, QuotationStatus.VALIDATED_BY_CUSTOMER);
            notificationService.notifyQuotationValidatedByCustomer(quotation, false);
        }
        return quotation;
    }

    private BigDecimal computeQuotationTotalPrice(IQuotation quotation) {
        // Compute prices
        BigDecimal preTaxPriceTotal = new BigDecimal(0);
        BigDecimal discountTotal = null;
        BigDecimal vatTotal = new BigDecimal(0);
        BigDecimal zeroValue = new BigDecimal(0);

        if (quotation.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder asso : quotation.getAssoAffaireOrders()) {
                for (Service service : asso.getServices())
                    for (Provision provision : service.getProvisions()) {
                        for (InvoiceItem invoiceItem : provision.getInvoiceItems()) {
                            preTaxPriceTotal = preTaxPriceTotal
                                    .add(invoiceItem.getPreTaxPrice() != null ? invoiceItem.getPreTaxPrice()
                                            : zeroValue);
                            if (invoiceItem.getDiscountAmount() != null
                                    && invoiceItem.getDiscountAmount().compareTo(zeroValue) > 0) {
                                if (discountTotal == null)
                                    discountTotal = invoiceItem.getDiscountAmount();
                                else
                                    discountTotal = discountTotal.add(invoiceItem.getDiscountAmount());
                            }
                            if (invoiceItem.getVat() != null && invoiceItem.getVatPrice() != null
                                    && invoiceItem.getVatPrice().compareTo(zeroValue) > 0) {
                                vatTotal = vatTotal.add(invoiceItem.getVatPrice());
                            }
                        }
                    }
            }

        return preTaxPriceTotal.subtract(discountTotal != null ? discountTotal : zeroValue).add(vatTotal);
    }

    @Transactional(rollbackFor = Exception.class)
    public void sendRemindersForQuotation() throws OsirisException {
        List<Quotation> quotations = quotationRepository.findQuotationForReminder(
                quotationStatusService.getQuotationStatusByCode(QuotationStatus.SENT_TO_CUSTOMER));

        if (quotations != null && quotations.size() > 0)
            for (Quotation quotation : quotations) {
                batchService.declareNewBatch(Batch.SEND_REMINDER_FOR_QUOTATION, quotation.getId());
            }
    }

    @Transactional(rollbackFor = Exception.class)
    public void sendReminderForQuotation(Quotation quotation)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        if (quotation != null) {
            // if only annonce légale
            boolean isOnlyAnnonceLegal = true;
            if (quotation.getAssoAffaireOrders() != null)
                loopAsso: for (AssoAffaireOrder asso : quotation.getAssoAffaireOrders())
                    for (Service service : asso.getServices())
                        for (Provision provision : service.getProvisions())
                            if (provision.getAnnouncement() == null) {
                                isOnlyAnnonceLegal = false;
                                break loopAsso;
                            }

            boolean toSend = false;
            if (isOnlyAnnonceLegal) {
                if (quotation.getFirstReminderDateTime() == null
                        && quotation.getCreatedDate().isBefore(LocalDateTime.now().minusDays(1))) {
                    toSend = true;
                    quotation.setFirstReminderDateTime(LocalDateTime.now());
                } else if (quotation.getFirstReminderDateTime() != null
                        && quotation.getSecondReminderDateTime() == null
                        && quotation.getCreatedDate().isBefore(LocalDateTime.now().minusDays(2))
                        && quotation.getFirstReminderDateTime().isBefore(LocalDateTime.now().minusDays(1))) {
                    toSend = true;
                    quotation.setSecondReminderDateTime(LocalDateTime.now());
                } else if (quotation.getSecondReminderDateTime() != null
                        && quotation.getCreatedDate().isBefore(LocalDateTime.now().minusDays(7))
                        && quotation.getSecondReminderDateTime().isBefore(LocalDateTime.now().minusDays(5))) {
                    toSend = true;
                    quotation.setThirdReminderDateTime(LocalDateTime.now());
                }
            } else {
                if (quotation.getFirstReminderDateTime() == null
                        && quotation.getCreatedDate().isBefore(LocalDateTime.now().minusDays(1 * 7))) {
                    toSend = true;
                    quotation.setFirstReminderDateTime(LocalDateTime.now());
                } else if (quotation.getFirstReminderDateTime() != null
                        && quotation.getSecondReminderDateTime() == null
                        && quotation.getCreatedDate().isBefore(LocalDateTime.now().minusDays(3 * 7))
                        && quotation.getFirstReminderDateTime().isBefore(LocalDateTime.now().minusDays(1 * 7))) {
                    toSend = true;
                    quotation.setSecondReminderDateTime(LocalDateTime.now());
                } else if (quotation.getSecondReminderDateTime() != null
                        && quotation.getCreatedDate().isBefore(LocalDateTime.now().minusDays(6 * 7))
                        && quotation.getSecondReminderDateTime().isBefore(LocalDateTime.now().minusDays(1 * 7))) {
                    toSend = true;
                    quotation.setThirdReminderDateTime(LocalDateTime.now());
                }
            }

            if (toSend) {
                mailHelper.sendQuotationToCustomer(quotation, false);
                quotationRepository.save(quotation);
            }
        }
    }

    @Override
    public void updateAssignedToForQuotation(Quotation quotation, Employee employee)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        quotation.setAssignedTo(employee);
        addOrUpdateQuotation(quotation);
    }

    @Override
    public List<QuotationSearchResult> searchByCustomerOrderId(Integer idCustomerOrder) {
        return quotationRepository.findQuotations(
                Arrays.asList(0), Arrays.asList(0), Arrays.asList(0), LocalDateTime.now().minusYears(100),
                LocalDateTime.now().plusYears(100), Arrays.asList(0), Arrays.asList(0), idCustomerOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean getIsOpenedQuotation(IQuotation quotation) {
        if (quotation instanceof CustomerOrder) {
            CustomerOrder customerOrder = customerOrderService.getCustomerOrder(quotation.getId());
            return customerOrder.getCustomerOrderStatus() != null
                    && customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.OPEN);
        }

        if (quotation instanceof Quotation) {
            Quotation quotationQuotation = getQuotation(quotation.getId());
            return quotationQuotation.getQuotationStatus() != null
                    && quotationQuotation.getQuotationStatus().getCode().equals(QuotationStatus.OPEN);
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void validateQuotationFromCustomer(Quotation quotation)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {
        quotation = getQuotation(quotation.getId());

        if (!quotation.getQuotationStatus().getCode().equals(QuotationStatus.SENT_TO_CUSTOMER))
            throw new OsirisValidationException("Wrong quotation status");

        if (quotation.getResponsable().getTiers().getIsProvisionalPaymentMandatory())
            throw new OsirisValidationException("Deposit mandatory");

        addOrUpdateQuotationStatus(quotation, QuotationStatus.VALIDATED_BY_CUSTOMER);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Quotation associateCustomerOrderToQuotation(Quotation quotation, CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        customerOrder = customerOrderService.getCustomerOrder(customerOrder.getId());

        quotation = getQuotation(quotation.getId());
        if (quotation.getCustomerOrders() == null)
            quotation.setCustomerOrders(new ArrayList<CustomerOrder>());
        quotation.getCustomerOrders().add(customerOrder);
        quotation.setQuotationStatus(
                quotationStatusService.getQuotationStatusByCode(QuotationStatus.VALIDATED_BY_CUSTOMER));
        return addOrUpdateQuotation(quotation);
    }

    public List<Quotation> searchQuotationsForCurrentUser(List<String> customerOrderStatus, Integer page,
            String sortBy) {
        List<QuotationStatus> quotationStatusToFilter = new ArrayList<QuotationStatus>();

        if (customerOrderStatus != null && customerOrderStatus.size() > 0) {
            for (String customerOrderStatusCode : customerOrderStatus) {
                QuotationStatus customerOrderStatusFetched = quotationStatusService
                        .getQuotationStatusByCode(customerOrderStatusCode);
                if (customerOrderStatusFetched != null
                        && !customerOrderStatusFetched.getCode().equals(QuotationStatus.ABANDONED))
                    quotationStatusToFilter.add(customerOrderStatusFetched);
            }

            List<Responsable> responsablesToFilter = userScopeService.getUserCurrentScopeResponsables();

            if (quotationStatusToFilter.size() > 0 && responsablesToFilter != null
                    && responsablesToFilter.size() > 0) {

                Order order = new Order(Direction.DESC, "createdDate");

                if (sortBy.equals("createdDateAsc"))
                    order = new Order(Direction.ASC, "createdDate");

                if (sortBy.equals("statusAsc"))
                    order = new Order(Direction.ASC, "customerOrderStatus");

                Sort sort = Sort.by(Arrays.asList(order));
                Pageable pageableRequest = PageRequest.of(page, 50, sort);
                return populateTransientField(quotationRepository.searchQuotationsForCurrentUser(responsablesToFilter,
                        quotationStatusToFilter, pageableRequest));
            }
        }

        return null;
    }

    @Override
    public List<Quotation> searchQuotations(List<QuotationStatus> quotationStatus, List<Responsable> responsables) {
        if (quotationStatus != null && quotationStatus.size() > 0 && quotationStatus.size() > 0
                && responsables != null && responsables.size() > 0) {
            return quotationRepository.searchQuotations(responsables, quotationStatus);
        }
        return null;
    }

    private List<Quotation> populateTransientField(List<Quotation> quotations) {
        if (quotations != null && quotations.size() > 0)
            for (Quotation quotation : quotations) {
                List<String> affaireLabels = new ArrayList<String>();
                List<String> serviceLabels = new ArrayList<String>();
                if (quotation.getAssoAffaireOrders() != null)
                    for (AssoAffaireOrder assoAffaireOrder : quotation.getAssoAffaireOrders()) {
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
                    }

                if (affaireLabels.size() > 0)
                    quotation.setAffairesList(String.join(" / ", affaireLabels));
                quotation.setServicesList(String.join(" / ", serviceLabels));
            }

        return quotations;
    }

    private final ConcurrentHashMap<Integer, LocalDateTime> validationIdQuotationMap = new ConcurrentHashMap<>();
    private LocalDateTime lastFloodFlush = LocalDateTime.now();
    private final int floodFlushDelayMinute = 60;

    @Override
    public Integer generateValidationIdForQuotation() {
        return quotationRepository.generateValidationIdForQuotation();
    }

    @Override
    public Boolean checkValidationIdQuotation(Integer validationId) {
        if (lastFloodFlush.isBefore(LocalDateTime.now().minusMinutes(floodFlushDelayMinute)))
            cleanValidationIdQuotationMap();
        if (validationIdQuotationMap.containsKey(validationId)) {
            return true;
        } else {
            validationIdQuotationMap.put(validationId, LocalDateTime.now());
            return false;
        }
    }

    private void cleanValidationIdQuotationMap() {
        for (Integer id : validationIdQuotationMap.keySet())
            if (validationIdQuotationMap.get(id).isBefore(LocalDateTime.now().minusMinutes(floodFlushDelayMinute)))
                validationIdQuotationMap.remove(id);
        lastFloodFlush = LocalDateTime.now();
    }

    @Override
    public List<Quotation> findQuotationByResponsable(Responsable responsable) {
        return quotationRepository.findByResponsable(responsable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Quotation saveQuotationFromMyJss(Quotation quotation, HttpServletRequest request)
            throws OsirisClientMessageException, OsirisValidationException, OsirisException {
        quotation.setResponsable(employeeService.getCurrentMyJssUser());
        quotation.setCustomerOrderOrigin(constantService.getCustomerOrderOriginMyJss());
        quotation.setQuotationStatus(
                quotationStatusService.getQuotationStatusByCode(CustomerOrderStatus.DRAFT));
        return addOrUpdateQuotationFromUser(quotation);
    }
}
