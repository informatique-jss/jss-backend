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
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.CustomerOrderOrigin;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

@Entity
@Table(indexes = { @Index(name = "idx_quotation_status", columnList = "id_quotation_status"),
		@Index(name = "idx_quotation_responsable", columnList = "id_responsable"),
		@Index(name = "idx_quotation_tiers", columnList = "id_tiers") })
public class Quotation implements IQuotation {

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
	@IndexedField
	private Confrere confrere;

	@ManyToMany
	@JoinTable(name = "asso_quotation_special_offer", joinColumns = @JoinColumn(name = "id_quotation"), inverseJoinColumns = @JoinColumn(name = "id_special_offer"))
	private List<SpecialOffer> specialOffers;

	@IndexedField
	@JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
	private LocalDateTime createdDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_quotation_status")
	private QuotationStatus quotationStatus;

	private LocalDateTime lastStatusUpdate;

	@Column(columnDefinition = "TEXT")
	private String observations;

	@Column(columnDefinition = "TEXT")
	private String description;

	@OneToMany(mappedBy = "quotation")
	@JsonIgnoreProperties(value = { "quotation" }, allowSetters = true)
	private List<Attachment> attachments;

	@OneToMany(targetEntity = Document.class, mappedBy = "quotation", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "quotation" }, allowSetters = true)
	private List<Document> documents;

	@OneToMany(targetEntity = AssoAffaireOrder.class, mappedBy = "quotation", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "quotation" }, allowSetters = true)
	private List<AssoAffaireOrder> assoAffaireOrders;

	@Column(nullable = false)
	private Boolean overrideSpecialOffer;

	@Column(length = 40)
	private String quotationLabel;

	@Column(nullable = false)
	private Boolean isQuotation;

	@Column(length = 40)
	private String validationToken;

	@ManyToMany
	@JoinTable(name = "asso_quotation_customer_order", joinColumns = @JoinColumn(name = "id_quotation"), inverseJoinColumns = @JoinColumn(name = "id_customer_order"))
	@JsonIgnoreProperties(value = { "quotations" }, allowSetters = true)
	@JsonIgnore // For client-side performance purpose
	private List<CustomerOrder> customerOrders;

	private LocalDateTime firstReminderDateTime;
	private LocalDateTime secondReminderDateTime;
	private LocalDateTime thirdReminderDateTime;

	@Column(columnDefinition = "TEXT")
	private String customerMailCustomMessage;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_customer_order_origin")
	private CustomerOrderOrigin customerOrderOrigin;

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

	public List<CustomerOrder> getCustomerOrders() {
		return customerOrders;
	}

	public void setCustomerOrders(List<CustomerOrder> customerOrders) {
		this.customerOrders = customerOrders;
	}

	public QuotationStatus getQuotationStatus() {
		return quotationStatus;
	}

	public void setQuotationStatus(QuotationStatus quotationStatus) {
		this.quotationStatus = quotationStatus;
	}

	public List<AssoAffaireOrder> getAssoAffaireOrders() {
		return assoAffaireOrders;
	}

	public void setAssoAffaireOrders(List<AssoAffaireOrder> assoAffaireOrders) {
		this.assoAffaireOrders = assoAffaireOrders;
	}

	public LocalDateTime getLastStatusUpdate() {
		return lastStatusUpdate;
	}

	public void setLastStatusUpdate(LocalDateTime lastStatusUpdate) {
		this.lastStatusUpdate = lastStatusUpdate;
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

	public String getQuotationLabel() {
		return quotationLabel;
	}

	public void setQuotationLabel(String quotationLabel) {
		this.quotationLabel = quotationLabel;
	}

	public Boolean getOverrideSpecialOffer() {
		return overrideSpecialOffer;
	}

	public void setOverrideSpecialOffer(Boolean overrideSpecialOffer) {
		this.overrideSpecialOffer = overrideSpecialOffer;
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

	public String getValidationToken() {
		return validationToken;
	}

	public void setValidationToken(String validationToken) {
		this.validationToken = validationToken;
	}

}
