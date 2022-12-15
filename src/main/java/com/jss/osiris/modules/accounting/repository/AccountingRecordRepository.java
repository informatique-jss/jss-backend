package com.jss.osiris.modules.accounting.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingBalance;
import com.jss.osiris.modules.accounting.model.AccountingBalanceBilan;
import com.jss.osiris.modules.accounting.model.AccountingJournal;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.accounting.model.AccountingRecordSearchResult;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.quotation.model.CustomerOrder;

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

        @Query("select max(letteringNumber) from AccountingRecord where letteringDateTime>=:minLetteringDateTime")
        Integer findMaxLetteringNumberForMinLetteringDateTime(
                        @Param("minLetteringDateTime") LocalDateTime minLetteringDateTime);

        @Query(nativeQuery = true, value = "" +
                        " select  " +
                        " r.operation_id as operationId, " +
                        " r.accounting_date_time as accountingDateTime, " +
                        " r.operation_date_time as operationDateTime, " +
                        " j.label as accountingJournalLabel, " +
                        " j.code as accountingJournalCode, " +
                        " a.accounting_account_number as accountingAccountNumber, " +
                        " a.accounting_account_sub_number as accountingAccountSubNumber, " +
                        " a.label as accountingAccountLabel, " +
                        " r.manual_accounting_document_number as manualAccountingDocumentNumber, " +
                        " r.manual_accounting_document_date as manualAccountingDocumentDate, " +
                        " r.debit_amount as debitAmount, " +
                        " r.credit_amount as creditAmount, " +
                        " r.label as label, " +
                        " r.lettering_number as letteringNumber, " +
                        " r.lettering_date_time as letteringDate, " +
                        " r.id_invoice as invoiceId, " +
                        " r.id_customer_order	 as customerId, " +
                        " r.id_payment as paymentId, " +
                        " r2.operation_id as contrePasseOperationId, " +
                        " (select STRING_AGG( case when af.denomination is not null and af.denomination!='' then af.denomination else af.firstname || ' '||af.lastname end,', ' order by 1) as affaireLabel from asso_affaire_order asso join affaire af on af.id = asso.id_affaire where  asso.id_customer_order = i.customer_order_id or asso.id_customer_order = r.id_customer_order)  as affaireLabel,"
                        +
                        " COALESCE(re1.firstname || ' ' || re1.lastname ,re2.firstname || ' ' || re2.lastname ) as responsable, "
                        +
                        " r.id_deposit as depositId " +
                        " from accounting_record r " +
                        " join accounting_journal j on j.id = r.id_accounting_journal " +
                        " join accounting_account a on a.id = r.id_accounting_account " +
                        " left join tiers t on (t.id_accounting_account_customer = r.id_accounting_account  or t.id_accounting_account_deposit=r.id_accounting_account) "
                        +
                        " left join accounting_record r2 on r2.id = r.id_contre_passe " +
                        " left join invoice i on i.id = r.id_invoice " +
                        " left join customer_order co on co.id = r.id_customer_order " +
                        " left join responsable re1 on re1.id = i.id_responsable " +
                        " left join responsable re2 on r2.id = co.id_responsable " +
                        " where ( COALESCE(:accountingAccountIds) =0 or r.id_accounting_account in (:accountingAccountIds)) "
                        +
                        " and (:journalId =0 or r.id_accounting_journal = :journalId) " +
                        " and (:responsableId =0 or COALESCE(re1.id ,re2.id )  = :responsableId and t.id is not null) "
                        +
                        " and (:tiersId =0 or COALESCE(i.id_tiers ,co.id_tiers )  = :tiersId and t.id is not null) " +
                        " and (:hideLettered = false or r.lettering_date is null ) " +
                        " and r.operation_date_time>=:startDate and r.operation_date_time<=:endDate  " +
                        " and (:accountingClassId =0 or a.id_accounting_account_class = :accountingClassId) ")
        List<AccountingRecordSearchResult> searchAccountingRecords(
                        @Param("accountingAccountIds") List<Integer> accountingAccountIds,
                        @Param("accountingClassId") Integer accountingClassId,
                        @Param("journalId") Integer journalId,
                        @Param("responsableId") Integer responsableId,
                        @Param("tiersId") Integer tiersId,
                        @Param("hideLettered") Boolean hideLettered,
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

        List<AccountingRecord> findByAccountingAccountAndInvoice(AccountingAccount accountingAccountCustomer,
                        Invoice invoice);

        List<AccountingRecord> findByInvoice(Invoice invoice);

        List<AccountingRecord> findByCustomerOrder(CustomerOrder customerOrder);

        List<AccountingRecord> findByTemporaryOperationId(Integer operationId);

        List<AccountingRecord> findByOperationId(Integer operationId);
}

;