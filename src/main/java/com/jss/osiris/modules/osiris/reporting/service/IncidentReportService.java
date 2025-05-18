package com.jss.osiris.modules.osiris.reporting.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.reporting.model.IncidentReport;
import com.jss.osiris.modules.osiris.reporting.model.IncidentReportStatus;

public interface IncidentReportService {
    public List<IncidentReport> getIncidentReports();

    public IncidentReport getIncidentReport(Integer id);

    public IncidentReport addOrUpdateIncidentReport(IncidentReport incidentReport) throws OsirisException;

    public List<IncidentReport> getIncidentReportsForCustomerOrder(CustomerOrder customerOrder);

    public List<IncidentReport> searchIncidentReport(List<Employee> employees,
            List<IncidentReportStatus> status);
}
