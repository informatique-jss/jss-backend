package com.jss.osiris.modules.osiris.accounting.repository;

import org.springframework.data.jpa.repository.QueryHints;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.accounting.model.PrincipalAccountingAccount;

import jakarta.persistence.QueryHint;

public interface PrincipalAccountingAccountRepository
        extends QueryCacheCrudRepository<PrincipalAccountingAccount, Integer> {

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    PrincipalAccountingAccount findByCode(String code);
}