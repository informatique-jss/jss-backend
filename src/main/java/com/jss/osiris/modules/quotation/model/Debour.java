package com.jss.osiris.modules.quotation.model;

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
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.miscellaneous.model.BillingType;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.miscellaneous.model.PaymentType;
import com.jss.osiris.modules.quotation.model.guichetUnique.CartRate;

@Entity
@Table(indexes = { @Index(name = "idx_debour_associated", columnList = "isAssociated") })
public class Debour implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@IndexedField
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_provision")
	@JsonIgnoreProperties(value = { "debours", "assoAffaireOrder" }, allowSetters = true)
	Provision provision;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_billing_type")
	private BillingType billingType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_competent_authority")
	@JsonIgnoreProperties(value = { "departments", "cities", "regions" }, allowSetters = true)
	private CompetentAuthority competentAuthority;

	private Float debourAmount;
	private Float invoicedAmount;

	private Float nonTaxableAmount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_payment_type")
	private PaymentType paymentType;

	private LocalDateTime paymentDateTime;

	private String comments;

	@Column(length = 100)
	@IndexedField
	private String checkNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_payment")
	@JsonIgnoreProperties(value = { "payment", "debours", "invoice" }, allowSetters = true)
	private Payment payment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_bank_transfert")
	@JsonIgnoreProperties(value = { "customerOrder" }, allowSetters = true)
	private BankTransfert bankTransfert;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_invoice_item")
	@JsonIgnoreProperties(value = { "provision", "debours" }, allowSetters = true)
	private InvoiceItem invoiceItem;

	@OneToMany(mappedBy = "debour")
	@JsonIgnoreProperties(value = { "debour", "customerOrder" }, allowSetters = true)
	private List<AccountingRecord> accountingRecords;

	private Boolean isAssociated;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cart_rate")
	@JsonIgnoreProperties(value = { "debours" }, allowSetters = true)
	private CartRate cartRate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Provision getProvision() {
		return provision;
	}

	public void setProvision(Provision provision) {
		this.provision = provision;
	}

	public BillingType getBillingType() {
		return billingType;
	}

	public void setBillingType(BillingType billingType) {
		this.billingType = billingType;
	}

	public CompetentAuthority getCompetentAuthority() {
		return competentAuthority;
	}

	public void setCompetentAuthority(CompetentAuthority competentAuthority) {
		this.competentAuthority = competentAuthority;
	}

	public Float getDebourAmount() {
		return debourAmount;
	}

	public void setDebourAmount(Float debourAmount) {
		this.debourAmount = debourAmount;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public LocalDateTime getPaymentDateTime() {
		return paymentDateTime;
	}

	public void setPaymentDateTime(LocalDateTime paymentDateTime) {
		this.paymentDateTime = paymentDateTime;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public BankTransfert getBankTransfert() {
		return bankTransfert;
	}

	public void setBankTransfert(BankTransfert bankTransfert) {
		this.bankTransfert = bankTransfert;
	}

	public String getCheckNumber() {
		return checkNumber;
	}

	public void setCheckNumber(String checkNumber) {
		this.checkNumber = checkNumber;
	}

	public InvoiceItem getInvoiceItem() {
		return invoiceItem;
	}

	public void setInvoiceItem(InvoiceItem invoiceItem) {
		this.invoiceItem = invoiceItem;
	}

	public Float getNonTaxableAmount() {
		return nonTaxableAmount;
	}

	public void setNonTaxableAmount(Float nonTaxableAmount) {
		this.nonTaxableAmount = nonTaxableAmount;
	}

	public List<AccountingRecord> getAccountingRecords() {
		return accountingRecords;
	}

	public void setAccountingRecords(List<AccountingRecord> accountingRecords) {
		this.accountingRecords = accountingRecords;
	}

	public Boolean getIsAssociated() {
		return isAssociated;
	}

	public void setIsAssociated(Boolean isAssociated) {
		this.isAssociated = isAssociated;
	}

	public Float getInvoicedAmount() {
		return invoicedAmount;
	}

	public void setInvoicedAmount(Float invoicedAmount) {
		this.invoicedAmount = invoicedAmount;
	}

	public CartRate getCartRate() {
		return cartRate;
	}

	public void setCartRate(CartRate cartRate) {
		this.cartRate = cartRate;
	}

}
