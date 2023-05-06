package com.jss.osiris.modules.accounting.repository;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.accounting.model.AccountingJournal;

public interface AccountingJournalRepository extends QueryCacheCrudRepository<AccountingJournal, Integer> {

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    AccountingJournal findByCode(String code);
}