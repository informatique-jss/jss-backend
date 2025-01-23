package com.jss.osiris.modules.osiris.quotation.repository;

import org.springframework.data.jpa.repository.QueryHints;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.AssignationType;

import jakarta.persistence.QueryHint;

public interface AssignationTypeRepository extends QueryCacheCrudRepository<AssignationType, Integer> {

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    AssignationType findByCode(String code);
}