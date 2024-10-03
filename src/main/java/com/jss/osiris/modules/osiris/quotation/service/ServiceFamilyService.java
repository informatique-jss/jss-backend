package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.ServiceFamily;

public interface ServiceFamilyService {
    public List<ServiceFamily> getServiceFamilies();

    public ServiceFamily getServiceFamily(Integer id);

    public ServiceFamily addOrUpdateServiceFamily(ServiceFamily serviceFamily);
}
