package com.jss.osiris.modules.osiris.reporting.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
public class AssoReportingDashboardWidget implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "asso_reporting_dashboard_widget_sequence", sequenceName = "asso_reporting_dashboard_widget_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asso_reporting_dashboard_widget_sequence")
	@JsonView({ JacksonViews.OsirisDetailedView.class })
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_reporting_widget")
	@JsonView({ JacksonViews.OsirisDetailedView.class })
	@JsonIgnoreProperties(value = { "payload", "reportingWidgetSeries" }, allowSetters = true)
	private ReportingWidget reportingWidget;

	@ManyToOne
	@JoinColumn(name = "id_reporting_dashboard")
	private ReportingDashboard reportingDashboard;

	@JsonView({ JacksonViews.OsirisDetailedView.class })
	private Integer widgetOrder;

	@JsonView({ JacksonViews.OsirisDetailedView.class })
	private String classToUse;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ReportingWidget getReportingWidget() {
		return reportingWidget;
	}

	public void setReportingWidget(ReportingWidget reportingWidget) {
		this.reportingWidget = reportingWidget;
	}

	public ReportingDashboard getReportingDashboard() {
		return reportingDashboard;
	}

	public void setReportingDashboard(ReportingDashboard reportingDashboard) {
		this.reportingDashboard = reportingDashboard;
	}

	public Integer getWidgetOrder() {
		return widgetOrder;
	}

	public void setWidgetOrder(Integer widgetOrder) {
		this.widgetOrder = widgetOrder;
	}

	public String getClassToUse() {
		return classToUse;
	}

	public void setClassToUse(String classToUse) {
		this.classToUse = classToUse;
	}

}
