package com.jss.osiris.modules.accounting.repository;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.accounting.model.PrincipalAccountingAccount;

public interface PrincipalAccountingAccountRepository
        extends QueryCacheCrudRepository<PrincipalAccountingAccount, Integer> {

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    PrincipalAccountingAccount findByCode(String code);
}