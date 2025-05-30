package com.jss.osiris.modules.osiris.reporting.service;

import java.util.List;

import com.jss.osiris.modules.osiris.reporting.model.IncidentType;

public interface IncidentTypeService {
    public List<IncidentType> getIncidentTypes();

    public IncidentType getIncidentType(Integer id);

    public IncidentType addOrUpdateIncidentType(IncidentType incidentType);
}
