package com.jss.osiris.libs.batch.repository;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.libs.batch.model.BatchStatus;

public interface BatchStatusRepository extends QueryCacheCrudRepository<BatchStatus, Integer> {
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    BatchStatus findByCode(String code);
}