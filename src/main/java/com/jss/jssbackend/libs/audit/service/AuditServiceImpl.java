package com.jss.jssbackend.libs.audit.service;

import java.util.List;

import com.jss.jssbackend.libs.audit.model.Audit;
import com.jss.jssbackend.libs.audit.repository.AuditRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditServiceImpl implements AuditService {

    @Autowired
    AuditRepository auditRepository;

    @Override
    public List<Audit> getAuditForEntity(String entityType, Integer entityId) {
        return auditRepository.findByEntityAndEntityId(entityType, entityId);
    }

}