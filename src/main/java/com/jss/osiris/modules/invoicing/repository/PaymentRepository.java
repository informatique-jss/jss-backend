package com.jss.osiris.modules.invoicing.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.PaymentSearchResult;

public interface PaymentRepository extends QueryCacheCrudRepository<Payment, Integer> {

        @Query(nativeQuery = true, value = "select p.* from payment p  where p.id_invoice is null and id_customer_order is null and id_refund is null and id_bank_transfert is null and p.is_externally_associated=false and p.is_cancelled=false ")
        List<Payment> findNotAssociatedPayments();

        @Query(nativeQuery = true, value = " select p.id as id,"
                        + " p.payment_Date as paymentDate,"
                        + " p.payment_amount  as paymentAmount ,"
                        + " p.label as paymentLabel,"
                        + " payment_type.label as paymentTypeLabel,"
                        + " p.is_externally_associated  as isExternallyAssociated ,"
                        + " p.is_cancelled  as isCancelled ,"
                        + " case when p.id_invoice is null and p.id_customer_order is null and p.id_refund is null and p.id_bank_transfert is null and p.is_externally_associated=false and p.is_cancelled=false then false else true end as isAssociated ,"
                        + " p.id_invoice as invoiceId,"
                        + " p.id_origin_payment as originPaymentId"
                        + " from payment p "
                        + " join payment_type on payment_type.id = p.id_payment_type"
                        + " where (:isHideAssociatedPayments=false OR ( p.id_invoice is null and p.id_customer_order is null and p.id_refund is null and p.id_bank_transfert is null and p.is_externally_associated=false and p.is_cancelled=false )) "
                        + " and (:isHideCancelledPayments=false or p.is_cancelled = false) "
                        + " and p.payment_date>=:startDate and p.payment_date<=:endDate "
                        + "  and (:minAmount is null or p.payment_amount>=CAST(CAST(:minAmount as text) as real) ) "
                        + "  and (:maxAmount is null or p.payment_amount<=CAST(CAST(:maxAmount as text) as real) )"
                        + " and (:label is null or CAST(p.id as text) = upper(CAST(:label as text)) or  upper(p.label)  like '%' || trim(upper(CAST(:label as text)))  || '%' )")
        List<PaymentSearchResult> findPayments(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        @Param("minAmount") Float minAmount, @Param("maxAmount") Float maxAmount,
                        @Param("label") String label,
                        @Param("isHideAssociatedPayments") boolean isHideAssociatedPayments,
                        @Param("isHideCancelledPayments") boolean isHideCancelledPayments);

        Payment findByBankId(String id);
}
