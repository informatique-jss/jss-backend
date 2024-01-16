package com.jss.osiris.modules.quotation.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.JacksonLocalDateTimeSerializer;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.invoicing.model.ICreatedDate;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.Refund;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.CustomerOrderOrigin;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

@Entity
@Table(indexes = { @Index(name = "idx_customer_order_status", columnList = "id_customer_order_status"),
		@Index(name = "idx_customer_order_responsable", columnList = "id_responsable"),
		@Index(name = "idx_customer_order_tiers", columnList = "id_tiers") })
public class CustomerOrder implements IQuotation, ICreatedDate {

	public CustomerOrder() {
	}

	public CustomerOrder(Employee assignedTo, Tiers tiers, Responsable responsable, Confrere confrere,
			List<SpecialOffer> specialOffers, LocalDateTime createdDate, CustomerOrderStatus customerOrderStatus,
			String observations, String description, String instructions, List<Attachment> attachments,
			List<Document> documents,
			List<AssoAffaireOrder> assoAffaireOrders,
			List<Quotation> quotations, String quotationLabel, Boolean isQuotation,
			List<Invoice> invoices, List<AccountingRecord> accountingRecords) {
		this.assignedTo = assignedTo;
		this.tiers = tiers;
		this.responsable = responsable;
		this.confrere = confrere;
		this.specialOffers = specialOffers;
		this.createdDate = createdDate;
		this.customerOrderStatus = customerOrderStatus;
		this.observations = observations;
		this.description = description;
		this.instructions = instructions;
		this.attachments = attachments;
		this.documents = documents;
		this.assoAffaireOrders = assoAffaireOrders;
		this.quotations = quotations;
		this.isQuotation = isQuotation;
		this.invoices = invoices;
		this.accountingRecords = accountingRecords;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@IndexedField
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_assigned_to")
	@IndexedField
	private Employee assignedTo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tiers")
	@IndexedField
	private Tiers tiers;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_responsable")
	@IndexedField
	private Responsable responsable;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_confrere")
	private Confrere confrere;

	@ManyToMany
	@JoinTable(name = "asso_customer_order_special_offer", joinColumns = @JoinColumn(name = "id_customer_order"), inverseJoinColumns = @JoinColumn(name = "id_special_offer"))
	private List<SpecialOffer> specialOffers;

	@JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
	@IndexedField
	private LocalDateTime createdDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_customer_order_status")
	private CustomerOrderStatus customerOrderStatus;

	private LocalDateTime lastStatusUpdate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_abandon_reason")
	private AbandonReason abandonReason;

	@Column(columnDefinition = "TEXT")
	private String observations;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(columnDefinition = "TEXT")
	private String instructions;

	@OneToMany(mappedBy = "customerOrder")
	@JsonIgnoreProperties(value = { "customerOrder" }, allowSetters = true)
	private List<Attachment> attachments;

	@OneToMany(targetEntity = Document.class, mappedBy = "customerOrder", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "customerOrder" }, allowSetters = true)
	private List<Document> documents;

	@OneToMany(targetEntity = AssoAffaireOrder.class, mappedBy = "customerOrder", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "customerOrder" }, allowSetters = true)
	private List<AssoAffaireOrder> assoAffaireOrders;

	@ManyToMany(mappedBy = "customerOrders")
	@JsonIgnoreProperties(value = { "customerOrders" }, allowSetters = true)
	@JsonIgnore // For client-side performance purpose
	private List<Quotation> quotations;

	@Column(nullable = false)
	private Boolean isQuotation;

	@OneToMany(mappedBy = "customerOrder")
	@JsonIgnoreProperties(value = { "customerOrder" }, allowSetters = true)
	@JsonIgnore // For client-side performance purpose
	private List<Invoice> invoices;

	@OneToMany(mappedBy = "customerOrder", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "invoice", "accountingRecords", "customerOrder", "originPayment",
			"childrenPayments" }, allowSetters = true)
	private List<Payment> payments;

	@OneToMany(targetEntity = AccountingRecord.class, mappedBy = "customerOrder")
	@JsonIgnoreProperties(value = { "customerOrder", "invoice", "deposit", "payment" }, allowSetters = true)
	@JsonIgnore // For client-side performance purpose
	private List<AccountingRecord> accountingRecords;

	private LocalDateTime firstReminderDateTime;
	private LocalDateTime secondReminderDateTime;
	private LocalDateTime thirdReminderDateTime;

	@OneToMany(targetEntity = Invoice.class, mappedBy = "customerOrderForInboundInvoice")
	@JsonIgnoreProperties(value = { "customerOrder" }, allowSetters = true)
	@JsonIgnore // For client-side performance purpose
	private List<Invoice> providerInvoices;

	@OneToMany(targetEntity = Refund.class, mappedBy = "customerOrder")
	@JsonIgnoreProperties(value = { "customerOrder" }, allowSetters = true)
	private List<Refund> refunds;

	@Column(columnDefinition = "TEXT")
	private String customerMailCustomMessage;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_customer_order_origin")
	private CustomerOrderOrigin customerOrderOrigin;

	private Boolean isGifted;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public CustomerOrderStatus getCustomerOrderStatus() {
		return customerOrderStatus;
	}

	public void setCustomerOrderStatus(CustomerOrderStatus customerOrderStatus) {
		this.customerOrderStatus = customerOrderStatus;
	}

	public LocalDateTime getLastStatusUpdate() {
		return lastStatusUpdate;
	}

	public void setLastStatusUpdate(LocalDateTime lastStatusUpdate) {
		this.lastStatusUpdate = lastStatusUpdate;
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

	public List<AssoAffaireOrder> getAssoAffaireOrders() {
		return assoAffaireOrders;
	}

	public void setAssoAffaireOrders(List<AssoAffaireOrder> assoAffaireOrders) {
		this.assoAffaireOrders = assoAffaireOrders;
	}

	public List<Quotation> getQuotations() {
		return quotations;
	}

	public void setQuotations(List<Quotation> quotations) {
		this.quotations = quotations;
	}

	public Boolean getIsQuotation() {
		return isQuotation;
	}

	public void setIsQuotation(Boolean isQuotation) {
		this.isQuotation = isQuotation;
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

	public List<Invoice> getProviderInvoices() {
		return providerInvoices;
	}

	public void setProviderInvoices(List<Invoice> providerInvoices) {
		this.providerInvoices = providerInvoices;
	}

	public String getCustomerMailCustomMessage() {
		return customerMailCustomMessage;
	}

	public void setCustomerMailCustomMessage(String customerMailCustomMessage) {
		this.customerMailCustomMessage = customerMailCustomMessage;
	}

	public Employee getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(Employee assignedTo) {
		this.assignedTo = assignedTo;
	}

	public CustomerOrderOrigin getCustomerOrderOrigin() {
		return customerOrderOrigin;
	}

	public void setCustomerOrderOrigin(CustomerOrderOrigin customerOrderOrigin) {
		this.customerOrderOrigin = customerOrderOrigin;
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}

	public Boolean getIsGifted() {
		return isGifted;
	}

	public void setIsGifted(Boolean isGifted) {
		this.isGifted = isGifted;
	}

	public AbandonReason getAbandonReason() {
		return abandonReason;
	}

	public void setAbandonReason(AbandonReason abandonReason) {
		this.abandonReason = abandonReason;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public List<Refund> getRefunds() {
		return refunds;
	}

	public void setRefunds(List<Refund> refunds) {
		this.refunds = refunds;
	}

}
