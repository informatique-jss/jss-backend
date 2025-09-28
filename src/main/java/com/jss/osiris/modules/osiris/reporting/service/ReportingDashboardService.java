package com.jss.osiris.modules.osiris.reporting.service;

import java.util.List;

import com.jss.osiris.modules.osiris.reporting.model.ReportingDashboard;

public interface ReportingDashboardService {
    public List<ReportingDashboard> getReportingDashboards();

    public ReportingDashboard getReportingDashboard(Integer id);

    public ReportingDashboard addOrUpdateReportingDashboard(ReportingDashboard reportingDashboard);

    public ReportingDashboard getReportingDashboardById(Integer id);

    public List<ReportingDashboard> getReportingDashboardsForCurrentUser();
}
