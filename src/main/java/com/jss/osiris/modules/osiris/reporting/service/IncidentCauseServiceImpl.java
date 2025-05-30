package com.jss.osiris.modules.osiris.reporting.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.osiris.reporting.model.IncidentCause;
import com.jss.osiris.modules.osiris.reporting.repository.IncidentCauseRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IncidentCauseServiceImpl implements IncidentCauseService {

    @Autowired
    IncidentCauseRepository incidentCauseRepository;

    @Override
    public List<IncidentCause> getIncidentCauses() {
        return IterableUtils.toList(incidentCauseRepository.findAll());
    }

    @Override
    public IncidentCause getIncidentCause(Integer id) {
        Optional<IncidentCause> incidentCause = incidentCauseRepository.findById(id);
        if (incidentCause.isPresent())
            return incidentCause.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IncidentCause addOrUpdateIncidentCause(
            IncidentCause incidentCause) {
        return incidentCauseRepository.save(incidentCause);
    }
}
