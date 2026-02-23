package com.jss.osiris.modules.osiris.miscellaneous.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class TooltipEntry implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "tooltip_entry_sequence", sequenceName = "tooltip_entry_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tooltip_entry_sequence")
	private Integer id;

	@Column(nullable = false, length = 300)
	private String label;

	@Column(nullable = false, length = 60)
	private String code;

	@Column(nullable = false)
	private Boolean isVisible;

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

	public Boolean getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(Boolean isVisible) {
		this.isVisible = isVisible;
	}
}
