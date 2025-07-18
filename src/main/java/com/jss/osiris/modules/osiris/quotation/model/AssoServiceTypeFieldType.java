package com.jss.osiris.modules.osiris.quotation.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(indexes = { @Index(name = "idx_asso_service_type_field_type", columnList = "id_service_type_field_type") })
public class AssoServiceTypeFieldType implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "asso_service_type_field_type_sequence", sequenceName = "asso_service_type_field_type_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asso_service_type_field_type_sequence")
	@JsonView({ JacksonViews.MyJssListView.class, JacksonViews.MyJssDetailedView.class })
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_service_type")
	@IndexedField
	private ServiceType serviceType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_service_type_field_type")
	@IndexedField
	@JsonView({ JacksonViews.MyJssListView.class, JacksonViews.MyJssDetailedView.class })
	private ServiceFieldType serviceFieldType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_service_type_field_type_dependancy")
	@IndexedField
	@JsonView({ JacksonViews.MyJssListView.class, JacksonViews.MyJssDetailedView.class })
	private ServiceFieldType serviceFieldTypeDependancy;

	@JsonView({ JacksonViews.MyJssListView.class, JacksonViews.MyJssDetailedView.class })
	private String serviceFieldTypeDependancyValue;

	private Boolean isMandatory;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ServiceType getServiceType() {
		return serviceType;
	}

	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}

	public ServiceFieldType getServiceFieldType() {
		return serviceFieldType;
	}

	public void setServiceFieldType(ServiceFieldType serviceFieldType) {
		this.serviceFieldType = serviceFieldType;
	}

	public Boolean getIsMandatory() {
		return isMandatory;
	}

	public void setIsMandatory(Boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	public ServiceFieldType getServiceFieldTypeDependancy() {
		return serviceFieldTypeDependancy;
	}

	public void setServiceFieldTypeDependancy(ServiceFieldType serviceFieldTypeDependancy) {
		this.serviceFieldTypeDependancy = serviceFieldTypeDependancy;
	}

	public String getServiceFieldTypeDependancyValue() {
		return serviceFieldTypeDependancyValue;
	}

	public void setServiceFieldTypeDependancyValue(String serviceFieldTypeDependancyValue) {
		this.serviceFieldTypeDependancyValue = serviceFieldTypeDependancyValue;
	}

}
