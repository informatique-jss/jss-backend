package com.jss.osiris.modules.osiris.reporting.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.osiris.reporting.model.IncidentResponsibility;
import com.jss.osiris.modules.osiris.reporting.repository.IncidentResponsibilityRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IncidentResponsibilityServiceImpl implements IncidentResponsibilityService {

    @Autowired
    IncidentResponsibilityRepository incidentResponsibilityRepository;

    @Override
    public List<IncidentResponsibility> getIncidentResponsibilities() {
        return IterableUtils.toList(incidentResponsibilityRepository.findAll());
    }

    @Override
    public IncidentResponsibility getIncidentResponsibility(Integer id) {
        Optional<IncidentResponsibility> incidentResponsibility = incidentResponsibilityRepository.findById(id);
        if (incidentResponsibility.isPresent())
            return incidentResponsibility.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IncidentResponsibility addOrUpdateIncidentResponsibility(
            IncidentResponsibility incidentResponsibility) {
        return incidentResponsibilityRepository.save(incidentResponsibility);
    }
}
