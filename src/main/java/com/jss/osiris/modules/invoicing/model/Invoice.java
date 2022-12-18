package com.jss.osiris.modules.invoicing.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.JacksonLocalDateSerializer;
import com.jss.osiris.libs.JacksonLocalDateTimeSerializer;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.City;
import com.jss.osiris.modules.miscellaneous.model.Country;
import com.jss.osiris.modules.miscellaneous.model.IAttachment;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.miscellaneous.model.Provider;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.tiers.model.BillingLabelType;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

@Entity
@Table(indexes = { @Index(name = "idx_invoice_status", columnList = "id_invoice_status") })
public class Invoice implements IId, IAttachment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@IndexedField
	private Integer id;

	@Column(nullable = false)
	@JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
	private LocalDateTime createdDate;

	@Column
	@JsonSerialize(using = JacksonLocalDateSerializer.class)
	private LocalDate dueDate;

	@OneToMany(targetEntity = InvoiceItem.class, mappedBy = "invoice", cascade = CascadeType.ALL)
	@JsonIgnoreProperties(value = { "invoice" }, allowSetters = true)
	private List<InvoiceItem> invoiceItems;

	@OneToOne
	@JsonIgnoreProperties(value = { "invoices" }, allowSetters = true)
	private CustomerOrder customerOrder;

	@ManyToOne
	@JoinColumn(name = "id_responsable")
	private Responsable responsable;

	@ManyToOne
	@JoinColumn(name = "id_confrere")
	private Confrere confrere;

	@ManyToOne
	@JoinColumn(name = "id_tiers")
	private Tiers tiers;

	@ManyToOne
	@JoinColumn(name = "id_provider")
	private Provider provider;

	@Column(length = 40, name = "billing_label")
	private String billingLabel;

	@Column(name = "billing_label_address", length = 160)
	private String billingLabelAddress;

	private String billingLabelPostalCode;

	@ManyToOne
	@JoinColumn(name = "id_billing_label_city")
	private City billingLabelCity;

	@Column(length = 20)
	private String cedexComplement;

	@ManyToOne
	@JoinColumn(name = "id_billing_label_country")
	private Country billingLabelCountry;
	private Boolean billingLabelIsIndividual;

	@ManyToOne
	@JoinColumn(name = "id_billing_label_type")
	private BillingLabelType billingLabelType;

	private Boolean isResponsableOnBilling;
	private Boolean isCommandNumberMandatory;

	@Column(length = 40)
	private String commandNumber;

	@Column(name = "total_price")
	private Float totalPrice;

	@OneToMany(mappedBy = "invoice")
	@JsonIgnoreProperties(value = { "invoice", "accountingRecords", "customerOrder" }, allowSetters = true)
	private List<Payment> payments;

	@OneToMany(mappedBy = "invoice")
	@JsonIgnoreProperties(value = { "invoice", "accountingRecords", "customerOrder" }, allowSetters = true)
	private List<Deposit> deposits;

	@ManyToOne
	@JoinColumn(name = "id_invoice_status")
	private InvoiceStatus invoiceStatus;

	@OneToMany(mappedBy = "invoice")
	@JsonIgnoreProperties(value = { "invoice", "customerOrder" }, allowSetters = true)
	private List<AccountingRecord> accountingRecords;

	@OneToMany(mappedBy = "invoice")
	@JsonIgnoreProperties(value = { "invoice" }, allowSetters = true)
	private List<Attachment> attachments;

	private LocalDateTime firstReminderDateTime;
	private LocalDateTime secondReminderDateTime;
	private LocalDateTime thirdReminderDateTime;

	private LocalDate manualAccountingDocumentDate;
	@Column(length = 150)
	private String manualAccountingDocumentNumber;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public List<InvoiceItem> getInvoiceItems() {
		return invoiceItems;
	}

	public void setInvoiceItems(List<InvoiceItem> invoiceItems) {
		this.invoiceItems = invoiceItems;
	}

	public Tiers getTiers() {
		return tiers;
	}

	public void setTiers(Tiers tiers) {
		this.tiers = tiers;
	}

	public Responsable getResponsable() {
		return responsable;
	}

	public void setResponsable(Responsable responsable) {
		this.responsable = responsable;
	}

	public Confrere getConfrere() {
		return confrere;
	}

	public void setConfrere(Confrere confrere) {
		this.confrere = confrere;
	}

	public String getBillingLabel() {
		return billingLabel;
	}

	public void setBillingLabel(String billingLabel) {
		this.billingLabel = billingLabel;
	}

	public String getBillingLabelAddress() {
		return billingLabelAddress;
	}

	public void setBillingLabelAddress(String billingLabelAddress) {
		this.billingLabelAddress = billingLabelAddress;
	}

	public String getBillingLabelPostalCode() {
		return billingLabelPostalCode;
	}

	public void setBillingLabelPostalCode(String billingLabelPostalCode) {
		this.billingLabelPostalCode = billingLabelPostalCode;
	}

	public City getBillingLabelCity() {
		return billingLabelCity;
	}

	public void setBillingLabelCity(City billingLabelCity) {
		this.billingLabelCity = billingLabelCity;
	}

	public Country getBillingLabelCountry() {
		return billingLabelCountry;
	}

	public void setBillingLabelCountry(Country billingLabelCountry) {
		this.billingLabelCountry = billingLabelCountry;
	}

	public Boolean getBillingLabelIsIndividual() {
		return billingLabelIsIndividual;
	}

	public void setBillingLabelIsIndividual(Boolean billingLabelIsIndividual) {
		this.billingLabelIsIndividual = billingLabelIsIndividual;
	}

	public BillingLabelType getBillingLabelType() {
		return billingLabelType;
	}

	public void setBillingLabelType(BillingLabelType billingLabelType) {
		this.billingLabelType = billingLabelType;
	}

	public Boolean getIsResponsableOnBilling() {
		return isResponsableOnBilling;
	}

	public void setIsResponsableOnBilling(Boolean isResponsableOnBilling) {
		this.isResponsableOnBilling = isResponsableOnBilling;
	}

	public Boolean getIsCommandNumberMandatory() {
		return isCommandNumberMandatory;
	}

	public void setIsCommandNumberMandatory(Boolean isCommandNumberMandatory) {
		this.isCommandNumberMandatory = isCommandNumberMandatory;
	}

	public String getCommandNumber() {
		return commandNumber;
	}

	public void setCommandNumber(String commandNumber) {
		this.commandNumber = commandNumber;
	}

	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}

	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public Float getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Float totalPrice) {
		this.totalPrice = totalPrice;
	}

	public InvoiceStatus getInvoiceStatus() {
		return invoiceStatus;
	}

	public void setInvoiceStatus(InvoiceStatus invoiceStatus) {
		this.invoiceStatus = invoiceStatus;
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}

	public List<AccountingRecord> getAccountingRecords() {
		return accountingRecords;
	}

	public void setAccountingRecords(List<AccountingRecord> accountingRecords) {
		this.accountingRecords = accountingRecords;
	}

	public List<Deposit> getDeposits() {
		return deposits;
	}

	public void setDeposits(List<Deposit> deposits) {
		this.deposits = deposits;
	}

	public LocalDateTime getFirstReminderDateTime() {
		return firstReminderDateTime;
	}

	public void setFirstReminderDateTime(LocalDateTime firstReminderDateTime) {
		this.firstReminderDateTime = firstReminderDateTime;
	}

	public LocalDateTime getSecondReminderDateTime() {
		return secondReminderDateTime;
	}

	public void setSecondReminderDateTime(LocalDateTime secondReminderDateTime) {
		this.secondReminderDateTime = secondReminderDateTime;
	}

	public LocalDateTime getThirdReminderDateTime() {
		return thirdReminderDateTime;
	}

	public void setThirdReminderDateTime(LocalDateTime thirdReminderDateTime) {
		this.thirdReminderDateTime = thirdReminderDateTime;
	}

	public String getCedexComplement() {
		return cedexComplement;
	}

	public void setCedexComplement(String cedexComplement) {
		this.cedexComplement = cedexComplement;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public String getManualAccountingDocumentNumber() {
		return manualAccountingDocumentNumber;
	}

	public void setManualAccountingDocumentNumber(String manualAccountingDocumentNumber) {
		this.manualAccountingDocumentNumber = manualAccountingDocumentNumber;
	}

	public LocalDate getManualAccountingDocumentDate() {
		return manualAccountingDocumentDate;
	}

	public void setManualAccountingDocumentDate(LocalDate manualAccountingDocumentDate) {
		this.manualAccountingDocumentDate = manualAccountingDocumentDate;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

}
