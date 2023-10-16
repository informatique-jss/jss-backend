package com.jss.osiris.modules.tiers.model;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import com.jss.osiris.modules.miscellaneous.model.IGenericTiers;
import com.jss.osiris.modules.miscellaneous.model.Language;
import com.jss.osiris.modules.miscellaneous.model.Mail;
import com.jss.osiris.modules.miscellaneous.model.Phone;
import com.jss.osiris.modules.profile.model.Employee;

@Entity
@Table(indexes = { @Index(name = "idx_responsable_tiers", columnList = "id_tiers"),
		@Index(name = "idx_responsable_login_web", columnList = "loginWeb", unique = true) })
@JsonIgnoreProperties(ignoreUnknown = true)
public class Responsable implements ITiers, IAttachment, IGenericTiers {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@IndexedField
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tiers")
	@IndexedField
	@JsonIgnoreProperties(value = { "responsables" }, allowSetters = true)
	private Tiers tiers;

	@Column(nullable = false)
	@IndexedField
	private Boolean isActive;

	@Column(nullable = false)
	private Boolean isBouclette;

	// Common responsable / tiers

	@JsonSerialize(using = JacksonLocalDateSerializer.class)
	private LocalDate firstBilling;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_responsable_type")
	@IndexedField
	private TiersType tiersType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_responsable_category")
	@IndexedField
	private TiersCategory tiersCategory;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_civility")
	@IndexedField
	private Civility civility;

	@Column(length = 40)
	@IndexedField
	private String firstname;

	@Column(length = 40)
	@IndexedField
	private String lastname;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_commercial")
	@IndexedField
	private Employee salesEmployee;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_default_customer_order_employee")
	private Employee defaultCustomerOrderEmployee;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_formaliste")
	private Employee formalisteEmployee;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_insertion")
	private Employee insertionEmployee;

	@Column(columnDefinition = "TEXT")
	private String mailRecipient;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_language")
	private Language language;

	@Column(length = 100)
	@IndexedField
	private String address;

	@Column(length = 10)
	@IndexedField
	private String postalCode;

	@Column(length = 20)
	private String cedexComplement;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_city")
	@IndexedField
	private City city;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_country")
	private Country country;

	private Float rffFormaliteRate;
	private Float rffInsertionRate;

	@Column(columnDefinition = "TEXT")
	private String observations;

	@ManyToMany
	@JoinTable(name = "asso_responsable_mail", joinColumns = @JoinColumn(name = "id_tiers"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> mails;

	@ManyToMany
	@JoinTable(name = "asso_responsable_phone", joinColumns = @JoinColumn(name = "id_tiers"), inverseJoinColumns = @JoinColumn(name = "id_phone"))
	private List<Phone> phones;

	@OneToMany(mappedBy = "responsable", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "mailsCCResponsableClient", "mailsCCResponsableAffaire",
			"responsable" }, allowSetters = true)
	private List<Document> documents;

	@OneToMany(mappedBy = "responsable")
	private List<Attachment> attachments;

	@OneToMany(mappedBy = "responsable")
	@JsonIgnoreProperties(value = { "responsable" }, allowSetters = true)
	private List<TiersFollowup> tiersFollowups;

	@Column(length = 100)
	private String function;

	@Column(length = 20)
	private String building;

	@Column(length = 8)
	private String floor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_subscription_period_type")
	private SubscriptionPeriodType subscriptionPeriodType;

	private String loginWeb;

	private Boolean canViewAllTiersInWeb;

	private String salt;

	@Column(length = 300)
	private String password;

	@IndexedField
	private Integer idAs400;
	private Integer newIdAs400;

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

	public SubscriptionPeriodType getSubscriptionPeriodType() {
		return subscriptionPeriodType;
	}

	public void setSubscriptionPeriodType(SubscriptionPeriodType subscriptionPeriodType) {
		this.subscriptionPeriodType = subscriptionPeriodType;
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

	public Float getRffFormaliteRate() {
		return rffFormaliteRate;
	}

	public void setRffFormaliteRate(Float rffFormaliteRate) {
		this.rffFormaliteRate = rffFormaliteRate;
	}

	public Float getRffInsertionRate() {
		return rffInsertionRate;
	}

	public void setRffInsertionRate(Float rffInsertionRate) {
		this.rffInsertionRate = rffInsertionRate;
	}

	public String getCedexComplement() {
		return cedexComplement;
	}

	public void setCedexComplement(String cedexComplement) {
		this.cedexComplement = cedexComplement;
	}

	public String getLoginWeb() {
		return loginWeb;
	}

	public void setLoginWeb(String loginWeb) {
		this.loginWeb = loginWeb;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getIdAs400() {
		return idAs400;
	}

	public void setIdAs400(Integer idAs400) {
		this.idAs400 = idAs400;
	}

	public Integer getNewIdAs400() {
		return newIdAs400;
	}

	public void setNewIdAs400(Integer newIdAs400) {
		this.newIdAs400 = newIdAs400;
	}

	public Boolean getCanViewAllTiersInWeb() {
		return canViewAllTiersInWeb;
	}

	public void setCanViewAllTiersInWeb(Boolean canViewAllTiersInWeb) {
		this.canViewAllTiersInWeb = canViewAllTiersInWeb;
	}

	public Employee getDefaultCustomerOrderEmployee() {
		return defaultCustomerOrderEmployee;
	}

	public void setDefaultCustomerOrderEmployee(Employee defaultCustomerOrderEmployee) {
		this.defaultCustomerOrderEmployee = defaultCustomerOrderEmployee;
	}

	public String getIntercommunityVat() {
		if (getTiers() != null)
			return getTiers().getIntercommunityVat();
		return null;
	}

	@Override
	public Boolean getIsProvisionalPaymentMandatory() {
		if (getTiers() != null)
			return getTiers().getIsProvisionalPaymentMandatory();
		return null;
	}

}
