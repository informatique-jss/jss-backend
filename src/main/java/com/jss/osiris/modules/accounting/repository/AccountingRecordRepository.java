package com.jss.osiris.modules.accounting.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingAccountClass;
import com.jss.osiris.modules.accounting.model.AccountingBalance;
import com.jss.osiris.modules.accounting.model.AccountingBalanceBilan;
import com.jss.osiris.modules.accounting.model.AccountingJournal;
import com.jss.osiris.modules.accounting.model.AccountingRecord;

public interface AccountingRecordRepository extends CrudRepository<AccountingRecord, Integer> {

        List<AccountingRecord> findByAccountingJournalAndIsTemporary(AccountingJournal accountingJournal,
                        boolean isTemporary);

        @Query("select max(accountingId) from AccountingRecord where  accountingJournal=:accountingJournal and operationDateTime>=:minOperationDateTime")
        Integer findMaxIdAccountingForAccontingJournalAndMinOperationDateTime(
                        @Param("accountingJournal") AccountingJournal accountingJournal,
                        @Param("minOperationDateTime") LocalDateTime minOperationDateTime);

        @Query("select max(operationId) from AccountingRecord where operationDateTime>=:minOperationDateTime")
        Integer findMaxIdOperationForMinOperationDateTime(
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

        @Query(nativeQuery = true, value = "select" + "        sum(case "
                        + "            when record.isanouveau=false then record.credit_amount "
                        + "        end) as creditAmount," + "        sum(case "
                        + "            when record.isanouveau=false then record.debit_amount "
                        + "        end) as debitAmount," + "		accounting.label as accountingAccountLabel,"
                        + "		accounting.accounting_account_number accountingAccountNumber,"
                        + "		accounting.accounting_account_sub_number as accountingAccountSubNumber,"
                        + "		sum(case when substring(accounting.accounting_account_number,1,1) in ('6','7') and i.due_date>now() and i.due_date<= (now() +INTERVAL '30 day') then i.total_price  end) as echoir30, "
                        + "		sum(case when substring(accounting.accounting_account_number,1,1) in ('6','7') and i.due_date>now() and i.due_date<= (now() +INTERVAL '60 day') and i.due_date> (now() +INTERVAL '30 day')  then i.total_price  end) as echoir60, "
                        + "		sum(case when substring(accounting.accounting_account_number,1,1) in ('6','7') and  i.due_date>now() and i.due_date> (now() +INTERVAL '60 day')  then i.total_price  end) as echoir90, "
                        + "		sum(case when substring(accounting.accounting_account_number,1,1) in ('6','7') and  i.due_date<=now() and i.due_date>= (now() -INTERVAL '30 day')  then i.total_price  end) as echu30, "
                        + "		sum(case when substring(accounting.accounting_account_number,1,1) in ('6','7') and  i.due_date<=now() and i.due_date>= (now() -INTERVAL '60 day') and i.due_date< (now() -INTERVAL '30 day')  then i.total_price  end) as echu60, "
                        + "		sum(case when substring(accounting.accounting_account_number,1,1) in ('6','7') and  i.due_date<=now() and i.due_date< (now() -INTERVAL '60 day')  then i.total_price  end) as echu90 "
                        + "    from" + "        accounting_record record " + "    inner join"
                        + "        accounting_account accounting "
                        + "            on record.id_accounting_account=accounting.id "
                        + "			left join invoice i on i.id = record.id_invoice"
                        + " where (accounting.id=:accountingAccountId or :accountingAccountId =0 ) and "
                        +
                        "(accounting.accounting_account_number=:accountingAccountNumber or :accountingAccountNumber ='' ) and "
                        +
                        "(record.accounting_date_time is null or (record.accounting_date_time >=:startDate and record.accounting_date_time <=:endDate )) and "
                        +
                        "(:accountingClassId =0 or accounting.id_accounting_account_class = :accountingClassId ) "
                        +
                        " group by accounting.id ")
        List<AccountingBalance> searchAccountingBalance(
                        @Param("accountingClassId") Integer accountingClassId,
                        @Param("accountingAccountId") Integer accountingAccountId,
                        @Param("accountingAccountNumber") String accountingAccountNumber,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query(nativeQuery = true, value = "select" + "        sum(case "
                        + "            when record.isanouveau=false then record.credit_amount "
                        + "        end) as creditAmount," + "        sum(case "
                        + "            when record.isanouveau=false then record.debit_amount "
                        + "        end) as debitAmount," + "	 "
                        + "		accounting.accounting_account_number accountingAccountNumber,"
                        + "		sum(case when substring(accounting.accounting_account_number,1,1) in ('6','7') and i.due_date>now() and i.due_date<= (now() +INTERVAL '30 day') then i.total_price  end) as echoir30, "
                        + "		sum(case when substring(accounting.accounting_account_number,1,1) in ('6','7') and i.due_date>now() and i.due_date<= (now() +INTERVAL '60 day') and i.due_date> (now() +INTERVAL '30 day')  then i.total_price  end) as echoir60, "
                        + "		sum(case when substring(accounting.accounting_account_number,1,1) in ('6','7') and  i.due_date>now() and i.due_date> (now() +INTERVAL '60 day')  then i.total_price  end) as echoir90, "
                        + "		sum(case when substring(accounting.accounting_account_number,1,1) in ('6','7') and  i.due_date<=now() and i.due_date>= (now() -INTERVAL '30 day')  then i.total_price  end) as echu30, "
                        + "		sum(case when substring(accounting.accounting_account_number,1,1) in ('6','7') and  i.due_date<=now() and i.due_date>= (now() -INTERVAL '60 day') and i.due_date< (now() -INTERVAL '30 day')  then i.total_price  end) as echu60, "
                        + "		sum(case when substring(accounting.accounting_account_number,1,1) in ('6','7') and  i.due_date<=now() and i.due_date< (now() -INTERVAL '60 day')  then i.total_price  end) as echu90 "
                        + "    from" + "        accounting_record record " + "    inner join"
                        + "        accounting_account accounting "
                        + "            on record.id_accounting_account=accounting.id "
                        + "			left join invoice i on i.id = record.id_invoice"
                        + " where (accounting.id=:accountingAccountId or :accountingAccountId =0 ) and "
                        +
                        "(accounting.accounting_account_number=:accountingAccountNumber or :accountingAccountNumber ='' ) and "
                        +
                        "(record.accounting_date_time is null or (record.accounting_date_time >=:startDate and record.accounting_date_time <=:endDate )) and "
                        +
                        "(:accountingClassId =0 or accounting.id_accounting_account_class = :accountingClassId ) "
                        +
                        " group by accounting.accounting_account_number ")
        List<AccountingBalance> searchAccountingBalanceGenerale(
                        @Param("accountingClassId") Integer accountingClassId,
                        @Param("accountingAccountId") Integer accountingAccountId,
                        @Param("accountingAccountNumber") String accountingAccountNumber,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("select sum(a.creditAmount) as creditAmount, " +
                        " sum(a.debitAmount) as debitAmount, substring(CONCAT(aa.accountingAccountNumber,aa.accountingAccountSubNumber),1,4) as accountingAccountNumber"
                        +
                        " from AccountingRecord a  JOIN a.accountingAccount aa  where " +
                        "(a.accountingDateTime is null or (a.accountingDateTime >=:startDate and a.accountingDateTime <=:endDate ))   "
                        +
                        " group by substring(CONCAT(aa.accountingAccountNumber,aa.accountingAccountSubNumber),1,4) ")
        List<AccountingBalanceBilan> getAccountingRecordAggregateByAccountingNumber(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);
}

;