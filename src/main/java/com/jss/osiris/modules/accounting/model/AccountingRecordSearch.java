package com.jss.osiris.modules.accounting.model;

import java.time.LocalDateTime;

public class AccountingRecordSearch {
  private AccountingAccountClass accountingClass;
  private AccountingAccount accountingAccount;
  private AccountingJournal accountingJournal;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private Boolean hideLettered;
  private Integer responsableId;
  private Integer confrereId;
  private Integer tiersId;

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

  public AccountingJournal getAccountingJournal() {
    return accountingJournal;
  }

  public void setAccountingJournal(AccountingJournal accountingJournal) {
    this.accountingJournal = accountingJournal;
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

  public Integer getResponsableId() {
    return responsableId;
  }

  public void setResponsableId(Integer responsableId) {
    this.responsableId = responsableId;
  }

  public Integer getTiersId() {
    return tiersId;
  }

  public void setTiersId(Integer tiersId) {
    this.tiersId = tiersId;
  }

  public Boolean getHideLettered() {
    return hideLettered;
  }

  public void setHideLettered(Boolean hideLettered) {
    this.hideLettered = hideLettered;
  }

  public Integer getConfrereId() {
    return confrereId;
  }

  public void setConfrereId(Integer confrereId) {
    this.confrereId = confrereId;
  }

}
