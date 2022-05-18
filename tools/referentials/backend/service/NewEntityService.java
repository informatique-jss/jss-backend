package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.NewEntity;

public interface NewEntityService {
    public List<NewEntity> getNewEntities();

    public NewEntity getNewEntity(Integer id);
}
