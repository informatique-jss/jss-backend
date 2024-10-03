package com.jss.osiris.modules.osiris.accounting.repository;

import org.springframework.data.jpa.repository.QueryHints;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.accounting.model.AccountingAccountClass;

import jakarta.persistence.QueryHint;

public interface AccountingAccountClassRepository extends QueryCacheCrudRepository<AccountingAccountClass, Integer> {

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    AccountingAccountClass findByCode(String code);
}