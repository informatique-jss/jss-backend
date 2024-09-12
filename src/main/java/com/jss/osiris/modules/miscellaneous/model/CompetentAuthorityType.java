package com.jss.osiris.modules.miscellaneous.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class CompetentAuthorityType implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "competent_authority_type_sequence", sequenceName = "competent_authority_type_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "competent_authority_type_sequence")
	private Integer id;

	@Column(nullable = false, length = 100)
	private String label;

	@Column(nullable = false, length = 20)
	private String code;

	@Column(nullable = false)
	private Boolean isDirectCharge;

	private Boolean isToReminder;

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

	public Boolean getIsDirectCharge() {
		return isDirectCharge;
	}

	public void setIsDirectCharge(Boolean isDirectCharge) {
		this.isDirectCharge = isDirectCharge;
	}

	public Boolean getIsToReminder() {
		return isToReminder;
	}

	public void setIsToReminder(Boolean isToReminder) {
		this.isToReminder = isToReminder;
	}

}
