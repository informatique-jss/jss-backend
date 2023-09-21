package com.jss.osiris.modules.miscellaneous.model;

import java.util.List;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.accounting.model.AccountingAccount;

@Entity
public class CompetentAuthority implements IAttachment, IGenericTiers {

	@Id
	@SequenceGenerator(name = "competent_authority_sequence", sequenceName = "competent_authority_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "competent_authority_sequence")
	private Integer id;

	private String apiId;

	@Column(nullable = false, length = 200)
	@IndexedField
	private String label;

	@Column(nullable = false, length = 20)
	private String code;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_competent_authority_type")
	private CompetentAuthorityType competentAuthorityType;

	@ManyToMany
	@JoinTable(name = "asso_competent_authority_department", joinColumns = @JoinColumn(name = "id_competent_authority"), inverseJoinColumns = @JoinColumn(name = "id_department"))
	private List<Department> departments;

	@ManyToMany
	@JoinTable(name = "asso_competent_authority_mail", joinColumns = @JoinColumn(name = "id_competent_authority"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> mails;

	@ManyToMany
	@JoinTable(name = "asso_competent_authority_accounting_mail", joinColumns = @JoinColumn(name = "id_competent_authority"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> accountingMails;

	@ManyToMany
	@JoinTable(name = "asso_competent_authority_phone", joinColumns = @JoinColumn(name = "id_competent_authority"), inverseJoinColumns = @JoinColumn(name = "id_phone"))
	private List<Phone> phones;

	@Column(length = 40)
	private String iban;

	@Column(length = 40)
	private String bic;

	@Column(length = 60)
	private String contact;

	@ManyToMany
	@JoinTable(name = "asso_competent_authority_city", joinColumns = @JoinColumn(name = "id_competent_authority"), inverseJoinColumns = @JoinColumn(name = "id_city"))
	@JsonIgnoreProperties(value = { "department", "country" }, allowSetters = true)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private List<City> cities;

	@ManyToMany
	@JoinTable(name = "asso_competent_authority_region", joinColumns = @JoinColumn(name = "id_competent_authority"), inverseJoinColumns = @JoinColumn(name = "id_region"))
	private List<Region> regions;

	@Column(length = 60)
	private String mailRecipient;

	@Column(length = 200)
	private String address;

	@Column(length = 10)
	private String postalCode;

	@Column(length = 40)
	private String cedexComplement;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_city")
	private City city;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_country")
	private Country country;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_accounting_account_provider")
	private AccountingAccount accountingAccountProvider;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_accounting_account_customer")
	private AccountingAccount accountingAccountCustomer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_accounting_account_deposit_provider")
	private AccountingAccount accountingAccountDepositProvider;

	private Integer reinvoicing;

	@Column(length = 2000)
	private String schedulle;

	@Column(columnDefinition = "TEXT")
	private String observations;

	@OneToMany(mappedBy = "competentAuthority")
	@JsonIgnoreProperties(value = { "invoice", "customerOrder" }, allowSetters = true)
	private List<Attachment> attachments;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_competent_authority_payment_type", joinColumns = @JoinColumn(name = "id_competent_authority"), inverseJoinColumns = @JoinColumn(name = "id_payment_type"))
	private List<PaymentType> paymentTypes;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_default_payment_type")
	private PaymentType defaultPaymentType;

	private String owncloudFolderName;

	private String inpiReference;

	private String azureCustomReference;

	@Column(length = 20)
	private String intercommunityVat;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getMailRecipient() {
		return mailRecipient;
	}

	public void setMailRecipient(String mailRecipient) {
		this.mailRecipient = mailRecipient;
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

	public List<Department> getDepartments() {
		return departments;
	}

	public void setDepartments(List<Department> departments) {
		this.departments = departments;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public List<City> getCities() {
		return cities;
	}

	public void setCities(List<City> cities) {
		this.cities = cities;
	}

	public List<Region> getRegions() {
		return regions;
	}

	public void setRegions(List<Region> regions) {
		this.regions = regions;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
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

	public CompetentAuthorityType getCompetentAuthorityType() {
		return competentAuthorityType;
	}

	public void setCompetentAuthorityType(CompetentAuthorityType competentAuthorityType) {
		this.competentAuthorityType = competentAuthorityType;
	}

	public Integer getReinvoicing() {
		return reinvoicing;
	}

	public void setReinvoicing(Integer reinvoicing) {
		this.reinvoicing = reinvoicing;
	}

	public String getSchedulle() {
		return schedulle;
	}

	public void setSchedulle(String schedulle) {
		this.schedulle = schedulle;
	}

	public List<Mail> getAccountingMails() {
		return accountingMails;
	}

	public void setAccountingMails(List<Mail> accountingMails) {
		this.accountingMails = accountingMails;
	}

	public String getCedexComplement() {
		return cedexComplement;
	}

	public void setCedexComplement(String cedexComplement) {
		this.cedexComplement = cedexComplement;
	}

	public String getApiId() {
		return apiId;
	}

	public void setApiId(String apiId) {
		this.apiId = apiId;
	}

	public String getBic() {
		return bic;
	}

	public void setBic(String bic) {
		this.bic = bic;
	}

	public AccountingAccount getAccountingAccountDepositProvider() {
		return accountingAccountDepositProvider;
	}

	public AccountingAccount getAccountingAccountDeposit() {
		return accountingAccountDepositProvider;
	}

	public void setAccountingAccountDepositProvider(AccountingAccount accountingAccountDepositProvider) {
		this.accountingAccountDepositProvider = accountingAccountDepositProvider;
	}

	public String getObservations() {
		return observations;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public List<PaymentType> getPaymentTypes() {
		return paymentTypes;
	}

	public void setPaymentTypes(List<PaymentType> paymentTypes) {
		this.paymentTypes = paymentTypes;
	}

	public String getOwncloudFolderName() {
		return owncloudFolderName;
	}

	public void setOwncloudFolderName(String owncloudFolderName) {
		this.owncloudFolderName = owncloudFolderName;
	}

	public PaymentType getDefaultPaymentType() {
		return defaultPaymentType;
	}

	public void setDefaultPaymentType(PaymentType defaultPaymentType) {
		this.defaultPaymentType = defaultPaymentType;
	}

	public String getInpiReference() {
		return inpiReference;
	}

	public void setInpiReference(String inpiReference) {
		this.inpiReference = inpiReference;
	}

	public String getAzureCustomReference() {
		return azureCustomReference;
	}

	public void setAzureCustomReference(String azureCustomReference) {
		this.azureCustomReference = azureCustomReference;
	}

	public String getIntercommunityVat() {
		return intercommunityVat;
	}

	public void setIntercommunityVat(String intercommunityVat) {
		this.intercommunityVat = intercommunityVat;
	}

}
