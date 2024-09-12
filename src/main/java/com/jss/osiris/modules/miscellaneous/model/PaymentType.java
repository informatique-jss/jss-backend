package com.jss.osiris.modules.miscellaneous.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

import com.jss.osiris.libs.search.model.IndexedField;

@Entity
public class PaymentType implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "payment_type_sequence", sequenceName = "payment_type_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_type_sequence")
	private Integer id;

	@Column(nullable = false, length = 100)
	@IndexedField
	private String label;

	@Column(nullable = false, length = 20)
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
