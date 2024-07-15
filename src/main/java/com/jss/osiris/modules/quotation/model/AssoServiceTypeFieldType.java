package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;

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

import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
@Table(indexes = { @Index(name = "idx_asso_service_type_field_type", columnList = "id_service_type_field_type") })
public class AssoServiceTypeFieldType implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "asso_service_type_field_type_sequence", sequenceName = "asso_service_type_field_type_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asso_service_type_field_type_sequence")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_service_type")
	@IndexedField
	private ServiceType serviceType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_service_field_type")
	@IndexedField
	private ServiceFieldType serviceFieldType;

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

}
