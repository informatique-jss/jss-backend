package com.jss.osiris.modules.osiris.tiers.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.jackson.JacksonLocalDateSerializer;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.City;
import com.jss.osiris.modules.osiris.miscellaneous.model.Civility;
import com.jss.osiris.modules.osiris.miscellaneous.model.Country;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.model.IAttachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.miscellaneous.model.Language;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;
import com.jss.osiris.modules.osiris.miscellaneous.model.Phone;
import com.jss.osiris.modules.osiris.profile.model.Employee;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(indexes = { @Index(name = "idx_responsable_tiers", columnList = "id_tiers"),
		@Index(name = "idx_responsable_commercial", columnList = "id_commercial"),
		@Index(name = "idx_responsable_mail", columnList = "id_mail"),
		@Index(name = "idx_responsable_login_web", columnList = "loginWeb", unique = true) })
@JsonIgnoreProperties(ignoreUnknown = true)
public class Responsable implements IAttachment, IId {
	@Id
	@SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
	@IndexedField
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tiers")
	@IndexedField
	@JsonIgnoreProperties(value = { "responsables", "attachments" }, allowSetters = true)
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
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
	private TiersType tiersType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_responsable_category")
	private TiersCategory tiersCategory;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_civility")
	@IndexedField
	@JsonView({ JacksonViews.MyJssListView.class, JacksonViews.MyJssDetailedView.class })
	private Civility civility;

	@Column(length = 40)
	@IndexedField
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })

	private String firstname;

	@Column(length = 40)
	@IndexedField
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	private String lastname;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_commercial")
	@IndexedField
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	private Employee salesEmployee;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_default_customer_order_employee")
	private Employee defaultCustomerOrderEmployee;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_formaliste")
	@JsonView({ JacksonViews.OsirisDetailedView.class })
	private Employee formalisteEmployee;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_insertion")
	@JsonView({ JacksonViews.OsirisDetailedView.class })
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
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private Country country;

	@Column(length = 40)
	@JsonProperty("rffIban")
	private String rffIban;

	@Column(length = 40)
	private String rffBic;

	@Column(length = 100)
	private String rffMail;

	@Column(columnDefinition = "TEXT")
	private String observations;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_mail")
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisDetailedView.class })
	private Mail mail;

	// TODO : remove after new webstie
	@ManyToMany
	@JoinTable(name = "asso_responsable_mail_old", joinColumns = @JoinColumn(name = "id_tiers"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> mails;

	@ManyToMany
	@JoinTable(name = "asso_responsable_phone", joinColumns = @JoinColumn(name = "id_tiers"), inverseJoinColumns = @JoinColumn(name = "id_phone"))
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisDetailedView.class })
	private List<Phone> phones;

	@OneToMany(mappedBy = "responsable", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "mailsCCResponsableClient", "mailsCCResponsableAffaire",
			"responsable" }, allowSetters = true)
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisDetailedView.class })
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

	private String salt; // TODO : delete

	@Column(length = 300)
	private String password; // TODO : delete

	@IndexedField
	private Integer idAs400;
	private Integer newIdAs400;

	@Column(length = 1024)
	private String loginToken;

	private LocalDateTime loginTokenExpirationDateTime;

	@JsonView(JacksonViews.MyJssDetailedView.class)
	private Integer numberOfGiftPostsPerMonth;

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

	public Mail getMail() {
		return mail;
	}

	public void setMail(Mail mail) {
		this.mail = mail;
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

	public Boolean getIsProvisionalPaymentMandatory() {
		if (getTiers() != null)
			return getTiers().getIsProvisionalPaymentMandatory();
		return null;
	}

	public String getLabel() {
		return getFirstname() + " " + getLastname();
	}

	public String getRffIban() {
		return rffIban;
	}

	public void setRffIban(String rffIban) {
		this.rffIban = rffIban;
	}

	public String getRffBic() {
		return rffBic;
	}

	public void setRffBic(String rffBic) {
		this.rffBic = rffBic;
	}

	public String getRffMail() {
		return rffMail;
	}

	public void setRffMail(String rffMail) {
		this.rffMail = rffMail;
	}

	public String getLoginToken() {
		return loginToken;
	}

	public void setLoginToken(String loginToken) {
		this.loginToken = loginToken;
	}

	public LocalDateTime getLoginTokenExpirationDateTime() {
		return loginTokenExpirationDateTime;
	}

	public void setLoginTokenExpirationDateTime(LocalDateTime loginTokenExpirationDateTime) {
		this.loginTokenExpirationDateTime = loginTokenExpirationDateTime;
	}

	public List<Mail> getMails() {
		return mails;
	}

	public void setMails(List<Mail> mails) {
		this.mails = mails;
	}

	public Integer getNumberOfGiftPostsPerMonth() {
		return numberOfGiftPostsPerMonth;
	}

	public void setNumberOfGiftPostsPerMonth(Integer numberOfPostsSharingAuthorized) {
		this.numberOfGiftPostsPerMonth = numberOfPostsSharingAuthorized;
	}
}
