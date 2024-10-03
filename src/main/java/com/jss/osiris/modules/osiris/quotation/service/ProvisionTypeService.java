package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.ProvisionType;

public interface ProvisionTypeService {
    public List<ProvisionType> getProvisionTypes();

    public ProvisionType getProvisionType(Integer id);

    public ProvisionType addOrUpdateProvisionType(ProvisionType provisionType);
}
