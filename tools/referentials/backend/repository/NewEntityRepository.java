package com.jss.osiris.modules.targetPackage.repository;

import com.jss.osiris.modules.targetPackage.model.NewEntity;

import org.springframework.data.repository.CrudRepository;

public interface NewEntityRepository extends CrudRepository<NewEntity, Integer> {
}