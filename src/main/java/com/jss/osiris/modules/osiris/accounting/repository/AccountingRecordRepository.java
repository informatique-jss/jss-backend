package com.jss.osiris.modules.osiris.accounting.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.accounting.model.AccountingAccount;
import com.jss.osiris.modules.osiris.accounting.model.AccountingBalance;
import com.jss.osiris.modules.osiris.accounting.model.AccountingBalanceBilan;
import com.jss.osiris.modules.osiris.accounting.model.AccountingJournal;
import com.jss.osiris.modules.osiris.accounting.model.AccountingRecord;
import com.jss.osiris.modules.osiris.accounting.model.AccountingRecordSearchResult;
import com.jss.osiris.modules.osiris.accounting.model.AccountingVatValue;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.Refund;
import com.jss.osiris.modules.osiris.quotation.model.BankTransfert;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;

import jakarta.persistence.QueryHint;

public interface AccountingRecordRepository extends QueryCacheCrudRepository<AccountingRecord, Integer> {

        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
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
                        " with invoice_label as (select i.id, string_agg(ii.label, ', ' order by ii.id) as label " +
                        " from invoice i  " +
                        " join invoice_item ii on ii.id_invoice = i.id " +
                        " where i.id_provider is not null and i.created_date between :startDate and :endDate " +
                        " group by i.id) " +
                        " select  " +
                        " r.id as recordId, " +
                        " r.operation_id as operationId, " +
                        " r.accounting_id  as id, " +
                        " r.temporary_operation_id as temporaryOperationId, " +
                        " r.accounting_date_time as accountingDateTime, " +
                        " r.operation_date_time as operationDateTime, " +
                        " j.label as accountingJournalLabel, " +
                        " j.code as accountingJournalCode, " +
                        " pa.code as principalAccountingAccountCode, " +
                        " lpad(concat(a.accounting_account_sub_number,''),8-length(pa.code),'0') as accountingAccountSubNumber, "
                        +
                        " a.label as accountingAccountLabel, " +
                        " coalesce(r.manual_accounting_document_number,r.id_invoice||'') as manualAccountingDocumentNumber, "
                        +
                        " coalesce(r.manual_accounting_document_date, i.created_date) as manualAccountingDocumentDate, "
                        +
                        " round(cast(r.debit_amount as numeric), 2) as debitAmount, " +
                        " round(cast(r.credit_amount as numeric), 2) as creditAmount, " +
                        " concat(r.label,' ',il.label) as label, " +
                        " r.lettering_number as letteringNumber, " +
                        " r.lettering_date_time as letteringDate, " +
                        " r.id_invoice as invoiceId, " +
                        " r.id_customer_order	 as customerId, " +
                        " r.id_payment as paymentId, " +
                        " r.is_temporary as isTemporary, " +
                        " r.is_from_as400 as isFromAs400, " +
                        " r.is_manual as isManual, " +
                        " (select STRING_AGG( case when af.denomination is not null and af.denomination!='' then af.denomination else af.firstname || ' '||af.lastname end  || ' ('||city.label ||')',', ' order by 1) as affaireLabel from asso_affaire_order asso join affaire af on af.id = asso.id_affaire left join city on city.id = af.id_city where  asso.id_customer_order = i.customer_order_id or asso.id_customer_order = r.id_customer_order)  as affaireLabel,"
                        +
                        " COALESCE(re1.firstname || ' ' || re1.lastname ,re2.firstname || ' ' || re2.lastname ) as responsable "
                        +
                        " from accounting_record r " +
                        " left join accounting_journal j on j.id = r.id_accounting_journal " +
                        " join accounting_account a on a.id = r.id_accounting_account " +
                        " join principal_accounting_account pa on pa.id = a.id_principal_accounting_account " +
                        " left join tiers t on (t.id_accounting_account_customer = r.id_accounting_account  or t.id_accounting_account_deposit=r.id_accounting_account)  and t.id = :tiersId "
                        +
                        " left join invoice i on i.id = r.id_invoice left join invoice_label il on il.id = i.id " +
                        " left join provider pr on (pr.id_accounting_account_provider = r.id_accounting_account or pr.id_accounting_account_deposit = r.id_accounting_account)  and pr.id = :tiersId "
                        +
                        " left join customer_order co on co.id = r.id_customer_order " +
                        " left join responsable re1 on re1.id = i.id_responsable " +
                        " left join responsable re2 on re2.id = co.id_responsable " +
                        " where ( COALESCE(:accountingAccountIds) =0 or r.id_accounting_account in (:accountingAccountIds)) "
                        +
                        " and (:journalId =0 or r.id_accounting_journal = :journalId) " +
                        " and (:tiersId =0  or coalesce(t.id, pr.id) is not null) "
                        +
                        " and (:hideLettered = false or coalesce(r.lettering_date_time,:endDate)>=:endDate ) " +
                        " and (:isFromAs400 = false or r.is_from_as400=true ) " +
                        " and (greatest(r.operation_date_time,'2023-01-01') between :startDate and :endDate)  "
                        +
                        " and (:canViewRestricted=true or a.is_view_restricted=false)  " +
                        " and (:accountingClassId =0 or pa.id_accounting_account_class = :accountingClassId) " +
                        " " +
                        " and (:idPayment = 0 or (r.id_payment  = :idPayment or r.id_customer_order = :idCustomerOrder or r.id_invoice = :idInvoice "
                        +
                        " or r.id_refund = :idRefund or r.id_bank_transfert = :idBankTransfert ))" +
                        " order by  r.operation_date_time  limit :limit" +
                        " " +
                        " ")
        List<AccountingRecordSearchResult> searchAccountingRecordsCurrent(
                        @Param("accountingAccountIds") List<Integer> accountingAccountIds,
                        @Param("accountingClassId") Integer accountingClassId,
                        @Param("journalId") Integer journalId,
                        @Param("tiersId") Integer tiersId,
                        @Param("hideLettered") Boolean hideLettered,
                        @Param("isFromAs400") Boolean isFromAs400,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        @Param("canViewRestricted") boolean canViewRestricted,
                        @Param("idPayment") Integer idPayment,
                        @Param("idCustomerOrder") Integer idCustomerOrder,
                        @Param("idInvoice") Integer idInvoice,
                        @Param("idRefund") Integer idRefund,
                        @Param("idBankTransfert") Integer idBankTransfert,
                        @Param("limit") Integer limit);

        @Query(nativeQuery = true, value = "" +
                        " with invoice_label as (select i.id, string_agg(ii.label, ', ' order by ii.id) as label " +
                        " from invoice i  " +
                        " join invoice_item ii on ii.id_invoice = i.id " +
                        " where i.id_provider is not null and i.created_date between :startDate and :endDate " +
                        " group by i.id) " +
                        " select  " +
                        " r.id as recordId, " +
                        " r.operation_id as operationId, " +
                        " r.accounting_id  as id, " +
                        " r.temporary_operation_id as temporaryOperationId, " +
                        " r.accounting_date_time as accountingDateTime, " +
                        " r.operation_date_time as operationDateTime, " +
                        " j.label as accountingJournalLabel, " +
                        " j.code as accountingJournalCode, " +
                        " pa.code as principalAccountingAccountCode, " +
                        " lpad(concat(a.accounting_account_sub_number,''),8-length(pa.code),'0') as accountingAccountSubNumber, "
                        +
                        " a.label as accountingAccountLabel, " +
                        " coalesce(r.manual_accounting_document_number,r.id_invoice||'') as manualAccountingDocumentNumber, "
                        +
                        " coalesce(r.manual_accounting_document_date, i.created_date) as manualAccountingDocumentDate, "
                        +
                        " round(cast(r.debit_amount as numeric), 2) as debitAmount, " +
                        " round(cast(r.credit_amount as numeric), 2) as creditAmount, " +
                        " concat(r.label,' ',il.label) as label, " +
                        " r.lettering_number as letteringNumber, " +
                        " r.lettering_date_time as letteringDate, " +
                        " r.id_invoice as invoiceId, " +
                        " r.id_customer_order	 as customerId, " +
                        " r.id_payment as paymentId, " +
                        " r.is_temporary as isTemporary, " +
                        " r.is_from_as400 as isFromAs400, " +
                        " r.is_manual as isManual, " +
                        " (select STRING_AGG( case when af.denomination is not null and af.denomination!='' then af.denomination else af.firstname || ' '||af.lastname end  || ' ('||city.label ||')',', ' order by 1) as affaireLabel from asso_affaire_order asso join affaire af on af.id = asso.id_affaire left join city on city.id = af.id_city where  asso.id_customer_order = i.customer_order_id or asso.id_customer_order = r.id_customer_order)  as affaireLabel,"
                        +
                        " COALESCE(re1.firstname || ' ' || re1.lastname ,re2.firstname || ' ' || re2.lastname ) as responsable "
                        +
                        " from closed_accounting_record r " +
                        " left join accounting_journal j on j.id = r.id_accounting_journal " +
                        " join accounting_account a on a.id = r.id_accounting_account " +
                        " join principal_accounting_account pa on pa.id = a.id_principal_accounting_account " +
                        " left join tiers t on (t.id_accounting_account_customer = r.id_accounting_account  or t.id_accounting_account_deposit=r.id_accounting_account)  and t.id = :tiersId "
                        +
                        " left join invoice i on i.id = r.id_invoice left join invoice_label il on il.id = i.id " +
                        " left join provider pr on (pr.id_accounting_account_provider = r.id_accounting_account or pr.id_accounting_account_deposit = r.id_accounting_account)  and pr.id = :tiersId "
                        +
                        " left join customer_order co on co.id = r.id_customer_order " +
                        " left join responsable re1 on re1.id = i.id_responsable " +
                        " left join responsable re2 on re2.id = co.id_responsable " +
                        " where ( COALESCE(:accountingAccountIds) =0 or r.id_accounting_account in (:accountingAccountIds)) "
                        +
                        " and (:journalId =0 or r.id_accounting_journal = :journalId) " +
                        " and (:tiersId =0  or coalesce(t.id, pr.id) is not null) "
                        +
                        " and (:hideLettered = false or coalesce(r.lettering_date_time,:endDate)>=:endDate ) " +
                        " and (:isFromAs400 = false or r.is_from_as400=true ) " +
                        " and (greatest(r.operation_date_time,'2023-01-01') between :startDate  and :endDate)  "
                        +
                        " and (:canViewRestricted=true or a.is_view_restricted=false)  " +
                        " and (:accountingClassId =0 or pa.id_accounting_account_class = :accountingClassId) " +
                        " " +
                        " and (:idPayment = 0 or (r.id_payment  = :idPayment or r.id_customer_order = :idCustomerOrder or r.id_invoice = :idInvoice "
                        +
                        " or r.id_refund = :idRefund or r.id_bank_transfert = :idBankTransfert ))" +
                        " order by  r.operation_date_time  limit :limit" +
                        " " +
                        " ")
        List<AccountingRecordSearchResult> searchAccountingRecordsClosed(
                        @Param("accountingAccountIds") List<Integer> accountingAccountIds,
                        @Param("accountingClassId") Integer accountingClassId,
                        @Param("journalId") Integer journalId,
                        @Param("tiersId") Integer tiersId,
                        @Param("hideLettered") Boolean hideLettered,
                        @Param("isFromAs400") Boolean isFromAs400,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        @Param("canViewRestricted") boolean canViewRestricted,
                        @Param("idPayment") Integer idPayment,
                        @Param("idCustomerOrder") Integer idCustomerOrder,
                        @Param("idInvoice") Integer idInvoice,
                        @Param("idRefund") Integer idRefund,
                        @Param("idBankTransfert") Integer idBankTransfert,
                        @Param("limit") Integer limit);

        @Query(nativeQuery = true, value = "" +
                        "select sum(coalesce(record.credit_amount , 0)) as creditAmount,sum(coalesce(record.debit_amount , 0))  as debitAmount,"
                        + "		accounting.label as accountingAccountLabel,"
                        + "		pa.code as principalAccountingAccountCode,"
                        + "		aac.label as accountingAccountClassLabel,"
                        + "		lpad(concat(accounting_account_sub_number,''),8-length(pa.code),'0') as accountingAccountSubNumber,"
                        + "		sum(case when i.due_date>now() and i.due_date<= (now() +INTERVAL '30 day') then i.total_price  end) as echoir30, "
                        + "		sum(case when i.due_date>now() and i.due_date<= (now() +INTERVAL '60 day') and i.due_date> (now() +INTERVAL '30 day')  then i.total_price  end) as echoir60, "
                        + "		sum(case when  i.due_date>now() and i.due_date> (now() +INTERVAL '60 day')  then i.total_price  end) as echoir90, "
                        + "		sum(case when  i.due_date<=now() and i.due_date>= (now() -INTERVAL '30 day')  then i.total_price  end) as echu30, "
                        + "		sum(case when  i.due_date<=now() and i.due_date>= (now() -INTERVAL '60 day') and i.due_date< (now() -INTERVAL '30 day')  then i.total_price  end) as echu60, "
                        + "		sum(case when  i.due_date<=now() and i.due_date< (now() -INTERVAL '60 day')  then i.total_price  end) as echu90 "
                        + "    from" + "        accounting_record record " + "    inner join"
                        + "        accounting_account accounting join principal_accounting_account pa on pa.id = accounting.id_principal_accounting_account "
                        + "            on record.id_accounting_account=accounting.id  join accounting_account_class aac on aac.id = pa.id_accounting_account_class  "
                        + "			left join invoice i on i.id = record.id_invoice"
                        + " where (accounting.id=:accountingAccountId or :accountingAccountId =0 ) and "
                        + "   (:isFromAs400 = false or record.is_from_as400=true ) and " +
                        "  (:accountingJournalId =0 or record.id_accounting_journal = :accountingJournalId) and " +
                        "(accounting.id_principal_accounting_account in (:principalAccountingAccountIds) or  0 in (:principalAccountingAccountIds) ) and "
                        +
                        "  (greatest(record.operation_date_time,'2023-01-01') between :startDate  and :endDate) and "
                        +
                        "(:accountingClassId =0 or pa.id_accounting_account_class = :accountingClassId ) "
                        + " and (:canViewRestricted=true or accounting.is_view_restricted=false ) " +
                        " group by aac.code, aac.label,accounting.label,pa.code,lpad(concat(accounting_account_sub_number,''),8-length(pa.code),'0') order by aac.code, pa.code,lpad(concat(accounting_account_sub_number,''),8-length(pa.code),'0')    "
                        +
                        "")
        List<AccountingBalance> searchAccountingBalanceCurrent(
                        @Param("accountingClassId") Integer accountingClassId,
                        @Param("accountingJournalId") Integer accountingJournalId,
                        @Param("accountingAccountId") Integer accountingAccountId,
                        @Param("principalAccountingAccountIds") List<Integer> principalAccountingAccountIds,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        @Param("canViewRestricted") boolean canViewRestricted,
                        @Param("isFromAs400") Boolean isFromAs400);

        @Query(nativeQuery = true, value = "" +
                        "select  sum(coalesce(record.credit_amount , 0)) as creditAmount,sum(coalesce(record.debit_amount , 0))  as debitAmount,"
                        + "		accounting.label as accountingAccountLabel,"
                        + "		pa.code as principalAccountingAccountCode,"
                        + "		aac.label as accountingAccountClassLabel,"
                        + "		lpad(concat(accounting_account_sub_number,''),8-length(pa.code),'0') as accountingAccountSubNumber,"
                        + "		sum(case when i.due_date>now() and i.due_date<= (now() +INTERVAL '30 day') then i.total_price  end) as echoir30, "
                        + "		sum(case when i.due_date>now() and i.due_date<= (now() +INTERVAL '60 day') and i.due_date> (now() +INTERVAL '30 day')  then i.total_price  end) as echoir60, "
                        + "		sum(case when  i.due_date>now() and i.due_date> (now() +INTERVAL '60 day')  then i.total_price  end) as echoir90, "
                        + "		sum(case when  i.due_date<=now() and i.due_date>= (now() -INTERVAL '30 day')  then i.total_price  end) as echu30, "
                        + "		sum(case when  i.due_date<=now() and i.due_date>= (now() -INTERVAL '60 day') and i.due_date< (now() -INTERVAL '30 day')  then i.total_price  end) as echu60, "
                        + "		sum(case when  i.due_date<=now() and i.due_date< (now() -INTERVAL '60 day')  then i.total_price  end) as echu90 "
                        + "    from" + "        closed_accounting_record record " + "    inner join"
                        + "        accounting_account accounting join principal_accounting_account pa on pa.id = accounting.id_principal_accounting_account "
                        + "            on record.id_accounting_account=accounting.id  join accounting_account_class aac on aac.id = pa.id_accounting_account_class  "
                        + "			left join invoice i on i.id = record.id_invoice"
                        + " where (accounting.id=:accountingAccountId or :accountingAccountId =0 ) and "
                        + "   (:isFromAs400 = false or record.is_from_as400=true ) and " +
                        "  (:accountingJournalId =0 or record.id_accounting_journal = :accountingJournalId) and " +
                        "(accounting.id_principal_accounting_account in (:principalAccountingAccountIds) or  0 in (:principalAccountingAccountIds) ) and "
                        +
                        "  (greatest(record.operation_date_time,'2023-01-01') between :startDate  and :endDate)  and "
                        +
                        "(:accountingClassId =0 or pa.id_accounting_account_class = :accountingClassId ) "
                        + " and (:canViewRestricted=true or accounting.is_view_restricted=false ) " +
                        " group by aac.code, aac.label,accounting.label,pa.code,lpad(concat(accounting_account_sub_number,''),8-length(pa.code),'0') order by aac.code, pa.code,lpad(concat(accounting_account_sub_number,''),8-length(pa.code),'0')    "
                        +
                        "")
        List<AccountingBalance> searchAccountingBalanceClosed(
                        @Param("accountingClassId") Integer accountingClassId,
                        @Param("accountingJournalId") Integer accountingJournalId,
                        @Param("accountingAccountId") Integer accountingAccountId,
                        @Param("principalAccountingAccountIds") List<Integer> principalAccountingAccountIds,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        @Param("canViewRestricted") boolean canViewRestricted,
                        @Param("isFromAs400") Boolean isFromAs400);

        @Query(nativeQuery = true, value = "select  sum(coalesce(record.credit_amount , 0)) as creditAmount,sum(coalesce(record.debit_amount , 0))  as debitAmount,"
                        + "		aac.label as accountingAccountClassLabel,"
                        + "		pa.code as principalAccountingAccountCode,"
                        + "		case when pa.code in ('401', '411', '4091', '4191','4161','4162') then pa.label else accounting.label end as principalAccountingAccountLabel,"
                        + "		case when pa.code in ('401','411','4091','4191','4161','4162') then lpad('',8-length(pa.code),'0') ||'' else lpad(concat(accounting_account_sub_number,''),8-length(pa.code),'0') ||'' end as accountingAccountSubNumber,"
                        + "		sum(case when i.due_date>now() and i.due_date<= (now() +INTERVAL '30 day') then i.total_price  end) as echoir30, "
                        + "		sum(case when i.due_date>now() and i.due_date<= (now() +INTERVAL '60 day') and i.due_date> (now() +INTERVAL '30 day')  then i.total_price  end) as echoir60, "
                        + "		sum(case when  i.due_date>now() and i.due_date> (now() +INTERVAL '60 day')  then i.total_price  end) as echoir90, "
                        + "		sum(case when  i.due_date<=now() and i.due_date>= (now() -INTERVAL '30 day')  then i.total_price  end) as echu30, "
                        + "		sum(case when  i.due_date<=now() and i.due_date>= (now() -INTERVAL '60 day') and i.due_date< (now() -INTERVAL '30 day')  then i.total_price  end) as echu60, "
                        + "		sum(case when  i.due_date<=now() and i.due_date< (now() -INTERVAL '60 day')  then i.total_price  end) as echu90 "
                        + "    from" + "        accounting_record record " + "    inner join"
                        + "        accounting_account accounting  join principal_accounting_account pa on pa.id = accounting.id_principal_accounting_account  "
                        + "            on record.id_accounting_account=accounting.id  join accounting_account_class aac on aac.id = pa.id_accounting_account_class  "
                        + "			left join invoice i on i.id = record.id_invoice"
                        + " where (accounting.id=:accountingAccountId or :accountingAccountId =0 ) and "
                        + "  (:isFromAs400 = false or coalesce(record.is_from_as400,false)=true ) and " +
                        "  (:accountingJournalId =0 or record.id_accounting_journal = :accountingJournalId) and " +
                        "(accounting.id_principal_accounting_account in (:principalAccountingAccountIds) or  0 in (:principalAccountingAccountIds) ) and "
                        +
                        "  (greatest(record.operation_date_time,'2023-01-01') between :startDate  and :endDate) and  "
                        +
                        "(:accountingClassId =0 or pa.id_accounting_account_class = :accountingClassId ) "
                        + " and (:canViewRestricted=true or accounting.is_view_restricted=false)  " +
                        " group by aac.code, aac.label,pa.code,case when pa.code in ('401','411','4091','4191','4161','4162') then lpad('',8-length(pa.code),'0') ||'' else lpad(concat(accounting_account_sub_number,''),8-length(pa.code),'0') ||'' end, case when pa.code in ('401', '411', '4091', '4191','4161','4162') then pa.label else accounting.label end  "
                        +
                        " order by aac.code, pa.code, case when pa.code in ('401','411','4091','4191','4161','4162') then lpad('',8-length(pa.code),'0') ||'' else lpad(concat(accounting_account_sub_number,''),8-length(pa.code),'0') ||'' end ")
        List<AccountingBalance> searchAccountingBalanceGeneraleCurrent(
                        @Param("accountingClassId") Integer accountingClassId,
                        @Param("accountingJournalId") Integer accountingJournalId,
                        @Param("accountingAccountId") Integer accountingAccountId,
                        @Param("principalAccountingAccountIds") List<Integer> principalAccountingAccountIds,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        @Param("canViewRestricted") boolean canViewRestricted,
                        @Param("isFromAs400") Boolean isFromAs400);

        @Query(nativeQuery = true, value = "select  sum(coalesce(record.credit_amount , 0)) as creditAmount,sum(coalesce(record.debit_amount , 0))  as debitAmount,"
                        + "		aac.label as accountingAccountClassLabel,"
                        + "		pa.code as principalAccountingAccountCode,"
                        + "		case when pa.code in ('401', '411', '4091', '4191','4161','4162') then pa.label else accounting.label end as principalAccountingAccountLabel,"
                        + "		case when pa.code in ('401','411','4091','4191','4161','4162') then lpad('',8-length(pa.code),'0') ||'' else lpad(concat(accounting_account_sub_number,''),8-length(pa.code),'0') ||'' end as accountingAccountSubNumber,"
                        + "		sum(case when i.due_date>now() and i.due_date<= (now() +INTERVAL '30 day') then i.total_price  end) as echoir30, "
                        + "		sum(case when i.due_date>now() and i.due_date<= (now() +INTERVAL '60 day') and i.due_date> (now() +INTERVAL '30 day')  then i.total_price  end) as echoir60, "
                        + "		sum(case when  i.due_date>now() and i.due_date> (now() +INTERVAL '60 day')  then i.total_price  end) as echoir90, "
                        + "		sum(case when  i.due_date<=now() and i.due_date>= (now() -INTERVAL '30 day')  then i.total_price  end) as echu30, "
                        + "		sum(case when  i.due_date<=now() and i.due_date>= (now() -INTERVAL '60 day') and i.due_date< (now() -INTERVAL '30 day')  then i.total_price  end) as echu60, "
                        + "		sum(case when  i.due_date<=now() and i.due_date< (now() -INTERVAL '60 day')  then i.total_price  end) as echu90 "
                        + "    from" + "        closed_accounting_record record " + "    inner join"
                        + "        accounting_account accounting  join principal_accounting_account pa on pa.id = accounting.id_principal_accounting_account  "
                        + "            on record.id_accounting_account=accounting.id  join accounting_account_class aac on aac.id = pa.id_accounting_account_class  "
                        + "			left join invoice i on i.id = record.id_invoice"
                        + " where (accounting.id=:accountingAccountId or :accountingAccountId =0 ) and "
                        + "  (:isFromAs400 = false or coalesce(record.is_from_as400,false)=true ) and " +
                        "  (:accountingJournalId =0 or record.id_accounting_journal = :accountingJournalId) and " +
                        "(accounting.id_principal_accounting_account in (:principalAccountingAccountIds) or  0 in (:principalAccountingAccountIds) ) and "
                        +
                        "  (greatest(record.operation_date_time,'2023-01-01') between :startDate  and :endDate) and "
                        +
                        "(:accountingClassId =0 or pa.id_accounting_account_class = :accountingClassId ) "
                        + " and (:canViewRestricted=true or accounting.is_view_restricted=false)  " +
                        " group by aac.code, aac.label,pa.code,case when pa.code in ('401','411','4091','4191','4161','4162') then lpad('',8-length(pa.code),'0') ||'' else lpad(concat(accounting_account_sub_number,''),8-length(pa.code),'0') ||'' end, case when pa.code in ('401', '411', '4091', '4191','4161','4162') then pa.label else accounting.label end  "
                        +
                        " order by aac.code, pa.code, case when pa.code in ('401','411','4091','4191','4161','4162') then lpad('',8-length(pa.code),'0') ||'' else lpad(concat(accounting_account_sub_number,''),8-length(pa.code),'0') ||'' end ")
        List<AccountingBalance> searchAccountingBalanceGeneraleClosed(
                        @Param("accountingClassId") Integer accountingClassId,
                        @Param("accountingJournalId") Integer accountingJournalId,
                        @Param("accountingAccountId") Integer accountingAccountId,
                        @Param("principalAccountingAccountIds") List<Integer> principalAccountingAccountIds,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        @Param("canViewRestricted") boolean canViewRestricted,
                        @Param("isFromAs400") Boolean isFromAs400);

        List<AccountingRecord> findByAccountingAccountAndInvoice(AccountingAccount accountingAccountCustomer,
                        Invoice invoice);

        List<AccountingRecord> findByInvoice(Invoice invoice);

        List<AccountingRecord> findByCustomerOrder(CustomerOrder customerOrder);

        List<AccountingRecord> findByTemporaryOperationId(Integer operationId);

        List<AccountingRecord> findByOperationId(Integer operationId);

        List<AccountingRecord> findByAccountingAccountAndRefund(AccountingAccount accountingAccount, Refund refund);

        List<AccountingRecord> findByAccountingAccountAndBankTransfert(AccountingAccount accountingAccount,
                        BankTransfert bankTransfert);

        @Query(nativeQuery = true, value = "select sum(accounting_record.debit_amount) - sum(accounting_record.credit_amount) as totalBalance from accounting_record "
                        + "where id_accounting_account=:accountingAccountId and operation_date_time<=:accountingDateTime")
        Number getAccountingRecordBalanceByAccountingAccountId(
                        @Param("accountingAccountId") Integer accountingAccountId,
                        @Param("accountingDateTime") LocalDateTime accountingDateTime);

        @Query(nativeQuery = true, value = "" +
                        "         select p.id " +
                        " from " +
                        " 	payment p join bank_transfert bt on bt.id = p.id_bank_transfert " +
                        " left join payment p_origin on " +
                        " 	p.id_origin_payment = p_origin.id " +
                        " where " +
                        " 	p.bank_id is null " +
                        " 	and p.payment_date <= :accountingDateTime " +
                        " and coalesce(bt.is_already_exported,false)= true " +
                        " 	and (p.id_origin_payment is null  " +
                        " 		or (p_origin.bank_id like 'H%' " +
                        " 			and p_origin.payment_date >:accountingDateTime)) " +
                        " 	and p.payment_amount < 0 ")
        List<Integer> getBankTransfertTotal(@Param("accountingDateTime") LocalDateTime accountingDateTime);

        @Query(nativeQuery = true, value = "" +
                        "         select  p.id " +
                        " from " +
                        " 	payment p join refund bt on bt.id = p.id_refund " +
                        " left join payment p_origin on " +
                        " 	p.id_refund = p_origin.id_refund and p.id <>p_origin.id " +
                        " where " +
                        " 	p.bank_id is null " +
                        " 	and p.payment_date <= :accountingDateTime " +
                        " and coalesce(bt.is_already_exported,false)= true " +
                        " 	and (p.id_origin_payment is null  " +
                        " 		or (p_origin.bank_id like 'H%' " +
                        " 			and p_origin.payment_date >:accountingDateTime)) " +
                        " 	and p.payment_amount < 0 ")
        List<Integer> getRefundTotal(@Param("accountingDateTime") LocalDateTime accountingDateTime);

        @Query(nativeQuery = true, value = "select p.id from payment p left join payment p_origin on p.id_origin_payment = p_origin.id "
                        + " where p.bank_id is null  and p.payment_date <= :accountingDateTime and p.check_number is not null  "
                        + " and (p.id_origin_payment is null or (p_origin.bank_id like 'H%' and p_origin.payment_date >:accountingDateTime)) and p.payment_amount < 0 ")
        List<Integer> getCheckTotal(@Param("accountingDateTime") LocalDateTime accountingDateTime);

        @Query(nativeQuery = true, value = "select p2.id from direct_debit_transfert ddt join payment p2 on p2.id_direct_debit_transfert = ddt.id and p2.bank_id is null "
                        + "where is_already_exported = true and exists (select 1 from payment p where p.payment_date <=:accountingDateTime and p.bank_id is null and p.id_direct_debit_transfert = ddt.id) and (ddt.is_cancelled is null or ddt.is_cancelled=false) and (is_matched = false or exists (select 1 from payment p where p.id_direct_debit_transfert=ddt.id and p.bank_id like 'H%' and p.payment_date >:accountingDateTime))")
        List<Integer> getDirectDebitTransfertTotal(@Param("accountingDateTime") LocalDateTime accountingDateTime);

        @Modifying
        @Query(nativeQuery = true, value = "delete from accounting_record where id_invoice in (select id from reprise_inpi_del) ")
        void deleteDuplicateAccountingRecord();

        @Query(nativeQuery = true, value = "select * from closed_accounting_record where id_payment =:idPayment")
        List<AccountingRecord> findClosedAccountingRecordsForPayment(@Param("idPayment") Integer idPayment);

        @Query("select sum(a.creditAmount) as creditAmount, " +
                        " sum(a.debitAmount) as debitAmount, pa.code as accountingAccountNumber "
                        +
                        " from AccountingRecord a  JOIN a.accountingAccount aa  JOIN aa.principalAccountingAccount pa  where "
                        +
                        "(a.accountingDateTime is null or (coalesce(a.manualAccountingDocumentDate,a.accountingDateTime) >=:startDate and coalesce(a.manualAccountingDocumentDate,a.accountingDateTime) <=:endDate ))   "
                        + " and (:canViewRestricted=true or aa.isViewRestricted=false)  " +
                        " group by pa.code ")
        List<AccountingBalanceBilan> getAccountingRecordAggregateByAccountingNumber(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("canViewRestricted") boolean canViewRestricted);

        @Modifying
        @Query(nativeQuery = true, value = "select * from accounting_record where id_accounting_account =:idAccountingAccount and id_accounting_journal =:idSalaryJournal and date(operation_date_time) = date(:operationDate) ;")
        List<AccountingRecord> findToDeleteRecordsByAccountingAccountAndJournalAndOperationDate(
                        @Param("idAccountingAccount") Integer idAccountingAccount,
                        @Param("idSalaryJournal") Integer idSalaryJournal,
                        @Param("operationDate") LocalDate operationDate);

        @Query(nativeQuery = true, value = "" +
                        " select  vat.code as vatCode, vat.label as vatLabel, sum(coalesce(debit_amount, 0))-sum(coalesce(credit_amount, 0)) as amount "
                        +
                        " from accounting_record ar  join accounting_account aa on aa.id =ar.id_accounting_account  " +
                        " join principal_accounting_account paa on paa.id = aa.id_principal_accounting_account  " +
                        " left join invoice_item ii on ii.id = ar.id_invoice_item  " +
                        " left join vat on vat.id =ii.id_vat " +
                        " where  ar.operation_date_time  >=:startDate and ar.operation_date_time <=:endDate " +
                        " and ar.id_accounting_journal =:idAccountingJournal  and paa.id_accounting_account_class =:idAccountingClass  "
                        +
                        " group by vat.label, vat.code,vat.id ")
        List<AccountingVatValue> getAccountingVatValueForJournal(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("idAccountingJournal") Integer idAccountingJournal,
                        @Param("idAccountingClass") Integer idAccountingClass);

}