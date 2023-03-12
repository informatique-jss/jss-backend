package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.quotation.model.ProvisionType;
import com.jss.osiris.modules.quotation.repository.ProvisionTypeRepository;

@Service
public class ProvisionTypeServiceImpl implements ProvisionTypeService {

    @Autowired
    ProvisionTypeRepository provisionTypeRepository;

    @Override
    @Cacheable(value = "provisionTypeList", key = "#root.methodName")
    public List<ProvisionType> getProvisionTypes() {
        return IterableUtils.toList(provisionTypeRepository.findAll());
    }

    @Override
    @Cacheable(value = "provisionType", key = "#id")
    public ProvisionType getProvisionType(Integer id) {
        Optional<ProvisionType> provisionType = provisionTypeRepository.findById(id);
        if (provisionType.isPresent())
            return provisionType.get();
        return null;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "provisionTypeList", allEntries = true),
            @CacheEvict(value = "provisionType", key = "#provisionType.id")
    })
    @Transactional(rollbackFor = Exception.class)
    public ProvisionType addOrUpdateProvisionType(
            ProvisionType provisionType) {
        return provisionTypeRepository.save(provisionType);
    }
}
