package com.jss.osiris.modules.accounting.repository;

import com.jss.osiris.modules.accounting.model.AccountingRecord;

import org.springframework.data.repository.CrudRepository;

public interface AccountingRecordRepository extends CrudRepository<AccountingRecord, Integer> {
}