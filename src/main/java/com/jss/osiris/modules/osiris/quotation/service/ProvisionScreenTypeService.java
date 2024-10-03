package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.ProvisionScreenType;

public interface ProvisionScreenTypeService {
    public List<ProvisionScreenType> getProvisionScreenTypes();

    public ProvisionScreenType getProvisionScreenType(Integer id);

    public void updateScreenTypes();

}
