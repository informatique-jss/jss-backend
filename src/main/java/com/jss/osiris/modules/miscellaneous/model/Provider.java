package com.jss.osiris.modules.miscellaneous.model;

import java.util.List;

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

import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.accounting.model.AccountingAccount;

@Entity
public class Provider implements IAttachment, IGenericTiers {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false)
	@IndexedField
	private String label;

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
	@JoinColumn(name = "id_default_billing_item")
	private BillingItem defaultBillingItem;

	@Column(length = 20)
	private String jssReference;

	@Column(length = 40)
	private String iban;

	@Column(length = 40)
	private String bic;

	@ManyToMany
	@JoinTable(name = "asso_provider_mail", joinColumns = @JoinColumn(name = "id_provider"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> mails;

	@ManyToMany
	@JoinTable(name = "asso_provider_accounting_mail", joinColumns = @JoinColumn(name = "id_provider"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> accountingMails;

	@ManyToMany
	@JoinTable(name = "asso_provider_phone", joinColumns = @JoinColumn(name = "id_provider"), inverseJoinColumns = @JoinColumn(name = "id_phone"))
	private List<Phone> phones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_vat_collection_type")
	private VatCollectionType vatCollectionType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_payment_type")
	private PaymentType paymentType;

	@OneToMany(mappedBy = "provider")
	private List<Attachment> attachments;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_city")
	private City city;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_country")
	private Country country;

	private Integer idAs400;

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

	public BillingItem getDefaultBillingItem() {
		return defaultBillingItem;
	}

	public void setDefaultBillingItem(BillingItem defaultBillingItem) {
		this.defaultBillingItem = defaultBillingItem;
	}

	public String getBic() {
		return bic;
	}

	public void setBic(String bic) {
		this.bic = bic;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
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

	public Integer getIdAs400() {
		return idAs400;
	}

	public void setIdAs400(Integer idAs400) {
		this.idAs400 = idAs400;
	}

}
