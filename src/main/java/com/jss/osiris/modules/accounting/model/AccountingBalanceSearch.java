package com.jss.osiris.modules.accounting.model;

import java.time.LocalDateTime;

public class AccountingBalanceSearch {
  private AccountingAccountClass accountingClass;
  private AccountingAccount accountingAccount;
  private PrincipalAccountingAccount principalAccountingAccount;

  private LocalDateTime startDate;
  private LocalDateTime endDate;

  private Boolean isFromAs400;

  public Boolean getIsFromAs400() {
    return isFromAs400;
  }

  public void setIsFromAs400(Boolean isFromAs400) {
    this.isFromAs400 = isFromAs400;
  }

  public AccountingAccountClass getAccountingClass() {
    return accountingClass;
  }

  public void setAccountingClass(AccountingAccountClass accountingClass) {
    this.accountingClass = accountingClass;
  }

  public AccountingAccount getAccountingAccount() {
    return accountingAccount;
  }

  public void setAccountingAccount(AccountingAccount accountingAccount) {
    this.accountingAccount = accountingAccount;
  }

  public LocalDateTime getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDateTime startDate) {
    this.startDate = startDate;
  }

  public LocalDateTime getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDateTime endDate) {
    this.endDate = endDate;
  }

  public PrincipalAccountingAccount getPrincipalAccountingAccount() {
    return principalAccountingAccount;
  }

  public void setPrincipalAccountingAccount(PrincipalAccountingAccount principalAccountingAccount) {
    this.principalAccountingAccount = principalAccountingAccount;
  }

}
