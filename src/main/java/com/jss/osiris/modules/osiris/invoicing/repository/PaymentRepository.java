package com.jss.osiris.modules.osiris.invoicing.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.invoicing.model.OutboundCheckSearchResult;
import com.jss.osiris.modules.osiris.invoicing.model.Payment;
import com.jss.osiris.modules.osiris.invoicing.model.PaymentSearchResult;

public interface PaymentRepository extends QueryCacheCrudRepository<Payment, Integer> {

        @Query(nativeQuery = true, value = "select p.* from payment p  where p.id_invoice is null and id_customer_order is null and id_direct_debit_transfert is null and id_refund is null and id_competent_authority is null and id_provider is null and id_bank_transfert is null and p.is_externally_associated=false and p.id_accounting_account is null and p.is_cancelled=false ")
        List<Payment> findNotAssociatedPayments();

        @Query(nativeQuery = true, value = " select p.id as id,"
                        + " p.payment_Date as paymentDate,"
                        + " p.payment_amount  as paymentAmount,"
                        + " p.label as paymentLabel,"
                        + " payment_type.label as paymentTypeLabel,"
                        + " p.comment as comment,"
                        + " p.is_externally_associated  as isExternallyAssociated ,"
                        + " p.is_cancelled  as isCancelled ,"
                        + " p.is_appoint  as isAppoint ,"
                        + " case when p.id_invoice is null and p.id_customer_order is null and id_direct_debit_transfert is null and p.id_refund is null and p.id_bank_transfert is null and p.is_externally_associated=false and p.is_cancelled=false and id_competent_authority is null and id_provider is null and id_accounting_account is null then false else true end as isAssociated ,"
                        + " p.id_invoice as invoiceId,"
                        + " p.id_origin_payment as originPaymentId"
                        + " from payment p "
                        + " join payment_type on payment_type.id = p.id_payment_type"
                        + " where (:isHideAssociatedPayments=false OR ( p.id_invoice is null and p.id_customer_order is null and p.id_refund is null and p.id_bank_transfert is null and p.is_externally_associated=false and p.is_cancelled=false  and id_competent_authority is null and id_provider is null and id_accounting_account is null   )) "
                        + " and (:isHideCancelledPayments=false or p.is_cancelled = false) "
                        + " and (:isHideAppoint=false or p.is_appoint = false) "
                        + " and (:idPayment=0 or p.id = :idPayment) "
                        + " and p.payment_date>=:startDate and p.payment_date<=:endDate "
                        + "  and (:minAmount is null or p.payment_amount>=CAST(CAST(:minAmount as text) as real) ) "
                        + "  and (:maxAmount is null or p.payment_amount<=CAST(CAST(:maxAmount as text) as real) )"
                        + " and (:label is null or CAST(p.id as text) = upper(CAST(:label as text)) or  upper(p.label)  like '%' || trim(upper(CAST(:label as text)))  || '%' )")
        List<PaymentSearchResult> findPayments(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        @Param("minAmount") Float minAmount, @Param("maxAmount") Float maxAmount,
                        @Param("label") String label,
                        @Param("isHideAssociatedPayments") boolean isHideAssociatedPayments,
                        @Param("isHideCancelledPayments") boolean isHideCancelledPayments,
                        @Param("isHideAppoint") boolean isHideAppoint,
                        @Param("idPayment") Integer idPayment);

        Payment findByBankId(String id);

        Payment findByCheckNumber(String checkNumber);

        @Query(nativeQuery = true, value = " select p.check_number as outboundCheckNumber,"
                        + " p.id as paymentNumber, "
                        + " p.payment_date as outboundCheckDate,"
                        + " p.payment_amount  as outboundCheckAmount,"
                        + " p.label as outboundCheckLabel,"
                        + " p.id_invoice as invoiceAssociated, "
                        + " case when p.id_origin_payment is not null and p_origin.bank_id like 'H%' then true else false end as isMatched"
                        + " from payment p left join payment p_origin on p.id_origin_payment = p_origin.id "
                        + " where ( p.bank_id is null and p.check_number is not null ) "
                        + " and (:isHideMatchedOutboundChecks=false or (p.id_origin_payment is null or p_origin.bank_id not like 'H%')) "
                        + " and p.payment_date>=:startDate and p.payment_date<=:endDate "
                        + " and (p.is_cancelled=false or p.is_cancelled is null) "
                        + " and p.payment_amount < 0 "
                        + " and (:minAmount is null or p.payment_amount>=CAST(CAST(:minAmount as text) as real) ) "
                        + " and (:maxAmount is null or p.payment_amount<=CAST(CAST(:maxAmount as text) as real) )"
                        + " and (:label is null or CAST(p.id as text) = upper(CAST(:label as text)) or  upper(p.label)  like '%' || trim(upper(CAST(:label as text)))  || '%' )")
        List<OutboundCheckSearchResult> findOutboundChecks(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        @Param("minAmount") Float minAmount, @Param("maxAmount") Float maxAmount,
                        @Param("label") String label,
                        @Param("isHideMatchedOutboundChecks") boolean isHideMatchedOutboundChecks);

        @Modifying
        @Query(nativeQuery = true, value = " delete from payment p where id_invoice in (select id from reprise_inpi_del) ")
        void deleteDuplicatePayments();

}
