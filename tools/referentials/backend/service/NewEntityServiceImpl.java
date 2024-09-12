package com.jss.osiris.modules.targetPackage.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.targetPackage.model.NewEntity;
import com.jss.osiris.modules.targetPackage.repository.NewEntityRepository; 
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NewEntityServiceImpl implements NewEntityService {

    @Autowired
    NewEntityRepository newEntityRepository;

    @Override
    public List<NewEntity> getNewEntities() {
        return IterableUtils.toList(newEntityRepository.findAll());
    }

    @Override
    public NewEntity getNewEntity(Integer id) {
        Optional<NewEntity> newEntity = newEntityRepository.findById(id);
        if (newEntity.isPresent())
            return newEntity.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NewEntity addOrUpdateNewEntity(
            NewEntity newEntity) {
        return newEntityRepository.save(newEntity);
    }
}
