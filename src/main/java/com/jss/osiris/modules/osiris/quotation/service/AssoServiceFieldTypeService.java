package com.jss.osiris.modules.osiris.quotation.service;

import com.jss.osiris.modules.osiris.quotation.model.AssoServiceFieldType;

public interface AssoServiceFieldTypeService {
    public AssoServiceFieldType getAssoServiceFieldType(Integer id);

    public AssoServiceFieldType addOrUpdateServiceFieldType(AssoServiceFieldType assoServiceFieldType);
}
