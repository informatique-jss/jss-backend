package com.jss.osiris.modules.accounting.service;

import java.util.List;

import com.jss.osiris.modules.accounting.model.AccountingAccount;

public interface AccountingAccountService {
    public List<AccountingAccount> getAccountingAccounts();

    public AccountingAccount getAccountingAccount(Integer id);

    public AccountingAccount addOrUpdateAccountingAccount(AccountingAccount accountingAccount);

    public List<AccountingAccount> getAccountingAccountByLabelOrCode(String label);
}
