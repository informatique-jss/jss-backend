package com.jss.osiris.modules.invoicing.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.libs.search.service.SearchService;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.Deposit;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceStatus;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.PaymentSearch;
import com.jss.osiris.modules.invoicing.repository.PaymentRepository;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.model.QuotationStatus;
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

    @Value("${invoicing.invoice.status.send.code}")
    private String invoiceStatusSendCode;

    @Value("${invoicing.invoice.status.payed.code}")
    private String invoiceStatusPayedCode;

    @Value("${quotation.customer.order.status.billed.code}")
    private String customerOrderStatusBilledCode;

    @Value("${quotation.customer.order.status.waiting.deposit.code}")
    private String customerOrderStatusWaitingDepositCode;

    @Value("${invoicing.payment.limit.refund.euros}")
    private String payementLimitRefundInEuros;

    @Override
    public List<Payment> getPayments() {
        return IterableUtils.toList(paymentRepository.findAll());
    }

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
        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> searchPayments(PaymentSearch paymentSearch) throws Exception {
        List<Payment> invoices = paymentRepository.findPayments(paymentSearch.getPaymentWays(),
                paymentSearch.getStartDate(),
                paymentSearch.getEndDate(), paymentSearch.getMinAmount(), paymentSearch.getMaxAmount(),
                paymentSearch.getLabel(), paymentSearch.isHideAssociatedPayments());
        return invoices;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payementGrab() throws Exception {
        // TODO : plug bank API
        automatchPaymentsInvoicesAndGeneratePaymentAccountingRecords();
    }

    private void automatchPaymentsInvoicesAndGeneratePaymentAccountingRecords() throws Exception {
        List<Payment> payments = paymentRepository.findNotAssociatedPayments();

        for (Payment payment : payments) {
            // Get corresponding entities
            List<IndexEntity> correspondingEntities = getCorrespondingEntityForPayment(payment);
            List<Invoice> correspondingInvoices = new ArrayList<Invoice>();
            List<CustomerOrder> correspondingCustomerOrder = new ArrayList<CustomerOrder>();

            boolean generateWaitingAccountAccountingRecords = true;
            if (payment.getAccountingRecords() != null && payment.getAccountingRecords().size() > 0) {
                generateWaitingAccountAccountingRecords = false;
            } else {
                accountingRecordService.generateBankAccountingRecordsForPayment(payment);
            }

            // Get invoices
            if (correspondingEntities != null && correspondingEntities.size() > 0) {
                for (IndexEntity foundEntity : correspondingEntities) {
                    Invoice invoice = getInvoiceForEntity(foundEntity);
                    if (invoice != null)
                        correspondingInvoices.add(invoice);
                    CustomerOrder customerOrder = getCustomerOrderAtDepositStatusForEntity(foundEntity);
                    if (customerOrder != null)
                        correspondingCustomerOrder.add(customerOrder);
                }
            }

            // If invoice and pending customer orders found, do nothing => to complicated to
            // manage automaticaly
            if (correspondingInvoices.size() > 0 && correspondingCustomerOrder.size() > 0)
                continue;
            // Invoices to payed found
            if (correspondingInvoices.size() > 0) {
                associatePayementAndInvoices(payment, correspondingInvoices, generateWaitingAccountAccountingRecords,
                        null);
            }

            // Customer order wainting for deposit found
            if (correspondingCustomerOrder.size() > 0) {
                associatePayementAndCustomerOrders(payment, correspondingCustomerOrder, correspondingInvoices,
                        generateWaitingAccountAccountingRecords, null, payment.getPaymentAmount());
            }
            // If payment not used, put it in waiting account
            if (generateWaitingAccountAccountingRecords) {
                accountingRecordService.generateAccountingRecordsForWaintingPayment(payment);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void manualMatchPaymentInvoicesAndGeneratePaymentAccountingRecords(Payment payment,
            List<Invoice> correspondingInvoices,
            List<CustomerOrder> correspondingCustomerOrder, Affaire affaireRefund, ITiers tiersRefund,
            List<Float> byPassAmount)
            throws Exception {

        float remainingMoney = payment.getPaymentAmount();
        // Invoices to payed found
        if (correspondingInvoices != null && correspondingInvoices.size() > 0) {
            remainingMoney = associatePayementAndInvoices(payment, correspondingInvoices, true, byPassAmount);
        }

        // Customer order wainting for deposit found
        if (correspondingCustomerOrder != null && correspondingCustomerOrder.size() > 0) {
            remainingMoney = associatePayementAndCustomerOrders(payment, correspondingCustomerOrder,
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
                refundService.generateRefund(tiersRefund, affaireRefund, payment, remainingMoney);
            }
        }
    }

    private Float associatePayementAndCustomerOrders(Payment payment, List<CustomerOrder> correspondingCustomerOrder,
            List<Invoice> correspondingInvoice,
            boolean generateWaitingAccountAccountingRecords, List<Float> byPassAmount, float remainingMoney)
            throws Exception {
        Float remainingToPay = 0f;
        int amountIndex = 0;
        if (correspondingInvoice != null)
            amountIndex = correspondingInvoice.size() - 1 + 1;

        for (CustomerOrder customerOrder : correspondingCustomerOrder) {
            remainingToPay += Math.round(
                    accountingRecordService.getRemainingAmountToPayForCustomerOrder(customerOrder) * 100f)
                    / 100f;
        }
        // If payment is not equal to all customer order, do nothing, a human will
        if (byPassAmount != null || remainingToPay >= payment.getPaymentAmount()) {
            // Payment will be used, not necessary to put it in wainting account
            generateWaitingAccountAccountingRecords = false;

            for (int i = 0; i < correspondingCustomerOrder.size(); i++) {
                if (remainingToPay > 0) {
                    Float remainingToPayForCurrentCustomerOrder = accountingRecordService
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
                    Deposit deposit = depositService.getNewDepositForCustomerOrder(
                            effectivePayment, LocalDateTime.now(), correspondingCustomerOrder.get(i), payment);

                    deposit.setCustomerOrder(correspondingCustomerOrder.get(i));
                    depositService.addOrUpdateDeposit(deposit);

                    remainingToPay -= effectivePayment;
                    // Unlocked customer order
                    if (correspondingCustomerOrder.get(i).getQuotationStatus().getCode()
                            .equals(QuotationStatus.WAITING_DEPOSIT)
                            && remainingToPayForCurrentCustomerOrder - effectivePayment <= 0)
                        customerOrderService.addOrUpdateCustomerOrderStatus(correspondingCustomerOrder.get(i),
                                QuotationStatus.BEING_PROCESSED);
                }
            }
            payment.setCustomerOrder(correspondingCustomerOrder.get(0));
            addOrUpdatePayment(payment);
        }
        return remainingMoney;
    }

    private Float associatePayementAndInvoices(Payment payment, List<Invoice> correspondingInvoices,
            boolean generateWaitingAccountAccountingRecords, List<Float> byPassAmount) throws Exception {
        InvoiceStatus payedStatus = invoiceStatusService.getInvoiceStatusByCode(invoiceStatusPayedCode);
        Float remainingToPay = 0f;
        int amountIndex = 0;
        Float remainingMoney = payment.getPaymentAmount();
        for (int i = 0; i < correspondingInvoices.size(); i++) {
            remainingToPay += (byPassAmount != null) ? byPassAmount.get(i)
                    : accountingRecordService.getRemainingAmountToPayForInvoice(correspondingInvoices.get(i));
        }
        // If payment is not over total of remaining to pay on all invoices
        if (byPassAmount != null || remainingToPay >= remainingMoney) {
            // Payment will be used, not necessary to put it in wainting account
            generateWaitingAccountAccountingRecords = false;

            for (int i = 0; i < correspondingInvoices.size(); i++) {
                Float remainingToPayForCurrentInvoice = accountingRecordService
                        .getRemainingAmountToPayForInvoice(correspondingInvoices.get(i));
                boolean isPayed = false;

                Float effectivePayment;
                if (byPassAmount != null) {
                    effectivePayment = byPassAmount.get(amountIndex);
                    amountIndex++;
                } else {
                    effectivePayment = Math.min(remainingToPayForCurrentInvoice, remainingMoney);
                }

                if (i == 0) {
                    // associate
                    List<Payment> invoicePayment = new ArrayList<Payment>();
                    invoicePayment.add(payment);
                    accountingRecordService.generateAccountingRecordsForSaleOnInvoicePayment(
                            correspondingInvoices.get(i), invoicePayment, null,
                            effectivePayment);

                    payment.setInvoice(correspondingInvoices.get(i));
                    addOrUpdatePayment(payment);
                    if ((remainingToPayForCurrentInvoice - payment.getPaymentAmount()) <= 0) {
                        isPayed = true;
                        remainingMoney -= remainingToPayForCurrentInvoice;
                    } else {
                        remainingMoney = 0f;
                    }
                } else if (remainingToPay > 0 && remainingMoney > 0) {
                    // If multiple invoice, use intermediary deposit
                    List<Deposit> invoiceDeposits = new ArrayList<Deposit>();
                    invoiceDeposits
                            .add(depositService.getNewDepositForInvoice(remainingMoney, LocalDateTime.now(),
                                    correspondingInvoices.get(i), payment));
                    accountingRecordService.generateAccountingRecordsForSaleOnInvoicePayment(
                            correspondingInvoices.get(i), null, invoiceDeposits,
                            effectivePayment);

                    invoiceDeposits.get(0).setInvoice(correspondingInvoices.get(0));
                    depositService.addOrUpdateDeposit(invoiceDeposits.get(0));
                    if (remainingToPayForCurrentInvoice - invoiceDeposits.get(0).getDepositAmount() <= 0) {
                        isPayed = true;
                        remainingMoney -= remainingToPayForCurrentInvoice;
                    } else {
                        remainingMoney = 0f;
                    }
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

    private List<IndexEntity> getCorrespondingEntityForPayment(Payment payment) {
        ArrayList<String> entityTypesToSearch = new ArrayList<String>();
        entityTypesToSearch.add(Invoice.class.getSimpleName());
        entityTypesToSearch.add(CustomerOrder.class.getSimpleName());
        entityTypesToSearch.add(Quotation.class.getSimpleName());

        Pattern p = Pattern.compile("\\d+");
        List<IndexEntity> entitiesFound = new ArrayList<IndexEntity>();

        // Find possible references
        if (payment.getLabel() != null) {
            Matcher m = p.matcher(payment.getLabel());
            while (m.find()) {
                List<IndexEntity> tmpEntitiesFound = searchService.searchForEntitiesById(Integer.parseInt(m.group()),
                        entityTypesToSearch);
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

    private Invoice getInvoiceForEntity(IndexEntity foundEntity) {
        if (foundEntity != null && foundEntity.getEntityType() != null) {
            if (foundEntity.getEntityType().equals(Invoice.class.getSimpleName())) {
                Invoice invoice = invoiceService.getInvoice(foundEntity.getEntityId());
                if (invoice.getInvoiceStatus().getCode().equals(invoiceStatusSendCode))
                    return invoice;
            }
            if (foundEntity.getEntityType().equals(CustomerOrder.class.getSimpleName())) {
                CustomerOrder customerOrder = customerOrderService.getCustomerOrder(foundEntity.getEntityId());
                return getInvoiceForCustomerOrder(customerOrder);
            }
            if (foundEntity.getEntityType().equals(Quotation.class.getSimpleName())) {
                Quotation quotation = quotationService.getQuotation(foundEntity.getEntityId());
                List<Invoice> invoices = new ArrayList<Invoice>();
                if (quotation != null && quotation.getCustomerOrders() != null
                        && quotation.getCustomerOrders().size() > 0) {
                    for (CustomerOrder customerOrder : quotation.getCustomerOrders()) {
                        Invoice invoice = getInvoiceForCustomerOrder(customerOrder);
                        if (invoice != null)
                            invoices.add(invoice);
                    }
                }
                if (invoices != null && invoices.size() == 1)
                    return invoices.get(0);
            }
        }
        return null;
    }

    private CustomerOrder getCustomerOrderAtDepositStatusForEntity(IndexEntity foundEntity) {
        if (foundEntity != null && foundEntity.getEntityType() != null) {
            if (foundEntity.getEntityType().equals(CustomerOrder.class.getSimpleName())) {
                CustomerOrder customerOrder = customerOrderService.getCustomerOrder(foundEntity.getEntityId());
                if (customerOrder.getQuotationStatus().getCode().equals(customerOrderStatusWaitingDepositCode))
                    return customerOrder;
            }
        }
        return null;
    }

    private Invoice getInvoiceForCustomerOrder(CustomerOrder customerOrder) {
        if (customerOrder.getInvoices() != null && customerOrder.getInvoices().size() > 0
                && customerOrder.getQuotationStatus().getCode().equals(customerOrderStatusBilledCode)) {
            for (Invoice invoice : customerOrder.getInvoices())
                if (invoice.getInvoiceStatus().getCode().equals(invoiceStatusSendCode))
                    return invoice;
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
                        if (invoice.getCustomerOrder() != null
                                && m.group().equals(invoice.getCustomerOrder().getId().toString()))
                            advisedPayments.add(payment);
                    }
                }
            }
            // If no match by name, attempt by amount
            Float totalRound = Math.round(invoice.getTotalPrice() * 100f) / 100f;
            for (Payment payment : payments) {
                Float paymentRound = Math.round(payment.getPaymentAmount() * 100f) / 100f;
                if (paymentRound.equals(totalRound)) {
                    advisedPayments.add(payment);
                }
            }
        }
        return advisedPayments;
    }
}
