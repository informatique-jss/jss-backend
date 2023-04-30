package com.jss.osiris.modules.miscellaneous.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
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

	private Float discountRate;

	private Float discountAmount;

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

	public Float getDiscountRate() {
		return discountRate;
	}

	public void setDiscountRate(Float discountRate) {
		this.discountRate = discountRate;
	}

	public Float getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(Float discountAmount) {
		this.discountAmount = discountAmount;
	}

}
