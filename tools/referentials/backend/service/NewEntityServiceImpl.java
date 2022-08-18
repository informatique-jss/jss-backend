package com.jss.osiris.modules.targetPackage.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.targetPackage.model.NewEntity;
import com.jss.osiris.modules.targetPackage.repository.NewEntityRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NewEntityServiceImpl implements NewEntityService {

    @Autowired
    NewEntityRepository newEntityRepository;

    @Override
	@Cacheable(value = "newEntityList", key = "#root.methodName")
    public List<NewEntity> getNewEntities() {
        return IterableUtils.toList(newEntityRepository.findAll());
    }

    @Override
	@Cacheable(value = "newEntity", key = "#id")
    public NewEntity getNewEntity(Integer id) {
        Optional<NewEntity> newEntity = newEntityRepository.findById(id);
        if (!newEntity.isEmpty())
            return newEntity.get();
        return null;
    }
	
	 @Override
	 @Caching(evict = {
            @CacheEvict(value = "newEntityList", allEntries = true),
            @CacheEvict(value = "newEntity", key = "#newEntity.id")
    })
    public NewEntity addOrUpdateNewEntity(
            NewEntity newEntity) {
        return newEntityRepository.save(newEntity);
    }
}
