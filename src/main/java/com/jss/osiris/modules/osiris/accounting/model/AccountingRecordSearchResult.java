package com.jss.osiris.modules.osiris.accounting.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface AccountingRecordSearchResult {
  Integer getRecordId();

  Integer getId();

  Integer getOperationId();

  Integer getTemporaryOperationId();

  LocalDateTime getAccountingDateTime();

  LocalDateTime getOperationDateTime();

  String getAccountingJournalLabel();

  String getAccountingJournalCode();

  String getPrincipalAccountingAccountCode();

  String getAccountingAccountSubNumber();

  String getAccountingAccountLabel();

  String getManualAccountingDocumentNumber();

  LocalDate getManualAccountingDocumentDate();

  BigDecimal getDebitAmount();

  BigDecimal getCreditAmount();

  String getLabel();

  Integer getLetteringNumber();

  LocalDateTime getLetteringDate();

  Integer getInvoiceId();

  Integer getCustomerId();

  Integer getPaymentId();

  String getAffaireLabel();

  String getResponsable();

  Boolean getIsTemporary();

  Boolean getIsFromAs400();

  Boolean getIsManual();
}
