package com.jss.osiris.libs.audit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.libs.audit.model.Audit;

public interface AuditRepository extends CrudRepository<Audit, Integer> {

    List<Audit> findByEntityAndEntityId(String entityType, Integer entityId);

    @Query("select a from Audit a where a.entity =:entityType AND a.entityId =:entityId AND a.fieldName =:fieldName AND (a.oldValue =:value OR a.newValue =:value)")
    List<Audit> findByEntityAndEntityIdAndFieldNameAndValue(String entityType, Integer entityId, String value,
            String fieldName);

    @Modifying
    @Query(nativeQuery = true, value = "call purge_audit()")
    void cleanAudit();
}