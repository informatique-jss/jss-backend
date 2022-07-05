package com.jss.osiris.libs.audit;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Session;
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

    private void auditEntity(Object[] previousState, Object[] currentState, Object entity,
            Serializable id, String[] propertyNames) {
        if (!entity.getClass().getName().equals(IndexEntity.class.getName())) {
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
                        audit.setDatetime(new Date());
                        audit.setEntity(entity.getClass().getSimpleName());
                        audit.setEntityId((Integer) id);
                        if (newField != null)
                            audit.setNewValue(newField.toString());
                        if (oldField != null)
                            audit.setOldValue(oldField.toString());
                        audit.setFieldName(propertyNames[i]);
                        auditRepository.save(audit);
                    }
                } else {
                    String oldCode = getCodeValue(oldField);
                    String newCode = getCodeValue(newField);
                    if (newCode != null && oldCode == null
                            || newCode == null && oldCode != null
                            || (newCode != null && !newCode.equals(oldCode))) {
                        Audit audit = new Audit();
                        audit.setUsername(activeDirectoryHelper.getCurrentUsername());
                        audit.setDatetime(new Date());
                        audit.setEntity(entity.getClass().getSimpleName());
                        audit.setEntityId((Integer) id);
                        if (newCode != null)
                            audit.setNewValue(newCode.toString());
                        if (oldCode != null)
                            audit.setOldValue(oldCode.toString());
                        audit.setFieldName(propertyNames[i]);
                        auditRepository.save(audit);
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