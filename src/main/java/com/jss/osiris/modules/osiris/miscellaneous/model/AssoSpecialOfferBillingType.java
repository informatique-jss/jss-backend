package com.jss.osiris.modules.osiris.miscellaneous.model;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(indexes = {
		@Index(name = "idx_special_offer_offer", columnList = "id_special_offer"),
		@Index(name = "idx_special_offer_billing_type", columnList = "id_billingType") })
public class AssoSpecialOfferBillingType implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "asso_special_offer_billing_type_sequence", sequenceName = "asso_special_offer_billing_type_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asso_special_offer_billing_type_sequence")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_special_offer")
	@JsonIgnoreProperties(value = { "assoSpecialOfferBillingTypes" }, allowSetters = true)
	private SpecialOffer specialOffer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_billingType")
	private BillingType billingType;

	@Column(columnDefinition = "NUMERIC", precision = 15, scale = 2)
	private BigDecimal discountRate;

	@Column(columnDefinition = "NUMERIC", precision = 15, scale = 2)
	private BigDecimal discountAmount;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public SpecialOffer getSpecialOffer() {
		return specialOffer;
	}

	public void setSpecialOffer(SpecialOffer specialOffer) {
		this.specialOffer = specialOffer;
	}

	public BillingType getBillingType() {
		return billingType;
	}

	public void setBillingType(BillingType billingType) {
		this.billingType = billingType;
	}

	public BigDecimal getDiscountRate() {
		return discountRate;
	}

	public void setDiscountRate(BigDecimal discountRate) {
		this.discountRate = discountRate;
	}

	public BigDecimal getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(BigDecimal discountAmount) {
		this.discountAmount = discountAmount;
	}

}
