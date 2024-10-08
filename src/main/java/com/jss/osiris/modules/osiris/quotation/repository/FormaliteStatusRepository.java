package com.jss.osiris.modules.osiris.quotation.repository;

import org.springframework.data.jpa.repository.QueryHints;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.FormaliteStatus;

import jakarta.persistence.QueryHint;

public interface FormaliteStatusRepository extends QueryCacheCrudRepository<FormaliteStatus, Integer> {

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    FormaliteStatus findByCode(String code);
}