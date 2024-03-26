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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.JacksonLocalDateSerializer;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.City;
import com.jss.osiris.modules.miscellaneous.model.Civility;
import com.jss.osiris.modules.miscellaneous.model.Country;
import com.jss.osiris.modules.miscellaneous.model.DeliveryService;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.IAttachment;
import com.jss.osiris.modules.miscellaneous.model.IGenericTiers;
import com.jss.osiris.modules.miscellaneous.model.Language;
import com.jss.osiris.modules.miscellaneous.model.Mail;
import com.jss.osiris.modules.miscellaneous.model.PaymentType;
import com.jss.osiris.modules.miscellaneous.model.Phone;
import com.jss.osiris.modules.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.profile.model.Employee;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(indexes = { @Index(name = "idx_tiers_commercial", columnList = "id_commercial"),
})
public class Tiers implements ITiers, IAttachment, IGenericTiers {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@IndexedField
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tiers_type")
	@IndexedField
	private TiersType tiersType;

	@Column(length = 60)
	@IndexedField
	private String denomination;

	@Column(nullable = false)
	@IndexedField
	private Boolean isIndividual;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tiers_category")
	@IndexedField
	private TiersCategory tiersCategory;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_delivery_service")
	private DeliveryService deliveryService;

	@Column(length = 12)
	private String intercom;

	@Column(length = 20)
	private String intercommunityVat;

	@ManyToMany
	@JoinTable(name = "asso_tiers_special_offer", joinColumns = @JoinColumn(name = "id_tiers"), inverseJoinColumns = @JoinColumn(name = "id_special_offer"))
	private List<SpecialOffer> specialOffers;

	@Column(columnDefinition = "TEXT")
	private String instructions;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_payment_type")
	private PaymentType paymentType;

	@Column(length = 40)
	@JsonProperty("paymentIban")
	private String paymentIban;

	@Column(length = 40)
	private String paymentBic;

	@Column(length = 40)
	@JsonProperty("rffIban")
	private String rffIban;

	@Column(length = 40)
	private String rffBic;

	@Column(length = 100)
	private String rffMail;

	@Column(nullable = false)
	private Boolean isProvisionalPaymentMandatory;

	private LocalDate sepaMandateSignatureDate;

	@Column(nullable = false)
	private Boolean isSepaMandateReceived;

