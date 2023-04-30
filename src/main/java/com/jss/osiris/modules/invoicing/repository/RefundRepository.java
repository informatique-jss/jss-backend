package com.jss.osiris.modules.invoicing.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import com.jss.osiris.libs.QueryCacheCrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.invoicing.model.Refund;
import com.jss.osiris.modules.invoicing.model.RefundSearchResult;

public interface RefundRepository extends QueryCacheCrudRepository<Refund, Integer> {

        @Query(nativeQuery = true, value = " select r.id as id,"
                        + " r.refund_date_time as refundDate,"
                        + " r.refund_amount  as refundAmount ,"
                        + " r.label as refundLabel,"
                        + " r.refundiban as refundIban,"
                        + " r.is_already_exported  as isAlreadyExported ,"
                        + " r.is_matched  as isMatched ,"
                        + " (select max(coalesce(a1.denomination,a1.firstname ||' ' || a1.lastname)) from affaire a1 join asso_affaire_order a2 on a1.id = a2.id_affaire where a2.id_customer_order = r.id_customer_order) as affaireLabel ,"
                        + " coalesce(affaire.denomination, affaire.firstname || ' ' || affaire.lastname, confrere.label, tiers.denomination, tiers.firstname || ' ' || tiers.lastname) as refundTiersLabel ,"
                        + " r.id_payment as paymentId"
                        + " from refund r "
                        + " left join affaire on affaire.id = r.id_affaire "
                        + " left join confrere on confrere.id = r.id_confrere "
                        + " left join tiers on tiers.id = r.id_tiers "
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