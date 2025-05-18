package com.jss.osiris.modules.osiris.reporting.service;

import java.util.List;

import com.jss.osiris.modules.osiris.reporting.model.IncidentResponsibility;

public interface IncidentResponsibilityService {
    public List<IncidentResponsibility> getIncidentResponsibilities();

    public IncidentResponsibility getIncidentResponsibility(Integer id);

    public IncidentResponsibility addOrUpdateIncidentResponsibility(IncidentResponsibility incidentResponsibility);
}
