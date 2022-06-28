package com.jss.osiris.modules.miscellaneous.model;

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

@Entity
public class CompetentAuthority implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	// TODO : reprendre les label + code, mettre des limites de taille et faire les
	// vérifs front / back
	@Column(nullable = false)
	private String label;

	private String code;

	@ManyToOne
	@JoinColumn(name = "id_competent_authority_type")
	private CompetentAuthorityType competentAuthorityType;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_competent_authority_department", joinColumns = @JoinColumn(name = "id_competent_authority"), inverseJoinColumns = @JoinColumn(name = "id_department"))
	private List<Department> departments;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_competent_authority_mail", joinColumns = @JoinColumn(name = "id_competent_authority"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> mails;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_competent_authority_phone", joinColumns = @JoinColumn(name = "id_competent_authority"), inverseJoinColumns = @JoinColumn(name = "id_phone"))
	private List<Phone> phones;

	@Column(length = 40, nullable = false)
	private String iban;

	@Column(length = 40)
	private String jssAccount;

	@Column(length = 60)
	private String contact;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_competent_authority_city", joinColumns = @JoinColumn(name = "id_competent_authority"), inverseJoinColumns = @JoinColumn(name = "id_city"))
	private List<City> cities;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_competent_authority_region", joinColumns = @JoinColumn(name = "id_competent_authority"), inverseJoinColumns = @JoinColumn(name = "id_region"))
	private List<Region> regions;

	@Column(nullable = false)
	private Boolean hasAccount;

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

	@ManyToOne
	@JoinColumn(name = "id_accounting_account_provider")
	private AccountingAccount accountingAccountProvider;

	@ManyToOne
	@JoinColumn(name = "id_accounting_account_customer")
	private AccountingAccount accountingAccountCustomer;

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

	public Boolean getHasAccount() {
		return hasAccount;
	}

	public void setHasAccount(Boolean hasAccount) {
		this.hasAccount = hasAccount;
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

	public String getJssAccount() {
		return jssAccount;
	}

	public void setJssAccount(String jssAccount) {
		this.jssAccount = jssAccount;
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

}
