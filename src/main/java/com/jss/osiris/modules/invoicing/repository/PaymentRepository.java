package com.jss.osiris.modules.invoicing.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.PaymentSearchResult;

public interface PaymentRepository extends CrudRepository<Payment, Integer> {

        // TODO : remove limit 500
        @Query(nativeQuery = true, value = "select p.* from payment p  where p.id_invoice is null and p.is_externally_associated=false and p.is_cancelled=false and not exists (select 1 from debour d where d.id_payment = p.id)  and not exists (select 1 from refund d where d.id_payment = p.id) order by p.payment_date desc limit 250 ")
        List<Payment> findNotAssociatedPayments();

        @Query(nativeQuery = true, value = " select p.id as id,"
                        + " pw.label as paymentWayLabel,"
                        + " pw.id as paymentWayId,"
                        + " p.payment_Date as paymentDate,"
                        + " p.payment_amount  as paymentAmount ,"
                        + " p.label as paymentLabel,"
                        + " payment_type.label as paymentTypeLabel,"
                        + " p.is_externally_associated  as isExternallyAssociated ,"
                        + " p.is_cancelled  as isCancelled ,"
                        + " p.id_invoice is not null or d.id is not null  or refund.id is not null  or p.is_cancelled=true  as isAssociated ,"
                        + " p.id_invoice as invoiceId"
                        + " from payment p "
                        + " join payment_way pw on pw.id = p.id_payment_way"
                        + " join payment_type on payment_type.id = p.id_payment_type"
                        + "  left join debour d on d.id_payment = p.id "
                        + "  left join refund on refund.id_payment = p.id "
                        + " where (:isHideAssociatedPayments=false OR (p.id_invoice is null and d.id is null  and p.is_externally_associated=false and p.is_cancelled=false)) "
                        + "  and ( COALESCE(:paymentWays)=0 or p.id_payment_way in (:paymentWays) )"
                        + " and p.payment_date>=:startDate and p.payment_date<=:endDate "
                        + "  and (:minAmount is null or p.payment_amount>=CAST(CAST(:minAmount as text) as real) ) "
                        + "  and (:maxAmount is null or p.payment_amount<=CAST(CAST(:maxAmount as text) as real) )"
                        + " and (:label is null or CAST(p.id as text) = upper(CAST(:label as text)) or  upper(p.label)  like '%' || trim(upper(CAST(:label as text)))  || '%' )")
        List<PaymentSearchResult> findPayments(@Param("paymentWays") List<Integer> paymentWays,
                        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
                        @Param("minAmount") Float minAmount, @Param("maxAmount") Float maxAmount,
                        @Param("label") String label,
                        @Param("isHideAssociatedPayments") boolean isHideAssociatedPayments);

        Payment findByBankId(String id);
}
