package com.jss.jssbackend.modules.miscellaneous.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class BillingType implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false)
	private String label;

	private Integer accountingCode;

	private Float preTaxPrice;

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

	public Integer getAccountingCode() {
		return accountingCode;
	}

	public void setAccountingCode(Integer accountingCode) {
		this.accountingCode = accountingCode;
	}

	public Float getPreTaxPrice() {
		return preTaxPrice;
	}

	public void setPreTaxPrice(Float preTaxPrice) {
		this.preTaxPrice = preTaxPrice;
	}

}
