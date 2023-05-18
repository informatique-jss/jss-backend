package com.jss.osiris.modules.quotation.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.service.DepositService;
import com.jss.osiris.modules.invoicing.service.PaymentService;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.service.MailService;
import com.jss.osiris.modules.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.miscellaneous.service.PhoneService;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.model.IQuotation;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.model.QuotationSearch;
import com.jss.osiris.modules.quotation.model.QuotationSearchResult;
import com.jss.osiris.modules.quotation.model.QuotationStatus;
import com.jss.osiris.modules.quotation.model.centralPay.CentralPayPaymentRequest;
import com.jss.osiris.modules.quotation.repository.QuotationRepository;
import com.jss.osiris.modules.tiers.model.ITiers;

@Service
public class QuotationServiceImpl implements QuotationService {

    @Autowired
    QuotationRepository quotationRepository;

    @Autowired
    IndexEntityService indexEntityService;

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
    DepositService depositService;

    @Autowired
    PaymentService paymentService;

    @Autowired
    AccountingRecordService accountingRecordService;

    @Autowired
    PricingHelper pricingHelper;

    @Autowired
    CentralPayPaymentRequestService centralPayPaymentRequestService;

    @Override
    public Quotation getQuotation(Integer id) {
        Optional<Quotation> quotation = quotationRepository.findById(id);
        if (quotation.isPresent())
            return quotation.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Quotation addOrUpdateQuotationFromUser(Quotation quotation)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        return addOrUpdateQuotation(quotation);
    }

    @Override
    public Quotation addOrUpdateQuotation(Quotation quotation)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        quotation.setIsQuotation(true);

        if (quotation.getDocuments() != null)
            for (Document document : quotation.getDocuments()) {
                document.setQuotation(quotation);
                mailService.populateMailIds(document.getMailsAffaire());
                mailService.populateMailIds(document.getMailsClient());
            }

        // Set default customer order assignation to sales employee if not set
        if (quotation.getAssignedTo() == null)
            quotation.setAssignedTo(
                    getCustomerOrderOfQuotation(quotation).getDefaultCustomerOrderEmployee());

        // Complete provisions
        if (quotation.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder assoAffaireOrder : quotation.getAssoAffaireOrders()) {
                assoAffaireOrder.setQuotation(quotation);
                assoAffaireOrderService.completeAssoAffaireOrder(assoAffaireOrder, quotation);
            }

        boolean isNewQuotation = quotation.getId() == null;
        if (isNewQuotation) {
            quotation.setCreatedDate(LocalDateTime.now());
            quotation = quotationRepository.save(quotation);
        }

        quotation = quotationRepository.save(quotation);

        pricingHelper.getAndSetInvoiceItemsForQuotation(quotation, true);

        quotation = getQuotation(quotation.getId());

        indexEntityService.indexEntity(quotation, quotation.getId());

