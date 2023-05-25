package com.jss.osiris.modules.invoicing.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.JacksonLocalDateSerializer;
import com.jss.osiris.libs.JacksonLocalDateTimeSerializer;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.City;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.Country;
import com.jss.osiris.modules.miscellaneous.model.IAttachment;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.miscellaneous.model.PaymentType;
import com.jss.osiris.modules.miscellaneous.model.Provider;
import com.jss.osiris.modules.quotation.model.BankTransfert;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.tiers.model.BillingLabelType;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;
import com.jss.osiris.modules.tiers.model.TiersFollowup;

@Entity
@Table(indexes = { @Index(name = "idx_invoice_status", columnList = "id_invoice_status"),
		@Index(name = "idx_invoice_manual_document_number", columnList = "id_competent_authority,manualAccountingDocumentNumber"), })
public class Invoice implements IId, IAttachment, ICreatedDate {

	@Id
	// @SequenceGenerator(name = "invoice_sequence", sequenceName =
	// "invoice_sequence", allocationSize = 1)
	@IndexedField
	// @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
	// "invoice_sequence")
	@GenericGenerator(name = "invoice_id", strategy = "com.jss.osiris.modules.invoicing.model.InvoiceKeyGenerator")
	@GeneratedValue(generator = "invoice_id")
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

	@OneToOne(fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "invoices" }, allowSetters = true)
	private CustomerOrder customerOrder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_responsable")
	@IndexedField
	private Responsable responsable;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_confrere")
	@IndexedField
	private Confrere confrere;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tiers")
	@IndexedField
	private Tiers tiers;
 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_competent_authority")
	@IndexedField 
	@JsonIgnoreProperties(value = { "departments", "cities", "regions" }, allowSetters = true)
	private CompetentAuthority competentAuthority;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_provider")
	@IndexedField
	private Provider provider;

	@Column(length = 300, name = "billing_label")
	@IndexedField
	private String billingLabel;

	@Column(name = "billing_label_address", length = 160)
	private String billingLabelAddress;

	private String billingLabelPostalCode;

	private String billingLabelIntercommunityVat;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_billing_label_city")
	private City billingLabelCity;

	@Column(length = 20)
	private String cedexComplement;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_billing_label_country")
	private Country billingLabelCountry;
	private Boolean billingLabelIsIndividual;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_billing_label_type")
	private BillingLabelType billingLabelType;

	private Boolean isResponsableOnBilling;
	private Boolean isCommandNumberMandatory;

	private Boolean isInvoiceFromProvider;

	@Column(length = 40)
	private String commandNumber;

	@Column(name = "total_price")
	private Float totalPrice;

	@OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "invoice", "accountingRecords", "customerOrder" }, allowSetters = true)
	private List<Payment> payments;

	@OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "invoice", "accountingRecords" }, allowSetters = true)
	private List<Appoint> appoints;

	@OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "invoice", "accountingRecords", "customerOrder" }, allowSetters = true)
	private List<Deposit> deposits;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_invoice_status")
	@IndexedField
	private InvoiceStatus invoiceStatus;

	@OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "invoice", "customerOrder" }, allowSetters = true)
	private List<AccountingRecord> accountingRecords;

	@OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "invoice" }, allowSetters = true)
	private List<Attachment> attachments;

	private LocalDateTime firstReminderDateTime;
	private LocalDateTime secondReminderDateTime;
	private LocalDateTime thirdReminderDateTime;

	private LocalDate manualAccountingDocumentDate;
	@Column(length = 150)
	private String manualAccountingDocumentNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_payment_type")
	private PaymentType manualPaymentType;

	@OneToMany(mappedBy = "invoice")
	@JsonIgnoreProperties(value = { "invoice" }, allowSetters = true)
	private List<TiersFollowup> tiersFollowups;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "invoices", "providerInvoices" }, allowSetters = true)
	@JoinColumn(name = "id_customer_order_for_inbound_invoice")
	private CustomerOrder customerOrderForInboundInvoice;

	private Boolean isCreditNote;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_credit_note")
	@JsonIgnoreProperties(value = { "reverseCreditNote" }, allowSetters = true)
	private Invoice creditNote;

	@OneToOne(mappedBy = "creditNote", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "creditNote", "customerOrder" }, allowSetters = true)
	private Invoice reverseCreditNote;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_bank_transfert")
	@JsonIgnoreProperties(value = { "customerOrder" }, allowSetters = true)
	private BankTransfert bankTransfert;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_azure_invoice")
	@JsonIgnoreProperties(value = { "invoices" }, allowSetters = true)
	private AzureInvoice azureInvoice;

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

	public Boolean getIsInvoiceFromProvider() {
		return isInvoiceFromProvider;
	}

	public void setIsInvoiceFromProvider(Boolean isInvoiceFromProvider) {
		this.isInvoiceFromProvider = isInvoiceFromProvider;
	}

	public List<TiersFollowup> getTiersFollowups() {
		return tiersFollowups;
	}

	public void setTiersFollowups(List<TiersFollowup> tiersFollowups) {
		this.tiersFollowups = tiersFollowups;
	}

	public PaymentType getManualPaymentType() {
		return manualPaymentType;
	}

	public void setManualPaymentType(PaymentType manualPaymentType) {
		this.manualPaymentType = manualPaymentType;
	}

	public CompetentAuthority getCompetentAuthority() {
		return competentAuthority;
	}

	public void setCompetentAuthority(CompetentAuthority competentAuthority) {
		this.competentAuthority = competentAuthority;
	}

	public CustomerOrder getCustomerOrderForInboundInvoice() {
		return customerOrderForInboundInvoice;
	}

	public void setCustomerOrderForInboundInvoice(CustomerOrder customerOrderForInboundInvoice) {
		this.customerOrderForInboundInvoice = customerOrderForInboundInvoice;
	}

	public Boolean getIsCreditNote() {
		return isCreditNote;
	}

	public void setIsCreditNote(Boolean isCreditNote) {
		this.isCreditNote = isCreditNote;
	}

	public Invoice getCreditNote() {
		return creditNote;
	}

	public void setCreditNote(Invoice creditNote) {
		this.creditNote = creditNote;
	}

	public Invoice getReverseCreditNote() {
		return reverseCreditNote;
	}

	public void setReverseCreditNote(Invoice reverseCreditNote) {
		this.reverseCreditNote = reverseCreditNote;
	}

	public BankTransfert getBankTransfert() {
		return bankTransfert;
	}

	public void setBankTransfert(BankTransfert bankTransfert) {
		this.bankTransfert = bankTransfert;
	}

	public String getBillingLabelIntercommunityVat() {
		return billingLabelIntercommunityVat;
	}

	public void setBillingLabelIntercommunityVat(String billingLabelIntercommunityVat) {
		this.billingLabelIntercommunityVat = billingLabelIntercommunityVat;
	}

	public List<Appoint> getAppoints() {
		return appoints;
	}

	public void setAppoints(List<Appoint> appoints) {
		this.appoints = appoints;
	}

	public AzureInvoice getAzureInvoice() {
		return azureInvoice;
	}

	public void setAzureInvoice(AzureInvoice azureInvoice) {
		this.azureInvoice = azureInvoice;
	}

}
