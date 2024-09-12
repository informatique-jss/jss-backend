package com.jss.osiris.libs.batch.repository;

import org.springframework.data.jpa.repository.QueryHints;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.libs.batch.model.BatchStatus;

import jakarta.persistence.QueryHint;

public interface BatchStatusRepository extends QueryCacheCrudRepository<BatchStatus, Integer> {
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    BatchStatus findByCode(String code);
}