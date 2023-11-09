package com.jss.osiris.modules.accounting.model;

import java.time.LocalDateTime;

public class AccountingRecordSearch {
  private AccountingAccountClass accountingClass;
  private AccountingAccount accountingAccount;
  private AccountingJournal accountingJournal;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private Boolean hideLettered;
  private Integer confrereId;
  private Integer tiersId;

  private Integer idPayment;
  private Integer idCustomerOrder;
  private Integer idInvoice;
  private Integer idRefund;
  private Integer idBankTransfert;

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

  public Integer getIdPayment() {
    return idPayment;
  }

  public void setIdPayment(Integer idPayment) {
    this.idPayment = idPayment;
  }

  public Integer getIdCustomerOrder() {
    return idCustomerOrder;
  }

  public void setIdCustomerOrder(Integer idCustomerOrder) {
    this.idCustomerOrder = idCustomerOrder;
  }

  public Integer getIdInvoice() {
    return idInvoice;
  }

  public void setIdInvoice(Integer idInvoice) {
    this.idInvoice = idInvoice;
  }

  public Integer getIdRefund() {
    return idRefund;
  }

  public void setIdRefund(Integer idRefund) {
    this.idRefund = idRefund;
  }

  public Integer getIdBankTransfert() {
    return idBankTransfert;
  }

  public void setIdBankTransfert(Integer idBankTransfert) {
    this.idBankTransfert = idBankTransfert;
  }

}
