package com.jss.osiris.modules.invoicing.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.miscellaneous.model.PaymentType;
import com.jss.osiris.modules.quotation.model.BankTransfert;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Provision;

@Entity
@Table(indexes = { @Index(name = "idx_bank_id", columnList = "bankId", unique = true),
		@Index(name = "idx_payment_id_invoice", columnList = "id_invoice") })
public class Payment implements Serializable, IId, ICreatedDate {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private String bankId;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String label;

	@Column(nullable = false)
	private LocalDateTime paymentDate;

	@Column(nullable = false)
	private Float paymentAmount;

	@OneToMany(mappedBy = "payment")
	@JsonIgnoreProperties(value = { "payment" }, allowSetters = true)
	private List<AccountingRecord> accountingRecords;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_invoice")
	@JsonIgnoreProperties(value = { "payments", "accountingRecords" }, allowSetters = true)
	private Invoice invoice;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_customer_order")
	@JsonIgnoreProperties(value = { "payments", "accountingRecords" }, allowSetters = true)
	private CustomerOrder customerOrder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_refund")
	@JsonIgnoreProperties(value = { "accountingRecords", "payments" }, allowSetters = true)
	private Refund refund;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_bank_transfert")
	@JsonIgnoreProperties(value = { "accountingRecords", "payments" }, allowSetters = true)
	private BankTransfert bankTransfert;

	@Column(nullable = false)
	private Boolean isExternallyAssociated;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_payment_type")
	private PaymentType paymentType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_origin_payment")
	@JsonIgnoreProperties(value = { "invoice", "customerOrder", "provision", "accountingRecords",
			"childrenPayments" }, allowSetters = true)
	private Payment originPayment;

	@OneToMany(targetEntity = Payment.class, mappedBy = "originPayment")
	@JsonIgnoreProperties(value = { "originPayment", "accountingRecords" })
	private List<Payment> childrenPayments;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_target_accounting_account")
	private AccountingAccount targetAccountingAccount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_source_accounting_account")
	private AccountingAccount sourceAccountingAccount;

	private Boolean isCancelled;

	private Boolean isAppoint;

	private Boolean isDeposit;

	@IndexedField
	private String checkNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_provision")
	private Provision provision;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public LocalDateTime getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDateTime paymentDate) {
		this.paymentDate = paymentDate;
	}

	public Float getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(Float paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public List<AccountingRecord> getAccountingRecords() {
		return accountingRecords;
	}

	public void setAccountingRecords(List<AccountingRecord> accountingRecords) {
		this.accountingRecords = accountingRecords;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public Boolean getIsExternallyAssociated() {
		return isExternallyAssociated;
	}

	public void setIsExternallyAssociated(Boolean isExternallyAssociated) {
		this.isExternallyAssociated = isExternallyAssociated;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public Payment getOriginPayment() {
		return originPayment;
	}

	public void setOriginPayment(Payment originPayment) {
		this.originPayment = originPayment;
	}

	public Boolean getIsCancelled() {
		return isCancelled;
	}

	public void setIsCancelled(Boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	public LocalDateTime getCreatedDate() {
		return getPaymentDate();
	}

	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}

	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	public Boolean getIsAppoint() {
		return isAppoint;
	}

	public void setIsAppoint(Boolean isAppoint) {
		this.isAppoint = isAppoint;
	}

	public Boolean getIsDeposit() {
		return isDeposit;
	}

	public void setIsDeposit(Boolean isDeposit) {
		this.isDeposit = isDeposit;
	}

	public AccountingAccount getTargetAccountingAccount() {
		return targetAccountingAccount;
	}

	public void setTargetAccountingAccount(AccountingAccount targetAccountingAccount) {
		this.targetAccountingAccount = targetAccountingAccount;
	}

	public Refund getRefund() {
		return refund;
	}

	public void setRefund(Refund refund) {
		this.refund = refund;
	}

	public String getCheckNumber() {
		return checkNumber;
	}

	public void setCheckNumber(String checkNumber) {
		this.checkNumber = checkNumber;
	}

	public BankTransfert getBankTransfert() {
		return bankTransfert;
	}

	public void setBankTransfert(BankTransfert bankTransfert) {
		this.bankTransfert = bankTransfert;
	}

	public Provision getProvision() {
		return provision;
	}

	public void setProvision(Provision provision) {
		this.provision = provision;
	}

	public AccountingAccount getSourceAccountingAccount() {
		return sourceAccountingAccount;
	}

	public void setSourceAccountingAccount(AccountingAccount sourceAccountingAccount) {
		this.sourceAccountingAccount = sourceAccountingAccount;
	}

	public List<Payment> getChildrenPayments() {
		return childrenPayments;
	}

	public void setChildrenPayments(List<Payment> childrenPayments) {
		this.childrenPayments = childrenPayments;
	}

}
