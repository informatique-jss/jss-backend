package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.miscellaneous.model.BillingType;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.miscellaneous.model.PaymentType;

@Entity
public class Debour implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@IndexedField
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_provision")
	@JsonIgnoreProperties(value = { "debours" }, allowSetters = true)
	Provision provision;

	@ManyToOne
	@JoinColumn(name = "id_billing_type")
	private BillingType billingType;

	@ManyToOne
	@JoinColumn(name = "id_competent_authority")
	private CompetentAuthority competentAuthority;

	private Float debourAmount;

	@ManyToOne
	@JoinColumn(name = "id_payment_type")
	private PaymentType paymentType;

	private LocalDateTime paymentDateTime;

	private String comments;

	@Column(length = 100)
	private String checkNumber;

	@ManyToOne
	@JoinColumn(name = "id_payment")
	@JsonIgnoreProperties(value = { "payment", "debours" }, allowSetters = true)
	private Payment payment;

	@ManyToOne
	@JoinColumn(name = "id_bank_transfert")
	private BankTransfert bankTransfert;

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

}
