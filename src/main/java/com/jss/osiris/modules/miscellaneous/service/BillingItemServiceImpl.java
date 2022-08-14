package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingAccountClass;
import com.jss.osiris.modules.accounting.service.AccountingAccountClassService;
import com.jss.osiris.modules.accounting.service.AccountingAccountService;
import com.jss.osiris.modules.miscellaneous.model.BillingItem;
import com.jss.osiris.modules.miscellaneous.model.BillingType;
import com.jss.osiris.modules.miscellaneous.repository.BillingItemRepository;

@Service
public class BillingItemServiceImpl implements BillingItemService {

    @Autowired
    BillingItemRepository billingItemRepository;

    @Autowired
    AccountingAccountService accountingAccountService;

    @Autowired
    AccountingAccountClassService accountingAccountClassService;

    @Override
    public List<BillingItem> getBillingItems() {
        return IterableUtils.toList(billingItemRepository.findAll());
    }

    @Override
    public BillingItem getBillingItem(Integer id) {
        Optional<BillingItem> billingItem = billingItemRepository.findById(id);
        if (!billingItem.isEmpty())
            return billingItem.get();
        return null;
    }

    @Override
    public List<BillingItem> getBillingItemByBillingType(BillingType billingType) {
        return billingItemRepository.findByBillingType(billingType);
    }

    @Override
    public BillingItem addOrUpdateBillingItem(
            BillingItem billingItem) throws Exception {
        return addOrUpdateBillingItem(billingItem, false);
    }

    @Override
    public BillingItem addOrUpdateBillingItem(
            BillingItem billingItem, boolean mustBeChargeOrProduct) throws Exception {
        BillingItem currentBillingItem;

        if (accountingAccountClassService.getAccountingAccountClassByCode("6") == null
                || accountingAccountClassService.getAccountingAccountClassByCode("7") == null)
            throw new Exception("Accounting account class not found for product or charge");

        AccountingAccountClass classCharge = accountingAccountClassService.getAccountingAccountClassByCode("6").get(0);
        AccountingAccountClass classProduct = accountingAccountClassService.getAccountingAccountClassByCode("7")
                .get(0);

        currentBillingItem = billingItemRepository.save(billingItem);
        for (AccountingAccount accountingAccount : billingItem.getAccountingAccounts()) {
            AccountingAccount currentAccountingAccount = null;
            if (currentBillingItem.getAccountingAccounts() != null
                    && currentBillingItem.getAccountingAccounts().size() > 0) {
                for (AccountingAccount accountingAccountCurrent : currentBillingItem.getAccountingAccounts()) {
                    if (accountingAccount.getAccountingAccountNumber()
                            .equals(accountingAccountCurrent.getAccountingAccountNumber()))
                        currentAccountingAccount = accountingAccountCurrent;
                }
            }

            if (currentAccountingAccount == null) {
                currentAccountingAccount = accountingAccount;
                AccountingAccountClass currentClass = null;
                if (accountingAccount.getAccountingAccountNumber().substring(0, 1).equals("6"))
                    currentClass = classCharge;
                if (accountingAccount.getAccountingAccountNumber().substring(0, 1).equals("7"))
                    currentClass = classProduct;
                if (currentClass == null && !mustBeChargeOrProduct) {
                    List<AccountingAccountClass> classes = accountingAccountClassService
                            .getAccountingAccountClassByCode(
                                    accountingAccount.getAccountingAccountNumber().substring(0, 1));
                    if (classes != null) {
                        currentClass = classes.get(0);
                    }
                }
                if (currentClass == null)
                    throw new Exception("Accounting account class not found");

                accountingAccount.setAccountingAccountClass(currentClass);
            }
            accountingAccount.setCode(accountingAccount.getAccountingAccountNumber());
            accountingAccount.setLabel(billingItem.getBillingType().getLabel());
            accountingAccountService.addOrUpdateAccountingAccount(accountingAccount);
        }
        return this.getBillingItem(billingItem.getId());
    }
}
