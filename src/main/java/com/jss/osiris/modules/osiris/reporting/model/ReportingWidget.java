package com.jss.osiris.modules.osiris.reporting.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

@Entity
public class ReportingWidget implements Serializable, IId {

	public static String LABEL_TYPE_NUMERIC = "numeric";
	public static String LABEL_TYPE_DATETIME = "datetime";
	public static String LABEL_TYPE_CATEGORY = "category";

	@Id
	@SequenceGenerator(name = "reporting_widget_sequence", sequenceName = "reporting_widget_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reporting_widget_sequence")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private Integer id;

	@Column(nullable = false)
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private String label;

	@JsonView({ JacksonViews.OsirisDetailedView.class })
	private String labelType;

	@JsonIgnore
	@Column(columnDefinition = "jsonb", insertable = false, updatable = false)
	private String payload;

	@OneToMany(mappedBy = "reportingWidget", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "reportingWidget" }, allowSetters = true)
	private List<ReportingWidgetSerie> reportingWidgetSeries;

	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private LocalDateTime lastUpdate;

	private String reportingUpdateFrequency;

	@Column(columnDefinition = "NUMERIC(15,2)", precision = 10, scale = 2)
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private BigDecimal lastValue;

	@Column(columnDefinition = "NUMERIC(15,2)", precision = 10, scale = 2)
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private BigDecimal currentEvolution;

	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private String lastValueUnit;

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

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public List<ReportingWidgetSerie> getReportingWidgetSeries() {
		return reportingWidgetSeries;
	}

	public void setReportingWidgetSeries(List<ReportingWidgetSerie> reportingWidgetSeries) {
		this.reportingWidgetSeries = reportingWidgetSeries;
	}

	public String getLabelType() {
		return labelType;
	}

	public void setLabelType(String labelType) {
		this.labelType = labelType;
	}

	public static String getLABEL_TYPE_NUMERIC() {
		return LABEL_TYPE_NUMERIC;
	}

	public static void setLABEL_TYPE_NUMERIC(String lABEL_TYPE_NUMERIC) {
		LABEL_TYPE_NUMERIC = lABEL_TYPE_NUMERIC;
	}

	public static String getLABEL_TYPE_DATETIME() {
		return LABEL_TYPE_DATETIME;
	}

	public static void setLABEL_TYPE_DATETIME(String lABEL_TYPE_DATETIME) {
		LABEL_TYPE_DATETIME = lABEL_TYPE_DATETIME;
	}

	public static String getLABEL_TYPE_CATEGORY() {
		return LABEL_TYPE_CATEGORY;
	}

	public static void setLABEL_TYPE_CATEGORY(String lABEL_TYPE_CATEGORY) {
		LABEL_TYPE_CATEGORY = lABEL_TYPE_CATEGORY;
	}

	public LocalDateTime getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(LocalDateTime lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getReportingUpdateFrequency() {
		return reportingUpdateFrequency;
	}

	public void setReportingUpdateFrequency(String reportingUpdateFrequency) {
		this.reportingUpdateFrequency = reportingUpdateFrequency;
	}

	public BigDecimal getLastValue() {
		return lastValue;
	}

	public void setLastValue(BigDecimal lastValue) {
		this.lastValue = lastValue;
	}

	public BigDecimal getCurrentEvolution() {
		return currentEvolution;
	}

	public void setCurrentEvolution(BigDecimal currentEvolution) {
		this.currentEvolution = currentEvolution;
	}

	public String getLastValueUnit() {
		return lastValueUnit;
	}

	public void setLastValueUnit(String lastUnit) {
		this.lastValueUnit = lastUnit;
	}

}
