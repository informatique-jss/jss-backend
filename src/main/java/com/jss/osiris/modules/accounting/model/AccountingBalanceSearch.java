package com.jss.osiris.modules.accounting.model;

import java.time.LocalDateTime;
import java.util.List;

public class AccountingBalanceSearch {
  private AccountingAccountClass accountingClass;
  private AccountingAccount accountingAccount;
  private List<PrincipalAccountingAccount> principalAccountingAccounts;

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

  public List<PrincipalAccountingAccount> getPrincipalAccountingAccounts() {
    return principalAccountingAccounts;
  }

  public void setPrincipalAccountingAccounts(List<PrincipalAccountingAccount> principalAccountingAccounts) {
    this.principalAccountingAccounts = principalAccountingAccounts;
  }

}
