package com.jss.osiris.modules.quotation.service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.quotation.model.AssoServiceFieldType;

public interface AssoServiceFieldTypeService {
    public AssoServiceFieldType getAssoServiceFieldType(Integer id);

    public AssoServiceFieldType addOrUpdateServiceFieldType(AssoServiceFieldType assoServiceFieldType)
            throws OsirisException;
}
