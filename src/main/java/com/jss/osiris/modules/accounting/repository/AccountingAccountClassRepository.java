package com.jss.osiris.modules.accounting.repository;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.accounting.model.AccountingAccountClass;

public interface AccountingAccountClassRepository extends QueryCacheCrudRepository<AccountingAccountClass, Integer> {

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    AccountingAccountClass findByCode(String code);
}