package com.jss.osiris.modules.osiris.quotation.model;

import java.io.Serializable;
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
	@JsonView({ JacksonViews.MyJssListView.class, JacksonViews.MyJssDetailedView.class,
			JacksonViews.OsirisListView.class })
	private Integer id;

	@Column(nullable = false)
	@IndexedField
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	private String label;

	@IndexedField
	@JsonView({ JacksonViews.MyJssListView.class, JacksonViews.MyJssDetailedView.class,
			JacksonViews.OsirisListView.class })
	private String customLabel;

	@JsonView({ JacksonViews.MyJssListView.class, JacksonViews.MyJssDetailedView.class,
			JacksonViews.OsirisListView.class })
	private String code;

	@Column(columnDefinition = "TEXT")
	@JsonView({ JacksonViews.MyJssDetailedView.class })
	private String comment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_service_family")
	@IndexedField
	@JsonView({ JacksonViews.OsirisListView.class })
	private ServiceFamily serviceFamily;

	@OneToMany(mappedBy = "serviceType", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "serviceType" }, allowSetters = true)
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private List<AssoServiceProvisionType> assoServiceProvisionTypes;

	@OneToMany(mappedBy = "serviceType", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "serviceType" }, allowSetters = true)
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private List<AssoServiceTypeDocument> assoServiceTypeDocuments;

	@OneToMany(mappedBy = "serviceType", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "serviceType" }, allowSetters = true)
	@JsonView({ JacksonViews.MyJssDetailedView.class })
	private List<AssoServiceTypeFieldType> assoServiceTypeFieldTypes;

	@JsonView({ JacksonViews.MyJssListView.class, JacksonViews.MyJssDetailedView.class,
			JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private Boolean isRequiringNewUnregisteredAffaire;

	@JsonView({ JacksonViews.MyJssListView.class, JacksonViews.MyJssDetailedView.class })
	@Transient
	private Boolean hasAnnouncement;

	@JsonView({ JacksonViews.MyJssListView.class, JacksonViews.MyJssDetailedView.class })
	@Transient
	private Boolean hasOnlyAnnouncement;

	private Integer suspiciousMarkup;

	@JsonView(JacksonViews.MyJssListView.class)
	private Boolean isMergeable;

	@JsonView({ JacksonViews.MyJssListView.class, JacksonViews.MyJssDetailedView.class })
	@JoinColumn(name = "id_service_type_linked")
	private ServiceType serviceTypeLinked;

	private Boolean hideInMyJss;

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

	public Integer getSuspiciousMarkup() {
		return suspiciousMarkup;
	}

	public void setSuspiciousMarkup(Integer suspiciousMarkup) {
		this.suspiciousMarkup = suspiciousMarkup;
	}

	public Boolean getIsMergeable() {
		return isMergeable;
	}

	public void setIsMergeable(Boolean isMergeable) {
		this.isMergeable = isMergeable;
	}

	public ServiceType getServiceTypeLinked() {
		return serviceTypeLinked;
	}

	public void setServiceTypeLinked(ServiceType serviceTypeLinked) {
		this.serviceTypeLinked = serviceTypeLinked;
	}

	public Boolean getHideInMyJss() {
		return hideInMyJss;
	}

	public void setHideInMyJss(Boolean hideInMyJss) {
		this.hideInMyJss = hideInMyJss;
	}
}