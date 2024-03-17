package com.jss.osiris.libs.batch.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.libs.batch.model.BatchSettings;

public interface BatchSettingsRepository extends CrudRepository<BatchSettings, Integer> {

    BatchSettings findByCode(String code);

}