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
public class Provider implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false)
	private String label;

	@ManyToOne
	@JoinColumn(name = "id_accounting_account_provider")
	private AccountingAccount accountingAccountProvider;

	@ManyToOne
	@JoinColumn(name = "id_accounting_account_customer")
	private AccountingAccount accountingAccountCustomer;

	@ManyToOne
	@JoinColumn(name = "id_accounting_account_deposit")
	private AccountingAccount accountingAccountDeposit;

	@Column(length = 40)
	private String bic;

	@Column(length = 20)
	private String jssReference;

	@Column(length = 40)
	private String iban;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_provider_mail", joinColumns = @JoinColumn(name = "id_provider"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> mails;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_provider_accounting_mail", joinColumns = @JoinColumn(name = "id_provider"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> accountingMails;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_provider_phone", joinColumns = @JoinColumn(name = "id_provider"), inverseJoinColumns = @JoinColumn(name = "id_phone"))
	private List<Phone> phones;

	@ManyToOne
	@JoinColumn(name = "id_vat_collection_type")
	private VatCollectionType vatCollectionType;

	@ManyToOne
	@JoinColumn(name = "id_payment_type")
	private PaymentType paymentType;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
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

	public String getBic() {
		return bic;
	}

	public void setBic(String bic) {
		this.bic = bic;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public AccountingAccount getAccountingAccountDeposit() {
		return accountingAccountDeposit;
	}

	public void setAccountingAccountDeposit(AccountingAccount accountingAccountDeposit) {
		this.accountingAccountDeposit = accountingAccountDeposit;
	}

	public List<Mail> getMails() {
		return mails;
	}

	public void setMails(List<Mail> mails) {
		this.mails = mails;
	}

	public List<Mail> getAccountingMails() {
		return accountingMails;
	}

	public void setAccountingMails(List<Mail> accountingMails) {
		this.accountingMails = accountingMails;
	}

	public List<Phone> getPhones() {
		return phones;
	}

	public void setPhones(List<Phone> phones) {
		this.phones = phones;
	}

	public String getJssReference() {
		return jssReference;
	}

	public void setJssReference(String jssReference) {
		this.jssReference = jssReference;
	}

	public VatCollectionType getVatCollectionType() {
		return vatCollectionType;
	}

	public void setVatCollectionType(VatCollectionType vatCollectionType) {
		this.vatCollectionType = vatCollectionType;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

}
