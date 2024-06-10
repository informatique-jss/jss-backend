package com.jss.osiris.modules.accounting.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingBalance;
import com.jss.osiris.modules.accounting.model.AccountingBalanceBilan;
import com.jss.osiris.modules.accounting.model.AccountingJournal;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.accounting.model.AccountingRecordSearchResult;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.Refund;
import com.jss.osiris.modules.quotation.model.BankTransfert;
import com.jss.osiris.modules.quotation.model.CustomerOrder;

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
                        " where i.is_invoice_from_provider and i.created_date between :startDate and :endDate " +
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
                        " r2.operation_id as contrePasseOperationId, " +
                        " (select STRING_AGG( case when af.denomination is not null and af.denomination!='' then af.denomination else af.firstname || ' '||af.lastname end  || ' ('||city.label ||')',', ' order by 1) as affaireLabel from asso_affaire_order asso join affaire af on af.id = asso.id_affaire left join city on city.id = af.id_city where  asso.id_customer_order = i.customer_order_id or asso.id_customer_order = r.id_customer_order)  as affaireLabel,"
                        +
                        " COALESCE(re1.firstname || ' ' || re1.lastname ,re2.firstname || ' ' || re2.lastname ) as responsable "
                        +
                        " from accounting_record r " +
                        " left join accounting_journal j on j.id = r.id_accounting_journal " +
                        " join accounting_account a on a.id = r.id_accounting_account " +
                        " join principal_accounting_account pa on pa.id = a.id_principal_accounting_account " +
                        " left join tiers t on (t.id_accounting_account_customer = r.id_accounting_account  or t.id_accounting_account_deposit=r.id_accounting_account) "
                        + " left join confrere cf on (cf.id_accounting_account_customer = r.id_accounting_account  or cf.id_accounting_account_deposit=r.id_accounting_account or cf.id_accounting_account_provider = r.id_accounting_account) "
                        + " left join accounting_record r2 on r2.id = r.id_contre_passe " +
                        " left join invoice i on i.id = r.id_invoice left join invoice_label il on il.id = i.id " +
                        " left join customer_order co on co.id = r.id_customer_order " +
                        " left join responsable re1 on re1.id = i.id_responsable " +
                        " left join responsable re2 on r2.id = co.id_responsable " +
                        " where ( COALESCE(:accountingAccountIds) =0 or r.id_accounting_account in (:accountingAccountIds)) "
                        +
                        " and (:journalId =0 or r.id_accounting_journal = :journalId) " +
                        " and (:confrereId =0 or cf.id is not null and cf.id =:confrereId ) "
                        +
                        " and (:tiersId =0 or t.id is not null and t.id = :tiersId) " +
                        " and (:hideLettered = false or coalesce(r.lettering_date_time,:endDate)>=:endDate ) " +
                        " and (:isFromAs400 = false or r.is_from_as400=true ) " +
                        " and (coalesce(r.manual_accounting_document_date, r.operation_date_time)>= :startDate or coalesce(r.manual_accounting_document_date, r.operation_date_time)<= date_trunc('year', cast(:startDate as date)) and r.lettering_number  is not null) and coalesce(r.manual_accounting_document_date,r.operation_date_time)<=:endDate  "
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
        List<AccountingRecordSearchResult> searchAccountingRecords(
                        @Param("accountingAccountIds") List<Integer> accountingAccountIds,
                        @Param("accountingClassId") Integer accountingClassId,
                        @Param("journalId") Integer journalId,
                        @Param("tiersId") Integer tiersId,
                        @Param("confrereId") Integer confrereId,
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
                        "select  round(cast(sum(case "
                        + "            when record.isanouveau=false or record.is_from_as400=true then record.credit_amount "
                        + "        end)  as numeric), 2) as creditAmount," + "        round(cast(sum(case "
                        + "            when record.isanouveau=false or record.is_from_as400=true then record.debit_amount "
                        + "        end) as numeric), 2) as debitAmount,"
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
                        "(accounting.id_principal_accounting_account in (:principalAccountingAccountIds) or  0 in (:principalAccountingAccountIds) ) and "
                        +
                        "(  (coalesce(record.manual_accounting_document_date, record.operation_date_time)>= :startDate or coalesce(record.manual_accounting_document_date, record.operation_date_time)<= date_trunc('year', cast(:startDate as date)) and record.lettering_number  is not null) and coalesce(record.manual_accounting_document_date,record.operation_date_time) <=:endDate ) and "
                        +
                        "(:accountingClassId =0 or pa.id_accounting_account_class = :accountingClassId ) "
                        + " and (:canViewRestricted=true or accounting.is_view_restricted=false ) " +
                        " group by aac.label,accounting.label,pa.code,lpad(concat(accounting_account_sub_number,''),8-length(pa.code),'0') order by pa.code   "
                        +
                        "")
        List<AccountingBalance> searchAccountingBalance(
                        @Param("accountingClassId") Integer accountingClassId,
                        @Param("accountingAccountId") Integer accountingAccountId,
                        @Param("principalAccountingAccountIds") List<Integer> principalAccountingAccountIds,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        @Param("canViewRestricted") boolean canViewRestricted,
                        @Param("isFromAs400") Boolean isFromAs400);

        @Query(nativeQuery = true, value = "select" + "       round(cast( sum(case "
                        + "            when record.isanouveau=false or record.is_from_as400=true then record.credit_amount  "
                        + "        end) as numeric), 2) as creditAmount," + "      round(cast(  sum(case "
                        + "            when record.isanouveau=false or record.is_from_as400=true then record.debit_amount  "
                        + "        end) as numeric), 2) as debitAmount," + "	 "
                        + "		aac.label as accountingAccountClassLabel,"
                        + "		pa.code as principalAccountingAccountCode,"
                        + "		case when pa.code in ('401', '411', '4091', '4191','4161','4162') then pa.label else accounting.label end as principalAccountingAccountLabel,"
                        + "		case when pa.code in ('401','411','4091','4191','4161','4162') then '' else lpad(concat(accounting_account_sub_number,''),8-length(pa.code),'0') ||'' end as accountingAccountSubNumber,"
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
                        "(accounting.id_principal_accounting_account in (:principalAccountingAccountIds) or  0 in (:principalAccountingAccountIds) ) and "
                        +
                        "(  (coalesce(record.manual_accounting_document_date, record.operation_date_time)>= :startDate or coalesce(record.manual_accounting_document_date, record.operation_date_time)<= date_trunc('year', cast(:startDate as date)) and record.lettering_number  is not null) and coalesce(record.manual_accounting_document_date,record.operation_date_time) <=:endDate ) and "
                        +
                        "(:accountingClassId =0 or pa.id_accounting_account_class = :accountingClassId ) "
                        + " and (:canViewRestricted=true or accounting.is_view_restricted=false)  " +
                        " group by aac.label,pa.code,case when pa.code in ('401','411','4091','4191','4161','4162') then '' else lpad(concat(accounting_account_sub_number,''),8-length(pa.code),'0') ||'' end, case when pa.code in ('401', '411', '4091', '4191','4161','4162') then pa.label else accounting.label end  "
                        +
                        " order by pa.code, case when pa.code in ('401','411','4091','4191','4161','4162') then '' else lpad(concat(accounting_account_sub_number,''),8-length(pa.code),'0') ||'' end ")
        List<AccountingBalance> searchAccountingBalanceGenerale(
                        @Param("accountingClassId") Integer accountingClassId,
                        @Param("accountingAccountId") Integer accountingAccountId,
                        @Param("principalAccountingAccountIds") List<Integer> principalAccountingAccountIds,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        @Param("canViewRestricted") boolean canViewRestricted,
                        @Param("isFromAs400") Boolean isFromAs400);

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

        List<AccountingRecord> findByAccountingAccountAndInvoice(AccountingAccount accountingAccountCustomer,
                        Invoice invoice);

        List<AccountingRecord> findByInvoice(Invoice invoice);

        List<AccountingRecord> findByCustomerOrder(CustomerOrder customerOrder);

        List<AccountingRecord> findByTemporaryOperationId(Integer operationId);

        List<AccountingRecord> findByOperationId(Integer operationId);

        List<AccountingRecord> findByAccountingAccountAndRefund(AccountingAccount accountingAccount, Refund refund);

        List<AccountingRecord> findByAccountingAccountAndBankTransfert(AccountingAccount accountingAccount,
                        BankTransfert bankTransfert);

}

;