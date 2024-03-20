package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class ServiceType implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "asso_service_type_sequence", sequenceName = "asso_service_type_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asso_service_type_sequence")
	private Integer id;

	@Column(nullable = false)
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

}