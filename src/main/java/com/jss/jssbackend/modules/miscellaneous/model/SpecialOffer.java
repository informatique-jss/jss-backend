package com.jss.jssbackend.modules.miscellaneous.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity
public class SpecialOffer implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false)
	private String label;

	private String code;

	@ManyToMany
	@JoinTable(name = "asso_special_offer_billing_item", joinColumns = @JoinColumn(name = "id_special_offer"), inverseJoinColumns = @JoinColumn(name = "id_billing_item"))
	private List<BillingItem> billingItems;

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

	public List<BillingItem> getBillingItems() {
		return billingItems;
	}

	public void setBillingItems(List<BillingItem> billingItems) {
		this.billingItems = billingItems;
	}

}
