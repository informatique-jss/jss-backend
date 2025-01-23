package com.jss.osiris.modules.osiris.miscellaneous.model;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(indexes = { @Index(name = "idx_department_vat_setting", columnList = "id_department", unique = true) })
public class DepartmentVatSetting implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
	private Integer id;

	private String code;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_department")
	private Department department;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_intermediate_vat")
	private Vat intermediateVat;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_reduced_vat")
	private Vat reducedVat;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_intermediate_vat_for_purshase")
	private Vat intermediateVatForPurshase;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_reduced_vat_for_purshase")
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
