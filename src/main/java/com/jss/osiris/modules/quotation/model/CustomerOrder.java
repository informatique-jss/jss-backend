package com.jss.osiris.modules.quotation.model;

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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.JacksonLocalDateTimeSerializer;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.invoicing.model.Deposit;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.tiers.model.BillingLabelType;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

@Entity
@Table(indexes = { @Index(name = "idx_customer_order_status", columnList = "id_customer_order_status") })
public class CustomerOrder implements IQuotation {

	public CustomerOrder() {
	}

	public CustomerOrder(Tiers tiers, Responsable responsable, Confrere confrere,
			List<SpecialOffer> specialOffers, LocalDateTime createdDate, CustomerOrderStatus customerOrderStatus,
			String observations, String description, List<Attachment> attachments, List<Document> documents,
			BillingLabelType labelType, Responsable customLabelResponsable, Tiers customLabelTiers,
			RecordType recordType, List<AssoAffaireOrder> assoAffaireOrders,
			List<Quotation> quotations, Boolean overrideSpecialOffer, String quotationLabel, Boolean isQuotation,
			List<Invoice> invoices, List<Payment> payments, List<Deposit> deposits,
			List<AccountingRecord> accountingRecords) {
		this.tiers = tiers;
		this.responsable = responsable;
		this.confrere = confrere;
		this.specialOffers = specialOffers;
		this.createdDate = createdDate;
		this.customerOrderStatus = customerOrderStatus;
		this.observations = observations;
		this.description = description;
		this.attachments = attachments;
		this.documents = documents;
		this.quotationLabelType = labelType;
		this.customLabelResponsable = customLabelResponsable;
		this.customLabelTiers = customLabelTiers;
		this.recordType = recordType;
		this.assoAffaireOrders = assoAffaireOrders;
		this.quotations = quotations;
		this.overrideSpecialOffer = overrideSpecialOffer;
		this.quotationLabel = quotationLabel;
		this.isQuotation = isQuotation;
		this.invoices = invoices;
		this.payments = payments;
		this.deposits = deposits;
		this.accountingRecords = accountingRecords;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@IndexedField
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_tiers")
	@IndexedField
	private Tiers tiers;

	@ManyToOne
	@JoinColumn(name = "id_responsable")
	@IndexedField
	private Responsable responsable;

	@ManyToOne
	@JoinColumn(name = "id_confrere")
	@IndexedField
	private Confrere confrere;

	@ManyToMany
	@JoinTable(name = "asso_customer_order_special_offer", joinColumns = @JoinColumn(name = "id_customer_order"), inverseJoinColumns = @JoinColumn(name = "id_special_offer"))
	private List<SpecialOffer> specialOffers;

	@IndexedField
	@JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
	private LocalDateTime createdDate;

	@ManyToOne
	@JoinColumn(name = "id_customer_order_status")
	private CustomerOrderStatus customerOrderStatus;

	private LocalDateTime lastStatusUpdate;

	@Column(columnDefinition = "TEXT")
	private String observations;

	@Column(columnDefinition = "TEXT")
	private String description;

	@OneToMany(mappedBy = "customerOrder")
	@JsonIgnoreProperties(value = { "customerOrder" }, allowSetters = true)
	private List<Attachment> attachments;

	@OneToMany(targetEntity = Document.class, mappedBy = "customerOrder", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "customerOrder" }, allowSetters = true)
	private List<Document> documents;

	@ManyToOne
	@JoinColumn(name = "id_quotation_label_type")
	private BillingLabelType quotationLabelType;

	@ManyToOne
	@JoinColumn(name = "id_custom_label_responsable")
	private Responsable customLabelResponsable;

	@ManyToOne
	@JoinColumn(name = "id_custom_label_tiers")
	private Tiers customLabelTiers;

	@ManyToOne
	@JoinColumn(name = "id_record_type")
	private RecordType recordType;

