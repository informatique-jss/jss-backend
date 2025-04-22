package com.jss.osiris.modules.osiris.quotation.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
	@JsonView({ JacksonViews.MyJssListView.class, JacksonViews.MyJssDetailedView.class })
	private Integer id;

	@OneToMany(targetEntity = Provision.class, mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "service" }, allowSetters = true)
	@IndexedField
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private List<Provision> provisions;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_asso_affaire_order")
	@JsonIgnoreProperties(value = { "services" }, allowSetters = true)
	private AssoAffaireOrder assoAffaireOrder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_service_type")
	@IndexedField
	@JsonView({ JacksonViews.MyJssListView.class, JacksonViews.MyJssDetailedView.class })
	@JsonIgnoreProperties(value = { "assoServiceTypeDocuments", "assoServiceTypeFieldTypes",
			"assoServiceProvisionTypes" }, allowSetters = true)
	private ServiceType serviceType;

	@OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "service" }, allowSetters = true)
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private List<AssoServiceDocument> assoServiceDocuments;

	@OneToMany(targetEntity = MissingAttachmentQuery.class, mappedBy = "service", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "service" }, allowSetters = true)
	private List<MissingAttachmentQuery> missingAttachmentQueries;

	@OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "service" }, allowSetters = true)
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private List<AssoServiceFieldType> assoServiceFieldTypes;

	@JsonView({ JacksonViews.MyJssListView.class, JacksonViews.MyJssDetailedView.class })
	private String customLabel;

	@Column(columnDefinition = "TEXT")
	@JsonView({ JacksonViews.MyJssListView.class, JacksonViews.MyJssDetailedView.class })
	private String customerComment;

	@Transient
	@JsonView({ JacksonViews.MyJssListView.class, JacksonViews.MyJssDetailedView.class })
	private boolean hasMissingInformations;

	@Transient
	@JsonView({ JacksonViews.MyJssListView.class, JacksonViews.MyJssDetailedView.class })
	private String serviceStatus;

	@Transient
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private BigDecimal servicePrice;

	@Transient
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private String confrereLabel;

	@Transient
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private LocalDateTime lastMissingAttachmentQueryDateTime;

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

	public String getCustomLabel() {
		return customLabel;
	}

	public void setCustomLabel(String customLabel) {
		this.customLabel = customLabel;
	}

	public ServiceType getServiceType() {
		return serviceType;
	}

	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
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

	public BigDecimal getServicePrice() {
		return servicePrice;
	}

	public void setServicePrice(BigDecimal servicePrice) {
		this.servicePrice = servicePrice;
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

}
