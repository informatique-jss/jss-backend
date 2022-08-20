package com.jss.osiris.modules.accounting.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.JacksonLocalDateTimeSerializer;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.Invoice;
import com.jss.osiris.modules.quotation.model.InvoiceItem;

@Entity
public class AccountingRecord implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
	private LocalDateTime accountingDateTime;

	@Column(nullable = false)
	@JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
	private LocalDateTime operationDateTime;

	private String manualAccountingDocumentNumber;
	private String label;
	private Float creditAmount;
	private Float debitAmount;

	@ManyToOne
	@JoinColumn(name = "id_accounting_account")
	private AccountingAccount accountingAccount;

	@Column(nullable = false)
	private Boolean isTemporary;
	// TODO : batch de passage à false chaque jour à minuit + qui passe accounting
	// datetime à sysdate

	@ManyToOne
	@JoinColumn(name = "id_invoice_item")
	private InvoiceItem invoiceItem;

	@OneToOne
	@JoinColumn(name = "id_invoice")
	private Invoice invoice;

	@ManyToOne
	@JoinColumn(name = "id_accounting_journal")
	private AccountingJournal accountingJournal;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalDateTime getAccountingDateTime() {
		return accountingDateTime;
	}

	public void setAccountingDateTime(LocalDateTime accountingDateTime) {
		this.accountingDateTime = accountingDateTime;
	}

	public LocalDateTime getOperationDateTime() {
		return operationDateTime;
	}

	public void setOperationDateTime(LocalDateTime operationDateTime) {
		this.operationDateTime = operationDateTime;
	}

	public Boolean getIsTemporary() {
		return isTemporary;
	}

	public void setIsTemporary(Boolean isTemporary) {
		this.isTemporary = isTemporary;
	}

	public String getManualAccountingDocumentNumber() {
		return manualAccountingDocumentNumber;
	}

	public void setManualAccountingDocumentNumber(String manualAccountingDocumentNumber) {
		this.manualAccountingDocumentNumber = manualAccountingDocumentNumber;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Float getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(Float creditAmount) {
		this.creditAmount = creditAmount;
	}

	public Float getDebitAmount() {
		return debitAmount;
	}

	public void setDebitAmount(Float debitAmount) {
		this.debitAmount = debitAmount;
	}

	public AccountingAccount getAccountingAccount() {
		return accountingAccount;
	}

	public void setAccountingAccount(AccountingAccount accountingAccount) {
		this.accountingAccount = accountingAccount;
	}

	public InvoiceItem getInvoiceItem() {
		return invoiceItem;
	}

	public void setInvoiceItem(InvoiceItem invoiceItem) {
		this.invoiceItem = invoiceItem;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public AccountingJournal getAccountingJournal() {
		return accountingJournal;
	}

	public void setAccountingJournal(AccountingJournal accountingJournal) {
		this.accountingJournal = accountingJournal;
	}

}
