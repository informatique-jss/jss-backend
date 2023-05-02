package com.jss.osiris.modules.quotation.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.quotation.model.ProvisionFamilyType;

public interface ProvisionFamilyTypeRepository extends QueryCacheCrudRepository<ProvisionFamilyType, Integer> {

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    List<ProvisionFamilyType> findAllByOrderByCode();
}