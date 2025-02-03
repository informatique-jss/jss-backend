package com.jss.osiris.modules.osiris.accounting.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.IndexedField;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;

public class SageRecord {

    public static String CREDIT_SAGE = "C";
    public static String DEBIT_SAGE = "D";

    private String targetAccountingAccountCode;

    private LocalDate operationDate;

    private String label;

    private String creditOrDebit;

    private BigDecimal creditAmount;

    private BigDecimal debitAmount;

    private LocalDateTime createdDate;

    @OneToMany(targetEntity = AccountingRecord.class, mappedBy = "sageRecord", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "sageRecord" }, allowSetters = true)
    @IndexedField
    private List<AccountingRecord> accountingRecords;

    public String getTargetAccountingAccountCode() {
        return targetAccountingAccountCode;
    }

    public void setTargetAccountingAccountCode(String sageAccount) {
        this.targetAccountingAccountCode = sageAccount;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCreditOrDebit() {
        return creditOrDebit;
    }

    public void setCreditOrDebit(String creditOrDebit) {
        this.creditOrDebit = creditOrDebit;
    }

    public BigDecimal getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(BigDecimal amount) {
        this.creditAmount = amount;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public List<AccountingRecord> getAccountingRecords() {
        return accountingRecords;
    }

    public void setAccountingRecords(List<AccountingRecord> accountingRecords) {
        this.accountingRecords = accountingRecords;
    }

    public LocalDate getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(LocalDate operationDate) {
        this.operationDate = operationDate;
    }

    public BigDecimal getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(BigDecimal debitAmount) {
        this.debitAmount = debitAmount;
    }

}
