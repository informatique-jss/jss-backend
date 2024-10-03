package com.jss.osiris.modules.osiris.quotation.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

@Entity
public class ServiceType implements Serializable, IId {
	public static String SERVICE_TYPE_OTHER = "OTHER";

	@Id
	@SequenceGenerator(name = "asso_service_type_sequence", sequenceName = "asso_service_type_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asso_service_type_sequence")
	private Integer id;

	@Column(nullable = false)
	@IndexedField
	private String label;

	private String code;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_service_family")
	@IndexedField
	private ServiceFamily serviceFamily;

	@OneToMany(mappedBy = "serviceType", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "serviceType" }, allowSetters = true)
	private List<AssoServiceProvisionType> assoServiceProvisionTypes;

	@OneToMany(mappedBy = "serviceType", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "serviceType" }, allowSetters = true)
	private List<AssoServiceTypeDocument> assoServiceTypeDocuments;

	@OneToMany(mappedBy = "serviceType", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "serviceType" }, allowSetters = true)
	private List<AssoServiceTypeFieldType> assoServiceTypeFieldTypes;

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

}