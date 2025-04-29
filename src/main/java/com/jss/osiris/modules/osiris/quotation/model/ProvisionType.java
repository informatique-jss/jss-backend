package com.jss.osiris.modules.osiris.quotation.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.osiris.miscellaneous.model.BillingType;
import com.jss.osiris.modules.osiris.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.osiris.miscellaneous.model.CustomerOrderFrequency;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.profile.model.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
public class ProvisionType implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "provision_type_sequence", sequenceName = "provision_type_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "provision_type_sequence")
	private Integer id;

	@Column(nullable = false, length = 255)
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

	private Boolean isDisplayActeDepositScreen;
	private Boolean isDisplayAnnualAccountScreen;

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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_customer_order_recuring_frequency")
	private CustomerOrderFrequency recurringFrequency;

	private Boolean isRecurring;

	private Boolean isMergeable;

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

	public Boolean getIsDisplayActeDepositScreen() {
		return isDisplayActeDepositScreen;
	}

	public void setIsDisplayActeDepositScreen(Boolean isDisplayActeDepositScreen) {
		this.isDisplayActeDepositScreen = isDisplayActeDepositScreen;
	}

	public Boolean getIsDisplayAnnualAccountScreen() {
		return isDisplayAnnualAccountScreen;
	}

	public void setIsDisplayAnnualAccountScreen(Boolean isDisplayAnnualAccountScreen) {
		this.isDisplayAnnualAccountScreen = isDisplayAnnualAccountScreen;
	}

	public CustomerOrderFrequency getRecurringFrequency() {
		return recurringFrequency;
	}

	public void setRecurringFrequency(CustomerOrderFrequency recurringFrequency) {
		this.recurringFrequency = recurringFrequency;
	}

	public Boolean getIsRecurring() {
		return isRecurring;
	}

	public void setIsRecurring(Boolean isRecurring) {
		this.isRecurring = isRecurring;
	}

	public Boolean getIsMergeable() {
		return isMergeable;
	}

	public void setIsMergeable(Boolean isMergeable) {
		this.isMergeable = isMergeable;
	}

}
