package com.jss.jssbackend.modules.quotation.service;

import java.util.List;

import com.jss.jssbackend.modules.quotation.model.ProvisionType;

public interface ProvisionTypeService {
    public List<ProvisionType> getProvisionTypes();

    public ProvisionType getProvisionType(Integer id);
}
