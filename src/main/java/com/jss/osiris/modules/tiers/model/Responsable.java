package com.jss.osiris.modules.tiers.model;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.JacksonLocalDateSerializer;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.City;
import com.jss.osiris.modules.miscellaneous.model.Civility;
import com.jss.osiris.modules.miscellaneous.model.Country;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.IAttachment;
import com.jss.osiris.modules.miscellaneous.model.Language;
import com.jss.osiris.modules.miscellaneous.model.Mail;
import com.jss.osiris.modules.miscellaneous.model.Phone;
import com.jss.osiris.modules.profile.model.Employee;

@Entity
@Table(indexes = { @Index(name = "pk_responsable", columnList = "id", unique = true),
		@Index(name = "idx_responsable_tiers", columnList = "id_tiers") })
public class Responsable implements ITiers, IAttachment {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@IndexedField
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_tiers")
	@IndexedField
	@JsonBackReference("responsable")
	private Tiers tiers;

	@Column(nullable = false)
	private Boolean isActive;

	@Column(nullable = false)
	private Boolean isBouclette;

	// Common responsable / tiers

	@JsonSerialize(using = JacksonLocalDateSerializer.class)
	private LocalDate firstBilling;

	@ManyToOne
	@JoinColumn(name = "id_tiers_type")
	@IndexedField
	private TiersType tiersType;

	@ManyToOne
	@JoinColumn(name = "id_tiers_category")
	@IndexedField
	private TiersCategory tiersCategory;

	@ManyToOne
	@JoinColumn(name = "id_civility")
	@IndexedField
	private Civility civility;

	@Column(length = 20)
	@IndexedField
	private String firstname;

	@Column(length = 20)
	@IndexedField
	private String lastname;

	@ManyToOne
	@JoinColumn(name = "id_commercial")
	@IndexedField
	private Employee salesEmployee;

	@ManyToOne
	@JoinColumn(name = "id_formaliste")
	private Employee formalisteEmployee;

	@ManyToOne
	@JoinColumn(name = "id_insertion")
	private Employee insertionEmployee;

	@Column(columnDefinition = "TEXT")
	private String mailRecipient;

	@ManyToOne
	@JoinColumn(name = "id_language")
	private Language language;

	@Column(length = 60, nullable = false)
	@IndexedField
	private String address;

	@Column(length = 10)
	@IndexedField
	private String postalCode;

	@ManyToOne
	@JoinColumn(name = "id_city")
	@IndexedField
	private City city;

	@ManyToOne
	@JoinColumn(name = "id_country")
	private Country country;

	private Float rcaFormaliteRate;
	private Float rcaInsertionRate;

