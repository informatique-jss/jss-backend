package com.jss.osiris.modules.invoicing.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.Deposit;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.Refund;
import com.jss.osiris.modules.invoicing.repository.DepositRepository;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.quotation.service.QuotationService;
import com.jss.osiris.modules.tiers.model.ITiers;

@Service
public class DepositServiceImpl implements DepositService {

    @Autowired
    DepositRepository depositRepository;

    @Autowired
    AccountingRecordService accountingRecordService;

    @Autowired
    ConstantService constantService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    InvoiceHelper invoiceHelper;

    @Autowired
    QuotationService quotationService;

    @Autowired
    RefundService refundService;

    @Autowired
    AppointService appointService;

    @Value("${invoicing.payment.limit.refund.euros}")
    private String payementLimitRefundInEuros;

    @Override
    public Deposit getDeposit(Integer id) {
        Optional<Deposit> deposit = depositRepository.findById(id);
        if (deposit.isPresent())
            return deposit.get();
        return null;
    }

    @Override
    public Deposit addOrUpdateDeposit(
            Deposit deposit) {
        return depositRepository.save(deposit);
    }

    @Override
    public Deposit getNewDepositForInvoice(Float depositAmount, LocalDateTime depositDatetime, Invoice invoice,
            Integer overrideAccountingOperationId, Payment payment, boolean isFromOriginPayment)
            throws OsirisException {
        Deposit deposit = new Deposit();
        deposit.setDepositAmount(depositAmount);
        deposit.setDepositDate(depositDatetime);
        deposit.setLabel("Acompte pour la facture n°" + invoice.getId());
        deposit.setIsCancelled(false);
        deposit.setOriginPayment(payment);
        deposit = addOrUpdateDeposit(deposit);
        deposit.setInvoice(invoice);
        deposit = addOrUpdateDeposit(deposit);
        accountingRecordService.generateAccountingRecordsForDepositOnInvoice(deposit, invoice,
                overrideAccountingOperationId, isFromOriginPayment);

        if (invoice.getDeposits() == null)
            invoice.setDeposits(new ArrayList<Deposit>());
        invoice.getDeposits().add(deposit);
        invoiceService.addOrUpdateInvoice(invoice);

        return getDeposit(deposit.getId());
    }

    @Override
    public Deposit getNewDepositForCustomerOrder(Float depositAmount, LocalDateTime depositDatetime,
            CustomerOrder customerOrder, Integer overrideAccountingOperationId, Payment payment,
            boolean isFromOriginPayment)
            throws OsirisException {
        Deposit deposit = new Deposit();
        deposit.setDepositAmount(depositAmount);
        deposit.setDepositDate(depositDatetime);
        deposit.setLabel("Acompte pour la commande n°" + customerOrder.getId());
        deposit.setOriginPayment(payment);
        deposit.setIsCancelled(false);
        deposit.setCustomerOrder(customerOrder);
        deposit = addOrUpdateDeposit(deposit);
        accountingRecordService.generateAccountingRecordsForDepositAndCustomerOrder(deposit, customerOrder,
                overrideAccountingOperationId, isFromOriginPayment);

        if (customerOrder.getDeposits() == null)
            customerOrder.setDeposits(new ArrayList<Deposit>());
        customerOrder.getDeposits().add(deposit);

        return getDeposit(deposit.getId());
    }

    @Override
    public void moveDepositToInvoice(Deposit deposit, Invoice toInvoice) throws OsirisException {
        cancelDeposit(deposit);
        getNewDepositForInvoice(deposit.getDepositAmount(), LocalDateTime.now(), toInvoice, null,
                deposit.getOriginPayment(), false);
    }

    @Override
    public void moveDepositFromInvoiceToCustomerOrder(Deposit deposit, Invoice fromInvoice,
            CustomerOrder toCustomerOrder) throws OsirisException {

        cancelDeposit(deposit);
        getNewDepositForCustomerOrder(deposit.getDepositAmount(), LocalDateTime.now(), toCustomerOrder, null,
                deposit.getOriginPayment(), false);

    }

