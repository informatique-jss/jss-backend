package com.jss.osiris.modules.accounting.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.accounting.model.AccountingJournal;
import com.jss.osiris.modules.accounting.model.AccountingRecord;

public interface AccountingRecordRepository extends CrudRepository<AccountingRecord, Integer> {

    List<AccountingRecord> findByAccountingJournalAndIsTemporary(AccountingJournal accountingJournal,
            boolean isTemporary);

    @Query("select max(operationId) from AccountingRecord where  accountingJournal=:accountingJournal and operationDateTime>=:minOperationDateTime")
    Integer findMaxIdOperationForAccontingJournalAndMinOperationDateTime(
            @Param("accountingJournal") AccountingJournal accountingJournal,
            @Param("minOperationDateTime") LocalDateTime minOperationDateTime);
}