        if (isNewQuotation) {
            notificationService.notifyNewQuotation(quotation);

            if (quotation.getIsCreatedFromWebSite())
                mailHelper.sendQuotationCreationConfirmationToCustomer(quotation);
        }
        return quotation;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Quotation addOrUpdateQuotationStatus(Quotation quotation, String targetStatusCode)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
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
        }

        // Target REFUSED from SENT TO CUSTOMER : notify user
        if (quotation.getQuotationStatus().getCode().equals(QuotationStatus.SENT_TO_CUSTOMER)
                && targetQuotationStatus.getCode().equals(QuotationStatus.REFUSED_BY_CUSTOMER))
            notificationService.notifyQuotationRefusedByCustomer(quotation);

        quotation.setLastStatusUpdate(LocalDateTime.now());
        quotation.setQuotationStatus(targetQuotationStatus);
        return this.addOrUpdateQuotation(quotation);
    }

    @Override
    public ITiers getCustomerOrderOfQuotation(IQuotation quotation) throws OsirisException {
        if (quotation.getConfrere() != null)
            return quotation.getConfrere();
        if (quotation.getResponsable() != null)
            return quotation.getResponsable();
        if (quotation.getTiers() != null)
            return quotation.getTiers();

        throw new OsirisException(null, "No customer order declared on IQuotation " + quotation.getId());
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
            for (ITiers tiers : quotationSearch.getCustomerOrders())
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
    public void reindexQuotation() {
        List<Quotation> quotations = IterableUtils.toList(quotationRepository.findAll());
        if (quotations != null)
            for (Quotation quotation : quotations)
                indexEntityService.indexEntity(quotation, quotation.getId());
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

        com.jss.osiris.modules.quotation.model.CentralPayPaymentRequest request = centralPayPaymentRequestService
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

        Float remainingToPay = computeQuotationTotalPrice(quotation);

        if (remainingToPay > 0) {
            CentralPayPaymentRequest paymentRequest = centralPayDelegateService.generatePayPaymentRequest(
                    remainingToPay, mail,
                    quotation.getId() + "", subject);

            centralPayPaymentRequestService.declareNewCentralPayPaymentRequest(paymentRequest.getPaymentRequestId(),
                    null, quotation, false);
            return paymentRequest.getBreakdowns().get(0).getEndpoint();
        }
        return "ok";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean validateCardPaymentLinkForQuotationDeposit(Quotation quotation,
            com.jss.osiris.modules.quotation.model.CentralPayPaymentRequest request)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        quotation = getQuotation(quotation.getId());
        if (request != null) {
            CentralPayPaymentRequest centralPayPaymentRequest = centralPayDelegateService
                    .getPaymentRequest(request.getPaymentRequestId());

            if (centralPayPaymentRequest != null) {
                if (centralPayPaymentRequest.getPaymentRequestStatus().equals(CentralPayPaymentRequest.CLOSED)
                        && centralPayPaymentRequest.getPaymentStatus().equals(CentralPayPaymentRequest.PAID)) {

                    if (quotation.getQuotationStatus().getCode()
                            .equals(QuotationStatus.SENT_TO_CUSTOMER)) {
                        unlockQuotationFromDeposit(quotation, centralPayPaymentRequest);
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

    private Quotation unlockQuotationFromDeposit(Quotation quotation, CentralPayPaymentRequest centralPayPaymentRequest)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {

        if (quotation.getQuotationStatus().getCode().equals(QuotationStatus.SENT_TO_CUSTOMER)) {
            // Generate customer order
            quotation = addOrUpdateQuotationStatus(quotation, QuotationStatus.VALIDATED_BY_CUSTOMER);
            notificationService.notifyQuotationValidatedByCustomer(quotation, false);
            customerOrderService.generateDepositOnCustomerOrderForCbPayment(quotation.getCustomerOrders().get(0),
                    centralPayPaymentRequest);
        }
        return quotation;
    }

    private Float computeQuotationTotalPrice(IQuotation quotation) {
        // Compute prices
        Float preTaxPriceTotal = 0f;
        Float discountTotal = null;
        Float vatTotal = 0f;

        for (AssoAffaireOrder asso : quotation.getAssoAffaireOrders()) {
            for (Provision provision : asso.getProvisions()) {
                for (InvoiceItem invoiceItem : provision.getInvoiceItems()) {
                    preTaxPriceTotal += invoiceItem.getPreTaxPrice() != null ? invoiceItem.getPreTaxPrice() : 0f;
                    if (invoiceItem.getDiscountAmount() != null && invoiceItem.getDiscountAmount() > 0) {
                        if (discountTotal == null)
                            discountTotal = invoiceItem.getDiscountAmount();
                        else
                            discountTotal += invoiceItem.getDiscountAmount();
                    }
                    if (invoiceItem.getVat() != null && invoiceItem.getVatPrice() != null
                            && invoiceItem.getVatPrice() > 0) {
                        vatTotal += invoiceItem.getVatPrice();
                    }
                }
            }
        }

        return preTaxPriceTotal - (discountTotal != null ? discountTotal : 0) + vatTotal;
    }

    @Transactional(rollbackFor = Exception.class)
    public void sendRemindersForQuotation()
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        List<Quotation> quotations = quotationRepository.findQuotationForReminder(
                quotationStatusService.getQuotationStatusByCode(QuotationStatus.SENT_TO_CUSTOMER));

        if (quotations != null && quotations.size() > 0)
            for (Quotation quotation : quotations) {
                // if only annonce légale
                boolean isOnlyAnnonceLegal = true;
                if (quotation.getAssoAffaireOrders() != null)
                    loopAsso: for (AssoAffaireOrder asso : quotation.getAssoAffaireOrders())
                        if (asso.getProvisions() != null)
                            for (Provision provision : asso.getProvisions())
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
                    } else if (quotation.getSecondReminderDateTime() == null
                            && quotation.getCreatedDate().isBefore(LocalDateTime.now().minusDays(2))) {
                        toSend = true;
                        quotation.setSecondReminderDateTime(LocalDateTime.now());
                    } else if (quotation.getCreatedDate().isBefore(LocalDateTime.now().minusDays(7))) {
                        toSend = true;
                        quotation.setThirdReminderDateTime(LocalDateTime.now());
                    }
                } else {
                    if (quotation.getFirstReminderDateTime() == null
                            && quotation.getCreatedDate().isBefore(LocalDateTime.now().minusDays(1 * 7))) {
                        toSend = true;
                        quotation.setFirstReminderDateTime(LocalDateTime.now());
                    } else if (quotation.getSecondReminderDateTime() == null
                            && quotation.getCreatedDate().isBefore(LocalDateTime.now().minusDays(3 * 7))) {
                        toSend = true;
                        quotation.setSecondReminderDateTime(LocalDateTime.now());
                    } else if (quotation.getCreatedDate().isBefore(LocalDateTime.now().minusDays(6 * 7))) {
                        toSend = true;
                        quotation.setThirdReminderDateTime(LocalDateTime.now());
                    }
                }

                if (toSend) {
                    mailHelper.sendQuotationToCustomer(quotation, false);
                    addOrUpdateQuotation(quotation);
                }
            }

    }

    @Override
    public void updateAssignedToForQuotation(Quotation quotation, Employee employee)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
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

}
