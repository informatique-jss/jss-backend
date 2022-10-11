package com.jss.osiris.modules.accounting.service;

import java.util.List;

import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingAccountTrouple;

public interface AccountingAccountService {
    public List<AccountingAccount> getAccountingAccounts();

    public AccountingAccount getAccountingAccount(Integer id);

    public AccountingAccount addOrUpdateAccountingAccount(AccountingAccount accountingAccount) throws Exception;

    public List<AccountingAccount> getAccountingAccountByLabelOrCode(String label);

    public List<AccountingAccount> getAccountingAccountByAccountingAccountNumber(String accountingAccountNumber);

    /**
     * Generate provider and customer accounting accounts for an entity
     * WARNING ! accounting accounts are persisted after operation but not
     * associated to entity : it must be done in called method
     * 
     * @param label Custom label. Leave null will put account number as
     *              label
     * 
     */
    public AccountingAccountTrouple generateAccountingAccountsForEntity(String label) throws Exception;

    public AccountingAccount getProductAccountingAccountFromAccountingAccountList(
            List<AccountingAccount> accountingAccounts);

    public AccountingAccount getBankAccountingAccount() throws Exception;

    public AccountingAccount getWaitingAccountingAccount() throws Exception;

    public AccountingAccount generateAccountingAccountsForProduct(String label) throws Exception;

    public AccountingAccount getProfitAccountingAccount() throws Exception;

    public AccountingAccount getLostAccountingAccount() throws Exception;
}
