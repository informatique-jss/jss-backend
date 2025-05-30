package com.jss.osiris.modules.osiris.reporting.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.reporting.model.IncidentReport;
import com.jss.osiris.modules.osiris.reporting.model.IncidentReportStatus;
import com.jss.osiris.modules.osiris.reporting.repository.IncidentReportRepository;

@Service
public class IncidentReportServiceImpl implements IncidentReportService {

    @Autowired
    IncidentReportRepository incidentReportRepository;

    @Autowired
    IncidentReportStatusService incidentReportStatusService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    NotificationService notificationService;

    @Override
    public List<IncidentReport> getIncidentReports() {
        return IterableUtils.toList(incidentReportRepository.findAll());
    }

    @Override
    public IncidentReport getIncidentReport(Integer id) {
        Optional<IncidentReport> incidentReport = incidentReportRepository.findById(id);
        if (incidentReport.isPresent())
            return incidentReport.get();
        return null;
    }

    @Override
    public List<IncidentReport> getIncidentReportsForCustomerOrder(CustomerOrder customerOrder) {
        return incidentReportRepository.findByCustomerOrder(customerOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IncidentReport addOrUpdateIncidentReport(
            IncidentReport incidentReport) throws OsirisException {

        IncidentReport currentIncidentReport = null;
        if (incidentReport.getId() != null)
            currentIncidentReport = getIncidentReport(incidentReport.getId());

        if (incidentReport.getIncidentReportStatus() == null) {
            incidentReport.setIncidentReportStatus(
                    incidentReportStatusService.getIncidentReportStatusByCode(IncidentReportStatus.TO_COMPLETE));
        }
        if (incidentReport.getInitiatedBy() == null)
            incidentReport.setInitiatedBy(employeeService.getCurrentEmployee());

        if (incidentReport.getAssignedTo() == null && incidentReport.getProvision() != null) {
            incidentReport.setAssignedTo(incidentReport.getProvision().getAssignedTo());
        }

        boolean isToNotify = incidentReport.getId() == null;

        if (!isToNotify && currentIncidentReport != null
                && !currentIncidentReport.getAssignedTo().getId().equals(incidentReport.getAssignedTo().getId()))
            isToNotify = true;

        incidentReportRepository.save(incidentReport);

        if (isToNotify)
            notificationService.notifyIncidentReportAsked(incidentReport);

        return incidentReport;

    }

    @Override
    public List<IncidentReport> searchIncidentReport(List<Employee> employees,
            List<IncidentReportStatus> status) {

        List<Integer> employeeIds = (employees != null && employees.size() > 0)
                ? employees.stream().map(Employee::getId).collect(Collectors.toList())
                : Arrays.asList(0);

        List<Integer> statusIds = (status != null && status.size() > 0)
                ? status.stream().map(IncidentReportStatus::getId).collect(Collectors.toList())
                : Arrays.asList(0);

        return incidentReportRepository.searchIncidentReport(employeeIds, statusIds);
    }
}
