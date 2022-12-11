package com.jss.osiris.modules.invoicing.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.Deposit;
import com.jss.osiris.modules.invoicing.model.Invoice;
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

    @Value("${invoicing.payment.limit.refund.euros}")
    private String payementLimitRefundInEuros;

    @Override
    public List<Deposit> getDeposits() {
        return IterableUtils.toList(depositRepository.findAll());
    }

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
            Integer overrideAccountingOperationId) throws OsirisException {
        Deposit deposit = new Deposit();
        deposit.setDepositAmount(depositAmount);
        deposit.setDepositDate(depositDatetime);
        deposit.setLabel("Acompte pour la facture n°" + invoice.getId());
        deposit = addOrUpdateDeposit(deposit);
        accountingRecordService.generateAccountingRecordsForTemporaryDepositForInvoice(deposit, invoice,
                overrideAccountingOperationId);
        return getDeposit(deposit.getId());
    }

    @Override
    public Deposit getNewDepositForCustomerOrder(Float depositAmount, LocalDateTime depositDatetime,
            CustomerOrder customerOrder, Integer overrideAccountingOperationId) throws OsirisException {
        Deposit deposit = new Deposit();
        deposit.setDepositAmount(depositAmount);
        deposit.setDepositDate(depositDatetime);
        deposit.setLabel("Acompte pour la commande n°" + customerOrder.getId());
        deposit = addOrUpdateDeposit(deposit);
        accountingRecordService.generateAccountingRecordsForDepositAndCustomerOrder(deposit, customerOrder,
                overrideAccountingOperationId);
        return getDeposit(deposit.getId());
    }

    @Override
    public void moveDepositFromCustomerOrderToInvoice(Deposit deposit, CustomerOrder fromCustomerOrder,
            Invoice toInvoice) throws OsirisException {
        deposit.setCustomerOrder(null);
        deposit.setInvoice(toInvoice);
        addOrUpdateDeposit(deposit);
        if (deposit.getAccountingRecords() != null)
            for (AccountingRecord accountingRecord : deposit.getAccountingRecords()) {
                accountingRecordService.generateCounterPart(accountingRecord);
            }
        accountingRecordService.generateAccountingRecordsForDepositOnInvoice(deposit, toInvoice, null);
    }

    @Override
    public void moveDepositFromInvoiceToCustomerOrder(Deposit deposit, Invoice fromInvoice,
            CustomerOrder toCustomerOrder) throws OsirisException {
        deposit.setCustomerOrder(toCustomerOrder);
        deposit.setInvoice(null);
        addOrUpdateDeposit(deposit);
        if (deposit.getAccountingRecords() != null)
            for (AccountingRecord accountingRecord : deposit.getAccountingRecords()) {
                accountingRecordService.generateCounterPart(accountingRecord);
            }
        accountingRecordService.generateAccountingRecordsForTemporaryDepositForInvoice(deposit, fromInvoice, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void manualMatchDepositInvoicesAndGenerateDepositAccountingRecords(Deposit deposit,
            List<Invoice> correspondingInvoices,
            List<CustomerOrder> correspondingCustomerOrder, Affaire affaireRefund, ITiers tiersRefund,
            List<Float> byPassAmount)
            throws OsirisException, OsirisClientMessageException {

        float remainingMoney = deposit.getDepositAmount();

        // Cancel account records for deposit
        if (deposit.getAccountingRecords() != null)
            for (AccountingRecord accountingRecord : deposit.getAccountingRecords()) {
                accountingRecordService.generateCounterPart(accountingRecord);
            }

        // If deposit already associated to a choosen invoice or customerOrder reduce
        // amount to byPassAmount
        Integer idFound = null;
        Integer correspondingInvoiceSize = 0;
        if (correspondingInvoices != null)
            for (int i = 0; i < correspondingInvoices.size(); i++) {
                Invoice invoice = correspondingInvoices.get(i);
                if (invoice.getId().equals(deposit.getInvoice().getId())) {
                    deposit.setDepositAmount(byPassAmount.get(i));
                    idFound = invoice.getId();
                    remainingMoney -= accountingRecordService.getRemainingAmountToPayForInvoice(invoice);
                    break;
                }
            }

        if (idFound == null && correspondingCustomerOrder != null)
            for (int i = 0; i < correspondingCustomerOrder.size(); i++) {
                CustomerOrder customerOrder = correspondingCustomerOrder.get(i);
                if (customerOrder.getId().equals(deposit.getCustomerOrder().getId())) {
                    deposit.setDepositAmount(byPassAmount.get(i + correspondingInvoiceSize));
                    idFound = customerOrder.getId();
                    remainingMoney -= accountingRecordService.getRemainingAmountToPayForCustomerOrder(customerOrder);
                    break;
                }
            }

        // Cancel deposit
        if (idFound == null) {
            deposit.setInvoice(null);
            deposit.setCustomerOrder(null);
        }
        addOrUpdateDeposit(deposit);

        if (correspondingInvoices != null)
            for (int i = 0; i < correspondingInvoices.size(); i++) {
                Invoice invoice = correspondingInvoices.get(i);
                if (!invoice.getId().equals(idFound)) {
                    List<Deposit> invoiceDeposits = new ArrayList<Deposit>();
                    Float remainingToPayForInvoice = Math
                            .min(accountingRecordService.getRemainingAmountToPayForInvoice(invoice),
                                    byPassAmount.get(i));
                    invoiceDeposits
                            .add(getNewDepositForInvoice(remainingToPayForInvoice, LocalDateTime.now(), invoice,
                                    deposit.getId()));
                    accountingRecordService.generateAccountingRecordsForSaleOnInvoicePayment(
                            correspondingInvoices.get(i), null, invoiceDeposits,
                            byPassAmount.get(i));

                    invoiceDeposits.get(0).setInvoice(invoice);
                    addOrUpdateDeposit(invoiceDeposits.get(0));

                    if (correspondingInvoices.get(i).getDeposits() == null)
                        correspondingInvoices.get(i).setDeposits(invoiceDeposits);
                    else
                        correspondingInvoices.get(i).getDeposits().add(invoiceDeposits.get(0));

                    invoiceService.addOrUpdateInvoice(correspondingInvoices.get(i));

                    if (remainingToPayForInvoice == 0) {
                        invoice.setInvoiceStatus(constantService.getInvoiceStatusPayed());
                        invoiceService.addOrUpdateInvoice(invoice);
                    }

                    remainingMoney -= byPassAmount.get(i);
                }
            }

        if (correspondingCustomerOrder != null)
            for (int i = 0; i < correspondingCustomerOrder.size(); i++) {
                CustomerOrder customerOrder = correspondingCustomerOrder.get(i);
                if (!customerOrder.getId().equals(idFound)) {
                    List<Deposit> customerOrderDeposits = new ArrayList<Deposit>();
                    Float remainingToPayForCustomerOrder = Math.min(accountingRecordService
                            .getRemainingAmountToPayForCustomerOrder(customerOrder),
                            byPassAmount.get(i + correspondingInvoiceSize));
                    Deposit newDeposit = getNewDepositForCustomerOrder(remainingToPayForCustomerOrder,
                            LocalDateTime.now(),
                            customerOrder, deposit.getId());
                    customerOrderDeposits
                            .add(newDeposit);

                    newDeposit.setCustomerOrder(correspondingCustomerOrder.get(i));
                    addOrUpdateDeposit(newDeposit);

                    if (correspondingCustomerOrder.get(i).getDeposits() == null)
                        correspondingCustomerOrder.get(i).setDeposits(customerOrderDeposits);
                    else
                        correspondingCustomerOrder.get(i).getDeposits().add(customerOrderDeposits.get(0));

                    customerOrderService.addOrUpdateCustomerOrder(correspondingCustomerOrder.get(i));

                    // Try unlocked customer order
                    customerOrderService.unlockCustomerOrderFromDeposit(correspondingCustomerOrder.get(i),
                            remainingToPayForCustomerOrder);

                    remainingMoney -= byPassAmount.get(i + correspondingInvoiceSize);
                }
            }

        if (remainingMoney > 0) {
            if (Math.abs(remainingMoney) <= Float.parseFloat(payementLimitRefundInEuros)) {
                if (correspondingInvoices != null && correspondingInvoices.size() > 0)
                    accountingRecordService.generateAppointForDeposit(deposit, remainingMoney,
                            invoiceHelper.getCustomerOrder(correspondingInvoices.get(0)));
                else if (correspondingCustomerOrder != null && correspondingCustomerOrder.size() > 0)
                    accountingRecordService.generateAppointForDeposit(deposit, remainingMoney,
                            quotationService.getCustomerOrderOfQuotation(correspondingCustomerOrder.get(0)));
            } else {
                refundService.generateRefund(tiersRefund, affaireRefund, null, deposit, remainingMoney);
            }
        }
    }

}
