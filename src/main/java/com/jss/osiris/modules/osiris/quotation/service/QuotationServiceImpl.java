package com.jss.osiris.modules.osiris.quotation.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule.Feature;
import com.jss.osiris.libs.ActiveDirectoryHelper;
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
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.libs.search.service.SearchService;
import com.jss.osiris.modules.myjss.profile.service.UserScopeService;
import com.jss.osiris.modules.myjss.quotation.service.MyJssQuotationDelegate;
import com.jss.osiris.modules.osiris.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceItemService;
import com.jss.osiris.modules.osiris.invoicing.service.PaymentService;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.CustomerOrderOrigin;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.model.Notification;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.CustomerOrderOriginService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.MailService;
import com.jss.osiris.modules.osiris.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.osiris.miscellaneous.service.PhoneService;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.controller.QuotationValidationHelper;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.Announcement;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderTransient;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.QuotationSearch;
import com.jss.osiris.modules.osiris.quotation.model.QuotationSearchResult;
import com.jss.osiris.modules.osiris.quotation.model.QuotationStatus;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.model.ServiceType;
import com.jss.osiris.modules.osiris.quotation.model.centralPay.CentralPayPaymentRequest;
import com.jss.osiris.modules.osiris.quotation.repository.QuotationRepository;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;
import com.jss.osiris.modules.osiris.tiers.service.TiersService;

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
    ServiceService serviceService;

    @Autowired
    BatchService batchService;

    @Autowired
    ProvisionService provisionService;

    @Autowired
    DocumentService documentService;

    @Autowired
    TiersService tiersService;

    @Autowired
    GeneratePdfDelegate generatePdfDelegate;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    QuotationValidationHelper quotationValidationHelper;

    @Autowired
    MyJssQuotationDelegate myJssQuotationDelegate;

    @Autowired
    AffaireService affaireService;

    @Autowired
    InvoiceItemService invoiceItemService;

    @Autowired
    UserScopeService userScopeService;

    @Autowired
    SearchService searchService;

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

    private Quotation simpleAddOrUpdateQuotation(Quotation quotation) {
        return quotationRepository.save(quotation);
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
                mailService.populateMailId(document.getReminderMail());
            }

        boolean isNewQuotation = quotation.getId() == null;
        boolean hasNewAsso = false;

        if (quotation.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder asso : quotation.getAssoAffaireOrders())
                if (asso.getId() == null) {
                    hasNewAsso = true;
                    break;
                }
        if (isNewQuotation) {
            quotation.setCreatedDate(LocalDateTime.now());
            quotation.setValidationToken(UUID.randomUUID().toString());
        }

        if (isNewQuotation || hasNewAsso)
            quotation = quotationRepository.save(quotation);

        // Complete provisions
        if (quotation.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder assoAffaireOrder : quotation.getAssoAffaireOrders()) {
                assoAffaireOrder.setQuotation(quotation);
                if (assoAffaireOrder.getServices() != null && assoAffaireOrder.getServices().size() > 0) {
                    assoAffaireOrderService.completeAssoAffaireOrder(assoAffaireOrder, quotation, true);
                    for (Service service : assoAffaireOrder.getServices())
                        if (service.getProvisions() != null)
                            for (Provision provision : service.getProvisions())
                                if (provision.getId() == null && !isNewQuotation) {
                                    provision.setService(service);
                                    provisionService.addOrUpdateProvision(provision);
                                }
                }
            }

        pricingHelper.getAndSetInvoiceItemsForQuotation(quotation, true);
        quotation = quotationRepository.save(quotation);

        quotation = getQuotation(quotation.getId());

        batchService.declareNewBatch(Batch.REINDEX_QUOTATION, quotation.getId());

        if (isNewQuotation && quotation.getCustomerOrderOrigin() == null) {
            List<CustomerOrderOrigin> origins = customerOrderOriginService
                    .getByUsername(activeDirectoryHelper.getCurrentUsername());
            if (origins != null && origins.size() == 1)
                quotation.setCustomerOrderOrigin(origins.get(0));
            else
                quotation.setCustomerOrderOrigin(constantService.getCustomerOrderOriginOsiris());

            if (quotation.getCustomerOrderOrigin().getId()
                    .equals(constantService.getCustomerOrderOriginMyJss().getId()))
                mailHelper.sendQuotationCreationConfirmationToCustomer(quotation);

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

        // Target SENT TO CUSTOMER : notify users and customer
        if (targetQuotationStatus.getCode().equals(QuotationStatus.SENT_TO_CUSTOMER)) {
            // save to recompute invoice item before sent it to customer
            quotation.setEffectiveDate(LocalDateTime.now());
            quotation = this.addOrUpdateQuotation(quotation);

            generateQuotationPdf(quotation);

            mailHelper.sendQuotationToCustomer(quotation, false);
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
            quotation.setPrincingEffectiveDate(LocalDate.now());
            customerOrder.setQuotations(new ArrayList<Quotation>());
            customerOrder.getQuotations().add(quotation);
            mailHelper.sendCustomerOrderCreationConfirmationOnQuotationValidation(quotation, customerOrder);
        }

        // If coming from MyJss, notify sales that quotation has been abandoned
        if (employeeService.getCurrentEmployee() == null && employeeService.getCurrentMyJssUser() != null
                && targetStatusCode.equals(QuotationStatus.ABANDONED)) {
            notificationService.notifyAbandonnedQuotationFromMyJss(quotation);
        }

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
                if (quotation != null && ((attachment.getUploadedFile() != null
                        && attachment.getUploadedFile().getFilename().contains(quotation.getId() + ""))
                        || (attachment.getDescription() != null
                                && attachment.getDescription().contains(quotation.getId() + "")))) {
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
                salesEmployeeId,
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
                        .getCardPaymentLinkForCustomerOrderDeposit(quotation.getCustomerOrders(), mail, subject);
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
                    quotation.getId() + "", subject, true);

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
                                quotation.getCustomerOrders(), centralPayPaymentRequest);
                    } else if (quotation.getQuotationStatus().getCode().equals(QuotationStatus.VALIDATED_BY_CUSTOMER)
                            && quotation.getCustomerOrders() != null) {
                        customerOrderService.validateCardPaymentLinkForCustomerOrder(quotation.getCustomerOrders(),
                                request);
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
    public List<QuotationSearchResult> searchByCustomerOrderId(Integer idCustomerOrder) {
        return quotationRepository.findQuotations(
                Arrays.asList(0), Arrays.asList(0), LocalDateTime.now().minusYears(100),
                LocalDateTime.now().plusYears(100), Arrays.asList(0), Arrays.asList(0), idCustomerOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean getIsOpenedQuotation(IQuotation quotation) {
        if (quotation instanceof CustomerOrder) {
            CustomerOrder customerOrder = customerOrderService.getCustomerOrder(quotation.getId());
            return customerOrder.getCustomerOrderStatus() != null
                    && customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.DRAFT);
        }

        if (quotation instanceof Quotation) {
            Quotation quotationQuotation = getQuotation(quotation.getId());
            return quotationQuotation.getQuotationStatus() != null
                    && quotationQuotation.getQuotationStatus().getCode().equals(QuotationStatus.DRAFT);
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

    public List<Quotation> searchQuotationsForCurrentUser(List<String> customerOrderStatus,
            List<Integer> responsableIdToFilter, Integer page,
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

            if (quotationStatusToFilter.size() > 0 && responsablesToFilter != null
                    && responsablesToFilter.size() > 0) {

                Order order = new Order(Direction.DESC, "createdDate");

                if (sortBy.equals("createdDateAsc"))
                    order = new Order(Direction.ASC, "createdDate");

                if (sortBy.equals("statusAsc"))
                    order = new Order(Direction.ASC, "customerOrderStatus");

                Sort sort = Sort.by(Arrays.asList(order));
                Pageable pageableRequest = PageRequest.of(page, 10, sort);
                return populateTransientField(quotationRepository.searchQuotationsForCurrentUser(responsablesToFilter,
                        quotationStatusToFilter, pageableRequest));
            }
        }

        return new ArrayList<>();
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
                                String serviceLabel = service.getServiceLabelToDisplay();
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
    public List<Quotation> getQuotationsByResponsablesAndStatusAndDates(List<Responsable> responsables,
            LocalDateTime startDate, LocalDateTime endDate, QuotationStatus quotationStatus) {
        return quotationRepository.findByResponsablesAndStatusAndCreatedDateBetween(responsables, startDate,
                endDate, quotationStatus);
    }

    @Override
    public Quotation completeAdditionnalInformationForQuotation(Quotation quotation,
            Boolean populationAssoAffaireOrderTransientField)
            throws OsirisException {
        return completeAdditionnalInformationForQuotations(Arrays.asList(quotation),
                populationAssoAffaireOrderTransientField).get(0);
    }

    public List<Quotation> completeAdditionnalInformationForQuotations(List<Quotation> quotations,
            Boolean populationAssoAffaireOrderTransientField)
            throws OsirisException {
        if (quotations != null && quotations.size() > 0) {

            // Prepare indexation usage
            List<IndexEntity> indexEntities = searchService.searchForEntitiesByIds(
                    quotations.stream().map(Quotation::getId).toList(), Quotation.class.getSimpleName());

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

            List<Notification> notifications = notificationService.getNotificationsForCurrentEmployee(true, false, null,
                    false, false);

            if (notifications != null)
                notifications = notifications.stream().filter(n -> n.getQuotation() != null).toList();

            for (Quotation quotation : quotations) {
                if (notifications != null)
                    notifications.stream().filter(n -> n.getQuotation().getId().equals(quotation.getId()))
                            .findFirst()
                            .ifPresent(n -> quotation.setIsHasNotifications(true));

                if (populationAssoAffaireOrderTransientField)
                    assoAffaireOrderService.populateTransientField(quotation.getAssoAffaireOrders());

                if (indexEntities != null) {
                    indexEntities.stream().filter(n -> n.getEntityId().equals(quotation.getId())).findFirst()
                            .ifPresent(c -> {
                                CustomerOrderTransient indexOrder = null;
                                try {
                                    indexOrder = objectMapper.readValue(c.getText(), CustomerOrderTransient.class);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (indexOrder != null) {
                                    quotation.setAffairesList(indexOrder.getAffairesList());
                                    quotation.setServicesList(indexOrder.getServicesList());
                                    quotation.setHasMissingInformations(indexOrder.getHasMissingInformations());
                                }
                            });
                }
            }
        }

        return quotations;
    }

    @Override
    public Quotation completeAdditionnalInformationForQuotationWhenIndexing(Quotation quotation)
            throws OsirisException {
        List<String> affaireLabels = new ArrayList<String>();
        List<String> serviceLabels = new ArrayList<String>();
        quotation.setHasMissingInformations(false);
        if (quotation.getAssoAffaireOrders() != null) {
            assoAffaireOrderService.populateTransientField(quotation.getAssoAffaireOrders());
            for (AssoAffaireOrder assoAffaireOrder : quotation.getAssoAffaireOrders()) {
                String affaireDenomination = assoAffaireOrder.getAffaire().getDenomination();
                if (affaireDenomination == null || affaireDenomination.length() == 0)
                    affaireDenomination = assoAffaireOrder.getAffaire().getFirstname() + " "
                            + assoAffaireOrder.getAffaire().getLastname();
                if (affaireLabels.indexOf(affaireDenomination) < 0)
                    affaireLabels.add(affaireDenomination);

                if (assoAffaireOrder.getServices() != null && assoAffaireOrder.getServices().size() > 0)
                    for (Service service : assoAffaireOrder.getServices()) {
                        String serviceLabel = service.getServiceLabelToDisplay();
                        if (serviceLabels.indexOf(serviceLabel) < 0)
                            serviceLabels.add(serviceLabel);
                    }

                if (assoAffaireOrder.getServices() != null)
                    for (Service service : assoAffaireOrder.getServices()) {
                        if (serviceService.isServiceHasMissingInformations(service)) {
                            quotation.setHasMissingInformations(true);
                        }
                    }
            }
        }

        if (affaireLabels.size() > 0)
            quotation.setAffairesList(String.join(" / ", affaireLabels));
        quotation.setServicesList(String.join(" / ", serviceLabels));
        return quotation;
    }

    @Override
    public List<Quotation> searchQuotation(List<Employee> commercials,
            List<QuotationStatus> status) throws OsirisException {

        List<Integer> commercialIds = (commercials != null && commercials.size() > 0)
                ? commercials.stream().map(Employee::getId).collect(Collectors.toList())
                : Arrays.asList(0);

        List<Integer> statusIds = (status != null && status.size() > 0)
                ? status.stream().map(QuotationStatus::getId).collect(Collectors.toList())
                : Arrays.asList(0);

        return completeAdditionnalInformationForQuotations(
                quotationRepository.searchQuotation(commercialIds, statusIds), false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean setEmergencyOnQuotation(Quotation quotation, Boolean isEnabled)
            throws OsirisClientMessageException, OsirisValidationException, OsirisException {
        if (quotation.getAssoAffaireOrders() != null && quotation.getAssoAffaireOrders().size() > 0
                && quotation.getAssoAffaireOrders().get(0).getServices() != null
                && quotation.getAssoAffaireOrders().get(0).getServices().size() > 0
                && quotation.getAssoAffaireOrders().get(0).getServices().get(0).getProvisions() != null
                && quotation.getAssoAffaireOrders().get(0).getServices().get(0).getProvisions().size() > 0) {
            quotation.getAssoAffaireOrders().get(0).getServices().get(0).getProvisions().get(0)
                    .setIsEmergency(isEnabled);
            provisionService.addOrUpdateProvision(
                    quotation.getAssoAffaireOrders().get(0).getServices().get(0).getProvisions().get(0));
            quotation = getQuotation(quotation.getId());
            pricingHelper.getAndSetInvoiceItemsForQuotation(quotation, true);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean setDocumentOnOrder(Quotation quotation, Document document)
            throws OsirisClientMessageException, OsirisValidationException, OsirisException {
        if (quotation.getDocuments() == null)
            quotation.setDocuments(new ArrayList<Document>());

        document.setQuotation(quotation);
        documentService.addOrUpdateDocument(document);

        quotation = getQuotation(quotation.getId());
        pricingHelper.getAndSetInvoiceItemsForQuotation(quotation, true);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void purgeQuotations() throws OsirisException {
        List<Quotation> quotations = quotationRepository.findQuotationOlderThanDate(
                quotationStatusService.getQuotationStatusByCode(QuotationStatus.DRAFT),
                LocalDateTime.now().minusMonths(3));

        if (quotations != null)
            for (Quotation quotation : quotations)
                batchService.declareNewBatch(Batch.PURGE_QUOTATION, quotation.getId());
    }

    @Override
    public List<Quotation> getQuotationByAffaire(Affaire affaire) {
        return quotationRepository.findQuotationByAffaireAndQuotationStatus(affaire,
                quotationStatusService.getQuotationStatusByCode(QuotationStatus.SENT_TO_CUSTOMER));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reinitInvoicing(Quotation quotation)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        quotation = getQuotation(quotation.getId());
        if (quotation.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder asso : quotation.getAssoAffaireOrders())
                for (Service service : asso.getServices())
                    for (Provision provision : service.getProvisions())
                        if (provision.getInvoiceItems() != null) {
                            for (InvoiceItem invoiceItem : provision.getInvoiceItems())
                                invoiceItemService.deleteInvoiceItem(invoiceItem);
                            provision.setInvoiceItems(null);
                        }

        addOrUpdateQuotation(getQuotation(quotation.getId()));
    }

    @Override
    @Transactional
    public void switchResponsable(Quotation quotation, Responsable responsable) {
        quotation = getQuotation(quotation.getId());
        List<Responsable> userScope = userScopeService.getPotentialUserScope();

        if (userScope != null)
            for (Responsable scope : userScope)
                if (scope.getId().equals(responsable.getId())) {
                    quotation.setResponsable(responsable);
                    simpleAddOrUpdateQuotation(quotation);
                }
    }

    @Override
    public boolean isDepositMandatory(IQuotation quotation) {

        if (Boolean.TRUE.equals(quotation.getResponsable().getTiers().getIsProvisionalPaymentMandatory()))
            return true;

        if (quotation.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder asso : quotation.getAssoAffaireOrders()) {
                if (asso.getAffaire() != null
                        && Boolean.TRUE.equals(asso.getAffaire().getIsProvisionalPaymentMandatory()))
                    return true;
                if (asso.getServices() != null)
                    for (Service service : asso.getServices())
                        if (service.getServiceTypes() != null)
                            for (ServiceType serviceType : service.getServiceTypes())
                                if (Boolean.TRUE.equals(serviceType.getIsProvisionalPaymentMandatory()))
                                    return true;
            }

        return false;
    }

    @Override
    public List<Quotation> getByCreatedDateBetweenAndStatus(LocalDateTime createdStartDate,
            LocalDateTime createdEndDate,
            QuotationStatus quotationStatus, LocalDateTime updateStartDate, LocalDateTime updateEndDate) {
        return quotationRepository.findByCreatedDateBetweenAndStatus(createdStartDate, createdEndDate, quotationStatus,
                updateStartDate, updateEndDate);
    }

    @Scheduled(initialDelay = 100, fixedDelay = 5000)
    public void test2() throws IOException, OsirisException {
        test();
    }

    @Autowired
    CustomerOrderServiceImpl customerOrderServiceImpl;

    private void test() throws IOException, OsirisException {
        boolean modeReel = true;

        FileInputStream file = new FileInputStream("C:/t/caisses.xlsx");
        Workbook workbook = new XSSFWorkbook(file);
        DataFormatter dataFormatter = new DataFormatter(java.util.Locale.FRANCE); // Vital pour gérer les dates et
                                                                                  // nombres sans format
        // scientifique

        // Boucle sur les 4 onglets
        Sheet sheet = workbook.getSheetAt(3);
        System.out.println("Traitement de l'onglet : " + sheet.getSheetName());

        // On suppose que la ligne 0 est le header, on commence à 1
        for (int rowIndex = 2; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null)
                continue;

            // 1. Extraction des données (Mapping manuel rapide)
            // Attention aux index de colonnes (0 based)
            String codeCaisse = getVal(row, 0, dataFormatter);
            String denomination = getVal(row, 1, dataFormatter);
            String adrSiege = getVal(row, 2, dataFormatter);
            String cpSiege = getVal(row, 3, dataFormatter);
            String villeSiege = getVal(row, 4, dataFormatter);
            String siret = getVal(row, 5, dataFormatter);
            String greffe = getVal(row, 6, dataFormatter);
            String denoAbsorbante = getVal(row, 7, dataFormatter);
            String dateAGE1 = getVal(row, 8, dataFormatter);
            String heureAGE1 = getVal(row, 9, dataFormatter);
            String lieuAgo = getVal(row, 10, dataFormatter);
            String addresseAgo = getVal(row, 11, dataFormatter);
            String cpAgo = getVal(row, 12, dataFormatter);
            String villeAgo = getVal(row, 13, dataFormatter);
            String dateAgo = getVal(row, 14, dataFormatter);
            String heureAgo = getVal(row, 15, dataFormatter);
            String email = getVal(row, 16, dataFormatter);

            // Si une donnée critique manque, on skip
            if (email.isEmpty() || denomination.isEmpty())
                System.err.println("Erreur");

            // 2. Remplacement dans le template
            String texteFinal = StringEscapeUtils.unescapeHtml4(modelString)
                    .replace("(DENOMINATION DE LA CAISSE ABSORBEE)", denomination)
                    .replace("DENOMINATION DE LA CAISSE ABSORBANTE", denoAbsorbante)
                    .replace("(adresse du siège)", adrSiege + " " + cpSiege + " " + villeSiege)
                    .replace("(N° SIREN)", siret.substring(0, 9))
                    .replace("(greffe)", villeSiege)
                    .replace("(date AGO/AGE2)", dateAgo)
                    .replace("(date AGE1)", dateAGE1)
                    .replace("(heure AGE1)", heureAGE1)
                    .replace("(heure AGO/AGE2)", heureAgo)
                    .replace("(Lieu AGO/AGE2 et adresse AGO/AGE2)",
                            lieuAgo + ", " + addresseAgo + " " + cpAgo + " " + villeAgo)
                    .replace("{{GREFFE}}", greffe);

            // 3. Appel ERP
            System.out.println("Création commande pour : " + denomination);

            if (modeReel) {
                try {
                    // Ta méthode existante qui prend le texte et l'email
                    customerOrderServiceImpl.create(texteFinal, siret, email);
                } catch (Exception e) {
                    throw new OsirisException(e, "Erreur pour le siret " + siret);
                }
            } else {
                System.out.println("---- PREVIEW ----");
                System.out.println(texteFinal);
                System.out.println("-----------------");
            }
        }
        workbook.close();

    }

    private String getVal(Row row, int colIndex, DataFormatter formatter) {
        Cell cell = row.getCell(colIndex);
        if (cell == null)
            return "";

        // Si c'est une cellule de type Date (Excel stocke les dates en NUMERIC)
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            DateTimeFormatter frenchFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return cell.getDateCellValue()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .format(frenchFormatter);
        }

        // Pour le reste (texte, SIRET, etc.), on garde le formateur standard
        return formatter.formatCellValue(cell).trim();
    }

    private static String modelString = "<p style='margin-top:0cm;margin-right:0cm;margin-bottom:8.0pt;margin-left:36.0pt;font-size:11.0pt;font-family:\"Calibri\",sans-serif;text-align:justify;'><span style=\"font-family: Calibri, sans-serif;\">CAISSE DE CREDIT MUTUEL (DENOMINATION DE LA CAISSE ABSORBANTE)</span></p>\r\n"
            + //
            "<p style='margin-top:0cm;margin-right:0cm;margin-bottom:0cm;margin-left:36.0pt;font-size:11.0pt;font-family:\"Calibri\",sans-serif;text-align:justify;'><span style=\"font-family: Calibri, sans-serif;\">Soci&eacute;t&eacute; coop&eacute;rative &agrave; capital variable et &agrave; responsabilit&eacute; statutairement limit&eacute;e</span></p>\r\n"
            + //
            "<p style='margin-top:0cm;margin-right:0cm;margin-bottom:0cm;margin-left:36.0pt;font-size:11.0pt;font-family:\"Calibri\",sans-serif;text-align:justify;'><span style=\"font-family: Calibri, sans-serif;\">Si&egrave;ge social : (adresse du si&egrave;ge)</span></p>\r\n"
            + //
            "<p style='margin-top:0cm;margin-right:0cm;margin-bottom:0cm;margin-left:36.0pt;font-size:11.0pt;font-family:\"Calibri\",sans-serif;text-align:justify;'><span style=\"font-family: Calibri, sans-serif;\">(N&deg; SIREN) RCS (greffe)</span></p>\r\n"
            + //
            "<p style='margin-top:0cm;margin-right:0cm;margin-bottom:0cm;margin-left:36.0pt;font-size:11.0pt;font-family:\"Calibri\",sans-serif;text-align:justify;'><span style=\"font-family: Calibri, sans-serif;\">Les soci&eacute;taires sont inform&eacute;s que les assembl&eacute;es g&eacute;n&eacute;rales de la Caisse de Cr&eacute;dit Mutuel ci-dessus sont convoqu&eacute;es par le Conseil d&apos;Administration :</span></p>\r\n"
            + //
            "<p style='margin-top:0cm;margin-right:0cm;margin-bottom:0cm;margin-left:36.0pt;font-size:11.0pt;font-family:\"Calibri\",sans-serif;text-align:justify;'><span style=\"font-family: Calibri, sans-serif;\">&nbsp;</span></p>\r\n"
            + //
            "<p style='margin-top:0cm;margin-right:0cm;margin-bottom:8.0pt;margin-left:36.0pt;font-size:11.0pt;font-family:\"Calibri\",sans-serif;text-align:justify;'><span style=\"font-family: Calibri, sans-serif;\">1) En Assembl&eacute;e G&eacute;n&eacute;rale Extraordinaire le (date AGE1) &agrave; (heure AGE1) au si&egrave;ge de la Caisse avec l&apos;ordre du jour suivant :</span></p>\r\n"
            + //
            "<ol style=\"list-style-type: decimal;margin-left: 78.9px;\">\r\n" + //
            "    <li style=\"font-family: Calibri, sans-serif;\">Bienvenue, ouverture de l&apos;Assembl&eacute;e G&eacute;n&eacute;rale Extraordinaire, constitution du bureau.</li>\r\n"
            + //
            "    <li style=\"font-family: Calibri, sans-serif;\">Pr&eacute;sentation du projet de fusion des Caisses de Cr&eacute;dit Mutuel de &laquo; (DENOMINATION DE LA CAISSE ABSORBEE) &raquo; et de &laquo; DENOMINATION DE LA CAISSE ABSORBANTE &raquo; par voie d&apos;absorption par la Caisse de Cr&eacute;dit Mutuel de &laquo; DENOMINATION DE LA CAISSE ABSORBANTE &raquo;,</li>\r\n"
            + //
            "    <li style=\"font-family: Calibri, sans-serif;\">Lecture du rapport du Conseil d&rsquo;Administration, du rapport du Conseil de Surveillance et de l&apos;Inspection,</li>\r\n"
            + //
            "    <li style=\"font-family: Calibri, sans-serif;\">Ratification de la convention de fusion,</li>\r\n" + //
            "    <li style=\"font-family: Calibri, sans-serif;\">Fixation du nombre de postes au Conseil d&rsquo;Administration et nomination des Administrateurs(trices),</li>\r\n"
            + //
            "    <li style=\"font-family: Calibri, sans-serif;\">Fixation du nombre de postes au Conseil de Surveillance et nomination des Conseillers(&egrave;res),</li>\r\n"
            + //
            "    <li style=\"font-family: Calibri, sans-serif;\">Modifications des statuts cons&eacute;cutives &agrave; la fusion,</li>\r\n"
            + //
            "    <li style=\"font-family: Calibri, sans-serif;\">Pouvoirs pour les formalit&eacute;s,</li>\r\n" + //
            "    <li style=\"font-family: Calibri, sans-serif;\">Cl&ocirc;ture de l&apos;Assembl&eacute;e G&eacute;n&eacute;rale Extraordinaire.</li>\r\n"
            + //
            "</ol>\r\n" + //
            "<p style='margin-top:0cm;margin-right:0cm;margin-bottom:8.0pt;margin-left:36.0pt;font-size:11.0pt;font-family:\"Calibri\",sans-serif;'><span style=\"font-family: Calibri, sans-serif;\">&nbsp;</span></p>\r\n"
            + //
            "<p style='margin-top:0cm;margin-right:0cm;margin-bottom:0cm;margin-left:36.0pt;font-size:11.0pt;font-family:\"Calibri\",sans-serif;text-align:justify;'><span style=\"font-family: Calibri, sans-serif;\">ATTENTION : dans l&apos;hypoth&egrave;se o&ugrave; le quorum pr&eacute;vu par les statuts de la Caisse ne serait pas atteint, le Conseil d&apos;Administration convoque d&egrave;s &agrave; pr&eacute;sent une deuxi&egrave;me Assembl&eacute;e G&eacute;n&eacute;rale Extraordinaire selon les modalit&eacute;s indiqu&eacute;es au point 3) ci-apr&egrave;s.</span></p>\r\n"
            + //
            "<p style='margin-top:0cm;margin-right:0cm;margin-bottom:0cm;margin-left:36.0pt;font-size:11.0pt;font-family:\"Calibri\",sans-serif;'><span style=\"font-family: Calibri, sans-serif;\">&nbsp;</span></p>\r\n"
            + //
            "<p style='margin-top:0cm;margin-right:0cm;margin-bottom:0cm;margin-left:36.0pt;font-size:11.0pt;font-family:\"Calibri\",sans-serif;'><span style=\"font-family: Calibri, sans-serif;\">2) En Assembl&eacute;e G&eacute;n&eacute;rale Ordinaire le (date AGO/AGE2) &agrave; (heure AGO/AGE2) &agrave; l&apos;adresse suivante :</span></p>\r\n"
            + //
            "<p style='margin-top:0cm;margin-right:0cm;margin-bottom:8.0pt;margin-left:36.0pt;font-size:11.0pt;font-family:\"Calibri\",sans-serif;'><span style=\"font-family: Calibri, sans-serif;\">(Lieu AGO/AGE2 et adresse AGO/AGE2)</span></p>\r\n"
            + //
            "<p style='margin-top:0cm;margin-right:0cm;margin-bottom:0cm;margin-left:36.0pt;font-size:11.0pt;font-family:\"Calibri\",sans-serif;'><span style=\"font-family: Calibri, sans-serif;\">&nbsp;</span></p>\r\n"
            + //
            "<p style='margin-top:0cm;margin-right:0cm;margin-bottom:0cm;margin-left:36.0pt;font-size:11.0pt;font-family:\"Calibri\",sans-serif;'><span style=\"font-family: Calibri, sans-serif;\">Avec l&apos;ordre du jour suivant :</span></p>\r\n"
            + //
            "<ol style=\"list-style-type: decimal;margin-left: 71.8px;\">\r\n" + //
            "    <li style=\"font-family: Calibri, sans-serif;\">Bienvenue, ouverture de l&apos;Assembl&eacute;e G&eacute;n&eacute;rale Ordinaire, constitution du bureau.</li>\r\n"
            + //
            "    <li style=\"font-family: Calibri, sans-serif;\">Compte rendu d&apos;activit&eacute;.</li>\r\n" + //
            "    <li style=\"font-family: Calibri, sans-serif;\">Pr&eacute;sentation du bilan et du compte de r&eacute;sultat.</li>\r\n"
            + //
            "    <li style=\"font-family: Calibri, sans-serif;\">Rapport du Conseil de Surveillance et certification des comptes.</li>\r\n"
            + //
            "    <li style=\"font-family: Calibri, sans-serif;\">Approbation du bilan et du compte de r&eacute;sultat.</li>\r\n"
            + //
            "    <li style=\"font-family: Calibri, sans-serif;\">Affectation du r&eacute;sultat.</li>\r\n" + //
            "    <li style=\"font-family: Calibri, sans-serif;\">Variation du capital social.</li>\r\n" + //
            "    <li style=\"font-family: Calibri, sans-serif;\">Quitus et d&eacute;charge au Conseil d&apos;Administration.</li>\r\n"
            + //
            "    <li style=\"font-family: Calibri, sans-serif;\">Elections au Conseil d&apos;Administration.</li>\r\n" + //
            "    <li style=\"font-family: Calibri, sans-serif;\">Elections au Conseil de Surveillance.</li>\r\n" + //
            "    <li style=\"font-family: Calibri, sans-serif;\">Pouvoirs pour les formalit&eacute;s.</li>\r\n" + //
            "    <li style=\"font-family: Calibri, sans-serif;\">Communications diverses.</li>\r\n" + //
            "    <li style=\"font-family: Calibri, sans-serif;\">Cl&ocirc;ture de l&apos;Assembl&eacute;e G&eacute;n&eacute;rale Ordinaire.</li>\r\n"
            + //
            "</ol>\r\n" + //
            "<p style='margin-top:0cm;margin-right:0cm;margin-bottom:8.0pt;margin-left:35.4pt;font-size:11.0pt;font-family:\"Calibri\",sans-serif;text-align:justify;'><span style=\"font-family: Calibri, sans-serif;\">Les candidatures sont &agrave; adresser au si&egrave;ge de la Caisse 30 jours au moins avant la date de l&apos;Assembl&eacute;e G&eacute;n&eacute;rale.</span></p>\r\n"
            + //
            "<p style='margin-top:0cm;margin-right:0cm;margin-bottom:8.0pt;margin-left:35.4pt;font-size:11.0pt;font-family:\"Calibri\",sans-serif;'><span style=\"font-family: Calibri, sans-serif;\">3) En Assembl&eacute;e G&eacute;n&eacute;rale Extraordinaire</span></p>\r\n"
            + //
            "<p style='margin-top:0cm;margin-right:0cm;margin-bottom:8.0pt;margin-left:35.4pt;font-size:11.0pt;font-family:\"Calibri\",sans-serif;text-align:justify;'><span style=\"font-family: Calibri, sans-serif;\">A la suite imm&eacute;diate de l&apos;assembl&eacute;e ordinaire et dans les m&ecirc;mes conditions, cette deuxi&egrave;me assembl&eacute;e extraordinaire est convoqu&eacute;e pour se tenir dans l&apos;hypoth&egrave;se o&ugrave; le nombre minimum requis de soci&eacute;taires ne pourra &ecirc;tre r&eacute;uni lors de la premi&egrave;re Assembl&eacute;e G&eacute;n&eacute;rale Extraordinaire, afin de d&eacute;lib&eacute;rer sur l&apos;ordre du jour pr&eacute;cis&eacute; ci-dessus, sous le point 1).</span></p>\r\n"
            + //
            "<p style='margin-top:0cm;margin-right:0cm;margin-bottom:8.0pt;margin-left:35.4pt;font-size:11.0pt;font-family:\"Calibri\",sans-serif;text-align:justify;'><span style=\"font-family: Calibri, sans-serif;\">Les votes pourront se faire 15 jours calendaires avant la tenue de l&apos;Assembl&eacute;e G&eacute;n&eacute;rale Ordinaire sur votre espace de banque &agrave; distance ou dans votre Caisse aux jours et horaires habituels d&apos;ouverture ou lors de l&rsquo;assembl&eacute;e g&eacute;n&eacute;rale. Les documents statutaires pourront &ecirc;tre consult&eacute;s sur place ainsi que sur votre espace de banque &agrave; distance.</span></p>\r\n"
            + //
            "<p style='margin-top:0cm;margin-right:0cm;margin-bottom:8.0pt;margin-left:35.4pt;font-size:11.0pt;font-family:\"Calibri\",sans-serif;text-align:justify;'><span style=\"font-family: Calibri, sans-serif;\">Le Pr&eacute;sident du Conseil d&apos;Administration</span></p>";
}
