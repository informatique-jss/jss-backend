package com.jss.osiris.modules.miscellaneous.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(indexes = { @Index(name = "idx_department_vat_setting", columnList = "department", unique = true) })
public class DepartmentVatSetting implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private String code;

	private Department department;

	private Vat intermediateVat;

	private Vat reducedVat;

	private Vat intermediateVatForPurshase;

	private Vat reducedVatForPurshase;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public Vat getIntermediateVat() {
		return intermediateVat;
	}

	public void setIntermediateVat(Vat intermediateVat) {
		this.intermediateVat = intermediateVat;
	}

	public Vat getReducedVat() {
		return reducedVat;
	}

	public void setReducedVat(Vat reducedVat) {
		this.reducedVat = reducedVat;
	}

	public Vat getIntermediateVatForPurshase() {
		return intermediateVatForPurshase;
	}

	public void setIntermediateVatForPurshase(Vat intermediateVatForPurshase) {
		this.intermediateVatForPurshase = intermediateVatForPurshase;
	}

	public Vat getReducedVatForPurshase() {
		return reducedVatForPurshase;
	}

	public void setReducedVatForPurshase(Vat reducedVatForPurshase) {
		this.reducedVatForPurshase = reducedVatForPurshase;
	}

}
