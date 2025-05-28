package com.jss.osiris.modules.osiris.reporting.service;

import java.util.List;

import com.jss.osiris.modules.osiris.reporting.model.IncidentCause;

public interface IncidentCauseService {
    public List<IncidentCause> getIncidentCauses();

    public IncidentCause getIncidentCause(Integer id);

    public IncidentCause addOrUpdateIncidentCause(IncidentCause incidentCause);
}
