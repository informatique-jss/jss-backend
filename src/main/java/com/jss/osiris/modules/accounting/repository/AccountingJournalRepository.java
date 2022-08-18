package com.jss.osiris.modules.accounting.repository;

import com.jss.osiris.modules.accounting.model.AccountingJournal;

import org.springframework.data.repository.CrudRepository;

public interface AccountingJournalRepository extends CrudRepository<AccountingJournal, Integer> {
}