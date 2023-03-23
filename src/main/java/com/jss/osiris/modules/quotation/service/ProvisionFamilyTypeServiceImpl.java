package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.quotation.model.ProvisionFamilyType;
import com.jss.osiris.modules.quotation.repository.ProvisionFamilyTypeRepository;

@Service
public class ProvisionFamilyTypeServiceImpl implements ProvisionFamilyTypeService {

    @Autowired
    ProvisionFamilyTypeRepository provisionFamilyTypeRepository;

    @Override
    @Cacheable(value = "provisionFamilyTypeList", key = "#root.methodName")
    public List<ProvisionFamilyType> getProvisionFamilyTypes() {
        return provisionFamilyTypeRepository.findAllByOrderByCode();
    }

    @Override
    @Cacheable(value = "provisionFamilyType", key = "#id")
    public ProvisionFamilyType getProvisionFamilyType(Integer id) {
        Optional<ProvisionFamilyType> provisionFamilyType = provisionFamilyTypeRepository.findById(id);
        if (provisionFamilyType.isPresent())
            return provisionFamilyType.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = "provisionFamilyTypeList", allEntries = true),
            @CacheEvict(value = "provisionFamilyType", key = "#provisionFamilyType.id")
    })
    public ProvisionFamilyType addOrUpdateProvisionFamilyType(ProvisionFamilyType provisionFamilyType) {
        return provisionFamilyTypeRepository.save(provisionFamilyType);
    }
}
