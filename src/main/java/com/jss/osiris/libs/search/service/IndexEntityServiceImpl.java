package com.jss.osiris.libs.search.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module.Feature;
import com.jss.osiris.libs.JacksonLocalDateSerializer;
import com.jss.osiris.libs.JacksonLocalDateTimeSerializer;
import com.jss.osiris.libs.audit.model.Audit;
import com.jss.osiris.libs.audit.service.AuditService;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.libs.search.repository.IndexEntityRepository;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.profile.service.EmployeeService;

@Service
public class IndexEntityServiceImpl implements IndexEntityService {

    @Autowired
    IndexEntityRepository indexEntityRepository;

    @Autowired
    AuditService auditService;

    @Autowired
    EmployeeService employeeService;

    @Override
    public void indexEntity(IId entity) {
        IndexEntity indexedEntity = new IndexEntity();
        if (entity.getClass().getSimpleName().contains("HibernateProxy"))
            entity = (IId) Hibernate.unproxy(entity);

        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule("SimpleModule");
        simpleModule.addSerializer(LocalDateTime.class, new JacksonLocalDateTimeSerializer());
        simpleModule.addSerializer(LocalDate.class, new JacksonLocalDateSerializer());
        objectMapper.registerModule(simpleModule);
        Hibernate5Module module = new Hibernate5Module();
        module.enable(Feature.FORCE_LAZY_LOADING);
        objectMapper.registerModule(module);

        indexedEntity.setEntityId(entity.getId());
        indexedEntity.setEntityType(entity.getClass().getSimpleName());
        try {
            indexedEntity.setText(objectMapper.writeValueAsString(cleanObjectForSerialization(entity)));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // Set creator / updator
        List<Audit> auditEntries = auditService.getAuditForEntity(entity.getClass().getSimpleName(), entity.getId());
        if (auditEntries != null && auditEntries.size() > 0) {
            Audit lastEntry = null;
            for (Audit auditEntry : auditEntries) {
                if (auditEntry.getFieldName().equals("id")) {
                    if (auditEntry.getUsername() != null) {
                        indexedEntity.setCreatedBy(employeeService.getEmployeeByUsername(auditEntry.getUsername()));
                        indexedEntity.setCreatedDate(auditEntry.getDatetime());
                    }
                } else {
                    if (lastEntry == null || lastEntry.getDatetime().isBefore(auditEntry.getDatetime()))
                        if (auditEntry.getUsername() != null)
                            lastEntry = auditEntry;
                }
            }
            if (lastEntry != null) {
                indexedEntity.setUpdatedBy(employeeService.getEmployeeByUsername(lastEntry.getUsername()));
                indexedEntity.setUdpatedDate(lastEntry.getDatetime());
            }
        }

        indexEntityRepository.save(indexedEntity);

    }

    @SuppressWarnings({ "unchecked" })
    private Object cleanObjectForSerialization(Object entity) {
        Map<String, Object> outObject = new HashMap<String, Object>();
        for (Field field : entity.getClass().getDeclaredFields()) {
            for (Annotation annotation : field.getAnnotations()) {
                if (annotation.annotationType().getName().equals(IndexedField.class.getName())) {
                    Method getter;
                    try {
                        getter = entity.getClass()
                                .getMethod("get" + StringUtils.capitalize(field.getName()));

                        Object fieldResult = getter.invoke(entity);

                        if (fieldResult != null && fieldResult.getClass().getSimpleName().contains("HibernateProxy"))
                            fieldResult = Hibernate.unproxy(fieldResult);

                        if (fieldResult instanceof String || fieldResult instanceof Integer
                                || fieldResult instanceof LocalDate || fieldResult instanceof LocalDateTime
                                || fieldResult instanceof Boolean || fieldResult instanceof Float) {
                            outObject.put(field.getName(), getter.invoke(entity));
                        } else if (fieldResult instanceof List) {
                            ArrayList<Object> cleanOutList = new ArrayList<Object>();
                            for (Object fieldResultObject : (List<Object>) fieldResult) {
                                if (fieldResultObject != null
                                        && fieldResultObject.getClass().getSimpleName().contains("HibernateProxy"))
                                    fieldResultObject = Hibernate.unproxy(fieldResultObject);
                                cleanOutList.add(cleanObjectForSerialization(fieldResultObject));
                            }
                            outObject.put(field.getName(), cleanOutList);
                        } else if (fieldResult != null) {
                            outObject.put(field.getName(), cleanObjectForSerialization(fieldResult));
                        }
                    } catch (NoSuchMethodException e) {
                        new OsirisException(e, "Indexation : getter not found for field " + field.getName());
                    } catch (SecurityException e) {
                        new OsirisException(e, "Indexation error");
                    } catch (IllegalAccessException e) {
                        new OsirisException(e, "Indexation error");
                    } catch (InvocationTargetException e) {
                        new OsirisException(e, "Indexation error");
                    }
                }
            }
        }
        return outObject;
    }
}