package com.jss.osiris.modules.invoicing.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.PaymentWay;

public interface PaymentRepository extends CrudRepository<Payment, Integer> {

    @Query("select i from Payment i where i.paymentWay IN :paymentWays and i.paymentDate>=:startDate and i.paymentDate<=:endDate")
    List<Payment> findPayments(@Param("paymentWays") List<PaymentWay> paymentWays,
            @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("select p from Payment p  where p.invoice is null and p.customerOrder is null")
    List<Payment> findNotAssociatedPayments();
}