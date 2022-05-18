package com.jss.jssbackend.modules.targetPackage.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.miscellaneous.model.NewEntity;
import com.jss.jssbackend.modules.miscellaneous.repository.NewEntityRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if (!newEntity.isEmpty())
            return newEntity.get();
        return null;
    }
}
