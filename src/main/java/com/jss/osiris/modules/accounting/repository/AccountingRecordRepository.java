package com.jss.osiris.modules.accounting.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingAccountClass;
import com.jss.osiris.modules.accounting.model.AccountingJournal;
import com.jss.osiris.modules.accounting.model.AccountingRecord;

public interface AccountingRecordRepository extends CrudRepository<AccountingRecord, Integer> {

        List<AccountingRecord> findByAccountingJournalAndIsTemporary(AccountingJournal accountingJournal,
                        boolean isTemporary);

        @Query("select max(operationId) from AccountingRecord where  accountingJournal=:accountingJournal and operationDateTime>=:minOperationDateTime")
        Integer findMaxIdOperationForAccontingJournalAndMinOperationDateTime(
                        @Param("accountingJournal") AccountingJournal accountingJournal,
                        @Param("minOperationDateTime") LocalDateTime minOperationDateTime);

        @Query("select a from AccountingRecord a where " +
                        "(a.accountingAccount=:accountingAccount or :accountingAccount is null ) and "
                        +
                        "(a.accountingJournal=:accountingJournal or :accountingJournal is null ) and "
                        +
                        "(a.accountingDateTime is null or (a.accountingDateTime >=:startDate and a.accountingDateTime <=:endDate )) and "
                        +
                        "(:accountingClass is null or a.accountingAccount.accountingAccountClass = :accountingClass ) ")
        List<AccountingRecord> searchAccountingRecords(
                        @Param("accountingClass") AccountingAccountClass accountingClass,
                        @Param("accountingAccount") AccountingAccount accountingAccount,
                        @Param("accountingJournal") AccountingJournal accountingJournal,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);
}

;