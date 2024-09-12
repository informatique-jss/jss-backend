package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;

import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.miscellaneous.model.IId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
public class ServiceFamily implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
	private Integer id;

	@Column(nullable = false)
	private String label;

	private String code;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_service_family_group")
	@IndexedField
	private ServiceFamilyGroup serviceFamilyGroup;

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

}
