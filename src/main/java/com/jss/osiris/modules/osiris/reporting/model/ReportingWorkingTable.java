package com.jss.osiris.modules.osiris.reporting.model;

import java.io.Serializable;

import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class ReportingWorkingTable implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "reporting_working_table_sequence", sequenceName = "reporting_working_table_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reporting_working_table_sequence")
	private Integer id;

	@Column(nullable = false)
	private String label;

	@Column(columnDefinition = "TEXT")
	private String sqlText;

	private String reportingUpdateFrequency;

	private String viewName;

	private Boolean isToPersist;

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

	public String getSqlText() {
		return sqlText;
	}

	public void setSqlText(String sqlText) {
		this.sqlText = sqlText;
	}

	public String getReportingUpdateFrequency() {
		return reportingUpdateFrequency;
	}

	public void setReportingUpdateFrequency(String reportingUpdateFrequency) {
		this.reportingUpdateFrequency = reportingUpdateFrequency;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public Boolean getIsToPersist() {
		return isToPersist;
	}

	public void setIsToPersist(Boolean isToPersist) {
		this.isToPersist = isToPersist;
	}

}
