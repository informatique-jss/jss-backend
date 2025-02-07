package com.jss.osiris.modules.osiris.quotation.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.invoicing.model.DirectDebitTransfertSearchResult;
import com.jss.osiris.modules.osiris.quotation.model.DirectDebitTransfert;

public interface DirectDebitTransfertRepository extends QueryCacheCrudRepository<DirectDebitTransfert, Integer> {

        @Query(nativeQuery = true, value = " select r.id as id,"
                        + " r.customer_order_label as customerOrderLabel,"
                        + " r.transfert_date_time as transfertDate,"
                        + " r.transfert_amount  as transfertAmount ,"
                        + " r.label as transfertLabel,"
                        + " r.transfert_iban as transfertIban,"
                        + " r.transfert_bic as transfertBic,"
                        + " r.is_already_exported  as isAlreadyExported, "
                        + " r.is_matched as isMatched, "
                        + " istt.label as invoiceStatus "
                        + " from direct_debit_transfert r "
                        + " left outer join invoice ie on r.id = ie.id_direct_debit_transfert"
                        + " left outer join invoice_status istt on ie.id_invoice_status = istt.id "
                        + " where r.is_cancelled=false and (:isHideExportedDirectDebitTransfert=false OR r.is_already_exported=false) and (:idDirectDebitTransfert=0 OR r.id=:idDirectDebitTransfert) "
                        + " and (:isHideMatchedDirectDebitTransfert=false OR r.is_matched=false) "
                        + " and r.transfert_date_time>=:startDate and r.transfert_date_time<=:endDate "
                        + "  and (:minAmount is null or r.transfert_amount>=CAST(CAST(:minAmount as text) as numeric(15, 2)) ) "
                        + "  and (:maxAmount is null or r.transfert_amount<=CAST(CAST(:maxAmount as text) as numeric(15, 2)) )"
                        + " and (:label is null or   CAST(r.id as text) = upper(CAST(:label as text)) or upper(r.label)  like '%' || upper(CAST(:label as text))  || '%' )")
        List<DirectDebitTransfertSearchResult> findTransferts(
                        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
                        @Param("minAmount") Float minAmount, @Param("maxAmount") Float maxAmount,
                        @Param("label") String label,
                        @Param("isHideMatchedDirectDebitTransfert") boolean isHideMatchedDirectDebitTransfert,
                        @Param("isHideExportedDirectDebitTransfert") boolean isHideExportedDirectDebitTransfert,
                        @Param("idDirectDebitTransfert") Integer idDirectDebitTransfert);
}