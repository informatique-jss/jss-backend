package com.jss.osiris.modules.osiris.reporting.repository;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.reporting.model.ReportingWidget;

public interface ReportingWidgetRepository extends QueryCacheCrudRepository<ReportingWidget, Integer> {

    List<ReportingWidget> findByReportingUpdateFrequency(String reportingFrequency);
}