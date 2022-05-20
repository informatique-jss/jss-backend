package com.jss.jssbackend.modules.targetPackage.service;

import java.util.List;

import com.jss.jssbackend.modules.targetPackage.model.NewEntity;

public interface NewEntityService {
    public List<NewEntity> getNewEntities();

    public NewEntity getNewEntity(Integer id);
}
