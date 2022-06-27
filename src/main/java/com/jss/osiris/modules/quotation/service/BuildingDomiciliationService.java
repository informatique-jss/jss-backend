package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.BuildingDomiciliation;

public interface BuildingDomiciliationService {
    public List<BuildingDomiciliation> getBuildingDomiciliations();

    public BuildingDomiciliation getBuildingDomiciliation(Integer id);

    public BuildingDomiciliation addOrUpdateBuildingDomiciliation(BuildingDomiciliation buildingDomiciliation);

}
