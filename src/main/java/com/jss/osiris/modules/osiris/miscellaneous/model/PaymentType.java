package com.jss.osiris.modules.osiris.miscellaneous.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.IndexedField;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class PaymentType implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "payment_type_sequence", sequenceName = "payment_type_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_type_sequence")
	@JsonView({ JacksonViews.MyJssView.class, JacksonViews.OsirisListView.class })
	private Integer id;

	@Column(nullable = false, length = 100)
	@IndexedField
	@JsonView({ JacksonViews.MyJssView.class, JacksonViews.OsirisListView.class })
	private String label;

	@Column(nullable = false, length = 20)
	@JsonView(JacksonViews.MyJssView.class)
	private String code;

	@Column(length = 20)
	private String codeInpi;

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

	public String getCodeInpi() {
		return codeInpi;
	}

	public void setCodeInpi(String codeInpi) {
		this.codeInpi = codeInpi;
	}

}
