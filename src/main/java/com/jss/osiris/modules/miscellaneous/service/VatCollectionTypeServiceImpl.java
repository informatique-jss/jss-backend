package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.miscellaneous.model.VatCollectionType;
import com.jss.osiris.modules.miscellaneous.repository.VatCollectionTypeRepository;

@Service
public class VatCollectionTypeServiceImpl implements VatCollectionTypeService {

    @Autowired
    VatCollectionTypeRepository vatCollectionTypeRepository;

    @Override
    @Cacheable(value = "vatCollectionTypeList", key = "#root.methodName")
    public List<VatCollectionType> getVatCollectionTypes() {
        return IterableUtils.toList(vatCollectionTypeRepository.findAll());
    }

    @Override
    @Cacheable(value = "vatCollectionType", key = "#id")
    public VatCollectionType getVatCollectionType(Integer id) {
        Optional<VatCollectionType> vatCollectionType = vatCollectionTypeRepository.findById(id);
        if (vatCollectionType.isPresent())
            return vatCollectionType.get();
        return null;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "vatCollectionTypeList", allEntries = true),
            @CacheEvict(value = "vatCollectionType", key = "#vatCollectionType.id")
    })
    public VatCollectionType addOrUpdateVatCollectionType(
            VatCollectionType vatCollectionType) {
        return vatCollectionTypeRepository.save(vatCollectionType);
    }
}
