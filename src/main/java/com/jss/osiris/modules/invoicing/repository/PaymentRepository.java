package com.jss.osiris.modules.invoicing.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.PaymentWay;

public interface PaymentRepository extends CrudRepository<Payment, Integer> {

    @Query("select i from Payment i where (:isHideAssociatedPayments=false OR (i.invoice is null and i.customerOrder is null and i.isExternallyAssociated=false)) and i.paymentWay IN :paymentWays and i.paymentDate>=:startDate and i.paymentDate<=:endDate and (:minAmount is null or paymentAmount>=CAST(CAST(:minAmount as string) as double)) and (:maxAmount is null or paymentAmount<=CAST(CAST(:maxAmount as string) as double)) and (:label is null or label like '%' || cast(:label as string) || '%')")
    List<Payment> findPayments(@Param("paymentWays") List<PaymentWay> paymentWays,
            @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
            @Param("minAmount") Float minAmount, @Param("maxAmount") Float maxAmount, @Param("label") String label,
            @Param("isHideAssociatedPayments") boolean isHideAssociatedPayments);

    @Query("select p from Payment p  where p.invoice is null and p.customerOrder is null and p.isExternallyAssociated=false")
    List<Payment> findNotAssociatedPayments();
}