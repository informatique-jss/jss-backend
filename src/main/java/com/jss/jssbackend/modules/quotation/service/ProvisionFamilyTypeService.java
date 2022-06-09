package com.jss.jssbackend.modules.quotation.service;

import java.util.List;

import com.jss.jssbackend.modules.quotation.model.ProvisionFamilyType;

public interface ProvisionFamilyTypeService {
    public List<ProvisionFamilyType> getProvisionFamilyTypes();

    public ProvisionFamilyType getProvisionFamilyType(Integer id);
}
