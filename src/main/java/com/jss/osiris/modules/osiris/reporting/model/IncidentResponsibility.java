package com.jss.osiris.modules.osiris.reporting.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class IncidentResponsibility implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "incident_responsibility_sequence", sequenceName = "incident_responsibility_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "incident_responsibility_sequence")
	@JsonView(JacksonViews.OsirisListView.class)
	private Integer id;

	@Column(nullable = false)
	@JsonView(JacksonViews.OsirisListView.class)
	private String label;

	@JsonView(JacksonViews.OsirisListView.class)
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
