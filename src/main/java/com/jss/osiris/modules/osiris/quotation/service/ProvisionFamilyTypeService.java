package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.ProvisionFamilyType;

public interface ProvisionFamilyTypeService {
    public List<ProvisionFamilyType> getProvisionFamilyTypes();

    public ProvisionFamilyType getProvisionFamilyType(Integer id);

    public ProvisionFamilyType addOrUpdateProvisionFamilyType(ProvisionFamilyType provisionFamilyType);
}
