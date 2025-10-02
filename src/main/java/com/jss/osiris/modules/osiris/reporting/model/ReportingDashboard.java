package com.jss.osiris.modules.osiris.reporting.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.miscellaneous.model.ActiveDirectoryGroup;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

@Entity
public class ReportingDashboard implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "reporting_dashboard_sequence", sequenceName = "reporting_dashboard_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reporting_dashboard_sequence")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private Integer id;

	@Column(nullable = false)
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private String label;

	@ManyToMany
	@JoinTable(name = "asso_dashboard_active_directory_group", joinColumns = @JoinColumn(name = "id_dashboard"), inverseJoinColumns = @JoinColumn(name = "id_active_directory_group"))
	private List<ActiveDirectoryGroup> activeDirectoryGroups;

	@OneToMany(mappedBy = "reportingDashboard", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "reportingDashboard" }, allowSetters = true)
	@JsonView({ JacksonViews.OsirisDetailedView.class })
	private List<AssoReportingDashboardWidget> assoReportingDashboardWidgets;

	private Integer dashboardOrder;

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

	public List<ActiveDirectoryGroup> getActiveDirectoryGroups() {
		return activeDirectoryGroups;
	}

	public void setActiveDirectoryGroups(List<ActiveDirectoryGroup> activeDirectoryGroups) {
		this.activeDirectoryGroups = activeDirectoryGroups;
	}

	public Integer getDashboardOrder() {
		return dashboardOrder;
	}

	public void setDashboardOrder(Integer order) {
		this.dashboardOrder = order;
	}

	public List<AssoReportingDashboardWidget> getAssoReportingDashboardWidgets() {
		return assoReportingDashboardWidgets;
	}

	public void setAssoReportingDashboardWidgets(List<AssoReportingDashboardWidget> assoReportingDashboardWidgets) {
		this.assoReportingDashboardWidgets = assoReportingDashboardWidgets;
	}

}
