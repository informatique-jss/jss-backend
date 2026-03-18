package com.jss.osiris.modules.osiris.tiers.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.tiers.model.BillingLabelType;

public interface BillingLabelTypeRepository extends QueryCacheCrudRepository<BillingLabelType, Integer> {

    @Query("SELECT a FROM BillingLabelType a WHERE a != :excludedEntity")
    List<BillingLabelType> findAllExcluding(@Param("excludedEntity") BillingLabelType excludedEntity);
}