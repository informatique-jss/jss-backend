package com.jss.osiris.modules.invoicing.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.miscellaneous.model.PaymentType;
import com.jss.osiris.modules.miscellaneous.model.Provider;
import com.jss.osiris.modules.quotation.model.BankTransfert;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.DirectDebitTransfert;
import com.jss.osiris.modules.quotation.model.Provision;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(indexes = { @Index(name = "idx_bank_id", columnList = "bankId", unique = true),
		@Index(name = "idx_payment_id_invoice", columnList = "id_invoice"),
		@Index(name = "idx_payment_id_refund", columnList = "id_refund"),
		@Index(name = "idx_payment_id_provision", columnList = "id_provision"),
		@Index(name = "idx_payment_id_direct_debit_transfert", columnList = "id_direct_debit_transfert"),
		@Index(name = "idx_payment_id_bank_transfert", columnList = "id_bank_transfert"),
		@Index(name = "idx_payment_id_customer_order", columnList = "id_customer_order"),
		@Index(name = "idx_payment_id_origin_payment", columnList = "id_origin_payment")
})
public class Payment implements Serializable, IId, ICreatedDate {

	@Id
	@SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
	@IndexedField
	private Integer id;

	@IndexedField
	private String bankId;

	@Column(nullable = false, columnDefinition = "TEXT")
	@IndexedField
	private String label;

	@Column(nullable = false)
	@IndexedField
	private LocalDateTime paymentDate;

	@Column(nullable = false)
	@IndexedField
	private Float paymentAmount;

	@OneToMany(mappedBy = "payment")
	@JsonIgnoreProperties(value = { "payment" }, allowSetters = true)
	private List<AccountingRecord> accountingRecords;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_invoice")
	@JsonIgnoreProperties(value = { "payments", "accountingRecords" }, allowSetters = true)
	private Invoice invoice;

	@Column(columnDefinition = "TEXT")
	private String comment;

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
	@JsonIgnoreProperties(value = { "accountingRecords", "customerOrder", "payments" }, allowSetters = true)
	private BankTransfert bankTransfert;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_direct_debit_transfert")
	@JsonIgnoreProperties(value = { "accountingRecords", "payments" }, allowSetters = true)
	private DirectDebitTransfert directDebitTransfert;

	@Column(nullable = false)
	private Boolean isExternallyAssociated;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_payment_type")
	@IndexedField
	private PaymentType paymentType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_origin_payment")
	@JsonIgnoreProperties(value = { "invoice", "customerOrder", "provision", "accountingRecords",
			"childrenPayments" }, allowSetters = true)
	private Payment originPayment;

	@OneToMany(targetEntity = Payment.class, mappedBy = "originPayment")
	@JsonIgnoreProperties(value = { "originPayment", "accountingRecords" }, allowSetters = true)
	private List<Payment> childrenPayments;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_target_accounting_account")
	private AccountingAccount targetAccountingAccount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_source_accounting_account")
	private AccountingAccount sourceAccountingAccount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_competent_authority")
	private CompetentAuthority competentAuthority;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_accounting_account")
	private AccountingAccount accountingAccount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_provider")
	private Provider provider;

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

	public void setCreatedDate(LocalDateTime createdDate) {
		setPaymentDate(createdDate);
		;
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

	public CompetentAuthority getCompetentAuthority() {
		return competentAuthority;
	}

	public void setCompetentAuthority(CompetentAuthority competentAuthority) {
		this.competentAuthority = competentAuthority;
	}

	public DirectDebitTransfert getDirectDebitTransfert() {
		return directDebitTransfert;
	}

	public void setDirectDebitTransfert(DirectDebitTransfert directDebitTransfert) {
		this.directDebitTransfert = directDebitTransfert;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public AccountingAccount getAccountingAccount() {
		return accountingAccount;
	}

	public void setAccountingAccount(AccountingAccount accountingAccount) {
		this.accountingAccount = accountingAccount;
	}

}
