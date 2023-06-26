package com.jss.osiris.libs.audit.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.libs.audit.model.Audit;

public interface AuditRepository extends CrudRepository<Audit, Integer> {

    List<Audit> findTop100ByEntityAndEntityId(String entityType, Integer entityId); // TODO : remove Top100 when
                                                                                    // multiple bug corrected
}