package com.jss.osiris.modules.quotation.model;

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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.miscellaneous.model.City;
import com.jss.osiris.modules.miscellaneous.model.Country;
import com.jss.osiris.modules.miscellaneous.model.Department;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.Language;
import com.jss.osiris.modules.miscellaneous.model.Mail;
import com.jss.osiris.modules.miscellaneous.model.PaymentType;
import com.jss.osiris.modules.miscellaneous.model.Phone;
import com.jss.osiris.modules.miscellaneous.model.Regie;
import com.jss.osiris.modules.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.miscellaneous.model.VatCollectionType;
import com.jss.osiris.modules.miscellaneous.model.WeekDay;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.tiers.model.ITiers;
import com.jss.osiris.modules.tiers.model.TiersFollowup;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Confrere implements ITiers {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false, length = 20)
	private String code;

	@Column(length = 100)
	private String label;

	@ManyToMany
	@JoinTable(name = "asso_confrere_department", joinColumns = @JoinColumn(name = "id_confrere"), inverseJoinColumns = @JoinColumn(name = "id_department"))
	private List<Department> departments;

	@ManyToMany
	@JoinTable(name = "asso_confrere_mail", joinColumns = @JoinColumn(name = "id_confrere"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> mails;

	@ManyToMany
	@JoinTable(name = "asso_confrere_accounting_mail", joinColumns = @JoinColumn(name = "id_confrere"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> accountingMails;

	@ManyToMany
	@JoinTable(name = "asso_confrere_phone", joinColumns = @JoinColumn(name = "id_confrere"), inverseJoinColumns = @JoinColumn(name = "id_phone"))
	private List<Phone> phones;

	@ManyToMany
	@JoinTable(name = "asso_confrere_special_offer", joinColumns = @JoinColumn(name = "id_confrere"), inverseJoinColumns = @JoinColumn(name = "id_special_offer"))
	private List<SpecialOffer> specialOffers;

	private Integer reinvoicing;

	@ManyToMany
	@JoinTable(name = "asso_confrere_publication_day", joinColumns = @JoinColumn(name = "id_confrere"), inverseJoinColumns = @JoinColumn(name = "id_weekday"))
	private List<WeekDay> weekDays;

	@ManyToOne
	@JoinColumn(name = "id_journal_type")
	private JournalType journalType;

	@Column(length = 60)
	private String lastShipmentForPublication;

	@Column(length = 20)
	private String publicationCertificateDocumentGrade;

	@Column(length = 20)
	private String billingGrade;

	@Column(length = 20)
	private String paperGrade;

	@Column(length = 20)
	private String boardGrade;

	private Float shippingCosts;

	private Float administrativeFees;

	private Integer numberOfPrint;

	private Float paperPrice;

	private Integer discountRate;

	@ManyToOne
	@JoinColumn(name = "id_accounting_account_provider")
	private AccountingAccount accountingAccountProvider;

	@ManyToOne
	@JoinColumn(name = "id_accounting_account_customer")
	private AccountingAccount accountingAccountCustomer;

	@ManyToOne
	@JoinColumn(name = "id_accounting_account_deposit")
	private AccountingAccount accountingAccountDeposit;

	@Column(length = 60)
	private String mailRecipient;

	@Column(length = 100)
	private String address;

	@Column(length = 10)
	private String postalCode;

	@Column(length = 20)
	private String cedexComplement;

	@ManyToOne
	@JoinColumn(name = "id_city")
	private City city;

	@ManyToOne
	@JoinColumn(name = "id_country")
	private Country country;

	@Column(columnDefinition = "TEXT")
	private String observations;

	@ManyToOne
	@JoinColumn(name = "id_vat_collection_type")
	private VatCollectionType vatCollectionType;

	@OneToMany(mappedBy = "confrere", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "confrere" }, allowSetters = true)
	private List<Document> documents;

	@Column(nullable = false)
	private Boolean isProvisionalPaymentMandatory;

	@Column(nullable = false)
	private Boolean isSepaMandateReceived;

	@ManyToOne
	@JoinColumn(name = "id_payment_type")
	private PaymentType paymentType;

	@Column(length = 40)
	@JsonProperty("paymentIban")
	private String paymentIban;

	@Column(length = 40)
	private String paymentBic;

	@ManyToOne
	@JoinColumn(name = "id_commercial")
	private Employee salesEmployee;

	@ManyToOne
	@JoinColumn(name = "id_formaliste")
	private Employee formalisteEmployee;

	@ManyToOne
	@JoinColumn(name = "id_insertion")
	private Employee insertionEmployee;

	@ManyToOne
	@JoinColumn(name = "id_language")
	private Language language;

	@ManyToOne
	@JoinColumn(name = "id_regie")
	private Regie regie;

	@OneToMany(mappedBy = "confrere")
	@JsonIgnoreProperties(value = { "confrere" }, allowSetters = true)
	private List<TiersFollowup> tiersFollowups;

	public Integer getId() {
		return id;
	}

	public List<Mail> getAccountingMails() {
		return accountingMails;
	}

	public Boolean getIsProvisionalPaymentMandatory() {
		return isProvisionalPaymentMandatory;
	}

	public void setIsProvisionalPaymentMandatory(Boolean isProvisionalPaymentMandatory) {
		this.isProvisionalPaymentMandatory = isProvisionalPaymentMandatory;
	}

	public Boolean getIsSepaMandateReceived() {
		return isSepaMandateReceived;
	}

	public void setIsSepaMandateReceived(Boolean isSepaMandateReceived) {
		this.isSepaMandateReceived = isSepaMandateReceived;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public void setAccountingMails(List<Mail> accountingMails) {
		this.accountingMails = accountingMails;
	}

	public JournalType getJournalType() {
		return journalType;
	}

	public void setJournalType(JournalType journalType) {
		this.journalType = journalType;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<Department> getDepartments() {
		return departments;
	}

	public void setDepartments(List<Department> departments) {
		this.departments = departments;
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

	public Integer getReinvoicing() {
		return reinvoicing;
	}

	public void setReinvoicing(Integer reinvoicing) {
		this.reinvoicing = reinvoicing;
	}

	public List<WeekDay> getWeekDays() {
		return weekDays;
	}

	public void setWeekDays(List<WeekDay> weekDays) {
		this.weekDays = weekDays;
	}

	public String getLastShipmentForPublication() {
		return lastShipmentForPublication;
	}

	public void setLastShipmentForPublication(String lastShipmentForPublication) {
		this.lastShipmentForPublication = lastShipmentForPublication;
	}

	public String getPublicationCertificateDocumentGrade() {
		return publicationCertificateDocumentGrade;
	}

	public void setPublicationCertificateDocumentGrade(String publicationCertificateDocumentGrade) {
		this.publicationCertificateDocumentGrade = publicationCertificateDocumentGrade;
	}

	public String getBillingGrade() {
		return billingGrade;
	}

	public void setBillingGrade(String billingGrade) {
		this.billingGrade = billingGrade;
	}

	public String getPaperGrade() {
		return paperGrade;
	}

	public void setPaperGrade(String paperGrade) {
		this.paperGrade = paperGrade;
	}

	public String getBoardGrade() {
		return boardGrade;
	}

	public void setBoardGrade(String boardGrade) {
		this.boardGrade = boardGrade;
	}

	public Float getShippingCosts() {
		return shippingCosts;
	}

	public void setShippingCosts(Float shippingCosts) {
		this.shippingCosts = shippingCosts;
	}

	public Float getAdministrativeFees() {
		return administrativeFees;
	}

	public void setAdministrativeFees(Float administrativeFees) {
		this.administrativeFees = administrativeFees;
	}

	public Integer getNumberOfPrint() {
		return numberOfPrint;
	}

	public void setNumberOfPrint(Integer numberOfPrint) {
		this.numberOfPrint = numberOfPrint;
	}

	public Float getPaperPrice() {
		return paperPrice;
	}

	public void setPaperPrice(Float paperPrice) {
		this.paperPrice = paperPrice;
	}

	public String getObservations() {
		return observations;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

	public VatCollectionType getVatCollectionType() {
		return vatCollectionType;
	}

	public void setVatCollectionType(VatCollectionType vatCollectionType) {
		this.vatCollectionType = vatCollectionType;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	public List<SpecialOffer> getSpecialOffers() {
		return specialOffers;
	}

	public void setSpecialOffers(List<SpecialOffer> specialOffers) {
		this.specialOffers = specialOffers;
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

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	@Override
	public Boolean getIsIndividual() {
		return false;
	}

	public AccountingAccount getAccountingAccountDeposit() {
		return accountingAccountDeposit;
	}

	public void setAccountingAccountDeposit(AccountingAccount accountingAccountDeposit) {
		this.accountingAccountDeposit = accountingAccountDeposit;
	}

	public Integer getDiscountRate() {
		return discountRate;
	}

	public void setDiscountRate(Integer discountRate) {
		this.discountRate = discountRate;
	}

	public Regie getRegie() {
		return regie;
	}

	public void setRegie(Regie regie) {
		this.regie = regie;
	}

	public List<TiersFollowup> getTiersFollowups() {
		return tiersFollowups;
	}

	public void setTiersFollowups(List<TiersFollowup> tiersFollowups) {
		this.tiersFollowups = tiersFollowups;
	}

	public String getCedexComplement() {
		return cedexComplement;
	}

	public void setCedexComplement(String cedexComplement) {
		this.cedexComplement = cedexComplement;
	}

	public String getPaymentIban() {
		return paymentIban;
	}

	public void setPaymentIban(String paymentIban) {
		this.paymentIban = paymentIban;
	}

	public String getPaymentBic() {
		return paymentBic;
	}

	public void setPaymentBic(String paymentBic) {
		this.paymentBic = paymentBic;
	}

}
