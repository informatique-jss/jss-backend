package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.quotation.model.ServiceFamily;
import com.jss.osiris.modules.osiris.quotation.model.ServiceFamilyGroup;
import com.jss.osiris.modules.osiris.quotation.repository.ServiceFamilyRepository;

@Service
public class ServiceFamilyServiceImpl implements ServiceFamilyService {

    @Autowired
    ServiceFamilyRepository serviceFamilyRepository;

    @Override
    public List<ServiceFamily> getServiceFamilies() {
        return IterableUtils.toList(serviceFamilyRepository.findAll());
    }

    @Override
    public ServiceFamily getServiceFamily(Integer id) {
        Optional<ServiceFamily> serviceFamily = serviceFamilyRepository.findById(id);
        if (serviceFamily.isPresent())
            return serviceFamily.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceFamily addOrUpdateServiceFamily(
            ServiceFamily serviceFamily) {
        return serviceFamilyRepository.save(serviceFamily);
    }

    @Override
    public List<ServiceFamily> getServiceFamiliesForFamilyGroup(ServiceFamilyGroup serviceFamilyGroup) {
        return serviceFamilyRepository.findByServiceFamilyGroup(serviceFamilyGroup);
    }
}
