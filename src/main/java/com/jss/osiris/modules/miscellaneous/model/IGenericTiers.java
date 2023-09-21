package com.jss.osiris.modules.miscellaneous.model;

import com.jss.osiris.modules.accounting.model.AccountingAccount;

public interface IGenericTiers extends IId {
    public Country getCountry();

    public City getCity();

    public AccountingAccount getAccountingAccountProvider();

    public AccountingAccount getAccountingAccountCustomer();

    public AccountingAccount getAccountingAccountDeposit();
}
