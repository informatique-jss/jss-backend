package com.jss.jssbackend.libs.audit.service;

import java.util.List;

import com.jss.jssbackend.libs.audit.model.Audit;

public interface AuditService {
    public List<Audit> getAuditForEntity(String entityType, Integer entityId);
}
