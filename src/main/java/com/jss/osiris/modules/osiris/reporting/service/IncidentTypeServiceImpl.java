package com.jss.osiris.modules.osiris.reporting.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.osiris.reporting.model.IncidentType;
import com.jss.osiris.modules.osiris.reporting.repository.IncidentTypeRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IncidentTypeServiceImpl implements IncidentTypeService {

    @Autowired
    IncidentTypeRepository incidentTypeRepository;

    @Override
    public List<IncidentType> getIncidentTypes() {
        return IterableUtils.toList(incidentTypeRepository.findAll());
    }

    @Override
    public IncidentType getIncidentType(Integer id) {
        Optional<IncidentType> incidentType = incidentTypeRepository.findById(id);
        if (incidentType.isPresent())
            return incidentType.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IncidentType addOrUpdateIncidentType(
            IncidentType incidentType) {
        return incidentTypeRepository.save(incidentType);
    }
}
