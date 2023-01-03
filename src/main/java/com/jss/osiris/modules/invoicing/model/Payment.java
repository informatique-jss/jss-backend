package com.jss.osiris.modules.invoicing.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.miscellaneous.model.PaymentType;

@Entity
@Table(indexes = { @Index(name = "idx_bank_id", columnList = "bankId", unique = true) })
public class Payment implements Serializable, IId {

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

	@ManyToOne
	@JoinColumn(name = "id_payment_way")
	private PaymentWay paymentWay;

	@OneToMany(mappedBy = "payment")
	@JsonIgnoreProperties(value = { "payment" }, allowSetters = true)
	private List<AccountingRecord> accountingRecords;

	@ManyToOne
	@JoinColumn(name = "id_invoice")
	@JsonIgnoreProperties(value = { "payments", "accountingRecords" }, allowSetters = true)
	private Invoice invoice;

	@Column(nullable = false)
	private Boolean isExternallyAssociated;

	@ManyToOne
	@JoinColumn(name = "id_payment_type")
	private PaymentType paymentType;

	@ManyToOne
	@JoinColumn(name = "id_origin_payment")
	private Payment originPayment;

	private Boolean isCancelled;

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

	public PaymentWay getPaymentWay() {
		return paymentWay;
	}

	public void setPaymentWay(PaymentWay paymentWay) {
		this.paymentWay = paymentWay;
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

	public boolean isExternallyAssociated() {
		return isExternallyAssociated;
	}

	public void setExternallyAssociated(boolean isExternallyAssociated) {
		this.isExternallyAssociated = isExternallyAssociated;
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

}
