package com.jss.osiris.modules.tiers.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class PaymentDeadlineType implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false, length = 100)
	private String label;

	@Column(nullable = false, length = 20)
	private String code;

	private Integer numberOfDay;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getNumberOfDay() {
		return numberOfDay;
	}

	public void setNumberOfDay(Integer numberOfDay) {
		this.numberOfDay = numberOfDay;
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
