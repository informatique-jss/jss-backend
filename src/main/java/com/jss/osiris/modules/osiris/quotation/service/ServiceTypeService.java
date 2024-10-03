package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.ServiceType;

public interface ServiceTypeService {
    public List<ServiceType> getServiceTypes();

    public ServiceType getServiceType(Integer id);

    public ServiceType getServiceTypeByCode(String code);

    public ServiceType addOrUpdateServiceType(ServiceType service);
}
