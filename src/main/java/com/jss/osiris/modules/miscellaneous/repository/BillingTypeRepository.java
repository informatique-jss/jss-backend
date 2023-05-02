package com.jss.osiris.modules.miscellaneous.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.miscellaneous.model.BillingType;

public interface BillingTypeRepository extends QueryCacheCrudRepository<BillingType, Integer> {

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    List<BillingType> findByIsDebourOrIsFee(boolean isDebour, boolean isFee);
}