package com.jss.osiris.modules.invoicing.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.Deposit;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.repository.DepositRepository;
import com.jss.osiris.modules.quotation.model.CustomerOrder;

@Service
public class DepositServiceImpl implements DepositService {

    @Autowired
    DepositRepository depositRepository;

    @Autowired
    AccountingRecordService accountingRecordService;

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
            Payment payment) throws OsirisException {
        Deposit deposit = new Deposit();
        deposit.setDepositAmount(depositAmount);
        deposit.setDepositDate(depositDatetime);
        deposit.setLabel("Acompte pour la facture n°" + invoice.getId());
        deposit = addOrUpdateDeposit(deposit);
        accountingRecordService.generateAccountingRecordsForDepositAndInvoice(deposit, invoice, payment);
        return getDeposit(deposit.getId());
    }

    @Override
    public Deposit getNewDepositForCustomerOrder(Float depositAmount, LocalDateTime depositDatetime,
            CustomerOrder customerOrder, Payment payment) throws OsirisException {
        Deposit deposit = new Deposit();
        deposit.setDepositAmount(depositAmount);
        deposit.setDepositDate(depositDatetime);
        deposit.setLabel("Acompte pour la commande n°" + customerOrder.getId());
        deposit = addOrUpdateDeposit(deposit);
        accountingRecordService.generateAccountingRecordsForDepositAndCustomerOrder(deposit, customerOrder, payment);
        return getDeposit(deposit.getId());
    }

}
