package com.jss.osiris.modules.osiris.miscellaneous.repository;

import org.springframework.data.jpa.repository.QueryHints;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.miscellaneous.model.PaymentType;

import jakarta.persistence.QueryHint;

public interface PaymentTypeRepository extends QueryCacheCrudRepository<PaymentType, Integer> {

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    PaymentType findByCodeInpi(String codeInpi);
}