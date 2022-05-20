package com.jss.jssbackend.modules.tiers.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.jss.jssbackend.modules.miscellaneous.model.VatRate;

@Entity
public class BillingItem implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private Float discountRate;

	private Float discountAmount;

	@ManyToOne
	@JoinColumn(name = "id_billing_type")
	BillingType billingType;

	@ManyToOne
	@JoinColumn(name = "id_vat_rate")
	VatRate vatRate;

	@ManyToMany(mappedBy = "billingItems")
	@JsonBackReference
	private List<SpecialOffer> specialOffers;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public BillingType getBillingType() {
		return billingType;
	}

	public void setBillingType(BillingType billingType) {
		this.billingType = billingType;
	}

	public VatRate getVatRate() {
		return vatRate;
	}

	public void setVatRate(VatRate vatRate) {
		this.vatRate = vatRate;
	}

	public List<SpecialOffer> getSpecialOffers() {
		return specialOffers;
	}

	public void setSpecialOffers(List<SpecialOffer> specialOffers) {
		this.specialOffers = specialOffers;
	}

}
