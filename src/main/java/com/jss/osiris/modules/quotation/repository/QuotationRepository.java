package com.jss.osiris.modules.quotation.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.model.QuotationSearchResult;
import com.jss.osiris.modules.quotation.model.QuotationStatus;

public interface QuotationRepository extends CrudRepository<Quotation, Integer> {

        @Query(nativeQuery = true, value = "select "
                        + " case when cf.id is not null then cf.label"
                        + " when r.id is not null then  r.firstname || ' '||r.lastname "
                        + " else case when t.denomination is not null and t.denomination!='' then t.denomination else t.firstname || ' '||t.lastname end end as customerOrderLabel,"
                        + "  case when t.denomination is not null and t.denomination!='' then t.denomination else t.firstname || ' '||t.lastname end as tiersLabel,"
                        + " cos.label as quotationStatus,"
                        + " co.created_date as createdDate,"
                        + " coalesce(cf.id_commercial,r.id_commercial,t.id_commercial,t2.id_commercial) as salesEmployeeId,"
                        + " co.id as quotationId,"
                        + " r.id as responsableId,"
                        + " t.id as tiersId,"
                        + " cf.id as confrereId,"
                        + " STRING_AGG( case when af.denomination is not null and af.denomination!='' then af.denomination else af.firstname || ' '||af.lastname end,', ' order by 1) as affaireLabel,"
                        + " co.description as quotationDescription"
                        + " from quotation co"
                        + " join quotation_status cos on cos.id = co.id_quotation_status"
                        + "  join asso_affaire_order asso on asso.id_quotation = co.id"
                        + "  join affaire af on af.id = asso.id_affaire"
                        + " left join confrere cf on cf.id = co.id_confrere"
                        + " left join responsable r on r.id = co.id_responsable"
                        + " left join tiers t on t.id = co.id_tiers"
                        + " left join tiers t2 on t2.id = r.id_tiers"
                        + " where ( COALESCE(:customerOrderStatus) =0 or co.id_quotation_status in (:customerOrderStatus)) "
                        + " and co.created_date>=:startDate and co.created_date<=:endDate "
                        + " and ( COALESCE(:salesEmployee) =0 or cf.id_commercial in (:salesEmployee) or r.id_commercial in (:salesEmployee) or t.id_commercial in (:salesEmployee) or t.id_commercial is null and t2.id_commercial in (:salesEmployee))"
                        + " and ( COALESCE(:customerOrder)=0 or cf.id in (:customerOrder) or r.id in (:customerOrder) or t.id in (:customerOrder))"
                        + " group by cf.id, cf.label, r.id, r.firstname, r.lastname, t.denomination, t.firstname, t.lastname, cos.label, "
                        + " co.created_date, cf.id_commercial, r.id_commercial, t.id_commercial, t2.id_commercial, co.id, r.id, t.id, cf.id, co.description ")
        List<QuotationSearchResult> findQuotations(@Param("salesEmployee") List<Integer> salesEmployee,
                        @Param("customerOrderStatus") List<Integer> customerOrderStatus,
                        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
                        @Param("customerOrder") List<Integer> customerOrder);

        @Query(value = "select n from Quotation n where quotationStatus=:quotationStatus and thirdReminderDateTime is null ")
        List<Quotation> findQuotationForReminder(@Param("quotationStatus") QuotationStatus quotationStatus);
}
