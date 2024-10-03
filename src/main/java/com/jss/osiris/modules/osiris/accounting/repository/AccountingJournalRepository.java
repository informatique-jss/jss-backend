package com.jss.osiris.modules.osiris.accounting.repository;

import org.springframework.data.jpa.repository.QueryHints;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.accounting.model.AccountingJournal;

import jakarta.persistence.QueryHint;

public interface AccountingJournalRepository extends QueryCacheCrudRepository<AccountingJournal, Integer> {

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    AccountingJournal findByCode(String code);
}