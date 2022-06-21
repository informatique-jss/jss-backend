package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.City;
import com.jss.osiris.modules.miscellaneous.model.Civility;
import com.jss.osiris.modules.miscellaneous.model.Country;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.miscellaneous.model.Language;
import com.jss.osiris.modules.miscellaneous.model.LegalForm;
import com.jss.osiris.modules.miscellaneous.model.Mail;
import com.jss.osiris.modules.miscellaneous.model.Phone;

@Entity
public class Domiciliation implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_provision")
	@JsonBackReference("provision")
	private Provision provision;

	@OneToMany(targetEntity = Attachment.class, mappedBy = "domiciliation", cascade = CascadeType.ALL)
	@JsonManagedReference("domiciliation")
	private List<Attachment> attachments;

	@ManyToOne
	@JoinColumn(name = "id_domiciliation_contract_type")
	private DomiciliationContractType domiciliationContractType;

	@ManyToOne
	@JoinColumn(name = "id_language")
	private Language language;

	@ManyToOne
	@JoinColumn(name = "id_building_domiciliation")
	private BuildingDomiciliation buildingDomiciliation;

	@ManyToOne
	@JoinColumn(name = "id_mail_redirectionType")
	private MailRedirectionType mailRedirectionType;

	@Column(length = 60)
	private String address;

	@Column(length = 10)
	private String postalCode;

	@ManyToOne
	@JoinColumn(name = "id_city")
	private City city;

	@ManyToOne
	@JoinColumn(name = "id_country")
	private Country country;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_domiciliation_mail", joinColumns = @JoinColumn(name = "id_domiciliation"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> mails;

	@Column(length = 60)
	private String mailRecipient;

	@Column(nullable = false)
	private Date startDate;

	@Column(nullable = false)
	private Date endDate;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String activityDescription;

	@Column(length = 60)
	private String activityMailRecipient;

	@Column(length = 60, nullable = false)
	private String activityAddress;

	@Column(length = 10, nullable = false)
	private String activityPostalCode;

	@ManyToOne
	@JoinColumn(name = "id_activity_city")
	private City activityCity;

	@ManyToOne
	@JoinColumn(name = "id_activity_country")
	private Country activityCountry;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_domiciliation_activity_mail", joinColumns = @JoinColumn(name = "id_domiciliation"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> activityMails;

	@Column(length = 60, nullable = false)
	private String accountingRecordDomiciliation;

	@JsonProperty(value = "isLegalPerson")
	@Column(nullable = false)
	private Boolean isLegalPerson;

	@ManyToOne
	@JoinColumn(name = "id_civility")
	private Civility legalGardianCivility;

	@Column(length = 20)
	private String legalGardianFirstname;

	@Column(length = 20)
	private String legalGardianLastname;

	private Date legalGardianBirthdate;

	@Column(length = 60)
	private String legalGardianPlaceOfBirth;

	@Column(length = 30)
	private String legalGardianJob;

	@Column(length = 9)
	private String legalGardianSiren;

	@Column(length = 60)
	private String legalGardianDenomination;

	@ManyToOne
	@JoinColumn(name = "id_legal_guardian_form")
	private LegalForm legalGardianLegalForm;

	@Column(length = 60)
	private String legalGardianMailRecipient;

	@Column(length = 60)
	private String legalGardianAddress;

	@Column(length = 10)
	private String legalGardianPostalCode;

	@ManyToOne
	@JoinColumn(name = "id_legal_guardian_city")
	private City legalGardianCity;

	@ManyToOne
	@JoinColumn(name = "id_legal_gardian_country")
	private Country legalGardianCountry;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_domiciliation_legal_guardian_mail", joinColumns = @JoinColumn(name = "id_domiciliation"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> legalGardianMails;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_domiciliation_legal_guardian_phone", joinColumns = @JoinColumn(name = "id_domiciliation"), inverseJoinColumns = @JoinColumn(name = "id_phone"))
	private List<Phone> legalGardianPhones;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAccountingRecordDomiciliation() {
		return accountingRecordDomiciliation;
	}

	public void setAccountingRecordDomiciliation(String accountingRecordDomiciliation) {
		this.accountingRecordDomiciliation = accountingRecordDomiciliation;
	}

	public Provision getProvision() {
		return provision;
	}

	public void setProvision(Provision provision) {
		this.provision = provision;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public DomiciliationContractType getDomiciliationContractType() {
		return domiciliationContractType;
	}

	public void setDomiciliationContractType(DomiciliationContractType domiciliationContractType) {
		this.domiciliationContractType = domiciliationContractType;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public BuildingDomiciliation getBuildingDomiciliation() {
		return buildingDomiciliation;
	}

	public void setBuildingDomiciliation(BuildingDomiciliation buildingDomiciliation) {
		this.buildingDomiciliation = buildingDomiciliation;
	}

	public MailRedirectionType getMailRedirectionType() {
		return mailRedirectionType;
	}

	public void setMailRedirectionType(MailRedirectionType mailRedirectionType) {
		this.mailRedirectionType = mailRedirectionType;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public List<Mail> getMails() {
		return mails;
	}

	public Boolean isLegalPerson() {
		return isLegalPerson;
	}

	public void setLegalPerson(Boolean isLegalPerson) {
		this.isLegalPerson = isLegalPerson;
	}

	public Boolean getIsLegalPerson() {
		return isLegalPerson;
	}

	public void setIsLegalPerson(Boolean isLegalPerson) {
		this.isLegalPerson = isLegalPerson;
	}

	public Civility getLegalGardianCivility() {
		return legalGardianCivility;
	}

	public void setLegalGardianCivility(Civility legalGardianCivility) {
		this.legalGardianCivility = legalGardianCivility;
	}

	public String getLegalGardianFirstname() {
		return legalGardianFirstname;
	}

	public void setLegalGardianFirstname(String legalGardianFirstname) {
		this.legalGardianFirstname = legalGardianFirstname;
	}

	public String getLegalGardianLastname() {
		return legalGardianLastname;
	}

	public void setLegalGardianLastname(String legalGardianLastname) {
		this.legalGardianLastname = legalGardianLastname;
	}

	public Date getLegalGardianBirthdate() {
		return legalGardianBirthdate;
	}

	public void setLegalGardianBirthdate(Date legalGardianBirthdate) {
		this.legalGardianBirthdate = legalGardianBirthdate;
	}

	public String getLegalGardianPlaceOfBirth() {
		return legalGardianPlaceOfBirth;
	}

	public void setLegalGardianPlaceOfBirth(String legalGardianPlaceOfBirth) {
		this.legalGardianPlaceOfBirth = legalGardianPlaceOfBirth;
	}

	public String getLegalGardianJob() {
		return legalGardianJob;
	}

	public void setLegalGardianJob(String legalGardianJob) {
		this.legalGardianJob = legalGardianJob;
	}

	public String getLegalGardianSiren() {
		return legalGardianSiren;
	}

	public void setLegalGardianSiren(String legalGardianSiren) {
		this.legalGardianSiren = legalGardianSiren;
	}

	public String getLegalGardianDenomination() {
		return legalGardianDenomination;
	}

	public void setLegalGardianDenomination(String legalGardianDenomination) {
		this.legalGardianDenomination = legalGardianDenomination;
	}

	public LegalForm getLegalGardianLegalForm() {
		return legalGardianLegalForm;
	}

	public void setLegalGardianLegalForm(LegalForm legalGardianLegalForm) {
		this.legalGardianLegalForm = legalGardianLegalForm;
	}

	public String getLegalGardianMailRecipient() {
		return legalGardianMailRecipient;
	}

	public void setLegalGardianMailRecipient(String legalGardianMailRecipient) {
		this.legalGardianMailRecipient = legalGardianMailRecipient;
	}

	public String getLegalGardianAddress() {
		return legalGardianAddress;
	}

	public void setLegalGardianAddress(String legalGardianAddress) {
		this.legalGardianAddress = legalGardianAddress;
	}

	public String getLegalGardianPostalCode() {
		return legalGardianPostalCode;
	}

	public void setLegalGardianPostalCode(String legalGardianPostalCode) {
		this.legalGardianPostalCode = legalGardianPostalCode;
	}

	public City getLegalGardianCity() {
		return legalGardianCity;
	}

	public void setLegalGardianCity(City legalGardianCity) {
		this.legalGardianCity = legalGardianCity;
	}

	public Country getLegalGardianCountry() {
		return legalGardianCountry;
	}

	public void setLegalGardianCountry(Country legalGardianCountry) {
		this.legalGardianCountry = legalGardianCountry;
	}

	public List<Mail> getLegalGardianMails() {
		return legalGardianMails;
	}

	public void setLegalGardianMails(List<Mail> legalGardianMails) {
		this.legalGardianMails = legalGardianMails;
	}

	public List<Phone> getLegalGardianPhones() {
		return legalGardianPhones;
	}

	public void setLegalGardianPhones(List<Phone> legalGardianPhones) {
		this.legalGardianPhones = legalGardianPhones;
	}

	public void setMails(List<Mail> mails) {
		this.mails = mails;
	}

	public String getMailRecipient() {
		return mailRecipient;
	}

	public void setMailRecipient(String mailRecipient) {
		this.mailRecipient = mailRecipient;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getActivityDescription() {
		return activityDescription;
	}

	public void setActivityDescription(String activityDescription) {
		this.activityDescription = activityDescription;
	}

	public String getActivityMailRecipient() {
		return activityMailRecipient;
	}

	public void setActivityMailRecipient(String activityMailRecipient) {
		this.activityMailRecipient = activityMailRecipient;
	}

	public String getActivityAddress() {
		return activityAddress;
	}

	public void setActivityAddress(String activityAddress) {
		this.activityAddress = activityAddress;
	}

	public String getActivityPostalCode() {
		return activityPostalCode;
	}

	public void setActivityPostalCode(String activityPostalCode) {
		this.activityPostalCode = activityPostalCode;
	}

	public City getActivityCity() {
		return activityCity;
	}

	public void setActivityCity(City activityCity) {
		this.activityCity = activityCity;
	}

	public Country getActivityCountry() {
		return activityCountry;
	}

	public void setActivityCountry(Country activityCountry) {
		this.activityCountry = activityCountry;
	}

	public List<Mail> getActivityMails() {
		return activityMails;
	}

	public void setActivityMails(List<Mail> activityMails) {
		this.activityMails = activityMails;
	}

}
