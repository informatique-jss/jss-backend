package com.jss.osiris.libs.audit.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.libs.audit.model.Audit;

public interface AuditRepository extends QueryCacheCrudRepository<Audit, Integer> {

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    List<Audit> findByEntityAndEntityId(String entityType, Integer entityId);
}