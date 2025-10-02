package com.jss.osiris.modules.osiris.reporting.repository;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.reporting.model.ReportingWorkingTable;

public interface ReportingWorkingTableRepository extends QueryCacheCrudRepository<ReportingWorkingTable, Integer> {

    List<ReportingWorkingTable> findByReportingUpdateFrequency(String reportingFrequency);
}