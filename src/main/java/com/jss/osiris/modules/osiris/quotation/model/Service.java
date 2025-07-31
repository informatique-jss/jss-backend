package com.jss.osiris.modules.osiris.quotation.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

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
@Table(indexes = { @Index(name = "idx_service_asso_affaire_order", columnList = "id_asso_affaire_order") })
public class Service implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "service_sequence", sequenceName = "service_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "service_sequence")
	@IndexedField
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisDetailedView.class })
	private Integer id;

	@OneToMany(targetEntity = Provision.class, mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "service" }, allowSetters = true)
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisDetailedView.class })
	@IndexedField
	private List<Provision> provisions;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_asso_affaire_order")
	@JsonIgnoreProperties(value = { "services" }, allowSetters = true)
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private AssoAffaireOrder assoAffaireOrder;

	@ManyToMany
	@JoinTable(name = "asso_service_service_type", joinColumns = @JoinColumn(name = "id_service"), inverseJoinColumns = @JoinColumn(name = "id_service_type"))
	@JsonProperty(value = "serviceTypes")
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	@JsonIgnoreProperties(value = { "assoServiceTypeDocuments" }, allowSetters = true)
	private List<ServiceType> serviceTypes;

	@OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "service" }, allowSetters = true)
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisDetailedView.class })
	private List<AssoServiceDocument> assoServiceDocuments;

	@OneToMany(targetEntity = MissingAttachmentQuery.class, mappedBy = "service", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "service" }, allowSetters = true)
	private List<MissingAttachmentQuery> missingAttachmentQueries;

	@OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "service" }, allowSetters = true)
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisDetailedView.class })
	private List<AssoServiceFieldType> assoServiceFieldTypes;

	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	private String customLabel;

	@Column(columnDefinition = "TEXT")
	@JsonView({ JacksonViews.MyJssListView.class, JacksonViews.MyJssDetailedView.class })
	private String customerComment;

	@Transient
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisDetailedView.class })
	private boolean hasMissingInformations;

	@Transient
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisDetailedView.class })
	private String serviceStatus;

	@Transient
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisDetailedView.class })
	private BigDecimal serviceTotalPrice;

	@Transient
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisDetailedView.class })
	private BigDecimal servicePreTaxPrice;

	@Transient
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisDetailedView.class })
	private BigDecimal serviceVatPrice;

	@Transient
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisDetailedView.class })
	private BigDecimal serviceDiscountAmount;

	@Transient
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private String confrereLabel;

	@Transient
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private String waitingAcLabel;

	@Transient
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisDetailedView.class })
	private LocalDateTime lastMissingAttachmentQueryDateTime;

	@JsonView({ JacksonViews.MyJssListView.class, JacksonViews.MyJssDetailedView.class })
	@Column(length = 2000)
	@IndexedField
	private String serviceLabelToDisplay;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<Provision> getProvisions() {
		return provisions;
	}

	public void setProvisions(List<Provision> provisions) {
		this.provisions = provisions;
	}

	public AssoAffaireOrder getAssoAffaireOrder() {
		return assoAffaireOrder;
	}

	public void setAssoAffaireOrder(AssoAffaireOrder assoAffaireOrder) {
		this.assoAffaireOrder = assoAffaireOrder;
	}

	public String getCustomerComment() {
		return customerComment;
	}

	public void setCustomerComment(String customerComment) {
		this.customerComment = customerComment;
	}

	public List<MissingAttachmentQuery> getMissingAttachmentQueries() {
		return missingAttachmentQueries;
	}

	public void setMissingAttachmentQueries(List<MissingAttachmentQuery> missingAttachmentQueries) {
		this.missingAttachmentQueries = missingAttachmentQueries;
	}

	public List<AssoServiceFieldType> getAssoServiceFieldTypes() {
		return assoServiceFieldTypes;
	}

	public void setAssoServiceFieldTypes(List<AssoServiceFieldType> assoServiceFieldTypes) {
		this.assoServiceFieldTypes = assoServiceFieldTypes;
	}

	public List<AssoServiceDocument> getAssoServiceDocuments() {
		return assoServiceDocuments;
	}

	public void setAssoServiceDocuments(List<AssoServiceDocument> assoServiceDocuments) {
		this.assoServiceDocuments = assoServiceDocuments;
	}

	public boolean isHasMissingInformations() {
		return hasMissingInformations;
	}

	public void setHasMissingInformations(boolean hasMissingInformations) {
		this.hasMissingInformations = hasMissingInformations;
	}

	public String getServiceStatus() {
		return serviceStatus;
	}

	public void setServiceStatus(String serviceStatus) {
		this.serviceStatus = serviceStatus;
	}

	public BigDecimal getServiceTotalPrice() {
		return serviceTotalPrice;
	}

	public void setServiceTotalPrice(BigDecimal servicePrice) {
		this.serviceTotalPrice = servicePrice;
	}

	public String getConfrereLabel() {
		return confrereLabel;
	}

	public void setConfrereLabel(String confrereLabel) {
		this.confrereLabel = confrereLabel;
	}

	public LocalDateTime getLastMissingAttachmentQueryDateTime() {
		return lastMissingAttachmentQueryDateTime;
	}

	public void setLastMissingAttachmentQueryDateTime(LocalDateTime lastMissingAttachmentQueryDateTime) {
		this.lastMissingAttachmentQueryDateTime = lastMissingAttachmentQueryDateTime;
	}

	public String getCustomLabel() {
		return customLabel;
	}

	public void setCustomLabel(String customLabel) {
		this.customLabel = customLabel;
	}

	public String getServiceLabelToDisplay() {
		return serviceLabelToDisplay;
	}

	public void setServiceLabelToDisplay(String serviceLabelToDisplay) {
		this.serviceLabelToDisplay = serviceLabelToDisplay;
	}

	public List<ServiceType> getServiceTypes() {
		return serviceTypes;
	}

	public void setServiceTypes(List<ServiceType> serviceTypes) {
		this.serviceTypes = serviceTypes;
	}

	public BigDecimal getServicePreTaxPrice() {
		return servicePreTaxPrice;
	}

	public void setServicePreTaxPrice(BigDecimal servicePreTaxPrice) {
		this.servicePreTaxPrice = servicePreTaxPrice;
	}

	public BigDecimal getServiceVatPrice() {
		return serviceVatPrice;
	}

	public void setServiceVatPrice(BigDecimal serviceVatPrice) {
		this.serviceVatPrice = serviceVatPrice;
	}

	public BigDecimal getServiceDiscountAmount() {
		return serviceDiscountAmount;
	}

	public void setServiceDiscountAmount(BigDecimal serviceDiscountAmount) {
		this.serviceDiscountAmount = serviceDiscountAmount;
	}

	public String getWaitingAcLabel() {
		return waitingAcLabel;
	}

	public void setWaitingAcLabel(String waitingAcLabel) {
		this.waitingAcLabel = waitingAcLabel;
	}

}
