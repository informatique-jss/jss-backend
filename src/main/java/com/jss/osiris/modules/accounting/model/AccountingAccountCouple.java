package com.jss.osiris.modules.accounting.model;

public class AccountingAccountCouple {
    private AccountingAccount accountingAccountProvider;
    private AccountingAccount accountingAccountCustomer;

    public AccountingAccount getAccountingAccountProvider() {
        return accountingAccountProvider;
    }

    public void setAccountingAccountProvider(AccountingAccount accountingAccountProvider) {
        this.accountingAccountProvider = accountingAccountProvider;
    }

    public AccountingAccount getAccountingAccountCustomer() {
        return accountingAccountCustomer;
    }

    public void setAccountingAccountCustomer(AccountingAccount accountingAccountCustomer) {
        this.accountingAccountCustomer = accountingAccountCustomer;
    }

}
