package com.jss.jssbackend.libs.audit;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Session;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class AuditEntityInterceptor extends EmptyInterceptor {

    Session session;

    @Autowired
    @Lazy
    AuditRepository auditRepository;

    public void setSession(Session session) {
        this.session = session;
    }

    private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

    public boolean onFlushDirty(Object entity, Serializable id,
            Object[] currentState, Object[] previousState,
            String[] propertyNames, Type[] types)
            throws CallbackException {

        for (int i = 0; i < previousState.length; i++) {
            Object oldField = previousState[i];
            if (oldField != null && WRAPPER_TYPES.contains(oldField.getClass())) {
                Object newField = currentState[i];
                if (newField != null && oldField == null
                        || newField == null && oldField != null
                        || (newField != null && !newField.equals(oldField))) {
                    Audit audit = new Audit();
                    audit.setAuthor("toto"); // TODO : change IT when AD connected
                    audit.setDatetime(new Date());
                    audit.setEntity(entity.getClass().getSimpleName());
                    audit.setEntityId((Integer) id);
                    audit.setNewValue(newField.toString());
                    audit.setOldValue(oldField.toString());
                    auditRepository.save(audit);
                }

            }
        }
        return false;
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