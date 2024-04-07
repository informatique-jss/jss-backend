package com.jss.osiris.modules.quotation.repository;

import org.springframework.data.jpa.repository.QueryHints;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.quotation.model.SimpleProvisionStatus;

import jakarta.persistence.QueryHint;

public interface SimpleProvisonStatusRepository extends QueryCacheCrudRepository<SimpleProvisionStatus, Integer> {

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    SimpleProvisionStatus findByCode(String code);
}