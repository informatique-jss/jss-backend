package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.ServiceFamilyGroup;

public interface ServiceFamilyGroupService {
    public List<ServiceFamilyGroup> getServiceFamilyGroups();

    public ServiceFamilyGroup getServiceFamilyGroup(Integer id);
	
	 public ServiceFamilyGroup addOrUpdateServiceFamilyGroup(ServiceFamilyGroup serviceFamilyGroup);
}
