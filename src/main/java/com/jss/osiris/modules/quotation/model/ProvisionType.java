package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.miscellaneous.model.BillingType;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.profile.model.Employee;

@Entity
public class ProvisionType implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "provision_type_sequence", sequenceName = "provision_type_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "provision_type_sequence")
	private Integer id;

	@Column(nullable = false, length = 100)
	@IndexedField
	private String label;

	@Column(nullable = false, length = 20)
	private String code;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_provision_family_type")
	private ProvisionFamilyType provisionFamilyType;

	@ManyToMany
	@JoinTable(name = "asso_provision_billing_type", joinColumns = @JoinColumn(name = "id_provision"), inverseJoinColumns = @JoinColumn(name = "id_billing_type"))
	@JsonProperty(value = "billingTypes")
	private List<BillingType> billingTypes;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_provision_screen_type")
	private ProvisionScreenType provisionScreenType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_assignation_type")
	private AssignationType assignationType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_default_assigned")
	private Employee defaultEmployee;

	private Integer assignationWeight;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_default_competent_authority_service_provider")
	private CompetentAuthority defaultCompetentAuthorityServiceProvider;

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

	public ProvisionScreenType getProvisionScreenType() {
		return provisionScreenType;
	}

	public void setProvisionScreenType(ProvisionScreenType provisionScreenType) {
		this.provisionScreenType = provisionScreenType;
	}

	public AssignationType getAssignationType() {
		return assignationType;
	}

	public void setAssignationType(AssignationType assignationType) {
		this.assignationType = assignationType;
	}

	public Employee getDefaultEmployee() {
		return defaultEmployee;
	}

	public void setDefaultEmployee(Employee defaultEmployee) {
		this.defaultEmployee = defaultEmployee;
	}

	public Integer getAssignationWeight() {
		return assignationWeight;
	}

	public void setAssignationWeight(Integer assignationWeight) {
		this.assignationWeight = assignationWeight;
	}

	public CompetentAuthority getDefaultCompetentAuthorityServiceProvider() {
		return defaultCompetentAuthorityServiceProvider;
	}

	public void setDefaultCompetentAuthorityServiceProvider(
			CompetentAuthority defaultCompetentAuthorityServiceProvider) {
		this.defaultCompetentAuthorityServiceProvider = defaultCompetentAuthorityServiceProvider;
	}

}
