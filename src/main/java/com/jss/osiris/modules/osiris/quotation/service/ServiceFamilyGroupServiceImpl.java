package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.quotation.model.ServiceFamilyGroup;
import com.jss.osiris.modules.osiris.quotation.repository.ServiceFamilyGroupRepository;

@Service
public class ServiceFamilyGroupServiceImpl implements ServiceFamilyGroupService {

    @Autowired
    ServiceFamilyGroupRepository serviceFamilyGroupRepository;

    @Override
    public List<ServiceFamilyGroup> getServiceFamilyGroups() {
        return IterableUtils.toList(serviceFamilyGroupRepository.findAll());
    }

    @Override
    public ServiceFamilyGroup getServiceFamilyGroup(Integer id) {
        Optional<ServiceFamilyGroup> serviceFamilyGroup = serviceFamilyGroupRepository.findById(id);
        if (serviceFamilyGroup.isPresent())
            return serviceFamilyGroup.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceFamilyGroup addOrUpdateServiceFamilyGroup(
            ServiceFamilyGroup serviceFamilyGroup) {
        return serviceFamilyGroupRepository.save(serviceFamilyGroup);
    }
}