	@OneToMany(mappedBy = "tiers", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "tiers" }, allowSetters = true)
	private List<Responsable> responsables;

	// Common responsable / tiers

	@JsonSerialize(using = JacksonLocalDateSerializer.class)
	private LocalDate firstBilling;

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

	@Column(length = 100, nullable = false)
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
	@JoinTable(name = "asso_tiers_mail", joinColumns = @JoinColumn(name = "id_tiers"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> mails;

	@ManyToMany
	@JoinTable(name = "asso_tiers_phone", joinColumns = @JoinColumn(name = "id_tiers"), inverseJoinColumns = @JoinColumn(name = "id_phone"))
	private List<Phone> phones;

	@OneToMany(mappedBy = "tiers", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "tiers" }, allowSetters = true)
	private List<Document> documents;

	@OneToMany(mappedBy = "tiers", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "tiers" }, allowSetters = true)
	private List<Attachment> attachments;

	@OneToMany(mappedBy = "tiers", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "tiers" }, allowSetters = true)
	private List<TiersFollowup> tiersFollowups;

	@ManyToMany
	@JoinTable(name = "asso_tiers_competitor", joinColumns = @JoinColumn(name = "id_tiers"), inverseJoinColumns = @JoinColumn(name = "id_competitor"))
	private List<Competitor> competitors;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_accounting_account_provider")
	private AccountingAccount accountingAccountProvider;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_accounting_account_customer")
	private AccountingAccount accountingAccountCustomer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_accounting_account_deposit")
	private AccountingAccount accountingAccountDeposit;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_rff_frequency")
	private RffFrequency rffFrequency;

	@IndexedField
	private Integer idAs400;
	private Integer newIdAs400;

	public List<Competitor> getCompetitors() {
		return competitors;
	}

	public void setCompetitors(List<Competitor> competitors) {
		this.competitors = competitors;
	}

	public TiersType getTiersType() {
		return tiersType;
	}

	public Boolean getIsSepaMandateReceived() {
		return isSepaMandateReceived;
	}

	public void setIsSepaMandateReceived(Boolean isSepaMandateReceived) {
		this.isSepaMandateReceived = isSepaMandateReceived;
	}

	public void setTiersType(TiersType tiersType) {
		this.tiersType = tiersType;
	}

	public String getDenomination() {
		return denomination;
	}

	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}

	public Boolean getIsIndividual() {
		return isIndividual;
	}

	public void setIsIndividual(Boolean isIndividual) {
		this.isIndividual = isIndividual;
	}

	public TiersCategory getTiersCategory() {
		return tiersCategory;
	}

	public void setTiersCategory(TiersCategory tiersCategory) {
		this.tiersCategory = tiersCategory;
	}

	public DeliveryService getDeliveryService() {
		return deliveryService;
	}

	public void setDeliveryService(DeliveryService deliveryService) {
		this.deliveryService = deliveryService;
	}

	public String getIntercom() {
		return intercom;
	}

	public void setIntercom(String intercom) {
		this.intercom = intercom;
	}

	public String getIntercommunityVat() {
		return intercommunityVat;
	}

	public void setIntercommunityVat(String intercommunityVat) {
		this.intercommunityVat = intercommunityVat;
	}

	public List<SpecialOffer> getSpecialOffers() {
		return specialOffers;
	}

	public void setSpecialOffers(List<SpecialOffer> specialOffers) {
		this.specialOffers = specialOffers;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public void setPaymentIban(String paymentIBAN) {
		this.paymentIban = paymentIBAN;
	}

	public Boolean getIsProvisionalPaymentMandatory() {
		return isProvisionalPaymentMandatory;
	}

	public void setIsProvisionalPaymentMandatory(Boolean isProvisionalPaymentMandatory) {
		this.isProvisionalPaymentMandatory = isProvisionalPaymentMandatory;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<Responsable> getResponsables() {
		return responsables;
	}

	public void setResponsables(List<Responsable> responsables) {
		this.responsables = responsables;
	}

	public LocalDate getFirstBilling() {
		return firstBilling;
	}

	public void setFirstBilling(LocalDate firstBilling) {
		this.firstBilling = firstBilling;
	}

	public Civility getCivility() {
		return civility;
	}

	public void setCivility(Civility civility) {
		this.civility = civility;
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

	public AccountingAccount getAccountingAccountProvider() {
		return accountingAccountProvider;
	}

	public void setAccountingAccountProvider(AccountingAccount accountingAccountProvider) {
		this.accountingAccountProvider = accountingAccountProvider;
	}

	public AccountingAccount getAccountingAccountCustomer() {
		return accountingAccountCustomer;
	}

	public void setAccountingAccountCustomer(AccountingAccount accountingAccountCustomer) {
		this.accountingAccountCustomer = accountingAccountCustomer;
	}

	public AccountingAccount getAccountingAccountDeposit() {
		return accountingAccountDeposit;
	}

	public void setAccountingAccountDeposit(AccountingAccount accountingAccountDeposit) {
		this.accountingAccountDeposit = accountingAccountDeposit;
	}

	public String getPaymentIban() {
		return paymentIban;
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

	public String getPaymentBic() {
		return paymentBic;
	}

	public void setPaymentBic(String paymentBic) {
		this.paymentBic = paymentBic;
	}

	public LocalDate getSepaMandateSignatureDate() {
		return sepaMandateSignatureDate;
	}

	public void setSepaMandateSignatureDate(LocalDate sepaMandateSignatureDate) {
		this.sepaMandateSignatureDate = sepaMandateSignatureDate;
	}

	public Employee getDefaultCustomerOrderEmployee() {
		return defaultCustomerOrderEmployee;
	}

	public void setDefaultCustomerOrderEmployee(Employee defaultCustomerOrderEmployee) {
		this.defaultCustomerOrderEmployee = defaultCustomerOrderEmployee;
	}

	@Override
	public String getLabel() {
		if (getDenomination() != null)
			return getDenomination();
		return getFirstname() + " " + getLastname();
	}

	public RffFrequency getRffFrequency() {
		return rffFrequency;
	}

	public void setRffFrequency(RffFrequency rffFrequency) {
		this.rffFrequency = rffFrequency;
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

}
