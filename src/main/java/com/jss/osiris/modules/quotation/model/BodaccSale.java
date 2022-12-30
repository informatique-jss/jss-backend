package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.JacksonLocalDateSerializer;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.miscellaneous.model.LegalForm;

@Entity
public class BodaccSale implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "bodacc_sequence", sequenceName = "bodacc_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bodacc_sequence")
	private Integer id;

	@Column(length = 100)
	private String divestedBusinessAddress;

	@ManyToOne
	@JoinColumn(name = "id_fund_type")
	private FundType fundType;

	@Column(columnDefinition = "TEXT")
	private String divestedBusinessActivities;

	@Column(length = 30)
	private String ownerFirstname;

	@Column(length = 30)
	private String ownerLastname;

	@Column(length = 60)
	private String ownerDenomination;

	@Column(length = 9)
	private String ownerSiren;

	@Column(length = 100)
	private String ownerAddress;

	@Column(nullable = false)
	private Boolean isOwnerIndividual;

	@Column(length = 20)
	private String ownerAbbreviation;

	@Column(length = 60)
	private String ownerBusinessName;

	private Integer ownerShareCapital;

	@ManyToOne
	@JoinColumn(name = "id_legal_form_owner")
	private LegalForm ownerLegalForm;

	@Column(length = 30)
	private String purchaserFirstname;

	@Column(length = 30)
	private String purchaserLastname;

	@Column(length = 9)
	private String purchaserSiren;

	@JsonSerialize(using = JacksonLocalDateSerializer.class)
	private LocalDate purchaserActivityStartDate;

	@Column(length = 60)
	private String purchaserDenomination;

	@ManyToOne
	@JoinColumn(name = "id_legal_form_purchaser")
	private LegalForm purchaserLegalForm;

	@Column(length = 20)
	private String purchaserAbbreviation;

	@Column(length = 60)
	private String purchaserBusinessName;

	private Integer purchaserShareCapital;
	private Integer purchaserMinimumCapital;

	@JsonSerialize(using = JacksonLocalDateSerializer.class)
	private LocalDate deedDate;

	private Integer salePrice;

	@JsonSerialize(using = JacksonLocalDateSerializer.class)
	private LocalDate registrationDate;

	@ManyToOne
	@JoinColumn(name = "id_registration_authority")
	private CompetentAuthority registrationAuthority;

	@Column(length = 50)
	private String registrationReferences;

	@ManyToOne
	@JoinColumn(name = "id_act_type")
	private ActType actType;

	@Column(length = 60)
	private String writor;

	@Column(length = 100)
	private String writorAddress;

	@Column(length = 100)
	@JsonProperty(value = "validityObjectionAddress")
	private String validityObjectionAddress;

	@Column(length = 100)
	@JsonProperty(value = "mailObjectionAddress")
	private String mailObjectionAddress;

	@JsonSerialize(using = JacksonLocalDateSerializer.class)
	private LocalDate leaseResilisationDate;

	private Integer leaseResilisationFee;

	@Column(length = 100)
	private String leaseAddress;

	@Column(length = 30)
	private String tenantFirstname;

	@Column(length = 30)
	private String tenantLastname;

	@Column(length = 60)
	private String tenantDenomination;

	@Column(length = 9)
	private String tenantSiren;

	@Column(length = 100)
	private String tenantAddress;

	@Column(nullable = false)
	private Boolean isTenantIndividual;

	@Column(length = 20)
	private String tenantAbbreviation;

	@Column(length = 60)
	private String tenantBusinessName;

	private Integer tenantShareCapital;
	private Integer tenantMinimumCapital;

	@ManyToOne
	@JoinColumn(name = "id_legal_form_tenant")
	private LegalForm tenantLegalForm;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDivestedBusinessAddress() {
		return divestedBusinessAddress;
	}

	public void setDivestedBusinessAddress(String divestedBusinessAddress) {
		this.divestedBusinessAddress = divestedBusinessAddress;
	}

	public FundType getFundType() {
		return fundType;
	}

	public void setFundType(FundType fundType) {
		this.fundType = fundType;
	}

	public String getDivestedBusinessActivities() {
		return divestedBusinessActivities;
	}

	public void setDivestedBusinessActivities(String divestedBusinessActivities) {
		this.divestedBusinessActivities = divestedBusinessActivities;
	}

	public String getOwnerFirstname() {
		return ownerFirstname;
	}

	public void setOwnerFirstname(String ownerFirstname) {
		this.ownerFirstname = ownerFirstname;
	}

	public String getOwnerLastname() {
		return ownerLastname;
	}

	public void setOwnerLastname(String ownerLastname) {
		this.ownerLastname = ownerLastname;
	}

	public String getOwnerDenomination() {
		return ownerDenomination;
	}

	public void setOwnerDenomination(String ownerDenomination) {
		this.ownerDenomination = ownerDenomination;
	}

	public String getOwnerSiren() {
		return ownerSiren;
	}

	public void setOwnerSiren(String ownerSiren) {
		this.ownerSiren = ownerSiren;
	}

	public String getOwnerAddress() {
		return ownerAddress;
	}

	public void setOwnerAddress(String ownerAddress) {
		this.ownerAddress = ownerAddress;
	}

	public Boolean getIsOwnerIndividual() {
		return isOwnerIndividual;
	}

	public void setIsOwnerIndividual(Boolean isOwnerIndividual) {
		this.isOwnerIndividual = isOwnerIndividual;
	}

	public String getOwnerAbbreviation() {
		return ownerAbbreviation;
	}

	public void setOwnerAbbreviation(String ownerAbbreviation) {
		this.ownerAbbreviation = ownerAbbreviation;
	}

	public String getOwnerBusinessName() {
		return ownerBusinessName;
	}

	public void setOwnerBusinessName(String ownerBusinessName) {
		this.ownerBusinessName = ownerBusinessName;
	}

	public Integer getOwnerShareCapital() {
		return ownerShareCapital;
	}

	public void setOwnerShareCapital(Integer ownerShareCapital) {
		this.ownerShareCapital = ownerShareCapital;
	}

	public LegalForm getOwnerLegalForm() {
		return ownerLegalForm;
	}

	public void setOwnerLegalForm(LegalForm ownerLegalForm) {
		this.ownerLegalForm = ownerLegalForm;
	}

	public String getPurchaserFirstname() {
		return purchaserFirstname;
	}

	public void setPurchaserFirstname(String purchaserFirstname) {
		this.purchaserFirstname = purchaserFirstname;
	}

	public String getPurchaserLastname() {
		return purchaserLastname;
	}

	public void setPurchaserLastname(String purchaserLastname) {
		this.purchaserLastname = purchaserLastname;
	}

	public String getPurchaserSiren() {
		return purchaserSiren;
	}

	public void setPurchaserSiren(String purchaserSiren) {
		this.purchaserSiren = purchaserSiren;
	}

	public LocalDate getPurchaserActivityStartDate() {
		return purchaserActivityStartDate;
	}

	public void setPurchaserActivityStartDate(LocalDate purchaserActivityStartDate) {
		this.purchaserActivityStartDate = purchaserActivityStartDate;
	}

	public String getPurchaserDenomination() {
		return purchaserDenomination;
	}

	public void setPurchaserDenomination(String purchaserDenomination) {
		this.purchaserDenomination = purchaserDenomination;
	}

	public LegalForm getPurchaserLegalForm() {
		return purchaserLegalForm;
	}

	public void setPurchaserLegalForm(LegalForm purchaserLegalForm) {
		this.purchaserLegalForm = purchaserLegalForm;
	}

	public String getPurchaserAbbreviation() {
		return purchaserAbbreviation;
	}

	public void setPurchaserAbbreviation(String purchaserAbbreviation) {
		this.purchaserAbbreviation = purchaserAbbreviation;
	}

	public String getPurchaserBusinessName() {
		return purchaserBusinessName;
	}

	public void setPurchaserBusinessName(String purchaserBusinessName) {
		this.purchaserBusinessName = purchaserBusinessName;
	}

	public Integer getPurchaserShareCapital() {
		return purchaserShareCapital;
	}

	public void setPurchaserShareCapital(Integer purchaserShareCapital) {
		this.purchaserShareCapital = purchaserShareCapital;
	}

	public Integer getPurchaserMinimumCapital() {
		return purchaserMinimumCapital;
	}

	public void setPurchaserMinimumCapital(Integer purchaserMinimumCapital) {
		this.purchaserMinimumCapital = purchaserMinimumCapital;
	}

	public LocalDate getDeedDate() {
		return deedDate;
	}

	public void setDeedDate(LocalDate deedDate) {
		this.deedDate = deedDate;
	}

	public Integer getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(Integer salePrice) {
		this.salePrice = salePrice;
	}

	public LocalDate getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(LocalDate registrationDate) {
		this.registrationDate = registrationDate;
	}

	public CompetentAuthority getRegistrationAuthority() {
		return registrationAuthority;
	}

	public void setRegistrationAuthority(CompetentAuthority registrationAuthority) {
		this.registrationAuthority = registrationAuthority;
	}

	public String getRegistrationReferences() {
		return registrationReferences;
	}

	public void setRegistrationReferences(String registrationReferences) {
		this.registrationReferences = registrationReferences;
	}

	public ActType getActType() {
		return actType;
	}

	public void setActType(ActType actType) {
		this.actType = actType;
	}

	public String getWritor() {
		return writor;
	}

	public void setWritor(String writor) {
		this.writor = writor;
	}

	public String getWritorAddress() {
		return writorAddress;
	}

	public void setWritorAddress(String writorAddress) {
		this.writorAddress = writorAddress;
	}

	public String getValidityObjectionAddress() {
		return validityObjectionAddress;
	}

	public void setValidityObjectionAddress(String validityObjectionAddress) {
		this.validityObjectionAddress = validityObjectionAddress;
	}

	public String getMailObjectionAddress() {
		return mailObjectionAddress;
	}

	public void setMailObjectionAddress(String mailObjectionAddress) {
		this.mailObjectionAddress = mailObjectionAddress;
	}

	public LocalDate getLeaseResilisationDate() {
		return leaseResilisationDate;
	}

	public void setLeaseResilisationDate(LocalDate leaseResilisationDate) {
		this.leaseResilisationDate = leaseResilisationDate;
	}

	public Integer getLeaseResilisationFee() {
		return leaseResilisationFee;
	}

	public void setLeaseResilisationFee(Integer leaseResilisationFee) {
		this.leaseResilisationFee = leaseResilisationFee;
	}

	public String getLeaseAddress() {
		return leaseAddress;
	}

	public void setLeaseAddress(String leaseAddress) {
		this.leaseAddress = leaseAddress;
	}

	public String getTenantFirstname() {
		return tenantFirstname;
	}

	public void setTenantFirstname(String tenantFirstname) {
		this.tenantFirstname = tenantFirstname;
	}

	public String getTenantLastname() {
		return tenantLastname;
	}

	public void setTenantLastname(String tenantLastname) {
		this.tenantLastname = tenantLastname;
	}

	public String getTenantDenomination() {
		return tenantDenomination;
	}

	public void setTenantDenomination(String tenantDenomination) {
		this.tenantDenomination = tenantDenomination;
	}

	public String getTenantSiren() {
		return tenantSiren;
	}

	public void setTenantSiren(String tenantSiren) {
		this.tenantSiren = tenantSiren;
	}

	public String getTenantAddress() {
		return tenantAddress;
	}

	public void setTenantAddress(String tenantAddress) {
		this.tenantAddress = tenantAddress;
	}

	public Boolean getIsTenantIndividual() {
		return isTenantIndividual;
	}

	public void setIsTenantIndividual(Boolean isTenantIndividual) {
		this.isTenantIndividual = isTenantIndividual;
	}

	public String getTenantAbbreviation() {
		return tenantAbbreviation;
	}

	public void setTenantAbbreviation(String tenantAbbreviation) {
		this.tenantAbbreviation = tenantAbbreviation;
	}

	public String getTenantBusinessName() {
		return tenantBusinessName;
	}

	public void setTenantBusinessName(String tenantBusinessName) {
		this.tenantBusinessName = tenantBusinessName;
	}

	public Integer getTenantShareCapital() {
		return tenantShareCapital;
	}

	public void setTenantShareCapital(Integer tenantShareCapital) {
		this.tenantShareCapital = tenantShareCapital;
	}

	public Integer getTenantMinimumCapital() {
		return tenantMinimumCapital;
	}

	public void setTenantMinimumCapital(Integer tenantMinimumCapital) {
		this.tenantMinimumCapital = tenantMinimumCapital;
	}

	public LegalForm getTenantLegalForm() {
		return tenantLegalForm;
	}

	public void setTenantLegalForm(LegalForm tenantLegalForm) {
		this.tenantLegalForm = tenantLegalForm;
	}

}
