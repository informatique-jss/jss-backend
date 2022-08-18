package com.jss.osiris.modules.accounting.service;

import java.util.List;

import com.jss.osiris.modules.accounting.model.AccountingAccountClass;

public interface AccountingAccountClassService {
    public List<AccountingAccountClass> getAccountingAccountClasses();

    public AccountingAccountClass getAccountingAccountClass(Integer id);

    public AccountingAccountClass getAccountingAccountClassByCode(String code);

    public AccountingAccountClass addOrUpdateAccountingAccountClass(AccountingAccountClass accountingAccountClass);
}
