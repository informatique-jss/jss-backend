package com.jss.osiris.modules.accounting.model;

public class AccountingAccountTrouple {
    private AccountingAccount accountingAccountProvider;
    private AccountingAccount accountingAccountCustomer;
    private AccountingAccount accountingAccountDeposit;

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

    public AccountingAccount getAccountingAccountDeposit() {
        return accountingAccountDeposit;
    }

    public void setAccountingAccountDeposit(AccountingAccount accountingAccountDeposit) {
        this.accountingAccountDeposit = accountingAccountDeposit;
    }

}
