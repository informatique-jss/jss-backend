package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jss.osiris.modules.miscellaneous.model.BillingType;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class ProvisionType implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false, length = 100)
	private String label;

	@Column(nullable = false, length = 20)
	private String code;

	@ManyToOne
	@JoinColumn(name = "id_provision_family_type")
	private ProvisionFamilyType provisionFamilyType;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_provision_billing_type", joinColumns = @JoinColumn(name = "id_provision"), inverseJoinColumns = @JoinColumn(name = "id_billing_type"))
	@JsonProperty(value = "billingTypes")
	private List<BillingType> billingTypes;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<BillingType> getBillingTypes() {
		return billingTypes;
	}

	public void setBillingTypes(List<BillingType> billingTypes) {
		this.billingTypes = billingTypes;
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

	public ProvisionFamilyType getProvisionFamilyType() {
		return provisionFamilyType;
	}

	public void setProvisionFamilyType(ProvisionFamilyType provisionFamilyType) {
		this.provisionFamilyType = provisionFamilyType;
	}

}
