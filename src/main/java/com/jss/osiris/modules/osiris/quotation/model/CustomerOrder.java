package com.jss.osiris.modules.osiris.quotation.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.jackson.JacksonLocalDateTimeDeserializer;
import com.jss.osiris.libs.jackson.JacksonLocalDateTimeGmtDeserializer;
import com.jss.osiris.libs.jackson.JacksonLocalDateTimeGmtSerializer;
import com.jss.osiris.libs.jackson.JacksonLocalDateTimeSerializer;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.osiris.invoicing.model.ICreatedDate;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.InvoicingBlockage;
import com.jss.osiris.modules.osiris.invoicing.model.Payment;
import com.jss.osiris.modules.osiris.invoicing.model.Refund;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.CustomerOrderFrequency;
import com.jss.osiris.modules.osiris.miscellaneous.model.CustomerOrderOrigin;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;

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
@Table(indexes = { @Index(name = "idx_customer_order_status", columnList = "id_customer_order_status"),
		@Index(name = "idx_customer_order_responsable", columnList = "id_responsable"),
		@Index(name = "idx_customer_order_assigned_to", columnList = "id_assigned_to"),
		@Index(name = "idx_customer_order_parent_recurring", columnList = "id_customer_order_parent_recurring"),
		@Index(name = "idx_customer_order_tiers", columnList = "id_tiers") })
public class CustomerOrder implements IQuotation, ICreatedDate {

	public CustomerOrder() {
	}

	public CustomerOrder(Employee assignedTo, Tiers tiers, Responsable responsable, /* Confrere confrere, */
			List<SpecialOffer> specialOffers, LocalDateTime createdDate, CustomerOrderStatus customerOrderStatus,
			String description, List<Attachment> attachments,
			List<Document> documents,
			List<AssoAffaireOrder> assoAffaireOrders,
			List<Quotation> quotations, Boolean isQuotation,
			List<Invoice> invoices, List<CustomerOrderComment> customerOrderComments,
			CustomerOrderOrigin customerOrderOrigin) {
		this.assignedTo = assignedTo;
		this.responsable = responsable;
		// this.confrere = confrere;
		// TODO refonte
		this.specialOffers = specialOffers;
		this.createdDate = createdDate;
		this.customerOrderStatus = customerOrderStatus;
		this.description = description;
		this.attachments = attachments;
		this.documents = documents;
		this.assoAffaireOrders = assoAffaireOrders;
		this.quotations = quotations;
		this.isQuotation = isQuotation;
		this.invoices = invoices;
		this.customerOrderComments = customerOrderComments;
		this.customerOrderOrigin = customerOrderOrigin;
	}

