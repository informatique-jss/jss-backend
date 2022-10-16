package com.jss.osiris.modules.miscellaneous.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class BillingType implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false, length = 100)
	private String label;

	@Column(nullable = false, length = 20)
	private String code;

	@Column(nullable = false)
	private Boolean canOverridePrice;

	@Column(nullable = false)
	private Boolean isPriceBasedOnCharacterNumber;

	private Boolean isOverrideVat;

	private Boolean isOptionnal;

	@ManyToOne
	@JoinColumn(name = "id_vat")
	private Vat vat;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Boolean getCanOverridePrice() {
		return canOverridePrice;
	}

	public void setCanOverridePrice(Boolean canOverridePrice) {
		this.canOverridePrice = canOverridePrice;
	}

	public Boolean getIsPriceBasedOnCharacterNumber() {
		return isPriceBasedOnCharacterNumber;
	}

	public void setIsPriceBasedOnCharacterNumber(Boolean isPriceBasedOnCharacterNumber) {
		this.isPriceBasedOnCharacterNumber = isPriceBasedOnCharacterNumber;
	}

	public Boolean getIsOverrideVat() {
		return isOverrideVat;
	}

	public void setIsOverrideVat(Boolean isOverrideVat) {
		this.isOverrideVat = isOverrideVat;
	}

	public Vat getVat() {
		return vat;
	}

	public void setVat(Vat vat) {
		this.vat = vat;
	}

	public Boolean getIsOptionnal() {
		return isOptionnal;
	}

	public void setIsOptionnal(Boolean isOptionnal) {
		this.isOptionnal = isOptionnal;
	}

}
