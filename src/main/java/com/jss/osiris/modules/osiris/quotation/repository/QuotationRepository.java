package com.jss.osiris.modules.osiris.quotation.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.QuotationSearchResult;
import com.jss.osiris.modules.osiris.quotation.model.QuotationStatus;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

import jakarta.persistence.QueryHint;

public interface QuotationRepository extends QueryCacheCrudRepository<Quotation, Integer> {

        @Query(nativeQuery = true, value = "select "
                        + "  r.firstname || ' '||r.lastname as customerOrderLabel,"
                        + " case when t2.denomination is not null and t2.denomination!='' then t2.denomination else t2.firstname || ' '||t2.lastname end as tiersLabel,"
                        + " cos.label as quotationStatus,"
                        + " co.created_date as createdDate,"
                        + " co.last_status_update as lastStatusUpdate,"
                        + "  t2.id_commercial as salesEmployeeId,"
                        + " co.id as quotationId,"
                        + " r.id as responsableId,"
                        + " t2.id as tiersId,"
                        + " origin.label as customerOrderOriginLabel,"
                        + " STRING_AGG(DISTINCT service.service_label_to_display,', ') as serviceTypeLabel,"
                        + " sum(COALESCE(i.pre_tax_price,0)+COALESCE(i.vat_price,0)-COALESCE(i.discount_amount,0)) as totalPrice ,"
                        + " STRING_AGG(DISTINCT case when af.denomination is not null and af.denomination!='' then af.denomination else af.firstname || ' '||af.lastname end  || ' ('||city.label ||')' ,', ') as affaireLabel,"
                        + " co.description as quotationDescription"
                        + " from quotation co"
                        + " join customer_order_origin origin on origin.id = co.id_customer_order_origin"
                        + " join quotation_status cos on cos.id = co.id_quotation_status"
                        + " left join asso_affaire_order asso on asso.id_quotation = co.id"
                        + " left join service on service.id_asso_affaire_order = asso.id"
                        + " left join provision on provision.id_service = service.id"
                        + " left join invoice_item i on i.id_provision = provision.id"
                        + " left join affaire af on af.id = asso.id_affaire"
                        + " left join city on af.id_city = city.id"
                        + " left join responsable r on r.id = co.id_responsable"
                        + " left join tiers t2 on t2.id = r.id_tiers"
                        + " left join asso_quotation_customer_order asso_co on asso_co.id_quotation = co.id "
                        + " where ( COALESCE(:customerOrderStatus) =0 or co.id_quotation_status in (:customerOrderStatus)) "
                        + " and co.created_date>=:startDate and co.created_date<=:endDate "
                        + " and ( :idCustomerOrder =0 or asso_co.id_customer_order = :idCustomerOrder)"
                        + " and ( COALESCE(:salesEmployee) =0 or r.id_commercial in (:salesEmployee)  or  t2.id_commercial in (:salesEmployee))"
                        + " and ( COALESCE(:customerOrder)=0 or r.id in (:customerOrder) )"
                        + " and ( COALESCE(:affaire)=0 or af.id in (:affaire) )"
                        + " group by  r.id,origin.label, r.firstname, r.lastname,  t2.denomination, t2.firstname, t2.lastname, cos.label, "
                        + " co.created_date,  r.id_commercial, t2.id_commercial, co.id, r.id, t2.id, co.description ")
        List<QuotationSearchResult> findQuotations(@Param("salesEmployee") List<Integer> salesEmployee,
                        @Param("customerOrderStatus") List<Integer> customerOrderStatus,
                        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
                        @Param("customerOrder") List<Integer> customerOrder, @Param("affaire") List<Integer> affaire,
                        @Param("idCustomerOrder") Integer idCustomerOrder);

        @Query(value = "select n from Quotation n where quotationStatus=:quotationStatus and thirdReminderDateTime is null ")
        List<Quotation> findQuotationForReminder(@Param("quotationStatus") QuotationStatus quotationStatus);

        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        List<Quotation> findByCustomerOrders_Id(Integer idCustomerOrder);

        @Query(value = "select q.* from quotation q where exists (select 1 from asso_affaire_order a join service on service.id_asso_affaire_order = a.id join provision p on p.id_service = service.id where a.id_quotation = q.id and  p.id_announcement = :announcementId)", nativeQuery = true)
        Optional<Quotation> findQuotationForAnnouncement(@Param("announcementId") Integer announcementId);

        @Query("select q from Quotation q where responsable in :responsableToFilter and quotationStatus in :quotationStatusToFilter")
        List<Quotation> searchQuotationsForCurrentUser(
                        @Param("responsableToFilter") List<Responsable> responsablesToFilter,
                        @Param("quotationStatusToFilter") List<QuotationStatus> quotationStatusToFilter,
                        Pageable pageableRequest);

        @Query("select q from Quotation q where responsable in :responsables and quotationStatus in :quotationStatus")
        List<Quotation> searchQuotations(List<Responsable> responsables, List<QuotationStatus> quotationStatus);

        @Query(value = "SELECT nextval('validation_id_quotation_sequence')", nativeQuery = true)
        Integer generateValidationIdForQuotation();

        List<Quotation> findByResponsable(Responsable responsable);

        @Query("select c from Quotation c join c.responsable r join fetch c.assoAffaireOrders a join fetch a.affaire af    "
                        +
                        "  where (0 in :commercials or r.salesEmployee.id in :commercials) " +
                        "    and (0 in :status or  c.quotationStatus.id in :status) order by c.createdDate desc ")
        List<Quotation> searchQuotation(List<Integer> commercials,
                        List<Integer> status);

        @Query(value = "select q from Quotation q where quotationStatus=:quotationStatus and createdDate<:dateLimit ")
        List<Quotation> findQuotationOlderThanDate(@Param("quotationStatus") QuotationStatus quotationStatus,
                        @Param("dateLimit") LocalDateTime dateLimit);
}
