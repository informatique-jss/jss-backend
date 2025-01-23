package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.quotation.model.BuildingDomiciliation;
import com.jss.osiris.modules.osiris.quotation.repository.BuildingDomiciliationRepository;

@Service
public class BuildingDomiciliationServiceImpl implements BuildingDomiciliationService {

    @Autowired
    BuildingDomiciliationRepository buildingDomiciliationRepository;

    @Override
    public List<BuildingDomiciliation> getBuildingDomiciliations() {
        return IterableUtils.toList(buildingDomiciliationRepository.findAll());
    }

    @Override
    public BuildingDomiciliation getBuildingDomiciliation(Integer id) {
        Optional<BuildingDomiciliation> buildingDomiciliation = buildingDomiciliationRepository.findById(id);
        if (buildingDomiciliation.isPresent())
            return buildingDomiciliation.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BuildingDomiciliation addOrUpdateBuildingDomiciliation(BuildingDomiciliation buildingDomiciliation) {
        return buildingDomiciliationRepository.save(buildingDomiciliation);
    }

}
