package com.jss.osiris.modules.osiris.quotation.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Transient;

@Entity
public class ServiceType implements Serializable, IId {
	public static String SERVICE_TYPE_OTHER = "OTHER";

	@Id
	@SequenceGenerator(name = "asso_service_type_sequence", sequenceName = "asso_service_type_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asso_service_type_sequence")
	@JsonView(JacksonViews.MyJssView.class)
	private Integer id;

	@Column(nullable = false)
	@IndexedField
	@JsonView(JacksonViews.MyJssView.class)
	private String label;

	@IndexedField
	@JsonView(JacksonViews.MyJssView.class)
	private String customLabel;

	@JsonView(JacksonViews.MyJssView.class)
	private String code;

	@Column(columnDefinition = "TEXT")
	@JsonView(JacksonViews.MyJssView.class)
	private String comment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_service_family")
	@IndexedField
	private ServiceFamily serviceFamily;

	@OneToMany(mappedBy = "serviceType", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "serviceType" }, allowSetters = true)
	private List<AssoServiceProvisionType> assoServiceProvisionTypes;

	@OneToMany(mappedBy = "serviceType", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "serviceType" }, allowSetters = true)
	@JsonView(JacksonViews.MyJssView.class)
	private List<AssoServiceTypeDocument> assoServiceTypeDocuments;

	@OneToMany(mappedBy = "serviceType", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "serviceType" }, allowSetters = true)
	@JsonView(JacksonViews.MyJssView.class)
	private List<AssoServiceTypeFieldType> assoServiceTypeFieldTypes;

	@JsonView(JacksonViews.MyJssView.class)
	private Boolean isRequiringNewUnregisteredAffaire;

	@JsonView(JacksonViews.MyJssView.class)
	@Transient
	private Boolean hasAnnouncement;

	@JsonView(JacksonViews.MyJssView.class)
	@Transient
	private Boolean hasOnlyAnnouncement;

	@Column(columnDefinition = "NUMERIC", precision = 10, scale = 2)
	private BigDecimal defaultDeboursPrice;

	@Column(columnDefinition = "NUMERIC", precision = 10, scale = 2)
	private BigDecimal defaultDeboursPriceNonTaxable;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public ServiceFamily getServiceFamily() {
		return serviceFamily;
	}

	public void setServiceFamily(ServiceFamily serviceFamily) {
		this.serviceFamily = serviceFamily;
	}

	public List<AssoServiceProvisionType> getAssoServiceProvisionTypes() {
		return assoServiceProvisionTypes;
	}

	public void setAssoServiceProvisionTypes(List<AssoServiceProvisionType> assoServiceProvisionTypes) {
		this.assoServiceProvisionTypes = assoServiceProvisionTypes;
	}

	public List<AssoServiceTypeDocument> getAssoServiceTypeDocuments() {
		return assoServiceTypeDocuments;
	}

	public void setAssoServiceTypeDocuments(List<AssoServiceTypeDocument> assoServiceTypeDocuments) {
		this.assoServiceTypeDocuments = assoServiceTypeDocuments;
	}

	public List<AssoServiceTypeFieldType> getAssoServiceTypeFieldTypes() {
		return assoServiceTypeFieldTypes;
	}

	public void setAssoServiceTypeFieldTypes(List<AssoServiceTypeFieldType> assoServiceTypeFieldType) {
		this.assoServiceTypeFieldTypes = assoServiceTypeFieldType;
	}

	public static String getSERVICE_TYPE_OTHER() {
		return SERVICE_TYPE_OTHER;
	}

	public static void setSERVICE_TYPE_OTHER(String sERVICE_TYPE_OTHER) {
		SERVICE_TYPE_OTHER = sERVICE_TYPE_OTHER;
	}

	public String getCustomLabel() {
		return customLabel;
	}

	public void setCustomLabel(String customLabel) {
		this.customLabel = customLabel;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Boolean getIsRequiringNewUnregisteredAffaire() {
		return isRequiringNewUnregisteredAffaire;
	}

	public void setIsRequiringNewUnregisteredAffaire(Boolean isRequiringNewUnregisteredAffaire) {
		this.isRequiringNewUnregisteredAffaire = isRequiringNewUnregisteredAffaire;
	}

	public BigDecimal getDefaultDeboursPrice() {
		return defaultDeboursPrice;
	}

	public void setDefaultDeboursPrice(BigDecimal defaultDeboursPrice) {
		this.defaultDeboursPrice = defaultDeboursPrice;
	}

	public BigDecimal getDefaultDeboursPriceNonTaxable() {
		return defaultDeboursPriceNonTaxable;
	}

	public void setDefaultDeboursPriceNonTaxable(BigDecimal defaultDeboursPriceNonTaxable) {
		this.defaultDeboursPriceNonTaxable = defaultDeboursPriceNonTaxable;
	}

	public Boolean getHasAnnouncement() {
		return hasAnnouncement;
	}

	public void setHasAnnouncement(Boolean hasAnnouncement) {
		this.hasAnnouncement = hasAnnouncement;
	}

	public Boolean getHasOnlyAnnouncement() {
		return hasOnlyAnnouncement;
	}

	public void setHasOnlyAnnouncement(Boolean hasOnlyAnnouncement) {
		this.hasOnlyAnnouncement = hasOnlyAnnouncement;
	}

}