	@OneToMany(targetEntity = AssoAffaireOrder.class, mappedBy = "customerOrder", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "customerOrder" }, allowSetters = true)
	private List<AssoAffaireOrder> assoAffaireOrders;

	@ManyToMany(mappedBy = "customerOrders")
	@JsonIgnoreProperties(value = { "customerOrders" }, allowSetters = true)
	private List<Quotation> quotations;

	@Column(nullable = false)
	private Boolean overrideSpecialOffer;

	@Column(length = 40)
	private String quotationLabel;

	@Column(nullable = false)
	private Boolean isQuotation;

	@OneToMany(mappedBy = "customerOrder")
	@JsonIgnoreProperties(value = { "customerOrder" }, allowSetters = true)
	private List<Invoice> invoices;

	@OneToMany(targetEntity = Payment.class, mappedBy = "customerOrder")
	@JsonIgnoreProperties(value = { "customerOrder", "accountingRecords", "invoice" }, allowSetters = true)
	private List<Payment> payments;

	@OneToMany(targetEntity = Deposit.class, mappedBy = "customerOrder")
	@JsonIgnoreProperties(value = { "customerOrder", "accountingRecords", "invoice" }, allowSetters = true)
	private List<Deposit> deposits;

	@OneToMany(targetEntity = AccountingRecord.class, mappedBy = "customerOrder")
	@JsonIgnoreProperties(value = { "customerOrder", "invoice", "deposit", "payment" }, allowSetters = true)
	private List<AccountingRecord> accountingRecords;

	private String centralPayPaymentRequestId;

	private Boolean isCreatedFromWebSite;

	private LocalDateTime firstReminderDateTime;
	private LocalDateTime secondReminderDateTime;
	private LocalDateTime thirdReminderDateTime;

	// TODO : put it at 0 when payment is grabbed and matched
	private Float centralPayPendingPaymentAmount;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Tiers getTiers() {
		return tiers;
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}

	public List<Deposit> getDeposits() {
		return deposits;
	}

	public void setDeposits(List<Deposit> deposits) {
		this.deposits = deposits;
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

	public List<SpecialOffer> getSpecialOffers() {
		return specialOffers;
	}

	public void setSpecialOffers(List<SpecialOffer> specialOffers) {
		this.specialOffers = specialOffers;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public String getObservations() {
		return observations;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	public RecordType getRecordType() {
		return recordType;
	}

	public void setRecordType(RecordType recordType) {
		this.recordType = recordType;
	}

	public Boolean getOverrideSpecialOffer() {
		return overrideSpecialOffer;
	}

	public void setOverrideSpecialOffer(Boolean overrideSpecialOffer) {
		this.overrideSpecialOffer = overrideSpecialOffer;
	}

	public Responsable getCustomLabelResponsable() {
		return customLabelResponsable;
	}

	public void setCustomLabelResponsable(Responsable customLabelResponsable) {
		this.customLabelResponsable = customLabelResponsable;
	}

	public Tiers getCustomLabelTiers() {
		return customLabelTiers;
	}

	public void setCustomLabelTiers(Tiers customLabelTiers) {
		this.customLabelTiers = customLabelTiers;
	}

	public String getQuotationLabel() {
		return quotationLabel;
	}

	public void setQuotationLabel(String quotationLabel) {
		this.quotationLabel = quotationLabel;
	}

	public Boolean getIsQuotation() {
		return isQuotation;
	}

	public void setIsQuotation(Boolean isQuotation) {
		this.isQuotation = isQuotation;
	}

	public Confrere getConfrere() {
		return confrere;
	}

	public void setConfrere(Confrere confrere) {
		this.confrere = confrere;
	}

	public List<Quotation> getQuotations() {
		return quotations;
	}

	public void setQuotations(List<Quotation> quotations) {
		this.quotations = quotations;
	}

	public List<Invoice> getInvoices() {
		return invoices;
	}

	public void setInvoices(List<Invoice> invoices) {
		this.invoices = invoices;
	}

	public List<AccountingRecord> getAccountingRecords() {
		return accountingRecords;
	}

	public void setAccountingRecords(List<AccountingRecord> accountingRecords) {
		this.accountingRecords = accountingRecords;
	}

	public List<AssoAffaireOrder> getAssoAffaireOrders() {
		return assoAffaireOrders;
	}

	public void setAssoAffaireOrders(List<AssoAffaireOrder> assoAffaireOrders) {
		this.assoAffaireOrders = assoAffaireOrders;
	}

	public CustomerOrderStatus getCustomerOrderStatus() {
		return customerOrderStatus;
	}

	public void setCustomerOrderStatus(CustomerOrderStatus customerOrderStatus) {
		this.customerOrderStatus = customerOrderStatus;
	}

	public String getCentralPayPaymentRequestId() {
		return centralPayPaymentRequestId;
	}

	public void setCentralPayPaymentRequestId(String centralPayPaymentRequestId) {
		this.centralPayPaymentRequestId = centralPayPaymentRequestId;
	}

	public Float getCentralPayPendingPaymentAmount() {
		return centralPayPendingPaymentAmount;
	}

	public void setCentralPayPendingPaymentAmount(Float centralPayPendingPaymentAmount) {
		this.centralPayPendingPaymentAmount = centralPayPendingPaymentAmount;
	}

	public LocalDateTime getLastStatusUpdate() {
		return lastStatusUpdate;
	}

	public void setLastStatusUpdate(LocalDateTime lastStatusUpdate) {
		this.lastStatusUpdate = lastStatusUpdate;
	}

	public Boolean getIsCreatedFromWebSite() {
		return isCreatedFromWebSite;
	}

	public void setIsCreatedFromWebSite(Boolean isCreatedFromWebSite) {
		this.isCreatedFromWebSite = isCreatedFromWebSite;
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

	public BillingLabelType getQuotationLabelType() {
		return quotationLabelType;
	}

	public void setQuotationLabelType(BillingLabelType quotationLabelType) {
		this.quotationLabelType = quotationLabelType;
	}

}
