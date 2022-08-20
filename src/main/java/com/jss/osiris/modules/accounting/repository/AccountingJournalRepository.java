package com.jss.osiris.modules.accounting.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.accounting.model.AccountingJournal;

public interface AccountingJournalRepository extends CrudRepository<AccountingJournal, Integer> {

    AccountingJournal findByCode(String code);
}