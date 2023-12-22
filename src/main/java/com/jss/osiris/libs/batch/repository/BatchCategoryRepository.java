package com.jss.osiris.libs.batch.repository;

import java.util.Optional;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.libs.batch.model.BatchCategory;

public interface BatchCategoryRepository extends QueryCacheCrudRepository<BatchCategory, Integer> {

    Optional<BatchCategory> findByCode(String code);
}