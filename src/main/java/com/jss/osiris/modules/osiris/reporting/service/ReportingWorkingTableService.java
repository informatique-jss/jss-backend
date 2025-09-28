package com.jss.osiris.modules.osiris.reporting.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.reporting.model.ReportingWorkingTable;

public interface ReportingWorkingTableService {
    public List<ReportingWorkingTable> getReportingWorkingTables();

    public ReportingWorkingTable getReportingWorkingTable(Integer id);

    public ReportingWorkingTable addOrUpdateReportingWorkingTable(ReportingWorkingTable reportingWorkingTable);

    public void computeReportingWorkingTable(String reportingFrequency) throws OsirisException;
}
