package com.jss.osiris.modules.osiris.accounting.repository;

import java.time.LocalDate;
import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.accounting.model.SageRecord;

public interface SageRecordRepository extends QueryCacheCrudRepository<SageRecord, Integer> {
    List<SageRecord> findByTargetAccountingAccountCodeAndOperationDate(String targetAccountingAccountCode,
            LocalDate operationDate);
}
