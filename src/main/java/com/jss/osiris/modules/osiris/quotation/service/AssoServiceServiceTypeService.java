package com.jss.osiris.modules.osiris.quotation.service;

import com.jss.osiris.modules.osiris.quotation.model.AssoServiceServiceType;

public interface AssoServiceServiceTypeService {
    public AssoServiceServiceType getAssoServiceServiceType(Integer id);

    public AssoServiceServiceType addOrUpdateAssoServiceServiceType(AssoServiceServiceType assoServiceServiceType);

}
