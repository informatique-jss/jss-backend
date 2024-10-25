package com.jss.osiris.modules.osiris.quotation.service;

import com.jss.osiris.modules.osiris.quotation.model.AssoServiceFieldType;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.model.ServiceFieldType;

public interface AssoServiceFieldTypeService {
    public AssoServiceFieldType getAssoServiceFieldType(Integer id);

    public AssoServiceFieldType addOrUpdateServiceFieldType(AssoServiceFieldType assoServiceFieldType);

    public AssoServiceFieldType createAssoServiceFieldType(Service service, ServiceFieldType serviceFieldType);
}
