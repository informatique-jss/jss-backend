package com.jss.osiris.modules.quotation.repository;

import org.springframework.data.jpa.repository.QueryHints;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;

import jakarta.persistence.QueryHint;

public interface CustomerOrderStatusRepository extends QueryCacheCrudRepository<CustomerOrderStatus, Integer> {
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    CustomerOrderStatus findByCode(String code);
}