package com.jss.jssbackend.libs.audit.repository;

import java.util.List;

import com.jss.jssbackend.libs.audit.model.Audit;

import org.springframework.data.repository.CrudRepository;

public interface AuditRepository extends CrudRepository<Audit, Integer> {

    List<Audit> findByEntityAndEntityId(String entityType, Integer entityId);
}