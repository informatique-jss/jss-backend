package com.jss.osiris.modules.osiris.invoicing.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.jackson.JacksonLocalDateSerializer;
import com.jss.osiris.libs.jackson.JacksonLocalDateTimeSerializer;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.osiris.accounting.model.AccountingRecord;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.City;
import com.jss.osiris.modules.osiris.miscellaneous.model.Country;
import com.jss.osiris.modules.osiris.miscellaneous.model.IAttachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.miscellaneous.model.PaymentType;
import com.jss.osiris.modules.osiris.miscellaneous.model.Provider;
import com.jss.osiris.modules.osiris.quotation.model.BankTransfert;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.DirectDebitTransfert;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.tiers.model.BillingLabelType;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Rff;
import com.jss.osiris.modules.osiris.tiers.model.TiersFollowup;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(indexes = { @Index(name = "idx_invoice_status", columnList = "id_invoice_status"),
		@Index(name = "idx_invoice_manual_document_number", columnList = "id_competent_authority,manualAccountingDocumentNumber"),
		@Index(name = "idx_invoice_tiers", columnList = "id_tiers"),
		@Index(name = "idx_invoice_confrere", columnList = "id_confrere"),
		@Index(name = "idx_invoice_rff", columnList = "id_rff"),
		@Index(name = "idx_invoice_provider", columnList = "id_provider"),
		@Index(name = "idx_invoice_credit_note", columnList = "id_credit_note"),
		@Index(name = "idx_invoice_provider", columnList = "id_provider"),
		@Index(name = "idx_invoice_provision", columnList = "id_provision"),
		@Index(name = "idx_invoice_customer_order_id ", columnList = "customer_order_id"),
		@Index(name = "idx_invoice_customer_order_for_inbound_invoice", columnList = "id_customer_order_for_inbound_invoice"),
		@Index(name = "idx_invoice_azure_invoice_id ", columnList = "id_azure_invoice"),
		@Index(name = "idx_invoice_bank_transfert ", columnList = "id_bank_transfert"),
		@Index(name = "idx_invoice_direct_debit_transfert ", columnList = "id_direct_debit_transfert"),
		@Index(name = "idx_invoice_responsable", columnList = "id_responsable"), })
public class Invoice implements IId, IAttachment, ICreatedDate {

	@Id
	@IndexedField
	@GenericGenerator(name = "invoice_id", strategy = "com.jss.osiris.modules.osiris.invoicing.model.InvoiceKeyGenerator")
	@GeneratedValue(generator = "invoice_id")
	private Integer id;

	@Column(nullable = false)
	@JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
	private LocalDateTime createdDate;

	@Column
	@JsonSerialize(using = JacksonLocalDateSerializer.class)
	private LocalDate dueDate;

	@OneToMany(targetEntity = InvoiceItem.class, mappedBy = "invoice", cascade = CascadeType.ALL)
	@JsonIgnoreProperties(value = { "invoice", "originProviderInvoice" }, allowSetters = true)
	private List<InvoiceItem> invoiceItems;

	@OneToOne(fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "invoices" }, allowSetters = true)
	private CustomerOrder customerOrder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_provision")
	@JsonIgnoreProperties(value = { "service", "payments",
			"providerInvoices" }, allowSetters = true)
	private Provision provision;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_responsable")
	private Responsable responsable;

	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "id_competent_authority")
	// @JsonIgnoreProperties(value = { "departments", "cities", "regions" },
	// allowSetters = true)
	// private CompetentAuthority competentAuthority;
	// TODO refonte

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_provider")
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

	// private Boolean isInvoiceFromProvider;

	@Column(length = 40)
	private String commandNumber;

	@Column(name = "total_price", scale = 2)
	private Double totalPrice;

	@OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "invoice", "accountingRecords", "customerOrder", "originPayment",
			"childrenPayments" }, allowSetters = true)
	private List<Payment> payments;

	@OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "invoice", "customerOrder" }, allowSetters = true)
	private List<Refund> refunds;

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
	@JsonIgnoreProperties(value = { "invoices", "providerInvoices", "payments" }, allowSetters = true)
	@JoinColumn(name = "id_customer_order_for_inbound_invoice")
	private CustomerOrder customerOrderForInboundInvoice;

	private Boolean isCreditNote;
	// private Boolean isProviderCreditNote;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_credit_note")
	@JsonIgnoreProperties(value = { "reverseCreditNote", "accountingRecords" }, allowSetters = true)
	private Invoice creditNote;

	@OneToOne(mappedBy = "creditNote", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "creditNote", "customerOrder" }, allowSetters = true)
	private Invoice reverseCreditNote;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_bank_transfert")
	@JsonIgnoreProperties(value = { "customerOrder", "invoices" }, allowSetters = true)
	private BankTransfert bankTransfert;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_direct_debit_transfert")
	private DirectDebitTransfert directDebitTransfert;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_azure_invoice")
	@JsonIgnoreProperties(value = { "invoices" }, allowSetters = true)
	@IndexedField
	private AzureInvoice azureInvoice;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "invoices" }, allowSetters = true)
	@JoinColumn(name = "id_rff")
	private Rff rff;

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

	public Responsable getResponsable() {
		return responsable;
	}

	public void setResponsable(Responsable responsable) {
		this.responsable = responsable;
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

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
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

	public AzureInvoice getAzureInvoice() {
		return azureInvoice;
	}

	public void setAzureInvoice(AzureInvoice azureInvoice) {
		this.azureInvoice = azureInvoice;
	}

	public DirectDebitTransfert getDirectDebitTransfert() {
		return directDebitTransfert;
	}

	public void setDirectDebitTransfert(DirectDebitTransfert directDebitTransfert) {
		this.directDebitTransfert = directDebitTransfert;
	}

	public List<Refund> getRefunds() {
		return refunds;
	}

	public void setRefunds(List<Refund> refunds) {
		this.refunds = refunds;
	}

	public Provision getProvision() {
		return provision;
	}

	public void setProvision(Provision provision) {
		this.provision = provision;
	}

	public Rff getRff() {
		return rff;
	}

	public void setRff(Rff rff) {
		this.rff = rff;
	}

}
