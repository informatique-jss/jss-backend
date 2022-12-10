package com.jss.osiris.modules.accounting.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface AccountingRecordSearchResult {
  Integer getId();

  Integer getOperationId();

  LocalDateTime getAccountingDateTime();

  LocalDate getOperationDateTime();

  String getAccountingJournalLabel();

  String getAccountingJournalCode();

  String getAccountingAccountNumber();

  Integer getAccountingAccountSubNumber();

  String getAccountingAccountLabel();

  String getManualAccountingDocumentNumber();

  LocalDate getManualAccountingDocumentDate();

  Float getDebitAmount();

  Float getCreditAmount();

  String getLabel();

  Integer getLetteringNumber();

  LocalDateTime getLetteringDate();

  Integer getInvoiceId();

  Integer getCustomerId();

  Integer getPaymentId();

  Integer getDepositId();

  Integer getContrePasseOperationId();

  String getResponsable();
}
