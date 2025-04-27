package com.jss.osiris.modules.osiris.quotation.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.jackson.JacksonLocalDateTimeDeserializer;
import com.jss.osiris.libs.jackson.JacksonLocalDateTimeSerializer;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.CustomerOrderOrigin;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(indexes = { @Index(name = "idx_quotation_status", columnList = "id_quotation_status"),
		@Index(name = "idx_quotation_responsable", columnList = "id_responsable"),
		@Index(name = "idx_quotation_tiers", columnList = "id_tiers") })
public class Quotation implements IQuotation {

	@Id
	@SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
	@IndexedField
	@JsonView({ JacksonViews.MyJssView.class, JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	private Integer id;

	private Integer validationId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_assigned_to")
	@IndexedField
	private Employee assignedTo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_responsable")
	@IndexedField
	@JsonView({ JacksonViews.MyJssView.class, JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	private Responsable responsable;

	@ManyToMany
	@JoinTable(name = "asso_quotation_special_offer", joinColumns = @JoinColumn(name = "id_quotation"), inverseJoinColumns = @JoinColumn(name = "id_special_offer"))
	@JsonView({ JacksonViews.MyJssView.class, JacksonViews.OsirisDetailedView.class })
	@JsonIgnoreProperties(value = { "assoSpecialOfferBillingTypes" }, allowSetters = true)
	private List<SpecialOffer> specialOffers;

	@JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
	@JsonDeserialize(using = JacksonLocalDateTimeDeserializer.class)
	@IndexedField
	@JsonView({ JacksonViews.MyJssView.class, JacksonViews.OsirisDetailedView.class })
	private LocalDateTime createdDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_quotation_status")
	@JsonView({ JacksonViews.MyJssView.class, JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	@IndexedField
	private QuotationStatus quotationStatus;

	@JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
	@JsonDeserialize(using = JacksonLocalDateTimeDeserializer.class)
	@JsonView({ JacksonViews.MyJssView.class, JacksonViews.OsirisDetailedView.class })
	private LocalDateTime lastStatusUpdate;

	@Column(columnDefinition = "TEXT") // TODO : delete when new website
	@JsonView({ JacksonViews.MyJssView.class, JacksonViews.OsirisDetailedView.class })
	private String description;

	@OneToMany(mappedBy = "quotation")
	@JsonIgnoreProperties(value = { "quotation" }, allowSetters = true)
	@JsonView({ JacksonViews.OsirisDetailedView.class })
	private List<Attachment> attachments;

	@OneToMany(targetEntity = Document.class, mappedBy = "quotation", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "quotation" }, allowSetters = true)
	private List<Document> documents;

	@OneToMany(targetEntity = AssoAffaireOrder.class, mappedBy = "quotation", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "quotation" }, allowSetters = true)
	@JsonView(JacksonViews.MyJssView.class)
	private List<AssoAffaireOrder> assoAffaireOrders;

	@Column(length = 40)
	private String quotationLabel;

	@Column(nullable = false)
	private Boolean isQuotation;

	@Column(length = 40)
	private String validationToken;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_abandon_reason")
	private QuotationAbandonReason abandonReason;

	@ManyToMany
	@JoinTable(name = "asso_quotation_customer_order", joinColumns = @JoinColumn(name = "id_quotation"), inverseJoinColumns = @JoinColumn(name = "id_customer_order"))
	@JsonIgnoreProperties(value = { "quotations" }, allowSetters = true)
	@JsonIgnore // For client-side performance purpose
	private List<CustomerOrder> customerOrders;

	@JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
	@JsonDeserialize(using = JacksonLocalDateTimeDeserializer.class)
	private LocalDateTime firstReminderDateTime;

	@JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
	@JsonDeserialize(using = JacksonLocalDateTimeDeserializer.class)
	private LocalDateTime secondReminderDateTime;

	@JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
	@JsonDeserialize(using = JacksonLocalDateTimeDeserializer.class)
	private LocalDateTime thirdReminderDateTime;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_customer_order_origin")
	private CustomerOrderOrigin customerOrderOrigin;

	@OneToMany(targetEntity = CustomerOrderComment.class, mappedBy = "quotation", cascade = CascadeType.REMOVE)
	@JsonIgnoreProperties(value = { "quotation" }, allowSetters = true)
	private List<CustomerOrderComment> customerOrderComments;

	@Transient
	@JsonView({ JacksonViews.MyJssView.class, JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	public String affairesList;

	@Transient
	@JsonView({ JacksonViews.MyJssView.class, JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	public String servicesList;

	@Transient
	@JsonView({ JacksonViews.MyJssView.class, JacksonViews.OsirisDetailedView.class })
	public Boolean hasMissingInformations;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public QuotationAbandonReason getAbandonReason() {
		return abandonReason;
	}

	public void setAbandonReason(QuotationAbandonReason abandonReason) {
		this.abandonReason = abandonReason;
	}

	public List<CustomerOrderComment> getCustomerOrderComments() {
		return customerOrderComments;
	}

	public void setCustomerOrderComments(List<CustomerOrderComment> customerOrderComments) {
		this.customerOrderComments = customerOrderComments;
	}

	public String getAffairesList() {
		return affairesList;
	}

	public void setAffairesList(String affairesList) {
		this.affairesList = affairesList;
	}

	public String getServicesList() {
		return servicesList;
	}

	public void setServicesList(String servicesList) {
		this.servicesList = servicesList;
	}

	public Integer getValidationId() {
		return validationId;
	}

	public void setValidationId(Integer validationId) {
		this.validationId = validationId;
	}

	public Boolean getHasMissingInformations() {
		return hasMissingInformations;
	}

	public void setHasMissingInformations(Boolean hasMissingInformations) {
		this.hasMissingInformations = hasMissingInformations;
	}

}
