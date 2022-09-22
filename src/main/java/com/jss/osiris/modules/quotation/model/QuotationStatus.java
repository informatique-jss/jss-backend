package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class QuotationStatus implements Serializable, IId {

	public static String OPEN = "OPEN";
	public static String TO_VERIFY = "TO_VERIFY";
	public static String VALIDATED_BY_JSS = "VALIDATED_BY_JSS";
	public static String SENT_TO_CUSTOMER = "SENT_TO_CUSTOMER";
	public static String VALIDATED_BY_CUSTOMER = "VALIDATED_BY_CUSTOMER";
	public static String REFUSED_BY_CUSTOMER = "REFUSED_BY_CUSTOMER";
	public static String ABANDONED = "ABANDONED";
	public static String BILLED = "BILLED";
	public static String CANCELLED = "CANCELLED";
	public static String WAITING_DEPOSIT = "WAITING_DEPOSIT";
	public static String BEING_PROCESSED = "BEING_PROCESSED";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false, length = 100)
	private String label;

	@Column(nullable = false, length = 20)
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
