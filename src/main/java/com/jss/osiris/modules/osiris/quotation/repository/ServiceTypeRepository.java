package com.jss.osiris.modules.osiris.quotation.repository;

import org.springframework.data.jpa.repository.QueryHints;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.ServiceType;

import jakarta.persistence.QueryHint;

public interface ServiceTypeRepository extends QueryCacheCrudRepository<ServiceType, Integer> {
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    ServiceType findByCode(String code);
}