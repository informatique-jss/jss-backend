package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;
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

import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.miscellaneous.model.City;
import com.jss.osiris.modules.miscellaneous.model.Country;
import com.jss.osiris.modules.miscellaneous.model.Department;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.miscellaneous.model.Mail;
import com.jss.osiris.modules.miscellaneous.model.Phone;
import com.jss.osiris.modules.miscellaneous.model.WeekDay;

@Entity
public class Confrere implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false, length = 20)
	private String code;

	@Column(length = 40)
	private String label;

	@ManyToMany
	@JoinTable(name = "asso_confrere_department", joinColumns = @JoinColumn(name = "id_confrere"), inverseJoinColumns = @JoinColumn(name = "id_department"))
	private List<Department> departments;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_confrere_mail", joinColumns = @JoinColumn(name = "id_confrere"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> mails;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_confrere_accounting_mail", joinColumns = @JoinColumn(name = "id_confrere"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> accountingMails;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_confrere_phone", joinColumns = @JoinColumn(name = "id_confrere"), inverseJoinColumns = @JoinColumn(name = "id_phone"))
	private List<Phone> phones;

	private Integer discountRate;

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

	private Integer paperPrice;

	@ManyToOne
	@JoinColumn(name = "id_accounting_account_provider")
	private AccountingAccount accountingAccountProvider;

	@ManyToOne
	@JoinColumn(name = "id_accounting_account_customer")
	private AccountingAccount accountingAccountCustomer;

	@Column(length = 60)
	private String mailRecipient;

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

	@Column(length = 40)
	private String iban;

	@ManyToOne
	@JoinColumn(name = "id_regie")
	private Regie regie;

	@Column(columnDefinition = "TEXT")
	private String observations;

	public Integer getId() {
		return id;
	}

	public List<Mail> getAccountingMails() {
		return accountingMails;
	}

	public void setAccountingMails(List<Mail> accountingMails) {
		this.accountingMails = accountingMails;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public Regie getRegie() {
		return regie;
	}

	public void setRegie(Regie regie) {
		this.regie = regie;
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

	public Integer getDiscountRate() {
		return discountRate;
	}

	public void setDiscountRate(Integer discountRate) {
		this.discountRate = discountRate;
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

	public Integer getPaperPrice() {
		return paperPrice;
	}

	public void setPaperPrice(Integer paperPrice) {
		this.paperPrice = paperPrice;
	}

	public String getObservations() {
		return observations;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

}
