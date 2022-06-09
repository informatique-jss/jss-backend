package com.jss.jssbackend.modules.quotation.service;

import java.util.List;

import com.jss.jssbackend.modules.quotation.model.BuildingDomiciliation;

public interface BuildingDomiciliationService {
    public List<BuildingDomiciliation> getBuildingDomiciliations();

    public BuildingDomiciliation getBuildingDomiciliation(Integer id);
}
