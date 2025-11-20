package com.jss.osiris.modules.osiris.quotation.dto;

import java.io.Serializable;
import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.quotation.model.ServiceTypeFieldTypePossibleValue;

public class ServiceFieldTypeDto implements Serializable, IId {

	private Integer id;

	private String label;

	private String code;

	private String dataType;

	private List<ServiceTypeFieldTypePossibleValue> serviceFieldTypePossibleValues;

	private String value;

	public List<ServiceTypeFieldTypePossibleValue> getServiceFieldTypePossibleValues() {
		return serviceFieldTypePossibleValues;
	}

	public void setServiceFieldTypePossibleValues(
			List<ServiceTypeFieldTypePossibleValue> serviceFieldTypePossibleValues) {
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
