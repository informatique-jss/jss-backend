package com.jss.osiris.modules.osiris.targetPackage.model;

import java.io.Serializable;

import com.jss.osiris.modules.miscellaneous.model.IId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class NewEntity implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "", sequenceName = "", allocationSize = 1) TOCOMPLETE
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "")
	private Integer id;

	@Column(nullable = false)
	private String label;

	private String code;

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

}
