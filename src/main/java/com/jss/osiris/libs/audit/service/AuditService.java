package com.jss.osiris.libs.audit.service;

import java.time.LocalDateTime;
import java.util.List;

import com.jss.osiris.libs.audit.model.Audit;

public interface AuditService {
    public List<Audit> getAuditForEntity(String entityType, Integer entityId);

    public Audit addOrUpdateAudit(Audit audit);

    public void cleanAudit();

    public LocalDateTime getCreationDateTimeForEntity(String entityType, Integer entityId);
}
