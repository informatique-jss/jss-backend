package com.jss.osiris.modules.osiris.miscellaneous.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class DocumentType implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "document_type_sequence", sequenceName = "document_type_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_type_sequence")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private Integer id;

	@Column(nullable = false, length = 100)
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private String label;

	@Column(nullable = false, length = 20)
	@JsonView(JacksonViews.MyJssDetailedView.class)
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
