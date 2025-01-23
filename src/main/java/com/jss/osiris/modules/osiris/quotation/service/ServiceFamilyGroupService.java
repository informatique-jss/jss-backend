package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.ServiceFamilyGroup;

public interface ServiceFamilyGroupService {
    public List<ServiceFamilyGroup> getServiceFamilyGroups();

    public ServiceFamilyGroup getServiceFamilyGroup(Integer id);

    public ServiceFamilyGroup addOrUpdateServiceFamilyGroup(ServiceFamilyGroup serviceFamilyGroup);
}
