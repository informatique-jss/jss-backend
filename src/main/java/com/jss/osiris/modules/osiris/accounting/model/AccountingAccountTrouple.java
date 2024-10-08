package com.jss.osiris.modules.osiris.accounting.model;

public class AccountingAccountTrouple {
    private AccountingAccount accountingAccountProvider;
    private AccountingAccount accountingAccountCustomer;
    private AccountingAccount accountingAccountDeposit;
    private AccountingAccount accountingAccountLitigious;
    private AccountingAccount accountingAccountSuspicious;

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

    public AccountingAccount getAccountingAccountLitigious() {
        return accountingAccountLitigious;
    }

    public void setAccountingAccountLitigious(AccountingAccount accountingAccountLitigious) {
        this.accountingAccountLitigious = accountingAccountLitigious;
    }

    public AccountingAccount getAccountingAccountSuspicious() {
        return accountingAccountSuspicious;
    }

    public void setAccountingAccountSuspicious(AccountingAccount accountingAccountSuspicious) {
        this.accountingAccountSuspicious = accountingAccountSuspicious;
    }
}
