package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.miscellaneous.model.IId;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

@Entity
public class ServiceFieldType implements Serializable, IId {
	public static String SERVICE_FIELD_TYPE_INTEGER = "SERVICE_FIELD_TYPE_INTEGER";
	public static String SERVICE_FIELD_TYPE_TEXT = "SERVICE_FIELD_TYPE_TEXT";
	public static String SERVICE_FIELD_TYPE_TEXTAREA = "SERVICE_FIELD_TYPE_TEXTAREA";
	public static String SERVICE_FIELD_TYPE_DATE = "SERVICE_FIELD_TYPE_DATE";
	public static String SERVICE_FIELD_TYPE_SELECT = "SERVICE_FIELD_TYPE_SELECT";
	@Id
	@SequenceGenerator(name = "service_field_type_sequence", sequenceName = "service_field_type_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "service_field_type_sequence")
	private Integer id;

	@Column(nullable = false)
	private String label;

	private String code;

	private String dataType;

	private Boolean isMandatory;

	@OneToMany(mappedBy = "serviceFieldType", cascade = CascadeType.ALL)
	@JsonIgnoreProperties(value = { "serviceFieldType" }, allowSetters = true)
	private List<ServiceFieldTypePossibleValue> serviceFieldTypePossibleValues;

	public List<ServiceFieldTypePossibleValue> getServiceFieldTypePossibleValues() {
		return serviceFieldTypePossibleValues;
	}

	public void setServiceFieldTypePossibleValues(List<ServiceFieldTypePossibleValue> serviceFieldTypePossibleValues) {
		this.serviceFieldTypePossibleValues = serviceFieldTypePossibleValues;
	}

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

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Boolean getIsMandatory() {
		return isMandatory;
	}

	public void setIsMandatory(Boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

}
