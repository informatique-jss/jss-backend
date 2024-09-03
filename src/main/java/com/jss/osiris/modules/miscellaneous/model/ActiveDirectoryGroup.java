package com.jss.osiris.modules.miscellaneous.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class ActiveDirectoryGroup implements Serializable, IId {

	public static String GROUP_FORMALITES = "FORMALITES";
	public static String GROUP_FACTURATION = "FACTURATION";
	@Id
	@SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
	private Integer id;

	@Column(nullable = false)
	private String label;

	private String code;

	@Column(nullable = false, length = 1000)
	private String activeDirectoryPath;

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

	public String getActiveDirectoryPath() {
		return activeDirectoryPath;
	}

	public void setActiveDirectoryPath(String activeDirectoryPath) {
		this.activeDirectoryPath = activeDirectoryPath;
	}

}