    private Deposit cancelDeposit(Deposit deposit) throws OsirisException {
        Integer operationIdCounterPart = ThreadLocalRandom.current().nextInt(1, 1000000000);
        if (deposit.getAccountingRecords() != null)
            for (AccountingRecord accountingRecord : deposit.getAccountingRecords()) {
                if ((accountingRecord.getIsCounterPart() == null || !accountingRecord.getIsCounterPart())
                        && !accountingRecord.getAccountingAccount().getPrincipalAccountingAccount().getId()
                                .equals(constantService.getPrincipalAccountingAccountBank().getId())
                        && !accountingRecord.getAccountingAccount().getPrincipalAccountingAccount().getId()
                                .equals(constantService.getPrincipalAccountingAccountCharge().getId()))
                    accountingRecordService.generateCounterPart(accountingRecord, operationIdCounterPart,
                            constantService.getAccountingJournalMiscellaneousOperations());
            }
        deposit.setIsCancelled(true);
        deposit.setCustomerOrder(null);
        deposit.setInvoice(null);
        return addOrUpdateDeposit(deposit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void manualMatchDepositInvoicesAndCustomerOrders(Deposit deposit,
            List<Invoice> correspondingInvoices,
            List<CustomerOrder> correspondingCustomerOrder, Affaire affaireRefund, ITiers tiersRefund,
            List<Float> byPassAmount)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {

        float remainingMoney = deposit.getDepositAmount();

        cancelDeposit(deposit);

        String refundLabelSuffix = "";
        int correspondingInvoiceSize = 0;
        if (correspondingInvoices != null) {
            for (int i = 0; i < correspondingInvoices.size(); i++) {
                Invoice invoice = correspondingInvoices.get(i);
                Float remainingToPayForInvoice = Math
                        .min(invoiceService.getRemainingAmountToPayForInvoice(invoice),
                                byPassAmount.get(i));

                getNewDepositForInvoice(remainingToPayForInvoice, LocalDateTime.now(), invoice, deposit.getId(),
                        deposit.getOriginPayment(), false);

                remainingToPayForInvoice = Math.min(invoiceService.getRemainingAmountToPayForInvoice(invoice),
                        byPassAmount.get(i));

                accountingRecordService.checkInvoiceForLettrage(invoice);

                remainingMoney -= remainingToPayForInvoice;
                refundLabelSuffix = "facture n°" + invoice.getId();

                if (Math.abs(remainingMoney) <= Float.parseFloat(payementLimitRefundInEuros)) {
                    appointService.generateAppointForInvoice(correspondingInvoices.get(i), deposit.getOriginPayment(),
                            deposit, remainingMoney);
                    deposit.setDepositAmount(remainingMoney - deposit.getDepositAmount());
                    addOrUpdateDeposit(deposit);
                    accountingRecordService.checkInvoiceForLettrage(correspondingInvoices.get(i));
                    remainingMoney = 0f;
                }
            }
            correspondingInvoiceSize += correspondingInvoices.size();
        }

        if (correspondingCustomerOrder != null)
            for (int i = 0; i < correspondingCustomerOrder.size(); i++) {
                CustomerOrder customerOrder = correspondingCustomerOrder.get(i);
                Float remainingToPayForCustomerOrder = byPassAmount.get(i + correspondingInvoiceSize);

                if (Math.abs(remainingToPayForCustomerOrder - remainingMoney) <= Float
                        .parseFloat(payementLimitRefundInEuros)) {
                    // Appoint handled when invoice created
                    remainingToPayForCustomerOrder = remainingMoney;
                }

                getNewDepositForCustomerOrder(remainingToPayForCustomerOrder, LocalDateTime.now(), customerOrder,
                        deposit.getId(), deposit.getOriginPayment(), false);

                // Try unlocked customer order
                customerOrderService.unlockCustomerOrderFromDeposit(correspondingCustomerOrder.get(i));

                remainingMoney -= remainingToPayForCustomerOrder;
                refundLabelSuffix = "commande n°" + customerOrder.getId();
            }

        if (Math.abs(Math.round(remainingMoney * 100f) / 100f) > 0) {
            if (tiersRefund == null)
                throw new OsirisValidationException("TiersRefund or ConfrereRefund");

            // Try to find a customerOrder ...
            CustomerOrder customerOrder = null;
            if (correspondingCustomerOrder != null && correspondingCustomerOrder.size() > 0)
                customerOrder = correspondingCustomerOrder.get(0);
            else if (correspondingInvoices != null && correspondingInvoices.size() > 0)
                for (Invoice invoice : correspondingInvoices)
                    if (invoice.getCustomerOrder() != null)
                        customerOrder = invoice.getCustomerOrder();
                    else if (invoice.getCustomerOrderForInboundInvoice() != null)
                        customerOrder = invoice.getCustomerOrderForInboundInvoice();

            Refund refund = refundService.generateRefund(tiersRefund, affaireRefund, null, deposit, remainingMoney,
                    refundLabelSuffix, customerOrder, null);
            accountingRecordService.generateAccountingRecordsForRefundOnGeneration(refund);
        }
    }

}
