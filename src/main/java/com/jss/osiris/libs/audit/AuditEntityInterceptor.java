package com.jss.osiris.libs.audit;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.audit.model.Audit;
import com.jss.osiris.libs.audit.repository.AuditRepository;
import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.libs.search.repository.IndexEntityRepository;

@Service
public class AuditEntityInterceptor extends EmptyInterceptor {

    Session session;

    @Autowired
    @Lazy
    AuditRepository auditRepository;

    private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

    @Autowired
    @Lazy
    IndexEntityRepository indexEntityRepository;

    @Autowired
    @Lazy
    ActiveDirectoryHelper activeDirectoryHelper;

    private HashSet<Audit> auditToSave = new HashSet<Audit>();

    @Override
    public boolean onFlushDirty(
            Object entity,
            Serializable id,
            Object[] currentState,
            Object[] previousState,
            String[] propertyNames,
            Type[] types) {
        this.auditEntity(previousState, currentState, entity, id, propertyNames);
        return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    }

    @Override
    public boolean onSave(
            Object entity,
            Serializable id,
            Object[] state,
            String[] propertyNames,
            Type[] types) {
        if (!entity.getClass().getName().equals(IndexEntity.class.getName())
                && !entity.getClass().getName().equals(Audit.class.getName())) {
            Audit audit = new Audit();
            audit.setUsername(activeDirectoryHelper.getCurrentUsername());
            audit.setDatetime(LocalDateTime.now());
            audit.setEntity(entity.getClass().getSimpleName());
            audit.setEntityId((Integer) id);
            audit.setNewValue(((Integer) id) + "");
            audit.setFieldName("id");
            addToAuditToSave(audit);
        }
        return super.onSave(entity, id, state, propertyNames, types);
    }

    private void auditEntity(Object[] previousState, Object[] currentState, Object entity,
            Serializable id, String[] propertyNames) {
        if (!entity.getClass().getName().equals(IndexEntity.class.getName()) && id instanceof Integer) {
            for (int i = 0; i < previousState.length; i++) {
                Object oldField = previousState[i];
                Object newField = currentState[i];
                if (oldField != null && WRAPPER_TYPES.contains(oldField.getClass())
                        || newField != null && WRAPPER_TYPES.contains(newField.getClass())) {
                    if (newField != null && oldField == null
                            || newField == null && oldField != null
                            || (newField != null && !newField.equals(oldField))) {
                        Audit audit = new Audit();
                        audit.setUsername(activeDirectoryHelper.getCurrentUsername());
                        audit.setDatetime(LocalDateTime.now());
                        audit.setEntity(entity.getClass().getSimpleName());
                        audit.setEntityId((Integer) id);
                        if (newField != null)
                            audit.setNewValue(newField.toString());
                        if (oldField != null)
                            audit.setOldValue(oldField.toString());
                        audit.setFieldName(propertyNames[i]);
                        addToAuditToSave(audit);
                    }
                } else {
                    String oldCode = getCodeValue(oldField);
                    String newCode = getCodeValue(newField);
                    if (newCode != null && oldCode == null
                            || newCode == null && oldCode != null
                            || (newCode != null && !newCode.equals(oldCode))) {
                        Audit audit = new Audit();
                        audit.setUsername(activeDirectoryHelper.getCurrentUsername());
                        audit.setDatetime(LocalDateTime.now());
                        audit.setEntity(entity.getClass().getSimpleName());
                        audit.setEntityId((Integer) id);
                        if (newCode != null)
                            audit.setNewValue(newCode.toString());
                        if (oldCode != null)
                            audit.setOldValue(oldCode.toString());
                        audit.setFieldName(propertyNames[i]);
                        addToAuditToSave(audit);
                    }
                }
            }
        }
    }

    private void addToAuditToSave(Audit auditToAdd) {
        synchronized (auditToSave) {
            boolean found = false;
            if (auditToSave != null && auditToSave.size() > 0) {
                for (Audit audit : auditToSave) {
                    if (audit.getEntity().equals(auditToAdd.getEntity())
                            && audit.getEntityId().equals(auditToAdd.getEntityId()) &&
                            audit.getFieldName().equals(auditToAdd.getFieldName()))
                        if (audit.getNewValue() == null && auditToAdd.getNewValue() == null
                                || audit.getNewValue() != null && auditToAdd.getNewValue() != null
                                        && audit.getNewValue().equals(auditToAdd.getNewValue()))
                            if (audit.getOldValue() == null && auditToAdd.getOldValue() == null
                                    || audit.getOldValue() != null && auditToAdd.getOldValue() != null
                                            && audit.getOldValue().equals(auditToAdd.getOldValue())) {
                                found = true;
                                break;
                            }
                }
            }

            if (!found)
                auditToSave.add(auditToAdd);
        }
    }

    @Override
    public void afterTransactionCompletion(Transaction tx) {
        synchronized (auditToSave) {
            HashSet<Audit> auditsToDelete = new HashSet<Audit>();
            if (auditToSave != null && auditToSave.size() > 0) {
                if (tx != null) {
                    if (!tx.getStatus().equals(TransactionStatus.ACTIVE)
                            && !tx.getStatus().equals(TransactionStatus.MARKED_ROLLBACK))
                        tx.begin();
                    try {
                        for (Audit audit : auditToSave) {
                            auditsToDelete.add(audit);
                            auditRepository.save(audit);
                        }
                    } catch (Exception e) {
                        tx.rollback();
                        return;
                    }
                    auditToSave.removeAll(auditsToDelete);
                    tx.commit();
                }
            }
        }
    }

    private String getCodeValue(Object entity) {
        if (entity != null) {
            Method m = null;
            try {
                m = entity.getClass().getDeclaredMethod("getCode");
            } catch (NoSuchMethodException e) {
                // Not a referential
            }

            if (m != null)
                try {
                    return m.invoke(entity).toString();
                } catch (Exception e) {
                }
        }
        return null;
    }

    private static Set<Class<?>> getWrapperTypes() {
        Set<Class<?>> ret = new HashSet<Class<?>>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        ret.add(String.class);
        return ret;
    }
}