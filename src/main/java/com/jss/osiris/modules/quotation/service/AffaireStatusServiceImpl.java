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

import com.jss.osiris.modules.quotation.model.AffaireStatus;
import com.jss.osiris.modules.quotation.repository.AffaireStatusRepository;

@Service
public class AffaireStatusServiceImpl implements AffaireStatusService {

    @Autowired
    AffaireStatusRepository affaireStatusRepository;

    @Override
    @Cacheable(value = "affaireStatusList", key = "#root.methodName")
    public List<AffaireStatus> getAffaireStatus() {
        return IterableUtils.toList(affaireStatusRepository.findAll());
    }

    @Override
    @Cacheable(value = "affaireStatus", key = "#id")
    public AffaireStatus getAffaireStatus(Integer id) {
        Optional<AffaireStatus> affaireStatus = affaireStatusRepository.findById(id);
        if (affaireStatus.isPresent())
            return affaireStatus.get();
        return null;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "affaireStatusList", allEntries = true),
            @CacheEvict(value = "affaireStatus", key = "#affaireStatus.id")
    })
    @Transactional(rollbackFor = Exception.class)
    public AffaireStatus addOrUpdateAffaireStatus(
            AffaireStatus affaireStatus) {
        return affaireStatusRepository.save(affaireStatus);
    }
}
