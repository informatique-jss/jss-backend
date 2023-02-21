package com.jss.osiris.modules.quotation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.invoicing.model.DebourSearchResult;
import com.jss.osiris.modules.quotation.model.Debour;

public interface DebourRepository extends CrudRepository<Debour, Integer> {

        @Query(nativeQuery = true, value = "" +
                        " select  " +
                        " d.id, " +
                        " d.comments, " +
                        " a.id_customer_order as customerOrderId, " +
                        " b.label as billingTypeLabel, " +
                        " c.label as competentAuthorityLabel, " +
                        " d.debour_amount  as debourAmount, " +
                        " pt.label as paymentTypeLabel, " +
                        " d.payment_date_time as paymentDateTime, " +
                        " d.id_payment as paymentId, " +
                        " i.id_invoice as invoiceId, " +
                        " d.check_number as checkNumber, " +
                        " case when d.id_payment is not null or i.id_invoice is not null then true else false end as isAssociated, "
                        +
                        " ct.is_direct_charge as isCompetentAuthorityDirectCharge " +
                        " from debour d " +
                        " join provision p on d.id_provision = p.id " +
                        " join asso_affaire_order a on a.id = p.id_asso_affaire_order " +
                        " join billing_type b on b.id = d.id_billing_type " +
                        " join competent_authority c on c.id = d.id_competent_authority " +
                        " join competent_authority_type ct on ct.id = c.id_competent_authority_type " +
                        " join payment_type pt on pt.id = d.id_payment_type " +
                        " left join invoice_item i on i.id = d.id_invoice_item " +
                        " where  (:competentAuthorityId=0 or c.id=:competentAuthorityId) " +
                        " and  (:customerOrderId=0 or a.id_customer_order=:customerOrderId) " +
                        "  and (:minAmount is null or d.debour_amount>=CAST(CAST(:minAmount as text) as real) ) " +
                        "  and (:maxAmount is null or d.debour_amount<=CAST(CAST(:maxAmount as text) as real) )" +
                        " and  (:isNonAssociated=false or d.id_payment is null and (d.id_payment_type not in (:paymentTypeAccountId))) "
                        +
                        " and  (:isCompetentAuthorityDirectCharge=false or ct.is_direct_charge=true) " +
                        "")
        List<DebourSearchResult> findDebours(@Param("competentAuthorityId") Integer competentAuthorityId,
                        @Param("customerOrderId") Integer customerOrderId, @Param("maxAmount") Float maxAmount,
                        @Param("minAmount") Float minAmount, @Param("isNonAssociated") boolean isNonAssociated,
                        @Param("isCompetentAuthorityDirectCharge") boolean isCompetentAuthorityDirectCharge,
                        @Param("paymentTypeAccountId") Integer paymentTypeAccountId);
}