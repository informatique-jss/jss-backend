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
import com.jss.osiris.libs.JacksonLocalDateDeserializer;
import com.jss.osiris.libs.JacksonLocalDateSerializer;
import com.jss.osiris.libs.JacksonLocalDateTimeDeserializer;
import com.jss.osiris.libs.JacksonLocalDateTimeSerializer;
import com.jss.osiris.libs.PrintDelegate;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
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
import com.jss.osiris.modules.miscellaneous.model.Attachment;
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
import com.jss.osiris.modules.quotation.controller.QuotationValidationHelper;
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

    @Value("${invoicing.payment.limit.refund.euros}")
    private String payementLimitRefundInEuros;

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

    @Autowired
    QuotationValidationHelper quotationValidationHelper;

    @Autowired
    CentralPayPaymentRequestService centralPayPaymentRequestService;

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
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        return addOrUpdateCustomerOrder(customerOrder, true, true);
    }

    @Override
    public CustomerOrder addOrUpdateCustomerOrder(CustomerOrder customerOrder, boolean isFromUser,
            boolean checkAllProvisionEnded)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        if (customerOrder.getId() == null) {
            customerOrder.setCreatedDate(LocalDateTime.now());
        }

        if (customerOrder.getIsCreatedFromWebSite() == null)
            customerOrder.setIsCreatedFromWebSite(false);

        // Set default customer order assignation to sales employee if not set
        if (customerOrder.getAssignedTo() == null)
            customerOrder.setAssignedTo(
                    quotationService.getCustomerOrderOfQuotation(customerOrder).getDefaultCustomerOrderEmployee());

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

        if (checkAllProvisionEnded)
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
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
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
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
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
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
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
                    quotationValidationHelper.validateQuotationAndCustomerOrder(customerOrder,
                            CustomerOrderStatus.BEING_PROCESSED);
                } catch (Exception e) {
                    hasError = true;
                }
                if (!hasError)
                    targetStatusCode = CustomerOrderStatus.BEING_PROCESSED;
            }
        }

        // Target : CANCELLED => vérifiy there is no more deposit
        if (targetStatusCode.equals(CustomerOrderStatus.ABANDONED)) {
            if (customerOrder.getDeposits() != null && customerOrder.getDeposits().size() > 0)
                throw new OsirisClientMessageException(
                        "Impossible d'abandonner cette commande, elle possède encore des acomptes associés");
        }

        // Target : BEING PROCESSED => notify customer
        if (targetStatusCode.equals(CustomerOrderStatus.BEING_PROCESSED)) {
            resetDeboursManuelAmount(customerOrder);
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
            this.addOrUpdateCustomerOrder(customerOrder, true, true);
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

            invoice.setManualPaymentType(paymentType);
            invoiceService.addOrUpdateInvoice(invoice);

            // Check invoice payed
            Float remainingToPayForCurrentInvoice = Math
                    .round(invoiceService.getRemainingAmountToPayForInvoice(invoice) * 100f) / 100f;

            // Handle appoint
            if (remainingToPayForCurrentInvoice != 0 && customerOrder.getDeposits() != null
                    && customerOrder.getDeposits().size() > 0) {
                if (Math.abs(remainingToPayForCurrentInvoice) <= Float.parseFloat(payementLimitRefundInEuros)) {
                    accountingRecordService.generateAppointForDeposit(customerOrder.getDeposits().get(0),
                            remainingToPayForCurrentInvoice, invoiceHelper.getCustomerOrder(invoice));
                    Deposit deposit = customerOrder.getDeposits().get(0);
                    deposit.setDepositAmount(deposit.getDepositAmount() + remainingToPayForCurrentInvoice);
                    depositService.addOrUpdateDeposit(deposit);
                }
            }

            if (remainingToPayForCurrentInvoice < 0) {
                throw new OsirisException(null, "Impossible to billed, too much money on customerOrder !");
            }
            accountingRecordService.checkInvoiceForLettrage(invoice);

            mailHelper.sendCustomerOrderFinalisationToCustomer(getCustomerOrder(customerOrder.getId()), false, false,
                    false);
        }

        // Target : going back to TO BILLED => cancel invoice
        if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BILLED)
                && targetStatusCode.equals(CustomerOrderStatus.TO_BILLED)) {
            Invoice invoiceToCancel = null;
            if (customerOrder.getInvoices() != null)
                for (Invoice invoice : customerOrder.getInvoices())
                    if (!invoice.getInvoiceStatus().getId()
                            .equals(constantService.getInvoiceStatusCancelled().getId())
                            && !invoice.getInvoiceStatus().getId()
                                    .equals(constantService.getInvoiceStatusCreditNoteEmited().getId())) {
                        invoiceToCancel = invoice;
                        break;
                    }
            moveInvoiceDepositToCustomerOrderDeposit(customerOrder, invoiceToCancel);
            invoiceService.cancelInvoiceEmitted(invoiceToCancel, customerOrder);
        }

        CustomerOrderStatus customerOrderStatus = customerOrderStatusService
                .getCustomerOrderStatusByCode(
                        targetStatusCode);
        if (customerOrderStatus == null)
            throw new OsirisException(null, "Quotation status not found for code " + targetStatusCode);

        customerOrder.setCustomerOrderStatus(customerOrderStatus);
        customerOrder.setLastStatusUpdate(LocalDateTime.now());
        return this.addOrUpdateCustomerOrder(customerOrder, false, false);
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
     * @throws OsirisValidationException
     */
    private boolean moveForwardAnnouncementFromWebsite(CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
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

        if (invoice.getPayments() != null && invoice.getPayments().size() > 0)
            for (Payment payment : invoice.getPayments()) {
                depositService.getNewDepositForCustomerOrder(payment.getPaymentAmount(), LocalDateTime.now(),
                        customerOrder, null,
                        payment, false);
            }
    }

    @Override
    public CustomerOrder unlockCustomerOrderFromDeposit(CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.WAITING_DEPOSIT)) {
            addOrUpdateCustomerOrderStatus(customerOrder, CustomerOrderStatus.BEING_PROCESSED, false);
            notificationService.notifyCustomerOrderToBeingProcessed(customerOrder, false);
        }

        return customerOrder;
    }

    private Invoice generateInvoice(CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
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
        File invoicePdf = mailHelper.generateInvoicePdf(customerOrder, invoice, null);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmm");
        try {
            attachmentService.addAttachment(new FileInputStream(invoicePdf), customerOrder.getId(),
                    CustomerOrder.class.getSimpleName(),
                    constantService.getAttachmentTypeInvoice(),
                    "Invoice_" + invoice.getId() + "_" + formatter.format(LocalDateTime.now()) + ".pdf",
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

        List<OrderingSearchResult> customerOrders = customerOrderRepository.findCustomerOrders(
                salesEmployeeId, assignedToEmployeeId,
                statusId,
                orderingSearch.getStartDate().withHour(0).withMinute(0),
                orderingSearch.getEndDate().withHour(23).withMinute(59), customerOrderId, affaireId, 0);
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
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
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
                if (centralPayPaymentRequest.getPaymentRequestStatus().equals(CentralPayPaymentRequest.ACTIVE))
                    centralPayDelegateService.cancelPaymentRequest(request.getPaymentRequestId());

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
                    customerOrder, null, true);
            return paymentRequest.getBreakdowns().get(0).getEndpoint();
        }
        return "ok";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean validateCardPaymentLinkForCustomerOrder(CustomerOrder customerOrder,
            com.jss.osiris.modules.quotation.model.CentralPayPaymentRequest request)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
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
                                    generatePaymentOnInvoiceForCbPayment(invoice, centralPayPaymentRequest);
                                    break;
                                }
                    } else {
                        Payment payment = generateDepositOnCustomerOrderForCbPayment(customerOrder,
                                centralPayPaymentRequest);
                        accountingRecordService.generateBankAccountingRecordsForInboundPayment(payment);
                        unlockCustomerOrderFromDeposit(customerOrder);
                    }
                }
                return centralPayPaymentRequest.getPaymentRequestStatus().equals(CentralPayPaymentRequest.CLOSED)
                        || centralPayPaymentRequest.getPaymentRequestStatus()
                                .equals(CentralPayPaymentRequest.CANCELED);
            }
        }
        return true;
    }

    @Override
    public Payment generateDepositOnCustomerOrderForCbPayment(CustomerOrder customerOrder,
            CentralPayPaymentRequest centralPayPaymentRequest)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        customerOrder = getCustomerOrder(customerOrder.getId());
        // Generate payment to materialize CB payment
        Payment payment = getCentralPayPayment(centralPayPaymentRequest, true, null);

        Deposit deposit = depositService.getNewDepositForCustomerOrder(payment.getPaymentAmount(), LocalDateTime.now(),
                customerOrder, null, payment, true);

        deposit.setCustomerOrder(customerOrder);
        depositService.addOrUpdateDeposit(deposit);

        paymentService.cancelPayment(payment, constantService.getAccountingJournalBank());

        addOrUpdateCustomerOrder(customerOrder, false, true);

        accountingRecordService.generateAccountingRecordsForCentralPayPayment(centralPayPaymentRequest,
                payment, deposit, customerOrder, null);

        return payment;
    }

    private void generatePaymentOnInvoiceForCbPayment(Invoice invoice,
            CentralPayPaymentRequest centralPayPaymentRequest) throws OsirisException, OsirisClientMessageException {
        // Generate payment to materialize CB payment
        Payment payment = getCentralPayPayment(centralPayPaymentRequest, false, invoice);

        accountingRecordService.generateBankAccountingRecordsForInboundPayment(payment);
        accountingRecordService.generateAccountingRecordsForSaleOnInvoicePayment(invoice, payment);
        accountingRecordService.generateAccountingRecordsForCentralPayPayment(centralPayPaymentRequest, payment,
                null, invoice.getCustomerOrder(), invoice);
    }

    private Payment getCentralPayPayment(CentralPayPaymentRequest centralPayPaymentRequest, boolean isForDepostit,
            Invoice invoice)
            throws OsirisException {
        Payment payment = new Payment();
        payment.setIsExternallyAssociated(false);
        payment.setBankId(centralPayPaymentRequest.getPaymentRequestId());
        payment.setLabel(centralPayPaymentRequest.getDescription());
        payment.setPaymentAmount(centralPayPaymentRequest.getTotalAmount() / 100f);
        payment.setPaymentDate(centralPayPaymentRequest.getCreationDate());
        payment.setPaymentWay(constantService.getPaymentWayInbound());
        payment.setPaymentType(constantService.getPaymentTypeCB());
        payment.setIsCancelled(isForDepostit);
        payment.setInvoice(invoice);
        paymentService.addOrUpdatePayment(payment);
        return payment;
    }

    @Override
    @Transactional
    public void sendRemindersForCustomerOrderDeposit()
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
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
                    addOrUpdateCustomerOrder(customerOrder, false, true);
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
            File file = mailHelper.generateLetterPdf(customerOrders);

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
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        customerOrder.setAssignedTo(employee);
        addOrUpdateCustomerOrder(customerOrder, true, false);
    }

    @Override
    public List<OrderingSearchResult> searchByQuotationId(Integer idQuotation) {
        return customerOrderRepository.findCustomerOrders(
                Arrays.asList(0), Arrays.asList(0), Arrays.asList(0), LocalDateTime.now().minusYears(100),
                LocalDateTime.now().plusYears(100), Arrays.asList(0), Arrays.asList(0), idQuotation);
    }
}
