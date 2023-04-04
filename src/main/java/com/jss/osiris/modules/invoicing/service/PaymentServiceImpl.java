package com.jss.osiris.modules.invoicing.service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.mutable.MutableBoolean;
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
import com.jss.osiris.libs.search.service.SearchService;
import com.jss.osiris.modules.accounting.model.AccountingJournal;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.model.InvoiceSearchResult;
import com.jss.osiris.modules.invoicing.model.InvoiceStatus;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.PaymentSearch;
import com.jss.osiris.modules.invoicing.model.PaymentSearchResult;
import com.jss.osiris.modules.invoicing.model.PaymentWay;
import com.jss.osiris.modules.invoicing.model.Refund;
import com.jss.osiris.modules.invoicing.repository.PaymentRepository;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.model.Debour;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.model.QuotationStatus;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.quotation.service.DebourService;
import com.jss.osiris.modules.quotation.service.ProvisionService;
import com.jss.osiris.modules.quotation.service.QuotationService;
import com.jss.osiris.modules.tiers.model.ITiers;

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
    CustomerOrderService customerOrderService;

    @Autowired
    QuotationService quotationService;

    @Autowired
    InvoiceStatusService invoiceStatusService;

    @Autowired
    AccountingRecordService accountingRecordService;

    @Autowired
    DepositService depositService;

    @Autowired
    RefundService refundService;

    @Autowired
    DebourService debourService;

    @Autowired
    ConstantService constantService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    OFXParser ofxParser;

    @Autowired
    ProvisionService provisionService;

    @Value("${invoicing.payment.limit.refund.euros}")
    private String payementLimitRefundInEuros;

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
        return paymentRepository.save(payment);
    }

    @Override
    public List<PaymentSearchResult> searchPayments(PaymentSearch paymentSearch) {
        ArrayList<Integer> paymentWayId = new ArrayList<Integer>();
        if (paymentSearch.getPaymentWays() != null) {
            for (PaymentWay paymentWay : paymentSearch.getPaymentWays())
                paymentWayId.add(paymentWay.getId());
        } else {
            paymentWayId.add(0);
        }

        if (paymentSearch.getStartDate() == null)
            paymentSearch.setStartDate(LocalDateTime.now().minusYears(100));

        if (paymentSearch.getEndDate() == null)
            paymentSearch.setEndDate(LocalDateTime.now().plusYears(100));

        return paymentRepository.findPayments(paymentWayId,
                paymentSearch.getStartDate().withHour(0).withMinute(0),
                paymentSearch.getEndDate().withHour(23).withMinute(59), paymentSearch.getMinAmount(),
                paymentSearch.getMaxAmount(),
                paymentSearch.getLabel(), paymentSearch.isHideAssociatedPayments());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payementGrab()
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        automatchPaymentsInvoicesAndGeneratePaymentAccountingRecords();
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
                    payment.setPaymentAmount(Math.abs(transaction.amount().floatValue()));
                    payment.setPaymentDate(transaction.datePosted().atStartOfDay());
                    payment.setPaymentWay(transaction.amount().floatValue() > 0 ? constantService.getPaymentWayInbound()
                            : constantService.getPaymentWayOutboud());
                    payment.setPaymentType(constantService.getPaymentTypeVirement());
                    addOrUpdatePayment(payment);
                }
            }
        automatchPaymentsInvoicesAndGeneratePaymentAccountingRecords();
        return null;
    }

    private void automatchPaymentsInvoicesAndGeneratePaymentAccountingRecords()
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        List<Payment> payments = paymentRepository.findNotAssociatedPayments();

        for (Payment payment : payments) {
            automatchPaymentInvoicesAndGeneratePaymentAccountingRecords(payment);
        }
    }

    private void automatchPaymentInvoicesAndGeneratePaymentAccountingRecords(Payment payment)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        // Match inbound payment
        if (payment.getPaymentWay().getId().equals(constantService.getPaymentWayInbound().getId())) {
            // Get corresponding entities
            List<IndexEntity> correspondingEntities = getCorrespondingEntityForInboudPayment(payment);
            List<Invoice> correspondingInvoices = new ArrayList<Invoice>();
            List<CustomerOrder> correspondingCustomerOrder = new ArrayList<CustomerOrder>();
            List<Quotation> correspondingQuotation = new ArrayList<Quotation>();

            // Use mutable to get value from children method
            MutableBoolean generateWaitingAccountAccountingRecords = new MutableBoolean(true);
            if (payment.getAccountingRecords() != null && payment.getAccountingRecords().size() > 0) {
                for (AccountingRecord record : payment.getAccountingRecords())
                    if (record.getIsCounterPart() == null || record.getIsCounterPart() == false)
                        generateWaitingAccountAccountingRecords.setValue(false);
            }

            if (generateWaitingAccountAccountingRecords.getValue())
                accountingRecordService.generateBankAccountingRecordsForInboundPayment(payment);

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
                    CustomerOrder customerOrder = getCustomerOrderAtDepositStatusForEntity(foundEntity);
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

            // If invoice and pending customer orders found, do nothing => to complicated to
            // manage automaticaly
            if (correspondingInvoices.size() > 0 && correspondingCustomerOrder.size() > 0
                    || correspondingInvoices.size() > 0 && correspondingQuotation.size() > 0
                    || correspondingCustomerOrder.size() > 0 && correspondingQuotation.size() > 0) {
                // If payment not used, put it in waiting account
                if (generateWaitingAccountAccountingRecords.getValue())
                    accountingRecordService.generateAccountingRecordsForWaitingInboundPayment(payment);
                return;
            }

            Float remainingMoney = payment.getPaymentAmount();

            // Invoices to payed found
            if (correspondingInvoices.size() > 0) {
                remainingMoney = associateInboundPaymentAndInvoices(payment, correspondingInvoices,
                        generateWaitingAccountAccountingRecords,
                        null);
            }

            // Quotation waiting customer answer found
            // Transform them to customer order
            if (correspondingQuotation.size() > 0 && remainingMoney > 0) {
                for (Quotation quotation : correspondingQuotation) {
                    if (quotation.getQuotationStatus().getCode().equals(QuotationStatus.VALIDATED_BY_CUSTOMER)
                            && quotation.getCustomerOrders() != null && quotation.getCustomerOrders().size() > 0) {
                        boolean found = false;
                        if (correspondingCustomerOrder.size() > 0)
                            for (CustomerOrder customerOrderFound : correspondingCustomerOrder)
                                if (customerOrderFound.getId().equals(quotation.getCustomerOrders().get(0).getId()))
                                    found = true;
                        if (!found)
                            correspondingCustomerOrder
                                    .add(quotation.getCustomerOrders().get(0));
                    } else if (quotation.getQuotationStatus().getCode().equals(QuotationStatus.SENT_TO_CUSTOMER)) {
                        correspondingCustomerOrder
                                .add(quotationService
                                        .addOrUpdateQuotationStatus(quotation,
                                                QuotationStatus.VALIDATED_BY_CUSTOMER)
                                        .getCustomerOrders().get(0));
                    }
                }
            }

            // Customer order waiting for deposit found
            if (correspondingCustomerOrder.size() > 0 && remainingMoney > 0) {
                associateInboundPaymentAndCustomerOrders(payment, correspondingCustomerOrder, correspondingInvoices,
                        generateWaitingAccountAccountingRecords, null, payment.getPaymentAmount());
                cancelPayment(payment, constantService.getAccountingJournalBank());
            }

            // If payment not used, put it in waiting account
            if (generateWaitingAccountAccountingRecords.getValue())
                accountingRecordService.generateAccountingRecordsForWaitingInboundPayment(payment);

        } else {
            // Get corresponding entities
            List<IndexEntity> correspondingEntities = getCorrespondingEntityForOutboundPayment(payment);
            List<Invoice> correspondingInvoices = new ArrayList<Invoice>();

            MutableBoolean generateWaitingAccountAccountingRecords = new MutableBoolean(true);
            if (payment.getAccountingRecords() != null && payment.getAccountingRecords().size() > 0) {
                generateWaitingAccountAccountingRecords = new MutableBoolean(false);
            } else {
                accountingRecordService.generateBankAccountingRecordsForOutboundPayment(payment);
            }

            // Get invoices
            if (correspondingEntities != null && correspondingEntities.size() > 0) {
                for (IndexEntity foundEntity : correspondingEntities) {
                    Invoice invoice = getInvoiceForEntity(foundEntity);
                    if (invoice != null
                            && invoice.getInvoiceStatus().getId()
                                    .equals(constantService.getInvoiceStatusReceived().getId())
                            && invoice.getProvider() != null) {
                        correspondingInvoices.add(invoice);
                        break;
                    }
                }
            }

            Refund refundFound = null;

            // Invoices to payed found
            if (correspondingInvoices.size() > 0) {
                associateOutboundPaymentAndInvoice(payment, correspondingInvoices.get(0),
                        generateWaitingAccountAccountingRecords,
                        null);
            }

            // If not found, try to match refunds
            if (correspondingEntities != null && correspondingEntities.size() > 0) {
                for (IndexEntity foundEntity : correspondingEntities) {
                    if (foundEntity.getEntityType().equals(Refund.class.getSimpleName())) {
                        Refund refund = refundService.getRefund(foundEntity.getEntityId());
                        if (refund != null && refund.getIsMatched() == false)
                            refundFound = refund;
                    }
                }
            }

            if (refundFound != null)
                associateOutboundPaymentAndRefund(payment, refundFound, generateWaitingAccountAccountingRecords);

            // If not found, try to match debour
            if (correspondingEntities != null && correspondingEntities.size() > 0) {
                for (IndexEntity foundEntity : correspondingEntities) {
                    if (foundEntity.getEntityType().equals(Debour.class.getSimpleName())) {
                        Debour debour = debourService.getDebour(foundEntity.getEntityId());
                        if (debour != null) {
                            generateWaitingAccountAccountingRecords = new MutableBoolean(true);
                            // Check case
                            if (debour.getPaymentType().getId()
                                    .equals(constantService.getPaymentTypeCheques().getId())
                                    && debour.getCheckNumber() != null
                                    && payment.getLabel().contains(debour.getCheckNumber())) {
                                associateOutboundPaymentAndDebour(payment, Arrays.asList(debour));
                                generateWaitingAccountAccountingRecords = new MutableBoolean(
                                        debour.getInvoiceItem() == null);
                            }
                            // Bank transfert case
                            if (debour.getPaymentType().getId()
                                    .equals(constantService.getPaymentTypeVirement().getId())) {
                                associateOutboundPaymentAndDebour(payment, Arrays.asList(debour));
                                generateWaitingAccountAccountingRecords = new MutableBoolean(
                                        debour.getInvoiceItem() == null);
                            }
                        }
                    }
                }
            }

            // If payment not used, put it in waiting account
            if (generateWaitingAccountAccountingRecords.isTrue()) {
                accountingRecordService.generateAccountingRecordsForWaintingOutboundPayment(payment);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void manualMatchPaymentInvoicesAndCustomerOrders(Payment payment,
            List<Invoice> correspondingInvoices,
            List<CustomerOrder> correspondingCustomerOrder, Affaire affaireRefund, ITiers tiersRefund,
            List<Float> byPassAmount)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {

        String refundLabelSuffix = "";
        float remainingMoney = payment.getPaymentAmount();
        if (payment.getPaymentWay().getId().equals(constantService.getPaymentWayInbound().getId())) {
            // Invoices to payed found
            if (correspondingInvoices != null && correspondingInvoices.size() > 0) {
                remainingMoney = associateInboundPaymentAndInvoices(payment, correspondingInvoices,
                        new MutableBoolean(true), byPassAmount);
                refundLabelSuffix = "facture n°" + correspondingInvoices.get(0).getId();
            }

            // Customer order waiting for deposit found
            boolean cancelPayment = false;
            if (correspondingCustomerOrder != null && correspondingCustomerOrder.size() > 0) {
                cancelPayment = true;
                remainingMoney = associateInboundPaymentAndCustomerOrders(payment, correspondingCustomerOrder,
                        correspondingInvoices, new MutableBoolean(true), byPassAmount, remainingMoney);
                refundLabelSuffix = "commande n°" + correspondingCustomerOrder.get(0).getId();
            }

            if (cancelPayment) {
                cancelPayment(payment, constantService.getAccountingJournalBank());
            }

            if (Math.abs(Math.round(remainingMoney * 100f) / 100f) > 0) {
                boolean modifyPayment = false;
                if (Math.abs(remainingMoney) <= Float.parseFloat(payementLimitRefundInEuros)) {
                    if (correspondingInvoices != null && correspondingInvoices.size() > 0) {
                        accountingRecordService.generateAppointForPayment(payment, remainingMoney,
                                invoiceHelper.getCustomerOrder(correspondingInvoices.get(0)));
                        modifyPayment = true;
                    } else if (correspondingCustomerOrder != null && correspondingCustomerOrder.size() > 0) {
                        accountingRecordService.generateAppointForPayment(payment, remainingMoney,
                                quotationService.getCustomerOrderOfQuotation(correspondingCustomerOrder.get(0)));
                        modifyPayment = true;
                    }
                    if (modifyPayment) {
                        payment.setPaymentAmount(payment.getPaymentAmount() - remainingMoney);
                    }
                } else {
                    refundService.generateRefund(tiersRefund, affaireRefund, payment, null, remainingMoney,
                            refundLabelSuffix);
                }
            }
        } else {
            // Invoices to payed found
            if (correspondingInvoices != null && correspondingInvoices.size() > 0) {
                remainingMoney = associateOutboundPaymentAndInvoice(payment, correspondingInvoices.get(0),
                        new MutableBoolean(true),
                        byPassAmount);
                refundLabelSuffix = "facture n°" + correspondingInvoices.get(0).getId();
            }
        }
    }

    private Float associateInboundPaymentAndCustomerOrders(Payment payment,
            List<CustomerOrder> correspondingCustomerOrder,
            List<Invoice> correspondingInvoice,
            MutableBoolean generateWaitingAccountAccountingRecords, List<Float> byPassAmount, float remainingMoney)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        Float remainingToPay = 0f;
        int amountIndex = 0;
        if (correspondingInvoice != null)
            amountIndex = correspondingInvoice.size() - 1 + 1;

        for (CustomerOrder customerOrder : correspondingCustomerOrder) {
            remainingToPay += Math.round(
                    customerOrderService.getRemainingAmountToPayForCustomerOrder(customerOrder) * 100f)
                    / 100f;
        }
        // if no by pass, put all on first customer order even if there is too much
        // money
        // Payment will be used, not necessary to put it in wainting account
        generateWaitingAccountAccountingRecords.setValue(false);

        for (int i = 0; i < correspondingCustomerOrder.size(); i++) {
            Float effectivePayment;
            if (byPassAmount != null) {
                effectivePayment = byPassAmount.get(amountIndex);
                amountIndex++;
            } else {
                effectivePayment = payment.getPaymentAmount();
            }

            remainingMoney -= effectivePayment;

            // Generate one deposit per customer order
            depositService.getNewDepositForCustomerOrder(
                    effectivePayment, LocalDateTime.now(), correspondingCustomerOrder.get(i), payment.getId(),
                    payment, true);

            remainingToPay -= effectivePayment;

            // Try unlocked customer order
            customerOrderService.unlockCustomerOrderFromDeposit(correspondingCustomerOrder.get(i));
        }
        addOrUpdatePayment(payment);
        return remainingMoney;
    }

    private Float associateInboundPaymentAndInvoices(Payment payment, List<Invoice> correspondingInvoices,
            MutableBoolean generateWaitingAccountAccountingRecords, List<Float> byPassAmount) throws OsirisException {
        InvoiceStatus payedStatus = constantService.getInvoiceStatusPayed();
        Float remainingToPay = 0f;
        int amountIndex = 0;
        Float remainingMoney = payment.getPaymentAmount();
        for (int i = 0; i < correspondingInvoices.size(); i++) {
            remainingToPay += (byPassAmount != null) ? byPassAmount.get(i)
                    : invoiceService.getRemainingAmountToPayForInvoice(correspondingInvoices.get(i));
        }
        // If payment is not over total of remaining to pay on all invoices
        if (byPassAmount != null || Math.round(remainingToPay * 100f) >= Math.round(remainingMoney * 100f)) {
            // Payment will be used, not necessary to put it in wainting account
            generateWaitingAccountAccountingRecords.setValue(false);

            for (int i = 0; i < correspondingInvoices.size(); i++) {
                Float remainingToPayForCurrentInvoice = invoiceService
                        .getRemainingAmountToPayForInvoice(correspondingInvoices.get(i));
                boolean isPayed = false;

                Float effectivePayment;
                if (byPassAmount != null) {
                    effectivePayment = byPassAmount.get(amountIndex);
                    amountIndex++;
                } else {
                    effectivePayment = Math.min(remainingToPayForCurrentInvoice, remainingMoney);
                }

                Payment newPayment = payment;
                // If more than 1 invoice to associate, cut payment
                if (correspondingInvoices.size() > 1) {
                    newPayment = generateNewPaymentFromPayment(payment, effectivePayment);
                    accountingRecordService.generateBankAccountingRecordsForInboundPayment(newPayment);

                    if (payment.getIsCancelled() == false)
                        cancelPayment(payment, constantService.getAccountingJournalBank());
                }

                // associate and remove waiting accounting record
                accountingRecordService.generateAccountingRecordsForSaleOnInvoicePayment(
                        correspondingInvoices.get(i), newPayment);

                newPayment.setInvoice(correspondingInvoices.get(i));
                addOrUpdatePayment(newPayment);
                if ((remainingToPayForCurrentInvoice - newPayment.getPaymentAmount()) <= 0) {
                    isPayed = true;
                    remainingMoney -= remainingToPayForCurrentInvoice;
                } else {
                    remainingMoney = 0f;
                }

                remainingMoney = Math.round(remainingMoney * 100f) / 100f;
                remainingToPay -= remainingToPayForCurrentInvoice;
                if (isPayed) {
                    Invoice updateInvoice = invoiceService.getInvoice(correspondingInvoices.get(i).getId());
                    updateInvoice.setInvoiceStatus(payedStatus);
                    invoiceService.addOrUpdateInvoice(updateInvoice);
                } else {
                    break;
                }
            }
        }
        return remainingMoney;
    }

    private Payment generateNewPaymentFromPayment(Payment payment, Float amountToUse) {
        Payment outPayment = new Payment();
        outPayment.setBankId(null);
        outPayment.setLabel(payment.getLabel());
        outPayment.setOriginPayment(payment);
        outPayment.setPaymentAmount(amountToUse);
        outPayment.setPaymentDate(payment.getPaymentDate());
        outPayment.setPaymentType(payment.getPaymentType());
        outPayment.setIsExternallyAssociated(false);
        outPayment.setPaymentWay(payment.getPaymentWay());

        addOrUpdatePayment(outPayment);
        return outPayment;
    }

    @Override
    public Payment generateNewPaymentFromDebour(Debour debour) throws OsirisException {
        Payment payment = new Payment();
        payment.setDebours(Arrays.asList(debour));
        payment.setIsExternallyAssociated(false);
        payment.setIsCancelled(false);
        payment.setLabel("Paiement du débours n°" + debour.getId());
        payment.setPaymentAmount(debour.getDebourAmount());
        payment.setPaymentDate(debour.getPaymentDateTime() != null ? debour.getPaymentDateTime()
                : LocalDateTime.now());
        payment.setPaymentType(debour.getPaymentType());
        payment.setPaymentWay(constantService.getPaymentWayOutboud());
        addOrUpdatePayment(payment);
        return payment;
    }

    @Override
    public Float associateOutboundPaymentAndInvoice(Payment payment, Invoice correspondingInvoice,
            MutableBoolean generateWaitingAccountAccountingRecords, List<Float> byPassAmount) throws OsirisException {
        Float remainingToPay = 0f;
        int amountIndex = 0;
        Float remainingMoney = Math.abs(payment.getPaymentAmount());
        remainingToPay += (byPassAmount != null) ? byPassAmount.get(0)
                : invoiceService.getRemainingAmountToPayForInvoice(correspondingInvoice);
        // If payment is not over total of remaining to pay on all invoices
        if (byPassAmount != null || remainingToPay >= remainingMoney) {
            // Payment will be used, not necessary to put it in wainting account
            generateWaitingAccountAccountingRecords = new MutableBoolean(false);

            Float remainingToPayForCurrentInvoice = invoiceService
                    .getRemainingAmountToPayForInvoice(correspondingInvoice);
            boolean isPayed = false;

            Float effectivePayment;
            if (byPassAmount != null) {
                effectivePayment = byPassAmount.get(amountIndex);
                amountIndex++;
            } else {
                effectivePayment = Math.min(remainingToPayForCurrentInvoice, remainingMoney);
            }

            // associate
            List<Payment> invoicePayment = new ArrayList<Payment>();
            invoicePayment.add(payment);

            // Do not generate, when it's for debour
            boolean debourFound = false;
            for (InvoiceItem invoiceItem : correspondingInvoice.getInvoiceItems())
                if (invoiceItem.getDebours() != null && invoiceItem.getDebours().size() > 0) {
                    debourFound = true;
                    break;
                }

            if (!debourFound) {
                accountingRecordService.generateAccountingRecordsForPurshaseOnInvoicePayment(
                        correspondingInvoice, invoicePayment, effectivePayment);
            } else {
                for (InvoiceItem invoiceItem : correspondingInvoice.getInvoiceItems())
                    if (invoiceItem.getDebours() != null && invoiceItem.getDebours().size() > 0) {
                        for (Debour debour : invoiceItem.getDebours()) {
                            if (debour.getPaymentType().getId().equals(constantService.getPaymentTypeCB().getId())) {
                                accountingRecordService.generateBankAccountingRecordsForOutboundDebourPayment(debour,
                                        correspondingInvoice.getCustomerOrderForInboundInvoice());
                            }
                        }
                    }
            }

            payment.setInvoice(correspondingInvoice);
            addOrUpdatePayment(payment);
            if ((remainingToPayForCurrentInvoice - payment.getPaymentAmount()) <= 0) {
                isPayed = true;
                remainingMoney -= remainingToPayForCurrentInvoice;
            } else {
                remainingMoney = 0f;
            }

            remainingMoney = Math.round(remainingMoney * 100f) / 100f;
            remainingToPay -= remainingToPayForCurrentInvoice;

            if (isPayed) {
                Invoice updateInvoice = invoiceService.getInvoice(correspondingInvoice.getId());
                updateInvoice.setInvoiceStatus(constantService.getInvoiceStatusPayed());
                invoiceService.addOrUpdateInvoice(updateInvoice);
            }

            // If invoice associated to debour, associate debour with payment
            for (InvoiceItem invoiceItem : correspondingInvoice.getInvoiceItems())
                if (invoiceItem.getDebours() != null)
                    for (Debour debour : invoiceItem.getDebours())
                        if (debour.getPayment() == null) {
                            debour.setPayment(payment);
                            debourService.addOrUpdateDebour(debour);
                            debourService.setDebourAsAssociated(debour);
                        }
        }
        return remainingMoney;
    }

    private Float associateOutboundPaymentAndRefund(Payment payment, Refund refund,
            MutableBoolean generateWaitingAccountAccountingRecords) throws OsirisException {

        Float refundAmount = Math.round(refund.getRefundAmount() * 100f) / 100f;
        Float paymentAmount = Math.round(payment.getPaymentAmount() * 100f) / 100f;

        if (refundAmount.equals(paymentAmount)) {
            generateWaitingAccountAccountingRecords.setFalse();
            accountingRecordService.generateAccountingRecordsForRefundOnVirement(refund);

            refund.setIsMatched(true);
            refund.setPayment(payment);
            refundService.addOrUpdateRefund(refund);
        }
        return 0f;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Float associateOutboundPaymentAndDebourFromUser(Payment payment, List<Debour> debours)
            throws OsirisException {
        return associateOutboundPaymentAndDebour(payment, debours);
    }

    // Return true if associated to invoice, false otherwise
    private Float associateOutboundPaymentAndDebour(Payment payment, List<Debour> debours)
            throws OsirisException {

        for (Debour debour : debours) {
            debour.setPayment(payment);
            debour = debourService.addOrUpdateDebour(debour);
            debourService.setDebourAsAssociated(debour);

            // If debour associated with invoice, associate payment with invoice
            if (debour.getInvoiceItem() != null && debour.getInvoiceItem().getInvoice() != null
                    && debour.getInvoiceItem().getInvoice().getInvoiceStatus().getId()
                            .equals(constantService.getInvoiceStatusReceived().getId())) {

                // do counter part of waiting record
                if (payment.getAccountingRecords() != null && payment.getAccountingRecords().size() > 0)
                    for (AccountingRecord record : payment.getAccountingRecords())
                        if (record.getIsCounterPart() == null || !record.getIsCounterPart()) {
                            if (record.getAccountingAccount().getPrincipalAccountingAccount().getId()
                                    .equals(constantService.getPrincipalAccountingAccountWaiting().getId())) {
                                accountingRecordService.letterWaitingRecords(record,
                                        accountingRecordService.generateCounterPart(record, payment.getId(),
                                                constantService.getAccountingJournalBank()));
                            }
                        }

                associateOutboundPaymentAndInvoice(payment,
                        invoiceService.getInvoice(debour.getInvoiceItem().getInvoice().getId()),
                        new MutableBoolean(false), null);
            } else if (debour.getCompetentAuthority().getCompetentAuthorityType().getIsDirectCharge()
                    && debour.getPaymentType().getId().equals(constantService.getPaymentTypeCB().getId())) {
                Provision provision = provisionService.getProvision(debour.getProvision().getId());
                accountingRecordService.generateBankAccountingRecordsForOutboundDebourPayment(debour,
                        provision.getAssoAffaireOrder().getCustomerOrder());
            }
        }
        return 0f;
    }

    private List<IndexEntity> getCorrespondingEntityForOutboundPayment(Payment payment) {
        ArrayList<String> entityTypesToSearch = new ArrayList<String>();
        entityTypesToSearch.add(Invoice.class.getSimpleName());
        entityTypesToSearch.add(Refund.class.getSimpleName());
        entityTypesToSearch.add(Debour.class.getSimpleName());

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
        List<IndexEntity> debourFound = new ArrayList<IndexEntity>();

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
                    debourFound = searchService.searchForEntities(idToFind + "", Debour.class.getSimpleName(), true);
                    if (debourFound != null && debourFound.size() > 0)
                        tmpEntitiesFound.addAll(debourFound);
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

    private CustomerOrder getCustomerOrderAtDepositStatusForEntity(IndexEntity foundEntity) {
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
                    if (paymentRound.equals(totalRound)) {
                        advisedPayments.add(payment);
                    }
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
    @Transactional
    public void setExternallyAssociated(Payment payment) {
        payment = getPayment(payment.getId());
        payment.setIsExternallyAssociated(true);
        if (payment.getAccountingRecords() != null && payment.getAccountingRecords().size() > 0)
            for (AccountingRecord record : payment.getAccountingRecords())
                accountingRecordService.deleteAccountingRecord(record);
        addOrUpdatePayment(payment);
    }

    @Override
    @Transactional
    public void unsetExternallyAssociated(Payment payment)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        payment.setIsExternallyAssociated(false);
        addOrUpdatePayment(payment);
        automatchPaymentsInvoicesAndGeneratePaymentAccountingRecords();
    }

    @Override
    public Payment cancelPayment(Payment payment, AccountingJournal journal) throws OsirisException {
        Integer operationIdCounterPart = ThreadLocalRandom.current().nextInt(1, 1000000000);
        if (payment.getAccountingRecords() != null)
            for (AccountingRecord accountingRecord : payment.getAccountingRecords()) {
                if (accountingRecord.getIsCounterPart() == null || !accountingRecord.getIsCounterPart())
                    if (!accountingRecord.getAccountingAccount().getPrincipalAccountingAccount().getId()
                            .equals(constantService.getPrincipalAccountingAccountBank().getId())
                            && !accountingRecord.getAccountingAccount().getPrincipalAccountingAccount().getId()
                                    .equals(constantService.getPrincipalAccountingAccountCharge().getId()))
                        if (accountingRecord.getAccountingId() == null) {
                            accountingRecordService.deleteAccountingRecord(accountingRecord);
                        } else {
                            accountingRecordService.letterWaitingRecords(accountingRecord,
                                    accountingRecordService.generateCounterPart(accountingRecord,
                                            operationIdCounterPart, journal));
                        }
            }
        payment.setIsCancelled(true);
        payment.setInvoice(null);
        return addOrUpdatePayment(payment);
    }

    @Override
    public void addCashPaymentForInvoice(Payment cashPayment, Invoice invoice) throws OsirisException {
        addOrUpdatePayment(cashPayment);
        accountingRecordService.generateBankAccountingRecordsForInboundCashPayment(cashPayment);

        associateInboundPaymentAndInvoices(getPayment(cashPayment.getId()), Arrays.asList(invoice),
                new MutableBoolean(false), null);
    }

    @Override
    public void addCheckPayment(Payment checkPayment)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        addOrUpdatePayment(checkPayment);
        automatchPaymentInvoicesAndGeneratePaymentAccountingRecords(checkPayment);
    }

    @Override
    public void addCashPaymentForCustomerOrder(Payment cashPayment, CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        addOrUpdatePayment(cashPayment);
        accountingRecordService.generateBankAccountingRecordsForInboundCashPayment(cashPayment);

        associateInboundPaymentAndCustomerOrders(getPayment(cashPayment.getId()), Arrays.asList(customerOrder),
                new ArrayList<Invoice>(),
                new MutableBoolean(false), null, cashPayment.getPaymentAmount());
        cancelPayment(getPayment(cashPayment.getId()), constantService.getAccountingJournalBank());
    }
}
