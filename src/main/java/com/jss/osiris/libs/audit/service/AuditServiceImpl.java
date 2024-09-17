package com.jss.osiris.libs.audit.service;

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

}