package com.jss.osiris.modules.invoicing.service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.ofx.OFXParser;
import com.jss.osiris.libs.ofx.OFXStatement;
import com.jss.osiris.libs.ofx.StatementTransaction;
import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.libs.search.service.SearchService;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.Invoice;
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
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
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
    ConstantService constantService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    OFXParser ofxParser;

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
            throws OsirisException, OsirisClientMessageException {
        automatchPaymentsInvoicesAndGeneratePaymentAccountingRecords();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Attachment> uploadOfxFile(InputStream file) throws OsirisException, OsirisClientMessageException {
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
                    payment.setExternallyAssociated(false);
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

    @Transactional(rollbackFor = Exception.class)
    @Scheduled(initialDelay = 100, fixedDelay = 2000000)
    public void automatchPaymentsInvoicesAndGeneratePaymentAccountingRecords()
            throws OsirisException, OsirisClientMessageException {
        List<Payment> payments = paymentRepository.findNotAssociatedPayments();

        for (Payment payment : payments) {
            // Match inbound payment
            if (payment.getPaymentWay().getId().equals(constantService.getPaymentWayInbound().getId())) {
                // Get corresponding entities
                List<IndexEntity> correspondingEntities = getCorrespondingEntityForInboudPayment(payment);
                List<Invoice> correspondingInvoices = new ArrayList<Invoice>();
                List<CustomerOrder> correspondingCustomerOrder = new ArrayList<CustomerOrder>();

                boolean generateWaitingAccountAccountingRecords = true;
                if (payment.getAccountingRecords() != null && payment.getAccountingRecords().size() > 0) {
                    generateWaitingAccountAccountingRecords = false;
                } else {
                    accountingRecordService.generateBankAccountingRecordsForInboundPayment(payment);
                }

                // Get invoices
                Float totalToPay = 0f;
                if (correspondingEntities != null && correspondingEntities.size() > 0) {
                    for (IndexEntity foundEntity : correspondingEntities) {
                        Invoice invoice = getInvoiceForEntity(foundEntity);
                        if (invoice != null
                                && invoice.getInvoiceStatus().getId()
                                        .equals(constantService.getInvoiceStatusSend().getId())
                                && invoice.getProvider() == null) {
                            correspondingInvoices.add(invoice);
                            totalToPay += invoiceService.getRemainingAmountToPayForInvoice(invoice);
                        }
                        CustomerOrder customerOrder = getCustomerOrderAtDepositStatusForEntity(foundEntity);
                        if (customerOrder != null) {
                            correspondingCustomerOrder.add(customerOrder);
                            totalToPay += customerOrderService.getRemainingAmountToPayForCustomerOrder(customerOrder);
                        }
                    }
                }

                // If invoice and pending customer orders found, do nothing => to complicated to
                // manage automaticaly
                // If too many money inbound, do nothing, a human will choose who must be refund
                if (correspondingInvoices.size() > 0 && correspondingCustomerOrder.size() > 0
                        || payment.getPaymentAmount() > totalToPay)
                    continue;

                Float remainingMoney = payment.getPaymentAmount();

                // Invoices to payed found
                if (correspondingInvoices.size() > 0) {
                    remainingMoney = associateInboundPaymentAndInvoices(payment, correspondingInvoices,
                            generateWaitingAccountAccountingRecords,
                            null);
                }

                // Customer order waiting for deposit found
                if (correspondingCustomerOrder.size() > 0 && remainingMoney > 0) {
                    associateInboundPaymentAndCustomerOrders(payment, correspondingCustomerOrder, correspondingInvoices,
                            generateWaitingAccountAccountingRecords, null, payment.getPaymentAmount());
                    cancelPayment(payment);
                }
                // If payment not used, put it in waiting account
                if (generateWaitingAccountAccountingRecords && !payment.getIsCancelled()) {
                    accountingRecordService.generateAccountingRecordsForWaintingInboundPayment(payment);
                }
            } else {
                // Get corresponding entities
                List<IndexEntity> correspondingEntities = getCorrespondingEntityForOutboundPayment(payment);
                List<Invoice> correspondingInvoices = new ArrayList<Invoice>();

                boolean generateWaitingAccountAccountingRecords = true;
                if (payment.getAccountingRecords() != null && payment.getAccountingRecords().size() > 0) {
                    generateWaitingAccountAccountingRecords = false;
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

                // Invoices to payed found
                if (correspondingInvoices.size() > 0) {
                    associateOutboundPaymentAndInvoice(payment, correspondingInvoices.get(0),
                            generateWaitingAccountAccountingRecords,
                            null);
                }

                // If not found, try to match refunds
                Refund refundFound = null;
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

                // If payment not used, put it in waiting account
                if (generateWaitingAccountAccountingRecords) {
                    accountingRecordService.generateAccountingRecordsForWaintingOutboundPayment(payment);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void manualMatchPaymentInvoicesAndCustomerOrders(Payment payment,
            List<Invoice> correspondingInvoices,
            List<CustomerOrder> correspondingCustomerOrder, Affaire affaireRefund, ITiers tiersRefund,
            List<Float> byPassAmount)
            throws OsirisException, OsirisClientMessageException {

        float remainingMoney = payment.getPaymentAmount();
        if (payment.getPaymentWay().getId().equals(constantService.getPaymentWayInbound().getId())) {
            // Invoices to payed found
            if (correspondingInvoices != null && correspondingInvoices.size() > 0) {
                remainingMoney = associateInboundPaymentAndInvoices(payment, correspondingInvoices, true, byPassAmount);
            }

            // Customer order wainting for deposit found
            boolean cancelPayment = false;
            if (correspondingCustomerOrder != null && correspondingCustomerOrder.size() > 0) {
                cancelPayment = true;
                remainingMoney = associateInboundPaymentAndCustomerOrders(payment, correspondingCustomerOrder,
                        correspondingInvoices, true, byPassAmount, remainingMoney);
            }

            if (remainingMoney > 0) {
                if (Math.abs(remainingMoney) <= Float.parseFloat(payementLimitRefundInEuros)) {
                    if (correspondingInvoices != null && correspondingInvoices.size() > 0)
                        accountingRecordService.generateAppointForPayment(payment, remainingMoney,
                                invoiceHelper.getCustomerOrder(correspondingInvoices.get(0)));
                    else if (correspondingCustomerOrder != null && correspondingCustomerOrder.size() > 0)
                        accountingRecordService.generateAppointForPayment(payment, remainingMoney,
                                quotationService.getCustomerOrderOfQuotation(correspondingCustomerOrder.get(0)));

                } else {
                    refundService.generateRefund(tiersRefund, affaireRefund, payment, null, remainingMoney);
                }
            }

            if (cancelPayment) {
                payment.setIsCancelled(true);
                addOrUpdatePayment(payment);
            }
        } else {
            // Invoices to payed found
            if (correspondingInvoices != null && correspondingInvoices.size() > 0) {
                remainingMoney = associateOutboundPaymentAndInvoice(payment, correspondingInvoices.get(0), true,
                        byPassAmount);
            }
        }
    }

    private Float associateInboundPaymentAndCustomerOrders(Payment payment,
            List<CustomerOrder> correspondingCustomerOrder,
            List<Invoice> correspondingInvoice,
            boolean generateWaitingAccountAccountingRecords, List<Float> byPassAmount, float remainingMoney)
            throws OsirisException, OsirisClientMessageException {
        Float remainingToPay = 0f;
        int amountIndex = 0;
        if (correspondingInvoice != null)
            amountIndex = correspondingInvoice.size() - 1 + 1;

        for (CustomerOrder customerOrder : correspondingCustomerOrder) {
            remainingToPay += Math.round(
                    customerOrderService.getRemainingAmountToPayForCustomerOrder(customerOrder) * 100f)
                    / 100f;
        }
        // If payment is not equal to all customer order, do nothing, a human will
        if (byPassAmount != null || remainingToPay >= payment.getPaymentAmount()) {
            // Payment will be used, not necessary to put it in wainting account
            generateWaitingAccountAccountingRecords = false;

            for (int i = 0; i < correspondingCustomerOrder.size(); i++) {
                if (remainingToPay > 0) {
                    Float remainingToPayForCurrentCustomerOrder = customerOrderService
                            .getRemainingAmountToPayForCustomerOrder(correspondingCustomerOrder.get(i));

                    Float effectivePayment;
                    if (byPassAmount != null) {
                        effectivePayment = byPassAmount.get(amountIndex);
                        amountIndex++;
                    } else {
                        effectivePayment = Math.min(payment.getPaymentAmount(),
                                remainingToPayForCurrentCustomerOrder);
                    }

                    remainingMoney -= effectivePayment;

                    // Generate one deposit per customer order
                    depositService.getNewDepositForCustomerOrder(
                            effectivePayment, LocalDateTime.now(), correspondingCustomerOrder.get(i), payment.getId(),
                            payment);

                    remainingToPay -= effectivePayment;

                    // Try unlocked customer order
                    customerOrderService.unlockCustomerOrderFromDeposit(correspondingCustomerOrder.get(i));
                }
            }
            addOrUpdatePayment(payment);
        }
        return remainingMoney;
    }

    private Float associateInboundPaymentAndInvoices(Payment payment, List<Invoice> correspondingInvoices,
            boolean generateWaitingAccountAccountingRecords, List<Float> byPassAmount) throws OsirisException {
        InvoiceStatus payedStatus = constantService.getInvoiceStatusPayed();
        Float remainingToPay = 0f;
        int amountIndex = 0;
        Float remainingMoney = payment.getPaymentAmount();
        for (int i = 0; i < correspondingInvoices.size(); i++) {
            remainingToPay += (byPassAmount != null) ? byPassAmount.get(i)
                    : invoiceService.getRemainingAmountToPayForInvoice(correspondingInvoices.get(i));
        }
        // If payment is not over total of remaining to pay on all invoices
        if (byPassAmount != null || remainingToPay >= remainingMoney) {
            // Payment will be used, not necessary to put it in wainting account
            generateWaitingAccountAccountingRecords = false;

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
                if (correspondingInvoices.size() > 1)
                    newPayment = generateNewPaymentFromPayment(payment, effectivePayment);

                // associate
                accountingRecordService.generateAccountingRecordsForSaleOnInvoicePayment(
                        correspondingInvoices.get(i), newPayment);

                if (correspondingInvoices.size() > 1)
                    payment.setIsCancelled(true);
                payment.setInvoice(correspondingInvoices.get(i));
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
                    correspondingInvoices.get(i).setInvoiceStatus(payedStatus);
                    invoiceService.addOrUpdateInvoice(correspondingInvoices.get(i));
                } else {
                    break;
                }
            }
        }
        return remainingMoney;
    }

    private Payment generateNewPaymentFromPayment(Payment payment, Float amountToUse) {
        Payment outPayment = new Payment();
        outPayment.setBankId(payment.getBankId());
        outPayment.setLabel(payment.getLabel());
        outPayment.setOriginPayment(payment);
        outPayment.setPaymentAmount(amountToUse);
        outPayment.setPaymentDate(payment.getPaymentDate());
        outPayment.setPaymentType(payment.getPaymentType());
        outPayment.setPaymentWay(payment.getPaymentWay());
        addOrUpdatePayment(outPayment);
        return outPayment;
    }

    private Float associateOutboundPaymentAndInvoice(Payment payment, Invoice correspondingInvoice,
            boolean generateWaitingAccountAccountingRecords, List<Float> byPassAmount) throws OsirisException {
        Float remainingToPay = 0f;
        int amountIndex = 0;
        Float remainingMoney = Math.abs(payment.getPaymentAmount());
        remainingToPay += (byPassAmount != null) ? byPassAmount.get(0)
                : invoiceService.getRemainingAmountToPayForInvoice(correspondingInvoice);
        // If payment is not over total of remaining to pay on all invoices
        if (byPassAmount != null || remainingToPay >= remainingMoney) {
            // Payment will be used, not necessary to put it in wainting account
            generateWaitingAccountAccountingRecords = false;

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
            accountingRecordService.generateAccountingRecordsForPurshaseOnInvoicePayment(
                    correspondingInvoice, invoicePayment, effectivePayment);

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
                correspondingInvoice.setInvoiceStatus(constantService.getInvoiceStatusPayed());
                invoiceService.addOrUpdateInvoice(correspondingInvoice);
            }
        }
        return remainingMoney;
    }

    private Float associateOutboundPaymentAndRefund(Payment payment, Refund refund,
            boolean generateWaitingAccountAccountingRecords) throws OsirisException {

        Float refundAmount = Math.round(refund.getRefundAmount() * 100f) / 100f;
        Float paymentAmount = Math.round(payment.getPaymentAmount() * 100f) / 100f;

        if (refundAmount == paymentAmount) {
            generateWaitingAccountAccountingRecords = false;
            accountingRecordService.generateAccountingRecordsForRefund(refund);

            refund.setIsMatched(true);
            refundService.addOrUpdateRefund(refund);
        }
        return 0f;
    }

    private List<IndexEntity> getCorrespondingEntityForOutboundPayment(Payment payment) {
        ArrayList<String> entityTypesToSearch = new ArrayList<String>();
        entityTypesToSearch.add(Invoice.class.getSimpleName());
        entityTypesToSearch.add(Refund.class.getSimpleName());

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

                if (idToFind != null)
                    tmpEntitiesFound = searchService.searchForEntitiesById(idToFind, entityTypesToSearch);
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
                if (invoice.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusSend().getId()) || invoice
                        .getInvoiceStatus().getId().equals(constantService.getInvoiceStatusReceived().getId()))
                    return invoice;
            }
            if (foundEntity.getEntityType().equals(CustomerOrder.class.getSimpleName())) {
                List<Invoice> invoices = invoiceService.getInvoiceForCustomerOrder(foundEntity.getEntityId());
                if (invoices != null && invoices.size() > 0)
                    for (Invoice invoice : invoices)
                        if (invoice.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusSend().getId()))
                            return invoice;
            }
            if (foundEntity.getEntityType().equals(Quotation.class.getSimpleName())) {
                Quotation quotation = quotationService.getQuotation(foundEntity.getEntityId());
                if (quotation != null && quotation.getCustomerOrders() != null
                        && quotation.getCustomerOrders().size() > 0) {
                    for (CustomerOrder customerOrder : quotation.getCustomerOrders()) {
                        List<Invoice> invoices = invoiceService.getInvoiceForCustomerOrder(customerOrder.getId());
                        if (invoices != null && invoices.size() > 0)
                            for (Invoice invoice : invoices)
                                if (invoice.getInvoiceStatus().getId()
                                        .equals(constantService.getInvoiceStatusSend().getId()))
                                    return invoice;
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
                                        .equals(CustomerOrderStatus.BEING_PROCESSED)))
                    return customerOrder;
            }
        }
        return null;
    }

    @Override
    public List<Payment> getAdvisedPaymentForInvoice(Invoice invoice) {
        List<Payment> payments = paymentRepository.findNotAssociatedPayments();
        List<Payment> advisedPayments = new ArrayList<Payment>();
        if (payments != null && payments.size() > 0) {
            Pattern p = Pattern.compile("\\d+");
            for (Payment payment : payments) {
                if (payment.getLabel() != null) {
                    Matcher m = p.matcher(payment.getLabel());
                    while (m.find()) {
                        if (m.group().equals(invoice.getId().toString()))
                            advisedPayments.add(payment);
                        else if (invoice.getCustomerOrder() != null
                                && m.group().equals(invoice.getCustomerOrder().getId().toString()))
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
    public void setExternallyAssociated(Payment payment) {
        payment.setIsExternallyAssociated(true);
        addOrUpdatePayment(payment);
    }

    @Override
    public void unsetExternallyAssociated(Payment payment) {
        payment.setIsExternallyAssociated(false);
        addOrUpdatePayment(payment);
    }

    private Payment cancelPayment(Payment payment) throws OsirisException {
        if (payment.getAccountingRecords() != null)
            for (AccountingRecord accountingRecord : payment.getAccountingRecords()) {
                if (!accountingRecord.getIsCounterPart())
                    accountingRecordService.generateCounterPart(accountingRecord);
            }
        payment.setIsCancelled(true);
        payment.setInvoice(null);
        return addOrUpdatePayment(payment);
    }
}
