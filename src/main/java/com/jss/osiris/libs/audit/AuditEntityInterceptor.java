package com.jss.osiris.libs.audit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.audit.model.Audit;
import com.jss.osiris.libs.audit.service.AuditService;
import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.node.model.Node;
import com.jss.osiris.libs.search.model.DoNotAudit;
import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.libs.search.repository.IndexEntityRepository;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Service
public class AuditEntityInterceptor implements Interceptor {

    Session session;

    private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

    @Autowired
    @Lazy
    IndexEntityRepository indexEntityRepository;

    @Autowired
    @Lazy
    ActiveDirectoryHelper activeDirectoryHelper;

    @Autowired
    @Lazy
    AuditService auditService;

    @Override
    public boolean onFlushDirty(
            Object entity,
            Object id,
            Object[] currentState,
            Object[] previousState,
            String[] propertyNames,
            Type[] types) {
        this.auditEntity(previousState, currentState, entity, id, propertyNames);
        return Interceptor.super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    }

    @Override
    public boolean onSave(
            Object entity,
            Object id,
            Object[] state,
            String[] propertyNames,
            Type[] types) {
        if (!entity.getClass().getName().equals(IndexEntity.class.getName())
                && !entity.getClass().getName().equals(Batch.class.getName())
                && !entity.getClass().getName().equals(Audit.class.getName())
                && id instanceof Integer && isAuditAuthorized(entity)) {
            Audit audit = new Audit();
            audit.setUsername(activeDirectoryHelper.getCurrentUsername());
            audit.setDatetime(LocalDateTime.now());
            audit.setEntity(entity.getClass().getSimpleName());
            audit.setEntityId((Integer) id);
            audit.setNewValue(((Integer) id) + "");
            audit.setFieldName("id");
            auditService.addOrUpdateAudit(audit);
        }
        return Interceptor.super.onSave(entity, id, state, propertyNames, types);
    }

    private void auditEntity(Object[] previousState, Object[] currentState, Object entity,
            Object id, String[] propertyNames) {
        if (!entity.getClass().getName().equals(IndexEntity.class.getName())
                && !entity.getClass().getName().equals(Batch.class.getName())
                && !entity.getClass().getName().equals(Node.class.getName())
                && id instanceof Integer && isAuditAuthorized(entity)) {
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
                        auditService.addOrUpdateAudit(audit);
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
                        auditService.addOrUpdateAudit(audit);
                    } else if (oldField != null && oldField instanceof IId
                            || newField != null && newField instanceof IId) {
                        Integer newId = null;
                        Integer oldId = null;
                        if (newField != null)
                            newId = ((IId) newField).getId();
                        if (oldField != null)
                            oldId = ((IId) oldField).getId();
                        if (newId != null && oldId == null
                                || newId == null && oldId != null
                                || (newId != null && !newId.equals(oldId))) {
                            Audit audit = new Audit();
                            audit.setUsername(activeDirectoryHelper.getCurrentUsername());
                            audit.setDatetime(LocalDateTime.now());
                            audit.setEntity(entity.getClass().getSimpleName());
                            audit.setEntityId((Integer) id);
                            if (newId != null)
                                audit.setNewValue(newId.toString());
                            if (oldId != null)
                                audit.setOldValue(oldId.toString());
                            audit.setFieldName(propertyNames[i]);
                            auditService.addOrUpdateAudit(audit);
                        }
                    }
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

    private boolean isAuditAuthorized(Object entity) {
        if (entity != null) {
            for (Annotation annotation : entity.getClass().getAnnotations()) {
                if (annotation.annotationType().getName().equals(DoNotAudit.class.getName()))
                    return false;
            }
        }
        return true;
    }
}