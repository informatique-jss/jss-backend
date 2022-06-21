package com.jss.osiris.modules.tiers.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.tiers.model.TiersCategory;

public interface TiersCategoryRepository extends CrudRepository<TiersCategory, Integer> {
}