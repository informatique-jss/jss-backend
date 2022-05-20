package com.jss.jssbackend.libs.audit;

import org.springframework.data.repository.CrudRepository;

public interface AuditRepository extends CrudRepository<Audit, Integer> {
}