package com.jss.osiris.modules.osiris.reporting.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.quotation.model.IWorkflowElement;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

@Entity
public class IncidentReportStatus extends IWorkflowElement implements Serializable {

	public static final String TO_COMPLETE = "TO_COMPLETE";

	public static final String TO_ANALYSE = "TO_ANALYSE";

	public static final String ACTIONS_IN_PROGRESS = "ACTIONS_IN_PROGRESS";

	public static final String FINALIZED = "FINALIZED";

	public static final String ABANDONED = "ABANDONED";

	@Id
	@SequenceGenerator(name = "incident_report_status_sequence", sequenceName = "incident_report_status_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "incident_report_status_sequence")
	@JsonView({ JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	private Integer id;

	@Column(nullable = false, length = 100)
	@JsonView({ JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	private String label;

	@Column(nullable = false, length = 100)
	@JsonView({ JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	private String code;

	private String icon;

	@OneToMany(targetEntity = IncidentReportStatus.class)
	@JoinTable(name = "asso_incident_report_status_successor", joinColumns = @JoinColumn(name = "id_incident_report_status"), inverseJoinColumns = @JoinColumn(name = "id_incident_report_status_successor"))
	@JsonIgnoreProperties(value = { "predecessors", "successors" })
	private List<IncidentReportStatus> successors;

	@OneToMany(targetEntity = IncidentReportStatus.class)
	@JoinTable(name = "asso_incident_report_status_predecessor", joinColumns = @JoinColumn(name = "id_incident_report_status"), inverseJoinColumns = @JoinColumn(name = "id_incident_report_status_predecessor"))
	@JsonIgnoreProperties(value = { "predecessors", "successors" })
	private List<IncidentReportStatus> predecessors;

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

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public List<IncidentReportStatus> getSuccessors() {
		return successors;
	}

	@SuppressWarnings({ "unchecked" })
	public void setSuccessors(List<? extends IWorkflowElement> successors) {
		this.successors = (List<IncidentReportStatus>) successors;
	}

	public List<IncidentReportStatus> getPredecessors() {
		return predecessors;
	}

	@SuppressWarnings({ "unchecked" })
	public void setPredecessors(List<? extends IWorkflowElement> predecessors) {
		this.predecessors = (List<IncidentReportStatus>) predecessors;
	}

}
