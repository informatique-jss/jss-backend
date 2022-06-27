package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.ProvisionFamilyType;

public interface ProvisionFamilyTypeService {
    public List<ProvisionFamilyType> getProvisionFamilyTypes();

    public ProvisionFamilyType getProvisionFamilyType(Integer id);

    public ProvisionFamilyType addOrUpdateProvisionFamilyType(ProvisionFamilyType provisionFamilyType);
}
