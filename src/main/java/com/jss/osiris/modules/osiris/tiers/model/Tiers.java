package com.jss.osiris.modules.osiris.tiers.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.jackson.JacksonLocalDateSerializer;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.osiris.accounting.model.AccountingAccount;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.City;
import com.jss.osiris.modules.osiris.miscellaneous.model.Civility;
import com.jss.osiris.modules.osiris.miscellaneous.model.Country;
import com.jss.osiris.modules.osiris.miscellaneous.model.DeliveryService;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.model.IAttachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.miscellaneous.model.Language;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;
import com.jss.osiris.modules.osiris.miscellaneous.model.PaymentType;
import com.jss.osiris.modules.osiris.miscellaneous.model.Phone;
import com.jss.osiris.modules.osiris.miscellaneous.model.SpecialOffer;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(indexes = { @Index(name = "idx_tiers_commercial", columnList = "id_commercial"),
})
public class Tiers implements IAttachment, IId {

	@Id
	@SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
	@IndexedField
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tiers_type")
	private TiersType tiersType;

	@Column(length = 80)
	@IndexedField
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	private String denomination;

	@Column(nullable = false)
	@IndexedField
	private Boolean isIndividual;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tiers_category")
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
	@JsonView(JacksonViews.MyJssDetailedView.class)
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
	@JsonView({ JacksonViews.MyJssListView.class, JacksonViews.MyJssDetailedView.class,
			JacksonViews.OsirisDetailedView.class })
	private String firstname;

	@Column(length = 40)
	@IndexedField
	@JsonView({ JacksonViews.MyJssListView.class, JacksonViews.MyJssDetailedView.class,
			JacksonViews.OsirisDetailedView.class })
	private String lastname;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_commercial")
	@IndexedField
	@JsonView(JacksonViews.OsirisDetailedView.class)
	private Employee salesEmployee;

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

	@Column(length = 14)
	@IndexedField
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private String siret;

	@Column(length = 100, nullable = false)
	@IndexedField
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisDetailedView.class })

	private String address;

	@Column(length = 10)
	@IndexedField
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisDetailedView.class })
	private String postalCode;

	@Column(length = 20)
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisDetailedView.class })

	private String cedexComplement;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_city")
	@IndexedField
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisDetailedView.class })
	private City city;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_country")
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisDetailedView.class })
	private Country country;

	@Column(columnDefinition = "NUMERIC(15,2)", precision = 15, scale = 2)
	private BigDecimal rffFormaliteRate;
	private BigDecimal rffInsertionRate;

	@Column(columnDefinition = "TEXT")
	private String observations;

	@ManyToMany
	@JoinTable(name = "asso_tiers_mail", joinColumns = @JoinColumn(name = "id_tiers"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private List<Mail> mails;

	@ManyToMany
	@JoinTable(name = "asso_tiers_phone", joinColumns = @JoinColumn(name = "id_tiers"), inverseJoinColumns = @JoinColumn(name = "id_phone"))
	@JsonView(JacksonViews.MyJssDetailedView.class)
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
	@JoinColumn(name = "id_accounting_account_customer")
	private AccountingAccount accountingAccountCustomer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_accounting_account_deposit")
	private AccountingAccount accountingAccountDeposit;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_accounting_account_litigious")
	private AccountingAccount accountingAccountLitigious;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_accounting_account_suspicious")
	private AccountingAccount accountingAccountSuspicious;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_rff_frequency")
	private RffFrequency rffFrequency;

	@IndexedField
	private Integer idAs400;
	private Integer newIdAs400;

	private Boolean isNewTiers;

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

	public BigDecimal getRffFormaliteRate() {
		return rffFormaliteRate;
	}

	public void setRffFormaliteRate(BigDecimal rffFormaliteRate) {
		this.rffFormaliteRate = rffFormaliteRate;
	}

	public BigDecimal getRffInsertionRate() {
		return rffInsertionRate;
	}

	public void setRffInsertionRate(BigDecimal rffInsertionRate) {
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

	public AccountingAccount getAccountingAccountLitigious() {
		return accountingAccountLitigious;
	}

	public void setAccountingAccountLitigious(AccountingAccount accountingAccountLitigious) {
		this.accountingAccountLitigious = accountingAccountLitigious;
	}

	public AccountingAccount getAccountingAccountSuspicious() {
		return accountingAccountSuspicious;
	}

	public void setAccountingAccountSuspicious(AccountingAccount accountingAccountSuspicious) {
		this.accountingAccountSuspicious = accountingAccountSuspicious;
	}

	public String getSiret() {
		return siret;
	}

	public void setSiret(String siret) {
		this.siret = siret;
	}

	public Boolean getIsNewTiers() {
		return isNewTiers;
	}

	public void setIsNewTiers(Boolean isNewTiers) {
		this.isNewTiers = isNewTiers;
	}

}
