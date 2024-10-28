package com.jss.osiris.libs.audit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.libs.audit.model.Audit;

public interface AuditRepository extends CrudRepository<Audit, Integer> {

    List<Audit> findByEntityAndEntityId(String entityType, Integer entityId);

    @Modifying
    @Query(nativeQuery = true, value = "call purge_audit()")
    void cleanAudit();
}