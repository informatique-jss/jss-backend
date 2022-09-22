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
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.miscellaneous.model.Mail;
import com.jss.osiris.modules.miscellaneous.model.Phone;

@Entity
public class Regie implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false, length = 100)
	private String label;

	@Column(nullable = false, length = 20)
	private String code;

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
	@JoinTable(name = "asso_regie_mail", joinColumns = @JoinColumn(name = "id_regie"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> mails;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_regie_phone", joinColumns = @JoinColumn(name = "id_regie"), inverseJoinColumns = @JoinColumn(name = "id_phone"))
	private List<Phone> phones;

	@Column(length = 40, nullable = false)
	private String iban;

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

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
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

	public AccountingAccount getAccountingAccountDeposit() {
		return accountingAccountDeposit;
	}

	public void setAccountingAccountDeposit(AccountingAccount accountingAccountDeposit) {
		this.accountingAccountDeposit = accountingAccountDeposit;
	}

}
