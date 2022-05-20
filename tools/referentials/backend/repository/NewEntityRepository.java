package com.jss.jssbackend.modules.targetPackage.repository;

import com.jss.jssbackend.modules.targetPackage.model.NewEntity;

import org.springframework.data.repository.CrudRepository;

public interface NewEntityRepository extends CrudRepository<NewEntity, Integer> {
}