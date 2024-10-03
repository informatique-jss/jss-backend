package com.jss.osiris.modules.osiris.miscellaneous.model;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class SpecialOffer implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "special_offer_sequence", sequenceName = "special_offer_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "special_offer_sequence")
	private Integer id;

	@Column(nullable = false, length = 100)
	private String label;

	@Column(nullable = false, length = 20)
	private String code;

	@OneToMany(targetEntity = AssoSpecialOfferBillingType.class, mappedBy = "specialOffer")
	@JsonIgnoreProperties(value = { "specialOffer" }, allowSetters = true)
	private List<AssoSpecialOfferBillingType> assoSpecialOfferBillingTypes;

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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<AssoSpecialOfferBillingType> getAssoSpecialOfferBillingTypes() {
		return assoSpecialOfferBillingTypes;
	}

	public void setAssoSpecialOfferBillingTypes(List<AssoSpecialOfferBillingType> assoSpecialOfferBillingTypes) {
		this.assoSpecialOfferBillingTypes = assoSpecialOfferBillingTypes;
	}

}
