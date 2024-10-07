package com.jss.osiris.modules.osiris.accounting.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.jackson.JacksonLocalDateTimeSerializer;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.osiris.invoicing.model.Payment;
import com.jss.osiris.modules.osiris.invoicing.model.Refund;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.quotation.model.BankTransfert;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(indexes = {
		@Index(name = "idx_accounting_record_payment", columnList = "id_payment"),
		@Index(name = "idx_accounting_record_customer_order", columnList = "id_customer_order"),
		@Index(name = "idx_accounting_record_accounting_account", columnList = "id_accounting_account"),
		@Index(name = "idx_accounting_record_accounting_journal", columnList = "id_accounting_journal"),
		@Index(name = "idx_accounting_record_invoice_item", columnList = "id_invoice_item"),
		@Index(name = "idx_accounting_record_accounting_account", columnList = "id_accounting_account"),
		@Index(name = "idx_accounting_record_invoice", columnList = "id_invoice")
})
public class AccountingRecord implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "accounting_record_sequence", sequenceName = "accounting_record_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accounting_record_sequence")
	private Integer id;

	@JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
	private LocalDateTime accountingDateTime;
	private Integer accountingId;

	@Column(nullable = false)
	@JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
	private LocalDateTime operationDateTime;

	private Integer operationId;

	@Column(nullable = false)
	private Integer temporaryOperationId;

	@Column(length = 150)
	private String manualAccountingDocumentNumber;

	private LocalDate manualAccountingDocumentDate;

	private LocalDate manualAccountingDocumentDeadline;

	@Column(nullable = false, length = 1000)
	private String label;

	@Column(precision = 2)
	private Float creditAmount;

	@Column(precision = 2)
	private Float debitAmount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_accounting_account")
	private AccountingAccount accountingAccount;

	@Column(nullable = false)
	private Boolean isTemporary;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_invoice_item")
	@JsonIgnoreProperties(value = { "originProviderInvoice" }, allowSetters = true)
	private InvoiceItem invoiceItem;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_invoice")
	@JsonIgnoreProperties(value = { "accountingRecords", "payments",
			"deposits" }, allowSetters = true)
	private Invoice invoice;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_customer_order")
	@JsonIgnoreProperties(value = { "accountingRecords", "deposits", "payments" }, allowSetters = true)
	private CustomerOrder customerOrder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_payment")
	@JsonIgnoreProperties(value = { "accountingRecords", "invoice", "customerOrder", "originPayment",
			"childrenPayments" }, allowSetters = true)
	private Payment payment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_refund")
	@JsonIgnoreProperties(value = { "accountingRecords", "tiers", "confrere", "affaire", "payment", "customerOrder",
			"deposit" }, allowSetters = true)
	private Refund refund;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_bank_transfert")
	@JsonIgnoreProperties(value = { "accountingRecords", "tiers", "confrere", "affaire", "payment", "customerOrder",
			"deposit" }, allowSetters = true)
	private BankTransfert bankTransfert;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_accounting_journal")
	private AccountingJournal accountingJournal;

	@OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_contre_passe")
	@JsonIgnoreProperties(value = { "accountingRecords", "deposit", "payment", "invoice",
			"customerOrder" }, allowSetters = true)
	private AccountingRecord contrePasse;

	private Integer letteringNumber;

	private LocalDateTime letteringDateTime;

	private Boolean isCounterPart;

	@Column(nullable = false)
	private Boolean isANouveau;

	private Boolean isFromAs400;

	private Boolean isManual;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}

	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
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

	public Integer getLetteringNumber() {
		return letteringNumber;
	}

	public void setLetteringNumber(Integer letteringNumber) {
		this.letteringNumber = letteringNumber;
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

	public Integer getOperationId() {
		return operationId;
	}

	public void setOperationId(Integer operationId) {
		this.operationId = operationId;
	}

	public LocalDate getManualAccountingDocumentDate() {
		return manualAccountingDocumentDate;
	}

	public void setManualAccountingDocumentDate(LocalDate manualAccountingDocumentDate) {
		this.manualAccountingDocumentDate = manualAccountingDocumentDate;
	}

	public AccountingRecord getContrePasse() {
		return contrePasse;
	}

	public void setContrePasse(AccountingRecord contrePasse) {
		this.contrePasse = contrePasse;
	}

	public Integer getTemporaryOperationId() {
		return temporaryOperationId;
	}

	public void setTemporaryOperationId(Integer temporaryOperationId) {
		this.temporaryOperationId = temporaryOperationId;
	}

	public Integer getAccountingId() {
		return accountingId;
	}

	public void setAccountingId(Integer accountingId) {
		this.accountingId = accountingId;
	}

	public Boolean getIsANouveau() {
		return isANouveau;
	}

	public void setIsANouveau(Boolean isANouveau) {
		this.isANouveau = isANouveau;
	}

	public LocalDateTime getLetteringDateTime() {
		return letteringDateTime;
	}

	public void setLetteringDateTime(LocalDateTime letteringDateTime) {
		this.letteringDateTime = letteringDateTime;
	}

	public LocalDate getManualAccountingDocumentDeadline() {
		return manualAccountingDocumentDeadline;
	}

	public void setManualAccountingDocumentDeadline(LocalDate manualAccountingDocumentDeadline) {
		this.manualAccountingDocumentDeadline = manualAccountingDocumentDeadline;
	}

	public Boolean getIsCounterPart() {
		return isCounterPart;
	}

	public void setIsCounterPart(Boolean isCounterPart) {
		this.isCounterPart = isCounterPart;
	}

	public Refund getRefund() {
		return refund;
	}

	public void setRefund(Refund refund) {
		this.refund = refund;
	}

	public BankTransfert getBankTransfert() {
		return bankTransfert;
	}

	public void setBankTransfert(BankTransfert bankTransfert) {
		this.bankTransfert = bankTransfert;
	}

	public Boolean getIsFromAs400() {
		return isFromAs400;
	}

	public void setIsFromAs400(Boolean isFromAs400) {
		this.isFromAs400 = isFromAs400;
	}

	public Boolean getIsManual() {
		return isManual;
	}

	public void setIsManual(Boolean isManual) {
		this.isManual = isManual;
	}
}
