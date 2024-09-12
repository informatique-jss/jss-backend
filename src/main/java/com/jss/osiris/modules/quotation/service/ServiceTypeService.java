package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.ServiceType;

public interface ServiceTypeService {
    public List<ServiceType> getServiceTypes();

    public ServiceType getServiceType(Integer id);

    public ServiceType addOrUpdateServiceType(ServiceType service);
}
