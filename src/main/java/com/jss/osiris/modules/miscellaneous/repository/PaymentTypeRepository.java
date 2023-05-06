package com.jss.osiris.modules.miscellaneous.repository;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.miscellaneous.model.PaymentType;

public interface PaymentTypeRepository extends QueryCacheCrudRepository<PaymentType, Integer> {

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    PaymentType findByCodeInpi(String codeInpi);
}