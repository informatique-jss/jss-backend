package com.jss.osiris.modules.accounting.model;

public class AccountingAccountBinome {
    private AccountingAccount accountingAccountProduct;
    private AccountingAccount accountingAccountCharge;

    public AccountingAccount getAccountingAccountProduct() {
        return accountingAccountProduct;
    }

    public void setAccountingAccountProduct(AccountingAccount accountingAccountProduct) {
        this.accountingAccountProduct = accountingAccountProduct;
    }

    public AccountingAccount getAccountingAccountCharge() {
        return accountingAccountCharge;
    }

    public void setAccountingAccountCharge(AccountingAccount accountingAccountCharge) {
        this.accountingAccountCharge = accountingAccountCharge;
    }

}
