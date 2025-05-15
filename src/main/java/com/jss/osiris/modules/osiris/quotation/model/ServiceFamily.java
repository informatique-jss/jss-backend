package com.jss.osiris.modules.osiris.quotation.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

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

@Entity
public class ServiceFamily implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class })
	private Integer id;

	@Column(nullable = false)
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class })
	private String label;

	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class })
	private String customLabel;

	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class })
	private String code;

	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class })
	private String myJssIcon;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_service_family_group")
	@IndexedField
	private ServiceFamilyGroup serviceFamilyGroup;

	@Column(columnDefinition = "TEXT")
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class })
	private String comment;

	@OneToMany(mappedBy = "serviceFamily")
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class })
	@JsonIgnoreProperties(value = { "serviceFamily" }, allowSetters = true)
	private List<ServiceType> services;

	private Integer myJssOrder;

	private Boolean hideInMyJssMandatoryDocument;

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

	public ServiceFamilyGroup getServiceFamilyGroup() {
		return serviceFamilyGroup;
	}

	public void setServiceFamilyGroup(ServiceFamilyGroup serviceFamilyGroup) {
		this.serviceFamilyGroup = serviceFamilyGroup;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCustomLabel() {
		return customLabel;
	}

	public void setCustomLabel(String customLabel) {
		this.customLabel = customLabel;
	}

	public String getMyJssIcon() {
		return myJssIcon;
	}

	public void setMyJssIcon(String myJssIcon) {
		this.myJssIcon = myJssIcon;
	}

	public List<ServiceType> getServices() {
		return services;
	}

	public void setServices(List<ServiceType> services) {
		this.services = services;
	}

	public Integer getMyJssOrder() {
		return myJssOrder;
	}

	public void setMyJssOrder(Integer myJssOrder) {
		this.myJssOrder = myJssOrder;
	}

	public Boolean getHideInMyJssMandatoryDocument() {
		return hideInMyJssMandatoryDocument;
	}

	public void setHideInMyJssMandatoryDocument(Boolean hideInMyJssMandatoryDocument) {
		this.hideInMyJssMandatoryDocument = hideInMyJssMandatoryDocument;
	}

}
