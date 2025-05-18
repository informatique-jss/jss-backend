package com.jss.osiris.modules.osiris.reporting.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.reporting.model.IncidentReportStatus;

public interface IncidentReportStatusService {
    public List<IncidentReportStatus> getIncidentReportStatusList();

    public IncidentReportStatus getIncidentReportStatus(Integer id);

    public IncidentReportStatus addOrUpdateIncidentReportStatus(IncidentReportStatus incidentReportStatus);

    public IncidentReportStatus getIncidentReportStatusByCode(String code);

    public void updateStatusReferential() throws OsirisException;
}
