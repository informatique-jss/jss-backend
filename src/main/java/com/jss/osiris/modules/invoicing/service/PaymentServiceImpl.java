package com.jss.osiris.modules.invoicing.service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.ofx.OFXParser;
import com.jss.osiris.libs.ofx.OFXStatement;
import com.jss.osiris.libs.ofx.StatementTransaction;
import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.libs.search.service.SearchService;
import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.service.AccountingAccountService;
import com.jss.osiris.modules.accounting.service.AccountingRecordGenerationService;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.model.InvoiceSearch;
import com.jss.osiris.modules.invoicing.model.InvoiceSearchResult;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.PaymentSearch;
import com.jss.osiris.modules.invoicing.model.PaymentSearchResult;
import com.jss.osiris.modules.invoicing.model.Refund;
import com.jss.osiris.modules.invoicing.repository.PaymentRepository;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.BillingItem;
import com.jss.osiris.modules.miscellaneous.model.IGenericTiers;
import com.jss.osiris.modules.miscellaneous.service.BillingItemService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.miscellaneous.service.VatService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.BankTransfert;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.model.QuotationStatus;
import com.jss.osiris.modules.quotation.model.centralPay.CentralPayPaymentRequest;
import com.jss.osiris.modules.quotation.model.centralPay.CentralPayTransaction;
import com.jss.osiris.modules.quotation.service.AffaireService;
import com.jss.osiris.modules.quotation.service.BankTransfertService;
import com.jss.osiris.modules.quotation.service.CentralPayDelegateService;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.quotation.service.PricingHelper;
import com.jss.osiris.modules.quotation.service.ProvisionService;
import com.jss.osiris.modules.quotation.service.QuotationService;
import com.jss.osiris.modules.tiers.model.ITiers;
import com.jss.osiris.modules.tiers.model.Tiers;
import com.jss.osiris.modules.tiers.service.TiersService;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    SearchService searchService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    InvoiceHelper invoiceHelper;

    @Autowired
    AccountingRecordGenerationService accountingRecordGenerationService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    QuotationService quotationService;

    @Autowired
    InvoiceStatusService invoiceStatusService;

    @Autowired
    AccountingRecordService accountingRecordService;

    @Autowired
    RefundService refundService;

    @Autowired
    BankTransfertService bankTransfertService;

    @Autowired
    ConstantService constantService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    OFXParser ofxParser;

    @Autowired
    ProvisionService provisionService;

    @Autowired
    AccountingAccountService accountingAccountService;

    @Autowired
    TiersService tiersService;

    @Autowired
    AffaireService affaireService;

    @Autowired
    BillingItemService billingItemService;

    @Autowired
    PricingHelper pricingHelper;

    @Autowired
    CentralPayDelegateService centralPayDelegateService;

    @Autowired
    VatService vatService;

    @Value("${invoicing.payment.limit.refund.euros}")
    private String payementLimitRefundInEuros;

    @Autowired
    IndexEntityService indexEntityService;

    @Override
    public Payment getPayment(Integer id) {
        Optional<Payment> payment = paymentRepository.findById(id);
        if (payment.isPresent())
            return payment.get();
        return null;
    }

    @Override
    public Payment addOrUpdatePayment(
            Payment payment) {
        if (payment.getIsCancelled() == null)
            payment.setIsCancelled(false);
        if (payment.getIsAppoint() == null)
            payment.setIsAppoint(false);
        if (payment.getIsDeposit() == null)
            payment.setIsDeposit(false);
        if (payment.getIsExternallyAssociated() == null)
            payment.setIsExternallyAssociated(false);
        paymentRepository.save(payment);
        indexEntityService.indexEntity(payment);
        return payment;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reindexPayments() {
        List<Payment> payments = IterableUtils.toList(paymentRepository.findAll());
        if (payments != null)
            for (Payment payment : payments)
                indexEntityService.indexEntity(payment);
    }

    @Override
    public List<PaymentSearchResult> searchPayments(PaymentSearch paymentSearch) {
        if (paymentSearch.getStartDate() == null)
            paymentSearch.setStartDate(LocalDateTime.now().minusYears(100));

        if (paymentSearch.getEndDate() == null)
            paymentSearch.setEndDate(LocalDateTime.now().plusYears(100));

        return paymentRepository.findPayments(paymentSearch.getStartDate().withHour(0).withMinute(0),
                paymentSearch.getEndDate().withHour(23).withMinute(59), paymentSearch.getMinAmount(),
                paymentSearch.getMaxAmount(),
                paymentSearch.getLabel(), paymentSearch.isHideAssociatedPayments(),
                paymentSearch.isHideCancelledPayments());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void paymentGrab()
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        List<Payment> payments = paymentRepository.findNotAssociatedPayments();

        for (Payment payment : payments) {
            automatchPayment(payment);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Attachment> uploadOfxFile(InputStream file)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        OFXStatement operationList = ofxParser.parseOfx(file);

        if (operationList != null && operationList.getAccountStatements() != null
                && operationList.getAccountStatements().size() > 0
                && operationList.getAccountStatements().get(0) != null
                && operationList.getAccountStatements().get(0).getBankTransactionList() != null
                && operationList.getAccountStatements().get(0).getBankTransactionList().transactions() != null
                && operationList.getAccountStatements().get(0).getBankTransactionList().transactions().size() > 0)
            for (StatementTransaction transaction : operationList.getAccountStatements().get(0).getBankTransactionList()
                    .transactions()) {
                Payment existingTransaction = paymentRepository.findByBankId(transaction.id());
                if (existingTransaction == null) {
                    Payment payment = new Payment();
                    payment.setBankId(transaction.id());
                    payment.setIsExternallyAssociated(false);
                    payment.setLabel(transaction.name() + " " + transaction.memo());
                    payment.setPaymentAmount(transaction.amount().floatValue());
                    payment.setPaymentDate(transaction.datePosted().atStartOfDay());
                    payment.setPaymentType(constantService.getPaymentTypeVirement());
                    payment.setIsAppoint(false);
                    payment.setIsDeposit(false);
                    payment.setSourceAccountingAccount(constantService.getAccountingAccountBankJss());
                    payment.setTargetAccountingAccount(accountingAccountService.getWaitingAccountingAccount());
                    addOrUpdatePayment(payment);
                    if (transaction.amount().floatValue() > 0)
                        accountingRecordGenerationService.generateAccountingRecordOnIncomingPaymentCreation(payment,
                                false);
                    else
                        accountingRecordGenerationService.generateAccountingRecordOnOutgoingPaymentCreation(payment);
                }
                automatchPayment(
                        paymentRepository.findByBankId(transaction.id()));
            }

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void automatchPaymentFromUser(Payment payment)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        automatchPayment(payment);
    }

    @Override
    public void automatchPayment(Payment payment)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        // Match inbound payment
        payment = getPayment(payment.getId());
        if (payment.getPaymentAmount() >= 0) {
            // Get corresponding entities
            List<IndexEntity> correspondingEntities = getCorrespondingEntityForInboudPayment(payment);
            List<Invoice> correspondingInvoices = new ArrayList<Invoice>();
            List<CustomerOrder> correspondingCustomerOrder = new ArrayList<CustomerOrder>();
            List<Quotation> correspondingQuotation = new ArrayList<Quotation>();

            // Get invoices and customer orders and quotations
            Float totalToPay = 0f;
            ArrayList<Integer> foundInvoices = new ArrayList<Integer>();
            if (correspondingEntities != null && correspondingEntities.size() > 0) {
                for (IndexEntity foundEntity : correspondingEntities) {
                    Invoice invoice = getInvoiceForEntity(foundEntity);
                    if (invoice != null
                            && invoice.getInvoiceStatus().getId()
                                    .equals(constantService.getInvoiceStatusSend().getId())
                            && invoice.getProvider() == null && !foundInvoices.contains(invoice.getId())) {
                        foundInvoices.add(invoice.getId());
                        correspondingInvoices.add(invoice);
                        totalToPay += invoiceService.getRemainingAmountToPayForInvoice(invoice);
                    }
                    CustomerOrder customerOrder = getCustomerOrderAtNotBilledStatusForEntity(foundEntity);
                    if (customerOrder != null) {
                        correspondingCustomerOrder.add(customerOrder);
                        totalToPay += customerOrderService.getRemainingAmountToPayForCustomerOrder(customerOrder);
                    }
                    Quotation quotation = getQuotationForEntity(foundEntity);
                    if (quotation != null) {
                        correspondingQuotation.add(quotation);
                        totalToPay += customerOrderService.getTotalForCustomerOrder(quotation);
                    }
                }
            }

            Float remainingMoney = Math.round(payment.getPaymentAmount() * 100f) / 100f;

            // Associate automatically only if we have enough item to put all money
            // For customerOrder and quotation, we can put unlimited money on them
            Float totalItemsAmount = 0f;
            if (correspondingInvoices.size() > 0)
                for (Invoice invoice : correspondingInvoices)
                    totalItemsAmount += invoiceService.getRemainingAmountToPayForInvoice(invoice);

            if (correspondingInvoices.size() > 0
                    && totalItemsAmount > (remainingMoney + Integer.parseInt(payementLimitRefundInEuros))
                    && (correspondingCustomerOrder.size() == 0
                            || correspondingQuotation.size() == 0))
                return;

            // Invoices to payed found
            if (correspondingInvoices.size() > 0) {
                remainingMoney = associateInboundPaymentAndInvoices(payment, correspondingInvoices, null);
            }

            // Quotation waiting customer answer found
            // Transform them to customer order
            if (correspondingQuotation.size() > 0 && remainingMoney > 0) {
                for (Quotation quotation : correspondingQuotation) {
                    if (quotation.getQuotationStatus().getCode().equals(QuotationStatus.VALIDATED_BY_CUSTOMER)
                            && quotation.getCustomerOrders() != null && quotation.getCustomerOrders().size() > 0) {
                        boolean found = false;
                        // If already in correspondingCustomerOrder list, do not consider it
                        if (correspondingCustomerOrder.size() > 0)
                            for (CustomerOrder customerOrderFound : correspondingCustomerOrder)
                                if (customerOrderFound.getId().equals(quotation.getCustomerOrders().get(0).getId()))
                                    found = true;
                        if (!found)
                            correspondingCustomerOrder
                                    .add(quotation.getCustomerOrders().get(0));
                    } else if (quotation.getQuotationStatus().getCode().equals(QuotationStatus.SENT_TO_CUSTOMER)) {
                        Quotation validatedQuotation = quotationService.unlockQuotationFromDeposit(quotation);
                        correspondingCustomerOrder.add(validatedQuotation.getCustomerOrders().get(0));
                    }
                }
            }

            // Customer order waiting for deposit found
            if (correspondingCustomerOrder.size() > 0 && remainingMoney > 0) {
                associateInboundPaymentAndCustomerOrders(payment, correspondingCustomerOrder, correspondingInvoices,
                        null, remainingMoney);
            }
        } else {
            // Get corresponding entities
            List<IndexEntity> correspondingEntities = getCorrespondingEntityForOutboundPayment(payment);
            List<Invoice> correspondingInvoices = new ArrayList<Invoice>();

            Refund refundFound = null;
            // Try to match refunds
            if (correspondingEntities != null && correspondingEntities.size() > 0) {
                for (IndexEntity foundEntity : correspondingEntities) {
                    if (foundEntity.getEntityType().equals(Refund.class.getSimpleName())) {
                        Refund refund = refundService.getRefund(foundEntity.getEntityId());
                        if (refund != null && refund.getIsMatched() == false)
                            refundFound = refund;
                    }
                }
            }

            if (refundFound != null) {
                associateOutboundPaymentAndRefund(payment, refundFound);
                return;
            }

            BankTransfert bankTransfertFound = null;
            // Try to match bank transferts
            if (correspondingEntities != null && correspondingEntities.size() > 0) {
                for (IndexEntity foundEntity : correspondingEntities) {
                    if (foundEntity.getEntityType().equals(BankTransfert.class.getSimpleName())) {
                        BankTransfert bankTransfert = bankTransfertService.getBankTransfert(foundEntity.getEntityId());
                        if (bankTransfert != null && bankTransfert.getIsMatched() == false)
                            bankTransfertFound = bankTransfert;
                    }
                }
            }

            if (bankTransfertFound != null) {
                associateOutboundPaymentAndBankTransfert(payment, bankTransfertFound);
                return;
            }

            // Get invoices
            if (correspondingEntities != null && correspondingEntities.size() > 0) {
                for (IndexEntity foundEntity : correspondingEntities) {
                    Invoice invoice = getInvoiceForEntity(foundEntity);
                    if (invoice != null && invoice.getInvoiceStatus().getId()
                            .equals(constantService.getInvoiceStatusReceived().getId())) {
                        correspondingInvoices.add(invoice);
                        break;
                    }
                }
            }

            // Invoices to payed found
            if (correspondingInvoices.size() > 0) {
                associateOutboundPaymentAndInvoice(payment, correspondingInvoices.get(0));
            }

            // Match checks
            if (correspondingEntities != null && correspondingEntities.size() > 0) {
                for (IndexEntity foundEntity : correspondingEntities) {
                    if (foundEntity.getEntityType().equals(Payment.class.getSimpleName())) {
                        Payment foundPayment = getPayment(foundEntity.getEntityId());

                        if (foundPayment.getInvoice() != null)
                            associateOutboundCheckPayment(payment, foundPayment);
                    }
                }
            }

            // If not found and CB payment, try to match randomly a invoice at same day or
            // in 3 days range before
            if (payment.getLabel().contains("FACTURE CARTE")) {
                InvoiceSearch invoiceSearch = new InvoiceSearch();
                invoiceSearch.setStartDate(payment.getPaymentDate().toLocalDate().atTime(0, 0));
                invoiceSearch.setEndDate(payment.getPaymentDate().toLocalDate().atTime(23, 59));
                invoiceSearch.setInvoiceStatus(Arrays.asList(constantService.getInvoiceStatusReceived()));
                invoiceSearch.setMaxAmount(payment.getPaymentAmount());
                invoiceSearch.setMinAmount(payment.getPaymentAmount());
                List<InvoiceSearchResult> invoicesFound = invoiceService.searchInvoices(invoiceSearch);

                if (invoicesFound != null && invoicesFound.size() > 0)
                    for (InvoiceSearchResult invoiceFound : invoicesFound)
                        if (invoiceFound.getIdPaymentType().equals(constantService.getPaymentTypeCB().getId())) {
                            associateOutboundPaymentAndInvoice(payment, correspondingInvoices.get(0));
                            break;
                        }

                invoiceSearch.setStartDate(payment.getPaymentDate().toLocalDate().minusDays(1).atTime(0, 0));
                invoiceSearch.setEndDate(payment.getPaymentDate().toLocalDate().minusDays(1).atTime(23, 59));
                invoicesFound = invoiceService.searchInvoices(invoiceSearch);

                if (invoicesFound != null && invoicesFound.size() > 0)
                    for (InvoiceSearchResult invoiceFound : invoicesFound)
                        if (invoiceFound.getIdPaymentType().equals(constantService.getPaymentTypeCB().getId())) {
                            associateOutboundPaymentAndInvoice(payment, correspondingInvoices.get(0));
                            break;
                        }

                invoiceSearch.setStartDate(payment.getPaymentDate().toLocalDate().minusDays(2).atTime(0, 0));
                invoiceSearch.setEndDate(payment.getPaymentDate().toLocalDate().minusDays(2).atTime(23, 59));
                invoicesFound = invoiceService.searchInvoices(invoiceSearch);

                if (invoicesFound != null && invoicesFound.size() > 0)
                    for (InvoiceSearchResult invoiceFound : invoicesFound)
                        if (invoiceFound.getIdPaymentType().equals(constantService.getPaymentTypeCB().getId())) {
                            associateOutboundPaymentAndInvoice(payment, correspondingInvoices.get(0));
                            break;
                        }

                invoiceSearch.setStartDate(payment.getPaymentDate().toLocalDate().minusDays(3).atTime(0, 0));
                invoiceSearch.setEndDate(payment.getPaymentDate().toLocalDate().minusDays(3).atTime(23, 59));
                invoicesFound = invoiceService.searchInvoices(invoiceSearch);

                if (invoicesFound != null && invoicesFound.size() > 0)
                    for (InvoiceSearchResult invoiceFound : invoicesFound)
                        if (invoiceFound.getIdPaymentType().equals(constantService.getPaymentTypeCB().getId())) {
                            associateOutboundPaymentAndInvoice(payment, correspondingInvoices.get(0));
                            break;
                        }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void manualMatchPaymentInvoicesAndCustomerOrders(Payment payment, List<Invoice> correspondingInvoices,
            List<CustomerOrder> correspondingCustomerOrder, Affaire affaireRefund, Tiers tiersRefund,
            Confrere confrereRefund, ITiers tiersOrder,
            List<Float> byPassAmount) throws OsirisException, OsirisClientMessageException, OsirisValidationException {

        payment = getPayment(payment.getId());

        float remainingMoney = payment.getPaymentAmount();

        if (payment.getPaymentAmount() >= 0) {
            // Invoices to payed found
            if (correspondingInvoices != null && correspondingInvoices.size() > 0) {
                remainingMoney = associateInboundPaymentAndInvoices(payment, correspondingInvoices, byPassAmount);
            }

            // Customer order waiting for deposit found
            if (correspondingCustomerOrder != null && correspondingCustomerOrder.size() > 0) {
                remainingMoney = associateInboundPaymentAndCustomerOrders(payment, correspondingCustomerOrder,
                        correspondingInvoices, byPassAmount, remainingMoney);
            }

            if (remainingMoney > 0) {
                // Refund
                // Try to find a customerOrder for decorate the refund ...
                CustomerOrder customerOrder = null;
                if (correspondingCustomerOrder != null && correspondingCustomerOrder.size() > 0)
                    customerOrder = correspondingCustomerOrder.get(0);
                else if (correspondingInvoices != null && correspondingInvoices.size() > 0)
                    for (Invoice invoice : correspondingInvoices)
                        if (invoice.getCustomerOrder() != null)
                            customerOrder = invoice.getCustomerOrder();
                        else if (invoice.getCustomerOrderForInboundInvoice() != null)
                            customerOrder = invoice.getCustomerOrderForInboundInvoice();

                refundService.refundPayment(tiersRefund, affaireRefund, confrereRefund, tiersOrder, payment,
                        remainingMoney,
                        customerOrder);
            }
        } else {
            if (correspondingInvoices != null && correspondingInvoices.size() == 1)
                associateOutboundPaymentAndInvoice(payment, correspondingInvoices.get(0));
        }
    }

    private Float associateInboundPaymentAndCustomerOrders(Payment payment,
            List<CustomerOrder> correspondingCustomerOrder, List<Invoice> correspondingInvoice,
            List<Float> byPassAmount, float remainingMoney)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {

        int amountIndex = 0;
        if (correspondingInvoice != null)
            amountIndex = correspondingInvoice.size() - 1 + 1;

        // if no by pass, put all on last customer order even if there is too much
        // money
        for (int i = 0; i < correspondingCustomerOrder.size(); i++) {
            Float remainingToPayForCustomerOrder = Math.max(Math.round(
                    customerOrderService.getRemainingAmountToPayForCustomerOrder(correspondingCustomerOrder.get(i))
                            * 100f)
                    / 100f, 0);
            Float effectivePayment;
            if (byPassAmount != null) {
                effectivePayment = byPassAmount.get(amountIndex);
                amountIndex++;
            } else if (i == correspondingCustomerOrder.size() - 1) { // if last, put all on last customer order
                effectivePayment = remainingMoney;
            } else {
                effectivePayment = Math.min(remainingToPayForCustomerOrder, remainingMoney);
            }

            // Generate one deposit per customer order
            cancelPayment(payment);
            IGenericTiers tiers = invoiceHelper.getCustomerOrder(correspondingCustomerOrder.get(i));
            Payment newPayment = generateNewPaymentFromPayment(payment, effectivePayment, true,
                    tiers.getAccountingAccountDeposit());
            newPayment.setCustomerOrder(correspondingCustomerOrder.get(i));
            addOrUpdatePayment(newPayment);

            accountingRecordGenerationService.generateAccountingRecordsForSaleOnCustomerOrderDeposit(
                    correspondingCustomerOrder.get(i), newPayment);

            remainingMoney -= effectivePayment;

            // Unlocked customer order if necessary
            if (remainingToPayForCustomerOrder <= effectivePayment)
                customerOrderService.unlockCustomerOrderFromDeposit(correspondingCustomerOrder.get(i));

            if (Math.round(remainingMoney * 100f) / 100f == 0f)
                return 0f;
        }
        return Math.round(remainingMoney * 100f) / 100f;
    }

    private Float associateInboundPaymentAndInvoices(Payment payment, List<Invoice> correspondingInvoices,
            List<Float> byPassAmount) throws OsirisException, OsirisValidationException {
        int amountIndex = 0;
        Float remainingMoney = payment.getPaymentAmount();

        // If payment is not over total of remaining to pay on all invoices
        if (remainingMoney >= 0f) {
            for (int i = 0; i < correspondingInvoices.size(); i++) {
                if (correspondingInvoices.get(i).getInvoiceStatus().getId()
                        .equals(constantService.getInvoiceStatusSend().getId())) {
                    Float remainingToPayForCurrentInvoice = Math.round(
                            invoiceService.getRemainingAmountToPayForInvoice(correspondingInvoices.get(i)) * 100f)
                            / 100f;
                    Float effectivePayment = 0f;

                    if (byPassAmount != null) {
                        effectivePayment = byPassAmount.get(amountIndex);
                        amountIndex++;
                    } else {
                        effectivePayment = remainingMoney;
                    }

                    // If there is an appoint, use all remaining money, it's handled in
                    // associatePaymentAndInvoice method
                    if (i == correspondingInvoices.size() - 1 && Math.abs(remainingToPayForCurrentInvoice) > 0
                            && Math.abs(remainingToPayForCurrentInvoice - remainingMoney) <= Integer
                                    .parseInt(payementLimitRefundInEuros))
                        effectivePayment = remainingMoney;

                    cancelPayment(payment);
                    IGenericTiers tiers = invoiceHelper.getCustomerOrder(correspondingInvoices.get(i));
                    Payment newPayment = generateNewPaymentFromPayment(payment, effectivePayment, false,
                            tiers.getAccountingAccountCustomer());
                    newPayment.setInvoice(correspondingInvoices.get(i));
                    addOrUpdatePayment(newPayment);

                    // Check appoint only for last invoice
                    associatePaymentAndInvoice(newPayment, correspondingInvoices.get(i),
                            i == correspondingInvoices.size() - 1);
                    remainingMoney -= effectivePayment;

                } else if (correspondingInvoices.get(i).getInvoiceStatus().getId()
                        .equals(constantService.getInvoiceStatusCreditNoteReceived().getId())) {
                    if (Math.round(
                            invoiceService.getRemainingAmountToPayForInvoice(correspondingInvoices.get(i))
                                    * 100f) != (Math.round(payment
                                            .getPaymentAmount() * 100f)))
                        throw new OsirisException(null,
                                "Wrong amount to pay on invoice " + correspondingInvoices.get(i).getId()
                                        + " and payment bank id " + payment.getBankId() + " " + payment.getId());
                    // It's a provider refund
                    cancelPayment(payment);
                    IGenericTiers tiers = invoiceHelper.getCustomerOrder(correspondingInvoices.get(i));
                    Payment newPayment = generateNewPaymentFromPayment(payment, payment.getPaymentAmount(), false,
                            tiers.getAccountingAccountProvider());
                    newPayment.setInvoice(correspondingInvoices.get(i));
                    addOrUpdatePayment(newPayment);

                    associatePaymentAndInvoice(newPayment, correspondingInvoices.get(i), false);
                    remainingMoney = 0f;
                }
            }
        }
        return Math.round(remainingMoney * 100f) / 100f;
    }

    private Float associateOutboundPaymentAndInvoice(Payment payment, Invoice correspondingInvoice)
            throws OsirisException, OsirisValidationException {
        Float paymentAmount = Math.round(Math.abs(payment.getPaymentAmount()) * 100f) / 100f;
        Float invoiceAmount = Math
                .round(Math.abs(invoiceService.getRemainingAmountToPayForInvoice(correspondingInvoice)) * 100f) / 100f;

        if (paymentAmount.equals(invoiceAmount)) {
            cancelPayment(payment);
            Payment newPayment = generateNewPaymentFromPayment(payment, payment.getPaymentAmount(), false,
                    payment.getTargetAccountingAccount());
            IGenericTiers tiers = invoiceHelper.getCustomerOrder(correspondingInvoice);
            newPayment.setSourceAccountingAccount(tiers.getAccountingAccountProvider());
            newPayment.setTargetAccountingAccount(constantService.getAccountingAccountBankJss());
            associatePaymentAndInvoice(newPayment, correspondingInvoice, false);
        }

        return Math.round(paymentAmount * 100f) / 100f;
    }

    private void associateOutboundPaymentAndRefund(Payment payment, Refund refund) throws OsirisException {

        Float refundAmount = Math.round(refund.getRefundAmount() * 100f) / 100f;
        Float paymentAmount = Math.round(payment.getPaymentAmount() * 100f) / 100f;

        if (refundAmount.equals(-paymentAmount)) {
            refund.setIsMatched(true);
            refundService.addOrUpdateRefund(refund);
            payment.setRefund(refund);
            addOrUpdatePayment(payment);
        }
    }

    private void associateOutboundPaymentAndBankTransfert(Payment payment, BankTransfert bankTransfert)
            throws OsirisException, OsirisValidationException {

        Float bankTransfertAmount = Math.round(bankTransfert.getTransfertAmount() * 100f) / 100f;
        Float paymentAmount = Math.round(payment.getPaymentAmount() * 100f) / 100f;

        if (bankTransfertAmount.equals(-paymentAmount)) {
            bankTransfert.setIsMatched(true);
            bankTransfertService.addOrUpdateBankTransfert(bankTransfert);
            payment.setBankTransfert(bankTransfert);
            addOrUpdatePayment(payment);
            cancelPayment(payment);
        }
    }

    private void associateOutboundCheckPayment(Payment inPayment, Payment checkPayment)
            throws OsirisException, OsirisValidationException {
        Float inAmount = Math.round(inPayment.getPaymentAmount() * 100f) / 100f;
        Float checkAmount = Math.round(checkPayment.getPaymentAmount() * 100f) / 100f;

        if (inAmount.equals(checkAmount) && checkPayment.getInvoice() != null) {
            cancelPayment(inPayment);
            checkPayment.setOriginPayment(inPayment);
            addOrUpdatePayment(checkPayment);
        }
    }

    public void cancelAppoint(Payment payment) throws OsirisException, OsirisValidationException {
        cancelPayment(payment);
    }

    private Payment cancelPayment(Payment paymentToCancel) throws OsirisException, OsirisValidationException {
        if (paymentToCancel.getIsCancelled())
            return paymentToCancel;
        paymentToCancel.setIsCancelled(true);
        accountingRecordGenerationService.generateAccountingRecordOnPaymentCancellation(paymentToCancel);
        return addOrUpdatePayment(paymentToCancel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCashPaymentForCustomerInvoice(Payment cashPayment, Invoice invoice)
            throws OsirisException, OsirisValidationException {
        cashPayment.setTargetAccountingAccount(accountingAccountService.getWaitingAccountingAccount());
        cashPayment.setSourceAccountingAccount(constantService.getAccountingAccountCaisse());
        addOrUpdatePayment(cashPayment);
        accountingRecordGenerationService.generateAccountingRecordOnIncomingPaymentCreation(cashPayment, false);
        associateInboundPaymentAndInvoices(getPayment(cashPayment.getId()), Arrays.asList(invoice), null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addInboundCheckPayment(Payment checkPayment)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        checkPayment.setTargetAccountingAccount(accountingAccountService.getWaitingAccountingAccount());
        checkPayment.setSourceAccountingAccount(constantService.getAccountingAccountBankJss());
        addOrUpdatePayment(checkPayment);
        accountingRecordGenerationService.generateAccountingRecordOnIncomingPaymentCreation(checkPayment, false);
        automatchPayment(checkPayment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Payment addOutboundPaymentForProvision(Payment payment, Provision provision)
            throws OsirisException, OsirisValidationException {
        payment.setProvision(provision);
        payment.setTargetAccountingAccount(accountingAccountService.getWaitingAccountingAccount());
        payment.setSourceAccountingAccount(
                payment.getPaymentType().getId().equals(constantService.getPaymentTypeEspeces().getId())
                        ? constantService.getAccountingAccountCaisse()
                        : constantService.getAccountingAccountBankJss());
        addOrUpdatePayment(payment);
        accountingRecordGenerationService.generateAccountingRecordOnOutgoingPaymentCreation(payment);
        return payment;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCashPaymentForCustomerOrder(Payment cashPayment, CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        cashPayment.setSourceAccountingAccount(constantService.getAccountingAccountCaisse());
        cashPayment.setTargetAccountingAccount(accountingAccountService.getWaitingAccountingAccount());
        addOrUpdatePayment(cashPayment);
        accountingRecordGenerationService.generateAccountingRecordOnIncomingPaymentCreation(cashPayment, false);
        associateInboundPaymentAndCustomerOrders(getPayment(cashPayment.getId()), Arrays.asList(customerOrder),
                new ArrayList<Invoice>(), null, cashPayment.getPaymentAmount());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refundPayment(Payment payment, Tiers tiers, Affaire affaire)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        tiers = tiersService.getTiers(tiers.getId());
        if (affaire != null)
            affaire = affaireService.getAffaire(affaire.getId());
        payment = getPayment(payment.getId());
        refundService.refundPayment(tiers, affaire, null, tiers, payment, payment.getPaymentAmount(), null);
    }

    private Payment generateNewPaymentFromPayment(Payment payment, Float paymentAmount, Boolean isDeposit,
            AccountingAccount targetAccountingAccount)
            throws OsirisException {
        if (!payment.getIsCancelled())
            throw new OsirisException(null, "Payment n°" + payment.getId() + " must be cancelled before clonning !");

        Payment newPayment = new Payment();
        newPayment.setIsAppoint(false);
        newPayment.setIsDeposit(isDeposit);
        newPayment.setIsCancelled(false);
        newPayment.setIsExternallyAssociated(false);
        newPayment.setOriginPayment(payment);
        newPayment.setPaymentAmount(paymentAmount);
        newPayment.setPaymentDate(LocalDateTime.now());
        newPayment.setPaymentType(payment.getPaymentType());
        newPayment.setLabel(payment.getLabel());

        if (targetAccountingAccount == null)
            throw new OsirisException(null, "No target accounting account defined for payment n°" + payment.getId());

        if (newPayment.getPaymentAmount() > 0) {
            newPayment.setSourceAccountingAccount(payment.getSourceAccountingAccount());
            newPayment.setTargetAccountingAccount(targetAccountingAccount);
        } else {
            newPayment.setTargetAccountingAccount(payment.getTargetAccountingAccount());
            newPayment.setSourceAccountingAccount(targetAccountingAccount);
        }

        return addOrUpdatePayment(newPayment);
    }

    private Payment generateNewAppointPayment(Float paymentAmount, IGenericTiers tiersToGiveAppoint, Invoice invoice)
            throws OsirisException {
        Payment newPayment = new Payment();
        newPayment.setIsAppoint(true);
        newPayment.setIsDeposit(false);
        newPayment.setIsCancelled(false);
        newPayment.setIsExternallyAssociated(false);
        newPayment.setOriginPayment(null);
        newPayment.setPaymentAmount(-paymentAmount);
        newPayment.setLabel("Appoint pour la facture " + invoice.getId());
        newPayment.setPaymentDate(LocalDateTime.now());
        newPayment.setPaymentType(constantService.getPaymentTypeAccount());
        if (newPayment.getPaymentAmount() > 0) {
            newPayment.setSourceAccountingAccount(tiersToGiveAppoint.getAccountingAccountCustomer());
            newPayment.setTargetAccountingAccount(accountingAccountService.getProfitAccountingAccount());
        } else {
            newPayment.setSourceAccountingAccount(accountingAccountService.getLostAccountingAccount());
            newPayment.setTargetAccountingAccount(tiersToGiveAppoint.getAccountingAccountCustomer());
        }

        return addOrUpdatePayment(newPayment);
    }

    @Override
    public Payment generateNewRefundPayment(Refund refund, Float paymentAmount, ITiers tiersToRefund,
            Payment paymentToRefund)
            throws OsirisException {
        Payment newPayment = new Payment();
        newPayment.setIsAppoint(false);
        newPayment.setIsDeposit(false);
        newPayment.setIsCancelled(false);
        newPayment.setIsExternallyAssociated(false);
        newPayment.setOriginPayment(null);
        newPayment.setPaymentAmount(paymentAmount);
        newPayment.setPaymentDate(LocalDateTime.now());
        newPayment.setLabel(refund.getLabel());
        newPayment.setRefund(refund);
        newPayment.setOriginPayment(paymentToRefund);
        newPayment.setPaymentType(constantService.getPaymentTypeVirement());
        newPayment.setTargetAccountingAccount(tiersToRefund.getAccountingAccountCustomer());
        newPayment.setSourceAccountingAccount(constantService.getAccountingAccountBankJss());

        return addOrUpdatePayment(newPayment);
    }

    @Override
    public Payment generateNewBankTransfertPayment(BankTransfert bankTransfert, Float paymentAmount,
            IGenericTiers tiersToPay)
            throws OsirisException {
        Payment newPayment = new Payment();
        newPayment.setIsAppoint(false);
        newPayment.setIsDeposit(false);
        newPayment.setIsCancelled(false);
        newPayment.setIsExternallyAssociated(false);
        newPayment.setOriginPayment(null);
        newPayment.setPaymentAmount(paymentAmount);
        newPayment.setPaymentDate(LocalDateTime.now());
        newPayment.setLabel(bankTransfert.getLabel());
        newPayment.setBankTransfert(bankTransfert);
        newPayment.setPaymentType(constantService.getPaymentTypeVirement());
        newPayment.setSourceAccountingAccount(constantService.getAccountingAccountBankJss());
        newPayment.setTargetAccountingAccount(tiersToPay.getAccountingAccountProvider());

        return addOrUpdatePayment(newPayment);
    }

    @Override
    public Payment generateNewAccountPayment(Float paymentAmount, AccountingAccount targetAccountingAccount,
            String label)
            throws OsirisException {
        Payment newPayment = new Payment();
        newPayment.setIsAppoint(false);
        newPayment.setIsDeposit(false);
        newPayment.setLabel(label);
        newPayment.setIsCancelled(false);
        newPayment.setIsExternallyAssociated(false);
        newPayment.setOriginPayment(null);
        newPayment.setPaymentAmount(paymentAmount);
        newPayment.setPaymentDate(LocalDateTime.now());
        newPayment.setPaymentType(constantService.getPaymentTypeAccount());
        newPayment.setSourceAccountingAccount(constantService.getAccountingAccountBankJss());
        newPayment.setTargetAccountingAccount(targetAccountingAccount);

        return addOrUpdatePayment(newPayment);
    }

    private void associatePaymentAndInvoice(Payment payment, Invoice invoice, boolean checkForAppoint)
            throws OsirisException, OsirisValidationException {
        invoice = invoiceService.getInvoice(invoice.getId());
        payment.setInvoice(invoice);
        if (invoice.getPayments() == null)
            invoice.setPayments(new ArrayList<Payment>());
        invoice.getPayments().add(payment);

        addOrUpdatePayment(payment);

        if (payment.getPaymentAmount() >= 0 || payment.getIsAppoint()) {
            if (invoice.getIsProviderCreditNote()) {
                accountingRecordGenerationService.generateAccountingRecordsForProviderInvoiceRefund(invoice, payment);
            } else {
                accountingRecordGenerationService.generateAccountingRecordsForSaleOnInvoicePayment(invoice, payment);

                Float remainingToPayForCurrentInvoice = invoiceService.getRemainingAmountToPayForInvoice(invoice);
                // Handle appoint
                if (checkForAppoint && Math.abs(remainingToPayForCurrentInvoice) > 0
                        && Math.abs(remainingToPayForCurrentInvoice) <= Integer.parseInt(payementLimitRefundInEuros)) {
                    Payment appoint = generateNewAppointPayment(remainingToPayForCurrentInvoice,
                            invoiceHelper.getCustomerOrder(invoice), invoice);

                    associatePaymentAndInvoice(appoint, invoice, false);
                }
            }
        } else {
            accountingRecordGenerationService.generateAccountingRecordsForPurschaseOnInvoicePayment(invoice, payment);
        }
    }

    @Override
    public void movePaymentFromInvoiceToCustomerOrder(Payment payment, Invoice invoice, CustomerOrder customerOrder)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        associateInboundPaymentAndCustomerOrders(payment, Arrays.asList(customerOrder), null,
                Arrays.asList(payment.getPaymentAmount()), payment.getPaymentAmount());
    }

    @Override
    public void movePaymentFromCustomerOrderToInvoice(Payment payment, CustomerOrder customerOrder, Invoice invoice)
            throws OsirisException, OsirisValidationException {
        associateInboundPaymentAndInvoices(payment, Arrays.asList(invoice), null);
    }

    private void generateInvoiceForCentralPayPayment(CentralPayPaymentRequest centralPayPaymentRequest,
            Payment payment, Invoice targetInvoice, CustomerOrder targetCustomerOrder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {

        Invoice invoice = new Invoice();

        invoice.setIsCreditNote(false);
        invoice.setIsProviderCreditNote(false);
        invoice.setIsInvoiceFromProvider(true);
        invoice.setProvider(constantService.getProviderCentralPay());
        invoice.setInvoiceItems(new ArrayList<InvoiceItem>());

        List<BillingItem> centralPayBillingItem = billingItemService
                .getBillingItemByBillingType(constantService.getBillingTypeCentralPayFees());

        InvoiceItem invoiceItem = new InvoiceItem();
        invoiceItem.setBillingItem(pricingHelper.getAppliableBillingItem(centralPayBillingItem));
        invoiceItem.setDiscountAmount(0f);
        invoiceItem.setIsGifted(false);
        invoiceItem.setIsOverridePrice(false);

        if (targetCustomerOrder == null && targetInvoice == null)
            throw new OsirisException(null,
                    "No target invoice and customer order set for central pay invoice generation");

        String invoiceLabel = "";
        if (targetCustomerOrder != null) {
            invoiceLabel = "Commission CentralPay - Commande n°" + targetCustomerOrder.getId();
            invoiceItem.setLabel(
                    "Commission CentralPay - Paiement d'acompte pour la commande n°" + targetCustomerOrder.getId());
        } else {
            invoiceLabel = "Commission CentralPay - Facture n°" + targetInvoice.getId();
            invoiceItem.setLabel("Commission CentralPay - Paiement pour la facture " + targetInvoice.getId());
        }

        CentralPayTransaction transaction = centralPayDelegateService.getTransaction(centralPayPaymentRequest);
        Float commission = (transaction.getCommission() != null ? transaction.getCommission() : 0f) / 100f;

        invoiceItem.setPreTaxPrice(Math.round(commission * 100f) / 100f);
        invoiceItem.setPreTaxPriceReinvoiced(invoiceItem.getPreTaxPrice());
        invoice.getInvoiceItems().add(invoiceItem);
        vatService.completeVatOnInvoiceItem(invoiceItem, invoice);

        Invoice centralPayInvoice = invoiceService.addOrUpdateInvoiceFromUser(invoice);
        Payment centralPayPayment = generateNewAccountPayment(
                -(invoiceItem.getPreTaxPrice() + invoiceItem.getVatPrice()),
                constantService.getProviderCentralPay().getAccountingAccountProvider(), invoiceLabel);
        accountingRecordGenerationService.generateAccountingRecordOnOutgoingPaymentCreation(centralPayPayment);

        associateOutboundPaymentAndInvoice(centralPayPayment, centralPayInvoice);
    }

    @Override
    public Payment generateDepositOnCustomerOrderForCbPayment(CustomerOrder customerOrder,
            CentralPayPaymentRequest centralPayPaymentRequest)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        customerOrder = customerOrderService.getCustomerOrder(customerOrder.getId());
        // Generate payment to materialize CB payment
        Payment payment = generateCentralPayPayment(centralPayPaymentRequest, true, null);
        generateInvoiceForCentralPayPayment(centralPayPaymentRequest, payment, null, customerOrder);
        associateInboundPaymentAndCustomerOrders(payment, Arrays.asList(customerOrder), null, null,
                payment.getPaymentAmount());
        return payment;
    }

    @Override
    public void generatePaymentOnInvoiceForCbPayment(Invoice invoice,
            CentralPayPaymentRequest centralPayPaymentRequest)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        // Generate payment to materialize CB payment
        Payment payment = generateCentralPayPayment(centralPayPaymentRequest, true, null);
        generateInvoiceForCentralPayPayment(centralPayPaymentRequest, payment, invoice, null);
        associateInboundPaymentAndInvoices(payment, Arrays.asList(invoice), null);
    }

    @Override
    public void unassociateInboundPaymentFromInvoice(Payment payment, Invoice invoice)
            throws OsirisException, OsirisValidationException {
        cancelPayment(payment);
        Payment newPayment = generateNewPaymentFromPayment(payment, payment.getPaymentAmount(), false,
                accountingAccountService.getWaitingAccountingAccount());
        newPayment.setTargetAccountingAccount(accountingAccountService.getWaitingAccountingAccount());
        addOrUpdatePayment(newPayment);
        accountingRecordGenerationService.generateAccountingRecordOnIncomingPaymentCreation(newPayment, true);
    }

    private Payment generateCentralPayPayment(CentralPayPaymentRequest centralPayPaymentRequest, boolean isForDepostit,
            Invoice invoice)
            throws OsirisException {
        Payment payment = new Payment();
        payment.setIsExternallyAssociated(false);
        payment.setBankId(centralPayPaymentRequest.getPaymentRequestId());
        payment.setLabel(centralPayPaymentRequest.getDescription());
        payment.setPaymentAmount(centralPayPaymentRequest.getTotalAmount() / 100f);
        payment.setPaymentDate(centralPayPaymentRequest.getCreationDate());
        payment.setPaymentType(constantService.getPaymentTypeCB());
        payment.setIsCancelled(isForDepostit);
        payment.setInvoice(invoice);
        payment.setSourceAccountingAccount(constantService.getAccountingAccountBankCentralPay());
        payment.setTargetAccountingAccount(accountingAccountService.getWaitingAccountingAccount());
        addOrUpdatePayment(payment);
        return payment;
    }

    private List<IndexEntity> getCorrespondingEntityForOutboundPayment(Payment payment) {
        ArrayList<String> entityTypesToSearch = new ArrayList<String>();
        entityTypesToSearch.add(Invoice.class.getSimpleName());
        entityTypesToSearch.add(Refund.class.getSimpleName());
        entityTypesToSearch.add(BankTransfert.class.getSimpleName());
        entityTypesToSearch.add(Payment.class.getSimpleName());

        return getCorrespondingEntityForPayment(payment, entityTypesToSearch);
    }

    private List<IndexEntity> getCorrespondingEntityForInboudPayment(Payment payment) {
        ArrayList<String> entityTypesToSearch = new ArrayList<String>();
        entityTypesToSearch.add(Invoice.class.getSimpleName());
        entityTypesToSearch.add(CustomerOrder.class.getSimpleName());
        entityTypesToSearch.add(Quotation.class.getSimpleName());

        return getCorrespondingEntityForPayment(payment, entityTypesToSearch);
    }

    private List<IndexEntity> getCorrespondingEntityForPayment(Payment payment, ArrayList<String> entityTypesToSearch) {
        Pattern p = Pattern.compile("\\d+");
        List<IndexEntity> entitiesFound = new ArrayList<IndexEntity>();

        // Find possible references
        if (payment.getLabel() != null) {
            Matcher m = p.matcher(payment.getLabel());
            while (m.find()) {
                Integer idToFind = null;
                List<IndexEntity> tmpEntitiesFound = null;
                try {
                    idToFind = Integer.parseInt(m.group());
                } catch (NumberFormatException e) {
                }

                if (idToFind != null) {
                    tmpEntitiesFound = searchService.searchForEntitiesById(idToFind, entityTypesToSearch);

                    Invoice directDebitTransfertInvoice = invoiceService
                            .searchInvoicesByIdDirectDebitTransfert(idToFind);
                    if (directDebitTransfertInvoice != null)
                        tmpEntitiesFound.addAll(searchService.searchForEntitiesById(directDebitTransfertInvoice.getId(),
                                Arrays.asList(Invoice.class.getSimpleName())));
                }
                if (tmpEntitiesFound != null && tmpEntitiesFound.size() > 0) {
                    for (IndexEntity newEntity : tmpEntitiesFound) {
                        boolean found = false;
                        for (IndexEntity entityAlreadyFound : entitiesFound) {
                            if (entityAlreadyFound.getEntityId().equals(newEntity.getEntityId())
                                    && entityAlreadyFound.getEntityType().equals(newEntity.getEntityType())) {
                                found = true;
                                break;
                            }
                        }
                        if (!found)
                            entitiesFound.add(newEntity);
                    }
                }
            }
        }
        return entitiesFound;
    }

    private Invoice getInvoiceForEntity(IndexEntity foundEntity) throws OsirisException {
        if (foundEntity != null && foundEntity.getEntityType() != null) {
            if (foundEntity.getEntityType().equals(Invoice.class.getSimpleName())) {
                Invoice invoice = invoiceService.getInvoice(foundEntity.getEntityId());

                if (invoice.getInvoiceStatus() == null)
                    throw new OsirisException(null,
                            "No invoice status found for entity n°" + foundEntity.getEntityId());

                if (invoice.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusSend().getId()) || invoice
                        .getInvoiceStatus().getId().equals(constantService.getInvoiceStatusReceived().getId()))
                    return invoice;
            }
            if (foundEntity.getEntityType().equals(CustomerOrder.class.getSimpleName())) {
                List<InvoiceSearchResult> invoices = invoiceService
                        .getInvoiceForCustomerOrder(foundEntity.getEntityId());
                if (invoices != null && invoices.size() > 0)
                    for (InvoiceSearchResult invoice : invoices) {
                        Invoice completeInvoice = invoiceService.getInvoice(invoice.getInvoiceId());
                        if (completeInvoice.getInvoiceStatus().getId()
                                .equals(constantService.getInvoiceStatusSend().getId()))
                            return completeInvoice;
                    }
            }
            if (foundEntity.getEntityType().equals(Quotation.class.getSimpleName())) {
                Quotation quotation = quotationService.getQuotation(foundEntity.getEntityId());
                if (quotation != null && quotation.getCustomerOrders() != null
                        && quotation.getCustomerOrders().size() > 0) {
                    for (CustomerOrder customerOrder : quotation.getCustomerOrders()) {
                        List<InvoiceSearchResult> invoices = invoiceService
                                .getInvoiceForCustomerOrder(customerOrder.getId());
                        if (invoices != null && invoices.size() > 0)
                            for (InvoiceSearchResult invoice : invoices) {
                                Invoice completeInvoice = invoiceService.getInvoice(invoice.getInvoiceId());
                                if (completeInvoice.getInvoiceStatus().getId()
                                        .equals(constantService.getInvoiceStatusSend().getId()))
                                    return completeInvoice;
                            }
                    }
                }
            }
        }
        return null;
    }

    private CustomerOrder getCustomerOrderAtNotBilledStatusForEntity(IndexEntity foundEntity) {
        if (foundEntity != null && foundEntity.getEntityType() != null) {
            if (foundEntity.getEntityType().equals(CustomerOrder.class.getSimpleName())) {
                CustomerOrder customerOrder = customerOrderService.getCustomerOrder(foundEntity.getEntityId());
                if (customerOrder.getCustomerOrderStatus() != null
                        && (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.WAITING_DEPOSIT)
                                || customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.OPEN)
                                || customerOrder.getCustomerOrderStatus().getCode()
                                        .equals(CustomerOrderStatus.BEING_PROCESSED)
                                || customerOrder.getCustomerOrderStatus().getCode()
                                        .equals(CustomerOrderStatus.TO_BILLED)))
                    return customerOrder;
            }
        }
        return null;
    }

    private Quotation getQuotationForEntity(IndexEntity foundEntity) {
        if (foundEntity != null && foundEntity.getEntityType() != null) {
            if (foundEntity.getEntityType().equals(Quotation.class.getSimpleName())) {
                Quotation quotation = quotationService.getQuotation(foundEntity.getEntityId());
                if (quotation.getQuotationStatus() != null
                        && (quotation.getQuotationStatus().getCode().equals(QuotationStatus.SENT_TO_CUSTOMER)
                                || quotation.getQuotationStatus().getCode()
                                        .equals(QuotationStatus.VALIDATED_BY_CUSTOMER)))
                    return quotation;
            }
        }
        return null;
    }

    @Override
    @Transactional
    public List<Payment> getAdvisedPaymentForInvoice(Invoice invoice) {
        invoice = invoiceService.getInvoice(invoice.getId());
        List<Payment> payments = paymentRepository.findNotAssociatedPayments();
        List<Payment> advisedPayments = new ArrayList<Payment>();
        if (payments != null && payments.size() > 0) {
            Pattern p = Pattern.compile("\\d+");
            for (Payment payment : payments) {
                if (payment.getLabel() != null) {
                    Matcher m = p.matcher(payment.getLabel());
                    while (m.find()) {
                        if (m.group().equals(invoice.getId().toString()) && !advisedPayments.contains(payment))
                            advisedPayments.add(payment);
                        else if (invoice.getCustomerOrder() != null
                                && m.group().equals(invoice.getCustomerOrder().getId().toString())
                                && !advisedPayments.contains(payment))
                            advisedPayments.add(payment);
                        else if (invoice.getCustomerOrder() != null
                                && invoice.getCustomerOrder().getQuotations() != null
                                && invoice.getCustomerOrder().getQuotations().size() > 0 && m.group()
                                        .equals(invoice.getCustomerOrder().getQuotations().get(0).getId().toString())
                                && !advisedPayments.contains(payment))
                            advisedPayments.add(payment);
                    }
                }
            }
            // If no match by name, attempt by amount
            if (advisedPayments.size() == 0) {
                Float totalRound = Math.round(invoice.getTotalPrice() * 100f) / 100f;
                for (Payment payment : payments) {
                    Float paymentRound = Math.round(payment.getPaymentAmount() * 100f) / 100f;
                    if (invoice.getIsInvoiceFromProvider() && paymentRound.equals(-totalRound))
                        advisedPayments.add(payment);
                    else if (invoice.getIsCreditNote() && paymentRound.equals(-totalRound))
                        advisedPayments.add(payment);
                    else if (paymentRound.equals(-totalRound))
                        advisedPayments.add(payment);
                }
            }
        }
        return advisedPayments;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Payment> getAdvisedPaymentForCustomerOrder(CustomerOrder customerOrder) {
        customerOrder = customerOrderService.getCustomerOrder(customerOrder.getId());
        List<Payment> payments = paymentRepository.findNotAssociatedPayments();
        List<Payment> advisedPayments = new ArrayList<Payment>();
        if (payments != null && payments.size() > 0) {
            Pattern p = Pattern.compile("\\d+");
            for (Payment payment : payments) {
                if (payment.getLabel() != null) {
                    Matcher m = p.matcher(payment.getLabel());
                    while (m.find()) {
                        if (m.group().equals(customerOrder.getId().toString()) && !advisedPayments.contains(payment))
                            advisedPayments.add(payment);
                        else if (customerOrder.getQuotations() != null && customerOrder.getQuotations().size() > 0
                                && m.group().equals(customerOrder.getQuotations().get(0).getId().toString())
                                && !advisedPayments.contains(payment))
                            advisedPayments.add(payment);
                    }
                }
            }
            // If no match by name, attempt by amount
            if (advisedPayments.size() == 0) {
                Float totalRound = customerOrderService.getTotalForCustomerOrder(customerOrder);
                for (Payment payment : payments) {
                    Float paymentRound = Math.round(payment.getPaymentAmount() * 100f) / 100f;
                    if (paymentRound.equals(totalRound)) {
                        advisedPayments.add(payment);
                    }
                }
            }
        }
        return advisedPayments;
    }

    @Override
    public Payment getOriginalPaymentOfPayment(Payment payment) {
        if (payment.getOriginPayment() == null)
            return payment;

        payment = payment.getOriginPayment();
        return getOriginalPaymentOfPayment(payment);
    }
}
