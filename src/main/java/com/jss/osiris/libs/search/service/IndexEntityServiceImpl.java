package com.jss.osiris.libs.search.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.libs.search.repository.IndexEntityRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class IndexEntityServiceImpl implements IndexEntityService {

    @Autowired
    IndexEntityRepository indexEntityRepository;

    @Override
    @Async
    public void indexEntity(Object entity, Integer entityId) {
        IndexEntity indexedEntity = new IndexEntity();
        Map<String, Object> indexMap = new HashMap<String, Object>();
        ObjectMapper objectMapper = new ObjectMapper();

        for (Field field : entity.getClass().getDeclaredFields()) {
            for (Annotation annotation : field.getAnnotations()) {
                if (annotation.annotationType().getName().equals(IndexedField.class.getName())) {
                    Method getter;
                    try {
                        getter = entity.getClass()
                                .getMethod("get" + StringUtils.capitalize(field.getName()));
                        indexMap.put(field.getName(), getter.invoke(entity));
                    } catch (NoSuchMethodException e) {
                        System.out.println("Indexation : getter not found for field " + field.getName());
                        e.printStackTrace();
                    } catch (SecurityException e) {
                        System.out.println("Indexation error");
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        System.out.println("Indexation error");
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        System.out.println("Indexation error");
                        e.printStackTrace();
                    }
                }
            }
            indexedEntity.setEntityId(entityId);
            indexedEntity.setEntityType(entity.getClass().getSimpleName());
            try {
                indexedEntity.setText(objectMapper.writeValueAsString(indexMap));
            } catch (JsonProcessingException e) {
                System.out.println("Indexation error during JSON generation");
                e.printStackTrace();
            }

            indexEntityRepository.save(indexedEntity);
        }
    }

}