package com.jss.osiris.modules.osiris.reporting.repository;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.reporting.model.IncidentReportStatus;

public interface IncidentReportStatusRepository extends QueryCacheCrudRepository<IncidentReportStatus, Integer> {

    IncidentReportStatus findByCode(String code);
}