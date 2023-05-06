package com.jss.osiris.modules.quotation.repository;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.quotation.model.AssignationType;

public interface AssignationTypeRepository extends QueryCacheCrudRepository<AssignationType, Integer> {

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    AssignationType findByCode(String code);
}