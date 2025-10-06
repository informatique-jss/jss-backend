package com.jss.osiris.modules.osiris.reporting.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
public class ReportingWidgetSerie {

    @Id
    @SequenceGenerator(name = "reporting_widget_serie_sequence", sequenceName = "reporting_widget_serie_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reporting_widget_serie_sequence")
    @JsonView({ JacksonViews.OsirisDetailedView.class })
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String serieSqlText;

    private String serieName;

    @Column(length = 1000)
    private String columns;

    private String plotType;
    private String stack;

    @ManyToOne
    @JoinColumn(name = "id_reporting_widget")
    private ReportingWidget reportingWidget;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSerieSqlText() {
        return serieSqlText;
    }

    public void setSerieSqlText(String labelSqlText) {
        this.serieSqlText = labelSqlText;
    }

    public ReportingWidget getReportingWidget() {
        return reportingWidget;
    }

    public void setReportingWidget(ReportingWidget reportingWidget) {
        this.reportingWidget = reportingWidget;
    }

    public String getSerieName() {
        return serieName;
    }

    public void setSerieName(String serieName) {
        this.serieName = serieName;
    }

    public String getPlotType() {
        return plotType;
    }

    public void setPlotType(String plotType) {
        this.plotType = plotType;
    }

    public String getColumns() {
        return columns;
    }

    public void setColumns(String columns) {
        this.columns = columns;
    }

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }

}
