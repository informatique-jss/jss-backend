package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.quotation.model.ServiceFamily;
import com.jss.osiris.modules.quotation.repository.ServiceFamilyRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
