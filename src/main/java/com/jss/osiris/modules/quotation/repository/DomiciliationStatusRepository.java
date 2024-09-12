package com.jss.osiris.modules.quotation.repository;

import org.springframework.data.jpa.repository.QueryHints;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.quotation.model.DomiciliationStatus;

import jakarta.persistence.QueryHint;

public interface DomiciliationStatusRepository extends QueryCacheCrudRepository<DomiciliationStatus, Integer> {

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    DomiciliationStatus findByCode(String code);
}