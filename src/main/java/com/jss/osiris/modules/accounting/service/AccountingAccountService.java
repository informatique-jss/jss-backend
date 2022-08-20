package com.jss.osiris.modules.accounting.service;

import java.util.List;

import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingAccountCouple;

public interface AccountingAccountService {
    public List<AccountingAccount> getAccountingAccounts();

    public AccountingAccount getAccountingAccount(Integer id);

    public AccountingAccount addOrUpdateAccountingAccount(AccountingAccount accountingAccount) throws Exception;

    public List<AccountingAccount> getAccountingAccountByLabelOrCode(String label);

    public AccountingAccount getAccountingAccountByAccountingAccountNumber(String accountingAccountNumber);

    /**
     * Generate provider and customer accounting accounts for an entity
     * WARNING ! accounting accounts are persisted after operation but not
     * associated to entity : it must be done in called method
     * 
     * @param label Custom label. Leave null will put account number as
     *              label
     * 
     */
    public AccountingAccountCouple generateAccountingAccountsForEntity(String label) throws Exception;

    public AccountingAccount getProductAccountingAccountFromAccountingAccountList(
            List<AccountingAccount> accountingAccounts);
}
