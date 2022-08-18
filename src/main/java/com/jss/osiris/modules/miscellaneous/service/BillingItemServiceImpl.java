package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(BillingItemServiceImpl.class);

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
                || accountingAccountClassService.getAccountingAccountClassByCode("7") == null) {
            logger.error("Accounting account class not found for product or charge");
            throw new Exception("Accounting account class not found for product or charge");
        }

        AccountingAccountClass classCharge = accountingAccountClassService.getAccountingAccountClassByCode("6");
        AccountingAccountClass classProduct = accountingAccountClassService.getAccountingAccountClassByCode("7");

        // Retrive existing accounting account
        if (billingItem != null && billingItem.getAccountingAccounts() != null) {
            for (AccountingAccount accountingAccount : billingItem.getAccountingAccounts()) {
                AccountingAccount foundAccountingAccount = accountingAccountService
                        .getAccountingAccountByAccountingAccountNumber(accountingAccount.getAccountingAccountNumber());
                if (foundAccountingAccount != null && foundAccountingAccount.getId() != null) {
                    accountingAccount.setId(foundAccountingAccount.getId());
                    accountingAccount.setAccountingAccountClass(foundAccountingAccount.getAccountingAccountClass());
                    accountingAccount.setLabel(foundAccountingAccount.getLabel());
                }
            }
        }

        currentBillingItem = billingItemRepository.save(billingItem);

        // Set accounting account class based on first number
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
                    currentClass = accountingAccountClassService
                            .getAccountingAccountClassByCode(
                                    accountingAccount.getAccountingAccountNumber().substring(0, 1));
                }
                if (currentClass == null) {
                    logger.error("Accounting account class not found for product or charge number "
                            + accountingAccount.getAccountingAccountNumber().substring(0, 1));
                    throw new Exception("Accounting account class not found");
                }

                accountingAccount.setAccountingAccountClass(currentClass);
            }
            accountingAccount.setLabel(billingItem.getBillingType().getLabel());
            accountingAccountService.addOrUpdateAccountingAccount(accountingAccount);
        }
        return this.getBillingItem(billingItem.getId());
    }
}
