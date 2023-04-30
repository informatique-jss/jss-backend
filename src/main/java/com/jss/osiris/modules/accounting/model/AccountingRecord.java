package com.jss.osiris.modules.accounting.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.JacksonLocalDateTimeSerializer;
import com.jss.osiris.modules.invoicing.model.Deposit;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.Refund;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Debour;

@Entity
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
	private Float creditAmount;
	private Float debitAmount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_accounting_account")
	private AccountingAccount accountingAccount;

	@Column(nullable = false)
	private Boolean isTemporary;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_invoice_item")
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
	@JsonIgnoreProperties(value = { "accountingRecords", "invoice", "customerOrder" }, allowSetters = true)
	private Payment payment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_deposit")
	@JsonIgnoreProperties(value = { "accountingRecords", "customerOrder" }, allowSetters = true)
	private Deposit deposit;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_debour")
	@JsonIgnoreProperties(value = { "accountingRecords", "customerOrder" }, allowSetters = true)
	private Debour debour;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_refund")
	@JsonIgnoreProperties(value = { "accountingRecords", "tiers", "confrere", "affaire", "payment", "customerOrder",
			"deposit" }, allowSetters = true)
	private Refund refund;

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

	public Deposit getDeposit() {
		return deposit;
	}

	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}

	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	public void setDeposit(Deposit deposit) {
		this.deposit = deposit;
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

	public Debour getDebour() {
		return debour;
	}

	public void setDebour(Debour debour) {
		this.debour = debour;
	}

	public Refund getRefund() {
		return refund;
	}

	public void setRefund(Refund refund) {
		this.refund = refund;
	}

}
