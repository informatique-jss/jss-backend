package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.ServiceFieldType;

public interface ServiceFieldTypeService {
    public List<ServiceFieldType> getServiceFieldTypes();

    public ServiceFieldType getServiceFieldType(Integer id);

    public ServiceFieldType addOrUpdateServiceFieldType(ServiceFieldType serviceFieldType);
}
