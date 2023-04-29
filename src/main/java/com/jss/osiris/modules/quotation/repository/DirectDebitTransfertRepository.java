package com.jss.osiris.modules.quotation.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.invoicing.model.DirectDebitTransfertSearchResult;
import com.jss.osiris.modules.quotation.model.DirectDebitTransfert;

public interface DirectDebitTransfertRepository extends CrudRepository<DirectDebitTransfert, Integer> {

        @Query(nativeQuery = true, value = " select r.id as id,"
                        + " r.customer_order_label as customerOrderLabel,"
                        + " r.transfert_date_time as transfertDate,"
                        + " r.transfert_amount  as transfertAmount ,"
                        + " r.label as transfertLabel,"
                        + " r.transfert_iban as transfertIban,"
                        + " r.is_already_exported  as isAlreadyExported "
                        + " from direct_debit_transfert r "
                        + " where is_cancelled=false and (:isHideExportedDirectDebitTransfert=false OR r.is_already_exported=false) "
                        + " and r.transfert_date_time>=:startDate and r.transfert_date_time<=:endDate "
                        + "  and (:minAmount is null or r.transfert_amount>=CAST(CAST(:minAmount as text) as real) ) "
                        + "  and (:maxAmount is null or r.transfert_amount<=CAST(CAST(:maxAmount as text) as real) )"
                        + " and (:label is null or  upper(r.label)  like '%' || upper(CAST(:label as text))  || '%' )")
        List<DirectDebitTransfertSearchResult> findTransferts(
                        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
                        @Param("minAmount") Float minAmount, @Param("maxAmount") Float maxAmount,
                        @Param("label") String label,
                        @Param("isHideExportedDirectDebitTransfert") boolean isHideExportedDirectDebitTransfert);
}