	@Id
	@SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
	@IndexedField
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisListView.class,
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
	@JsonIgnoreProperties(value = { "attachments" }, allowSetters = true)
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	private Responsable responsable;

	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "id_confrere")
	// private Confrere confrere;

	@ManyToMany
	@JoinTable(name = "asso_customer_order_special_offer", joinColumns = @JoinColumn(name = "id_customer_order"), inverseJoinColumns = @JoinColumn(name = "id_special_offer"))
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisDetailedView.class })
	@JsonIgnoreProperties(value = { "assoSpecialOfferBillingTypes" }, allowSetters = true)
	private List<SpecialOffer> specialOffers;

	@JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
	@JsonDeserialize(using = JacksonLocalDateTimeDeserializer.class)
	@IndexedField
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	private LocalDateTime createdDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_customer_order_status")
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	@IndexedField
	private CustomerOrderStatus customerOrderStatus;

	@JsonView({ JacksonViews.MyJssListView.class, JacksonViews.OsirisDetailedView.class })
	private LocalDateTime lastStatusUpdate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_abandon_reason")
	private QuotationAbandonReason abandonReason;

	@Column(columnDefinition = "TEXT")
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisDetailedView.class })
	private String description;

	@OneToMany(mappedBy = "customerOrder")
	@JsonIgnoreProperties(value = { "customerOrder" }, allowSetters = true)
	@JsonView({ JacksonViews.OsirisDetailedView.class, JacksonViews.MyJssDetailedView.class }) // TODO : remove and use
																								// lazy loading for
																								// attachments
	private List<Attachment> attachments;

	@OneToMany(targetEntity = Document.class, mappedBy = "customerOrder", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "customerOrder" }, allowSetters = true)
	@IndexedField
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisDetailedView.class })
	private List<Document> documents;

	@OneToMany(targetEntity = PaperSet.class, mappedBy = "customerOrder", cascade = CascadeType.REMOVE)
	@JsonIgnoreProperties(value = { "customerOrder" }, allowSetters = true)
	private List<PaperSet> paperSets;

	@OneToMany(targetEntity = AssoAffaireOrder.class, mappedBy = "customerOrder", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "customerOrder" }, allowSetters = true)
	@JsonView({ JacksonViews.MyJssDetailedView.class })
	private List<AssoAffaireOrder> assoAffaireOrders;

	@ManyToMany(mappedBy = "customerOrders")
	@JsonIgnoreProperties(value = { "customerOrders" }, allowSetters = true)
	@JsonIgnore // For client-side performance purpose
	private List<Quotation> quotations;

	@Column(nullable = false)
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private Boolean isQuotation;

	@OneToMany(mappedBy = "customerOrder")
	@JsonIgnoreProperties(value = { "customerOrder" }, allowSetters = true)
	@JsonIgnore // For client-side performance purpose
	private List<Invoice> invoices;

	@OneToMany(mappedBy = "customerOrder", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "invoice", "accountingRecords", "customerOrder", "originPayment",
			"childrenPayments" }, allowSetters = true)
	private List<Payment> payments;

	@JsonDeserialize(using = JacksonLocalDateTimeGmtDeserializer.class)
	@JsonSerialize(using = JacksonLocalDateTimeGmtSerializer.class)
	private LocalDateTime firstReminderDateTime;

	@JsonDeserialize(using = JacksonLocalDateTimeGmtDeserializer.class)
	@JsonSerialize(using = JacksonLocalDateTimeGmtSerializer.class)
	private LocalDateTime secondReminderDateTime;

	@JsonDeserialize(using = JacksonLocalDateTimeGmtDeserializer.class)
	@JsonSerialize(using = JacksonLocalDateTimeGmtSerializer.class)
	private LocalDateTime thirdReminderDateTime;

	@OneToMany(targetEntity = Invoice.class, mappedBy = "customerOrderForInboundInvoice")
	@JsonIgnoreProperties(value = { "customerOrder" }, allowSetters = true)
	@JsonIgnore // For client-side performance purpose
	private List<Invoice> providerInvoices;

	@OneToMany(targetEntity = Refund.class, mappedBy = "customerOrder")
	@JsonIgnoreProperties(value = { "customerOrder" }, allowSetters = true)
	private List<Refund> refunds;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_customer_order_origin")
	private CustomerOrderOrigin customerOrderOrigin;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_customer_order_parent_recurring")
	@JsonIgnore // For client-side performance purpose
	private CustomerOrder customerOrderParentRecurring;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_recurring_frequency")
	private CustomerOrderFrequency customerOrderFrequency;

	@Transient
	private Boolean hasCustomerOrderParentRecurring;

	private LocalDate recurringPeriodStartDate;
	private LocalDate recurringPeriodEndDate;

	@JsonView(JacksonViews.MyJssDetailedView.class)
	private LocalDate recurringStartDate;

	@JsonView(JacksonViews.MyJssDetailedView.class)
	private LocalDate recurringEndDate;

	@JsonView(JacksonViews.MyJssDetailedView.class)
	private Boolean isRecurring;
	private Boolean isRecurringAutomaticallyBilled;

	@JsonView(JacksonViews.MyJssDetailedView.class)
	private Boolean isGifted;

	@JsonView(JacksonViews.MyJssDetailedView.class)
	@Column(name = "is_payed")
	private Boolean isPayed;

	@OneToMany(targetEntity = CustomerOrderComment.class, mappedBy = "customerOrder", cascade = CascadeType.REMOVE)
	@JsonIgnoreProperties(value = { "customerOrder", "provision", "quotation" }, allowSetters = true)
	private List<CustomerOrderComment> customerOrderComments;

	@Transient
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	private String affairesList;

	@Transient
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	private String servicesList;

	@Transient
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisDetailedView.class })
	private Boolean hasMissingInformations;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_service_family_group")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private ServiceFamilyGroup serviceFamilyGroup;

	@Transient
	@JsonView({ JacksonViews.OsirisListView.class })
	private Boolean isHasNotifications;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_invoicing_employee")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private Employee invoicingEmployee;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_invoicing_blockage")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private InvoicingBlockage invoicingBlockage;

	private LocalDate princingEffectiveDate;

	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private LocalDateTime productionEffectiveDateTime;

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

	public QuotationAbandonReason getAbandonReason() {
		return abandonReason;
	}

	public void setAbandonReason(QuotationAbandonReason abandonReason) {
		this.abandonReason = abandonReason;
	}

	public List<Refund> getRefunds() {
		return refunds;
	}

	public void setRefunds(List<Refund> refunds) {
		this.refunds = refunds;
	}

	public CustomerOrder getCustomerOrderParentRecurring() {
		return customerOrderParentRecurring;
	}

	public void setCustomerOrderParentRecurring(CustomerOrder customerOrderParentRecurring) {
		this.customerOrderParentRecurring = customerOrderParentRecurring;
	}

	public LocalDate getRecurringStartDate() {
		return recurringStartDate;
	}

	public void setRecurringStartDate(LocalDate recurringStartDate) {
		this.recurringStartDate = recurringStartDate;
	}

	public LocalDate getRecurringEndDate() {
		return recurringEndDate;
	}

	public void setRecurringEndDate(LocalDate recurringEndDate) {
		this.recurringEndDate = recurringEndDate;
	}

	public Boolean getIsRecurringAutomaticallyBilled() {
		return isRecurringAutomaticallyBilled;
	}

	public void setIsRecurringAutomaticallyBilled(Boolean isRecurringAutomaticallyBilled) {
		this.isRecurringAutomaticallyBilled = isRecurringAutomaticallyBilled;
	}

	public LocalDate getRecurringPeriodStartDate() {
		return recurringPeriodStartDate;
	}

	public void setRecurringPeriodStartDate(LocalDate recurringPeriodStartDate) {
		this.recurringPeriodStartDate = recurringPeriodStartDate;
	}

	public LocalDate getRecurringPeriodEndDate() {
		return recurringPeriodEndDate;
	}

	public void setRecurringPeriodEndDate(LocalDate recurringPeriodEndDate) {
		this.recurringPeriodEndDate = recurringPeriodEndDate;
	}

	public Boolean getHasCustomerOrderParentRecurring() {
		return hasCustomerOrderParentRecurring;
	}

	public void setHasCustomerOrderParentRecurring(Boolean hasCustomerOrderParentRecurring) {
		this.hasCustomerOrderParentRecurring = hasCustomerOrderParentRecurring;
	}

	public Boolean getIsRecurring() {
		return isRecurring;
	}

	public void setIsRecurring(Boolean isRecurring) {
		this.isRecurring = isRecurring;
	}

	public CustomerOrderFrequency getCustomerOrderFrequency() {
		return customerOrderFrequency;
	}

	public void setCustomerOrderFrequency(CustomerOrderFrequency customerOrderFrequency) {
		this.customerOrderFrequency = customerOrderFrequency;
	}

	public List<CustomerOrderComment> getCustomerOrderComments() {
		return customerOrderComments;
	}

	public void setCustomerOrderComments(List<CustomerOrderComment> customerOrderComments) {
		this.customerOrderComments = customerOrderComments;
	}

	public List<PaperSet> getPaperSets() {
		return paperSets;
	}

	public void setPaperSets(List<PaperSet> paperSets) {
		this.paperSets = paperSets;
	}

	public Boolean getIsPayed() {
		return isPayed;
	}

	public void setIsPayed(Boolean isPayed) {
		this.isPayed = isPayed;
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

	public Boolean getHasMissingInformations() {
		return hasMissingInformations;
	}

	public void setHasMissingInformations(Boolean hasMissingInformations) {
		this.hasMissingInformations = hasMissingInformations;
	}

	public Integer getValidationId() {
		return validationId;
	}

	public void setValidationId(Integer validationId) {
		this.validationId = validationId;
	}

	public ServiceFamilyGroup getServiceFamilyGroup() {
		return serviceFamilyGroup;
	}

	public void setServiceFamilyGroup(ServiceFamilyGroup serviceFamilyGroup) {
		this.serviceFamilyGroup = serviceFamilyGroup;
	}

	public Boolean getIsHasNotifications() {
		return isHasNotifications;
	}

	public void setIsHasNotifications(Boolean isHasNotifications) {
		this.isHasNotifications = isHasNotifications;
	}

	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	public Employee getInvoicingEmployee() {
		return invoicingEmployee;
	}

	public void setInvoicingEmployee(Employee invoicingEmployee) {
		this.invoicingEmployee = invoicingEmployee;
	}

	public InvoicingBlockage getInvoicingBlockage() {
		return invoicingBlockage;
	}

	public void setInvoicingBlockage(InvoicingBlockage invoicingBlockage) {
		this.invoicingBlockage = invoicingBlockage;
	}

	public LocalDate getPrincingEffectiveDate() {
		return princingEffectiveDate;
	}

	public void setPrincingEffectiveDate(LocalDate princingEffectiveDate) {
		this.princingEffectiveDate = princingEffectiveDate;
	}

	public LocalDateTime getProductionEffectiveDateTime() {
		return productionEffectiveDateTime;
	}

	public void setProductionEffectiveDateTime(LocalDateTime productionEffectiveDateTime) {
		this.productionEffectiveDateTime = productionEffectiveDateTime;
	}

}
