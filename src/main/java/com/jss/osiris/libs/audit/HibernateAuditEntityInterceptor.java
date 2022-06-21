package com.jss.osiris.libs.audit;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

@Component
public class HibernateAuditEntityInterceptor implements HibernatePropertiesCustomizer {

    @Autowired
    AuditEntityInterceptor auditEntityInterceptor;

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put("hibernate.session_factory.interceptor", auditEntityInterceptor);
    }
}