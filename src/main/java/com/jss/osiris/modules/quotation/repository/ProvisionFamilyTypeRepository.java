package com.jss.osiris.modules.quotation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.QueryHints;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.quotation.model.ProvisionFamilyType;

import jakarta.persistence.QueryHint;

public interface ProvisionFamilyTypeRepository extends QueryCacheCrudRepository<ProvisionFamilyType, Integer> {

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    List<ProvisionFamilyType> findAllByOrderByCode();
}