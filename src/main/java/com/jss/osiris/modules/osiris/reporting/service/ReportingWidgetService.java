package com.jss.osiris.modules.osiris.reporting.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.reporting.model.ReportingWidget;

public interface ReportingWidgetService {
    public List<ReportingWidget> getReportingWidgets();

    public List<ReportingWidget> getReportingWidgetsByFrequency(String reportingFrequency);

    public ReportingWidget getReportingWidget(Integer id);

    public ReportingWidget addOrUpdateReportingWidget(ReportingWidget reportingWidget);

    public void computeReportingWidget(Integer widgetId) throws OsirisException;

    public String getReportingWidgetPayload(ReportingWidget widget);
}
