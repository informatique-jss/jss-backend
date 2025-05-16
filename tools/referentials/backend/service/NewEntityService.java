package com.jss.osiris.modules.osiris.targetPackage.service;

import java.util.List;

import com.jss.osiris.modules.osiris.targetPackage.model.NewEntity;

public interface NewEntityService {
    public List<NewEntity> getNewEntities();

    public NewEntity getNewEntity(Integer id);

    public NewEntity addOrUpdateNewEntity(NewEntity newEntity);
}