	@Column(columnDefinition = "TEXT")
	private String observations;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_responsable_mail", joinColumns = @JoinColumn(name = "id_tiers"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> mails;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_responsable_phone", joinColumns = @JoinColumn(name = "id_tiers"), inverseJoinColumns = @JoinColumn(name = "id_phone"))
	private List<Phone> phones;

	@OneToMany(targetEntity = Document.class, mappedBy = "responsable", cascade = CascadeType.ALL)
	@JsonManagedReference("responsable")
	private List<Document> documents;

	@OneToMany(targetEntity = Attachment.class, mappedBy = "responsable", cascade = CascadeType.ALL)
	@JsonManagedReference("responsable")
	private List<Attachment> attachments;

	@OneToMany(targetEntity = TiersFollowup.class, mappedBy = "responsable", cascade = CascadeType.ALL)
	@JsonManagedReference("responsable")
	private List<TiersFollowup> tiersFollowups;

	@Column(length = 20)
	private String function;

	@Column(length = 20)
	private String building;

	@Column(length = 8)
	private String floor;

	@ManyToOne(targetEntity = JssSubscription.class, cascade = CascadeType.ALL)
	@JsonManagedReference("responsable")
	private JssSubscription jssSubscription;

	@ManyToOne
	@JoinColumn(name = "id_subscription_period_type")
	private SubscriptionPeriodType subscriptionPeriodType;

	@ManyToMany
	@JoinTable(name = "asso_tiers_document_mail_creation_affaire", joinColumns = @JoinColumn(name = "id_tiers_document"), inverseJoinColumns = @JoinColumn(name = "id_responsable"))
	private List<Mail> mailsCreationAffaire;

	@ManyToMany
	@JoinTable(name = "asso_tiers_document_mail_provisionning_confirmation", joinColumns = @JoinColumn(name = "id_tiers_document"), inverseJoinColumns = @JoinColumn(name = "id_responsable"))
	private List<Mail> mailsProvisionningConfirmation;

	@ManyToMany
	@JoinTable(name = "asso_tiers_document_mail_missing_formality", joinColumns = @JoinColumn(name = "id_tiers_document"), inverseJoinColumns = @JoinColumn(name = "id_responsable"))
	private List<Mail> mailsMissingItemFormality;

	public Tiers getTiers() {
		return tiers;
	}

	public void setTiers(Tiers tiers) {
		this.tiers = tiers;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public TiersType getTiersType() {
		return tiersType;
	}

	public void setTiersType(TiersType tiersType) {
		this.tiersType = tiersType;
	}

	public TiersCategory getTiersCategory() {
		return tiersCategory;
	}

	public void setTiersCategory(TiersCategory tiersCategory) {
		this.tiersCategory = tiersCategory;
	}

	public Civility getCivility() {
		return civility;
	}

	public void setCivility(Civility civility) {
		this.civility = civility;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsBouclette() {
		return isBouclette;
	}

	public void setIsBouclette(Boolean isBouclette) {
		this.isBouclette = isBouclette;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalDate getFirstBilling() {
		return firstBilling;
	}

	public void setFirstBilling(LocalDate firstBilling) {
		this.firstBilling = firstBilling;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public Employee getSalesEmployee() {
		return salesEmployee;
	}

	public void setSalesEmployee(Employee salesEmployee) {
		this.salesEmployee = salesEmployee;
	}

	public Employee getFormalisteEmployee() {
		return formalisteEmployee;
	}

	public void setFormalisteEmployee(Employee formalisteEmployee) {
		this.formalisteEmployee = formalisteEmployee;
	}

	public Employee getInsertionEmployee() {
		return insertionEmployee;
	}

	public void setInsertionEmployee(Employee insertionEmployee) {
		this.insertionEmployee = insertionEmployee;
	}

	public String getMailRecipient() {
		return mailRecipient;
	}

	public void setMailRecipient(String mailRecipient) {
		this.mailRecipient = mailRecipient;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
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

	public Float getRcaFormaliteRate() {
		return rcaFormaliteRate;
	}

	public void setRcaFormaliteRate(Float rcaFormaliteRate) {
		this.rcaFormaliteRate = rcaFormaliteRate;
	}

	public Float getRcaInsertionRate() {
		return rcaInsertionRate;
	}

	public void setRcaInsertionRate(Float rcaInsertionRate) {
		this.rcaInsertionRate = rcaInsertionRate;
	}

	public String getObservations() {
		return observations;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

	public List<Mail> getMails() {
		return mails;
	}

	public void setMails(List<Mail> mails) {
		this.mails = mails;
	}

	public List<Phone> getPhones() {
		return phones;
	}

	public void setPhones(List<Phone> phones) {
		this.phones = phones;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public List<TiersFollowup> getTiersFollowups() {
		return tiersFollowups;
	}

	public void setTiersFollowups(List<TiersFollowup> tiersFollowups) {
		this.tiersFollowups = tiersFollowups;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public JssSubscription getJssSubscription() {
		return jssSubscription;
	}

	public void setJssSubscription(JssSubscription jssSubscription) {
		this.jssSubscription = jssSubscription;
	}

	public SubscriptionPeriodType getSubscriptionPeriodType() {
		return subscriptionPeriodType;
	}

	public void setSubscriptionPeriodType(SubscriptionPeriodType subscriptionPeriodType) {
		this.subscriptionPeriodType = subscriptionPeriodType;
	}

	public List<Mail> getMailsCreationAffaire() {
		return mailsCreationAffaire;
	}

	public void setMailsCreationAffaire(List<Mail> mailsCreationAffaire) {
		this.mailsCreationAffaire = mailsCreationAffaire;
	}

	public List<Mail> getMailsProvisionningConfirmation() {
		return mailsProvisionningConfirmation;
	}

	public void setMailsProvisionningConfirmation(List<Mail> mailsProvisionningConfirmation) {
		this.mailsProvisionningConfirmation = mailsProvisionningConfirmation;
	}

	public List<Mail> getMailsMissingItemFormality() {
		return mailsMissingItemFormality;
	}

	public void setMailsMissingItemFormality(List<Mail> mailsMissingItemFormality) {
		this.mailsMissingItemFormality = mailsMissingItemFormality;
	}

	@Override
	public Boolean getIsIndividual() {
		return true;
	}

	@Override
	public AccountingAccount getAccountingAccountProvider() {
		if (this.getTiers() != null)
			return this.getTiers().getAccountingAccountProvider();
		return null;
	}

	@Override
	public AccountingAccount getAccountingAccountCustomer() {
		if (this.getTiers() != null)
			return this.getTiers().getAccountingAccountCustomer();
		return null;
	}

	@Override
	public AccountingAccount getAccountingAccountDeposit() {
		if (this.getTiers() != null)
			return this.getTiers().getAccountingAccountDeposit();
		return null;
	}

}
