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

    public AccountingAccountCouple generateAccountingAccountsForEntity(String label) throws Exception;
}
