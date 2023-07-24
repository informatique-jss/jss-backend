package com.jss.osiris.modules.miscellaneous.model;

import com.jss.osiris.modules.accounting.model.AccountingAccount;

public interface IVat {
    public Country getCountry();

    public City getCity();

    public AccountingAccount getAccountingAccountProvider();

    public AccountingAccount getAccountingAccountCustomer();

    public AccountingAccount getAccountingAccountDeposit();
}
