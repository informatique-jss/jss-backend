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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.tiers.model.RefundType;
import com.jss.osiris.modules.tiers.model.Tiers;

@Entity
public class Refund implements Serializable, IId {

	@Id
	@IndexedField
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false)
	@IndexedField
	private String label;

	private Float refundAmount;

	private LocalDateTime refundDateTime;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tiers")
	private Tiers tiers;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_confrere")
	private Confrere confrere;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_affaire")
	private Affaire affaire;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_refund_type")
	private RefundType refundType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_customer_order")
	@JsonIgnoreProperties(value = { "deposits" }, allowSetters = true)
	private CustomerOrder customerOrder;

	@Column(length = 40)
	private String refundIBAN;

	@Column(length = 40)
	private String refundBic;

	private Boolean isMatched;

	private Boolean isAlreadyExported;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_invoice")
	@JsonIgnoreProperties(value = { "payments", "accountingRecords", "refunds" }, allowSetters = true)
	private Invoice invoice;

	@OneToMany(mappedBy = "refund", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "refund", "originPayment", "childrenPayments",
			"accountingRecords" }, allowSetters = true)
	private List<Payment> payments;

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

	public Float getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(Float refundAmount) {
		this.refundAmount = refundAmount;
	}

	public LocalDateTime getRefundDateTime() {
		return refundDateTime;
	}

	public void setRefundDateTime(LocalDateTime refundDateTime) {
		this.refundDateTime = refundDateTime;
	}

	public Tiers getTiers() {
		return tiers;
	}

	public void setTiers(Tiers tiers) {
		this.tiers = tiers;
	}

	public Confrere getConfrere() {
		return confrere;
	}

	public void setConfrere(Confrere confrere) {
		this.confrere = confrere;
	}

	public Affaire getAffaire() {
		return affaire;
	}

	public void setAffaire(Affaire affaire) {
		this.affaire = affaire;
	}

	public RefundType getRefundType() {
		return refundType;
	}

	public void setRefundType(RefundType refundType) {
		this.refundType = refundType;
	}

	public String getRefundIBAN() {
		return refundIBAN;
	}

	public void setRefundIBAN(String refundIBAN) {
		this.refundIBAN = refundIBAN;
	}

	public Boolean getIsMatched() {
		return isMatched;
	}

	public void setIsMatched(Boolean isMatched) {
		this.isMatched = isMatched;
	}

	public Boolean getIsAlreadyExported() {
		return isAlreadyExported;
	}

	public void setIsAlreadyExported(Boolean isAlreadyExported) {
		this.isAlreadyExported = isAlreadyExported;
	}

	public String getRefundBic() {
		return refundBic;
	}

	public void setRefundBic(String refundBic) {
		this.refundBic = refundBic;
	}

	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}

	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}
}
