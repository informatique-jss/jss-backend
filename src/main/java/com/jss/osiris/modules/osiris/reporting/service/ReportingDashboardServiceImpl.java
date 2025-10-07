package com.jss.osiris.modules.osiris.reporting.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.miscellaneous.model.ActiveDirectoryGroup;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.reporting.model.AssoReportingDashboardWidget;
import com.jss.osiris.modules.osiris.reporting.model.ReportingDashboard;
import com.jss.osiris.modules.osiris.reporting.repository.ReportingDashboardRepository;

@Service
public class ReportingDashboardServiceImpl implements ReportingDashboardService {

    @Autowired
    ReportingDashboardRepository reportingDashboardRepository;

    @Autowired
    EmployeeService employeeService;

    @Override
    public List<ReportingDashboard> getReportingDashboards() {
        return IterableUtils.toList(reportingDashboardRepository.findAll());
    }

    @Override
    public ReportingDashboard getReportingDashboard(Integer id) {
        Optional<ReportingDashboard> reportingDashboard = reportingDashboardRepository.findById(id);
        if (reportingDashboard.isPresent())
            return reportingDashboard.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReportingDashboard addOrUpdateReportingDashboard(
            ReportingDashboard reportingDashboard) {
        if (reportingDashboard.getAssoReportingDashboardWidgets() != null)
            for (AssoReportingDashboardWidget asso : reportingDashboard.getAssoReportingDashboardWidgets())
                asso.setReportingDashboard(reportingDashboard);
        return reportingDashboardRepository.save(reportingDashboard);
    }

    @Override
    public ReportingDashboard getReportingDashboardById(Integer id) {
        ReportingDashboard dashboard = getReportingDashboard(id);
        if (dashboard != null) {
            if (dashboard.getActiveDirectoryGroups() == null || dashboard.getActiveDirectoryGroups().size() == 0)
                return dashboard;

            for (ActiveDirectoryGroup adGroup : dashboard.getActiveDirectoryGroups())
                if (employeeService.isCurrentUserHasAdGroup(adGroup))
                    return dashboard;
        }

        return null;
    }

    @Override
    public List<ReportingDashboard> getReportingDashboardsForCurrentUser() {

        List<ReportingDashboard> outDashboards = new ArrayList<ReportingDashboard>();
        List<ReportingDashboard> dashboards = getReportingDashboards();
        if (dashboards != null) {
            outerLoop: for (ReportingDashboard dashboard : dashboards) {
                if (dashboard.getActiveDirectoryGroups() == null || dashboard.getActiveDirectoryGroups().size() == 0) {
                    outDashboards.add(dashboard);
                    continue;
                }

                for (ActiveDirectoryGroup adGroup : dashboard.getActiveDirectoryGroups())
                    if (employeeService.isCurrentUserHasAdGroup(adGroup)) {
                        outDashboards.add(dashboard);
                        continue outerLoop;
                    }
            }
        }

        outDashboards.sort(new Comparator<ReportingDashboard>() {
            @Override
            public int compare(ReportingDashboard c0, ReportingDashboard c1) {
                if (c0 == null && c1 == null)
                    return 0;
                if (c0 != null && c1 == null)
                    return 1;
                if (c0 == null && c1 != null)
                    return -1;
                if (c1 != null && c0 != null)
                    return c0.getDashboardOrder().compareTo(c1.getDashboardOrder());
                return 0;
            }
        });
        return outDashboards;
    }
}
