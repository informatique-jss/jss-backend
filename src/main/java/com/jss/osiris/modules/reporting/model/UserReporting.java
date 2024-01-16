package com.jss.osiris.modules.reporting.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.profile.model.Employee;

@Entity
public class UserReporting implements Serializable, IId {

	public static String REPORTING_DATASET_QUOTATION = "Donneur d'ordre / Devis / Prestations - année calendaire courante";
	public static String REPORTING_DATASET_TURNOVER_AMOUNT = "CA HT / TTC / avec et sans débours";
	public static String REPORTING_DATASET_TURNOVER_VAT_AMOUNT = "CA HT / TTC / avec et sans débours - TVA";
	public static String REPORTING_DATASET_CUSTOMER_ORDER = "Commandes";
	public static String REPORTING_DATASET_PROVISION = "Prestations";
	public static String REPORTING_DATASET_ANNOUNCEMENT = "Annonces légales";
	public static String REPORTING_DATASET_TIERS = "Tiers / responsables";
	public static String REPORTING_DATASET_PROVISION_PRODUCTION = "Production : prestations";
	public static String REPORTING_DATASET_RECOVERY = "Recouvrement";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_employee", nullable = false)
	Employee employee;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String settings;

	@Column(nullable = false)
	private String dataset;

	@Column(nullable = false)
	private String name;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getDataset() {
		return dataset;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	public String getSettings() {
		return settings;
	}

	public void setSettings(String settings) {
		this.settings = settings;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
