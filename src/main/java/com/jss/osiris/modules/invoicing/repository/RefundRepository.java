package com.jss.osiris.modules.invoicing.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.invoicing.model.Refund;
import com.jss.osiris.modules.invoicing.model.RefundSearchResult;

public interface RefundRepository extends CrudRepository<Refund, Integer> {

        @Query(nativeQuery = true, value = " select r.id as id,"
                        + " r.refund_date_time as refundDate,"
                        + " r.refund_amount  as refundAmount ,"
                        + " r.label as refundLabel,"
                        + " r.refundiban as refundIban,"
                        + " r.is_already_exported  as isAlreadyExported ,"
                        + " r.is_matched  as isMatched ,"
                        + " r.id_payment as paymentId"
                        + " from refund r "
                        + " where (:isHideExportedRefunds=false OR r.is_already_exported=false) "
                        + " and (:isHideMatchedRefunds=false OR r.is_matched=false) "
                        + " and r.refund_date_time>=:startDate and r.refund_date_time<=:endDate "
                        + "  and (:minAmount is null or r.refund_amount>=CAST(CAST(:minAmount as text) as real) ) "
                        + "  and (:maxAmount is null or r.refund_amount<=CAST(CAST(:maxAmount as text) as real) )"
                        + " and (:label is null or  upper(r.label)  like '%' || upper(CAST(:label as text))  || '%' )")
        List<RefundSearchResult> findRefunds(
                        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
                        @Param("minAmount") Float minAmount, @Param("maxAmount") Float maxAmount,
                        @Param("label") String label,
                        @Param("isHideExportedRefunds") boolean isHideExportedRefunds,
                        @Param("isHideMatchedRefunds") boolean isHideMatchedRefunds);
}