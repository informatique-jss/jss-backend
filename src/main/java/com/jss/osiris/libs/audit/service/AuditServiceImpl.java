package com.jss.osiris.libs.audit.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.audit.model.Audit;
import com.jss.osiris.libs.audit.repository.AuditRepository;

@Service
public class AuditServiceImpl implements AuditService {

    @Autowired
    AuditRepository auditRepository;

    @Override
    public List<Audit> getAuditForEntity(String entityType, Integer entityId) {
        return auditRepository.findByEntityAndEntityId(entityType, entityId);
    }

    @Override
    public List<Audit> getAuditForEntityAndFieldName(String entityType, Integer entityId, String status,
            String fieldName) {
        return auditRepository.findByEntityAndEntityIdAndFieldNameAndValue(entityType, entityId, status, fieldName);
    }

    @Transactional
    @Override
    public Audit addOrUpdateAudit(Audit audit) {
        return auditRepository.save(audit);
    }

    @Override
    public void cleanAudit() {
        try {
            auditRepository.cleanAudit();
        } catch (Exception e) {
            if (!e.getMessage().contains("could not extract ResultSet") || !e.getMessage().contains("ultat retourn"))
                throw e;
        }
    }

    @Override
    public LocalDateTime getCreationDateTimeForEntity(String entityType, Integer entityId) {
        List<Audit> audit = getAuditForEntity(entityType, entityId);
        if (audit != null && audit.size() > 0) {
            for (Audit auditItem : audit) {
                if (auditItem.getFieldName().equals("id"))
                    return auditItem.getDatetime();
            }
        }
        return null;
    }

}