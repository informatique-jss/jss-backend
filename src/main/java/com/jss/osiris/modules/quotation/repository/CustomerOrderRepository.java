package com.jss.osiris.modules.quotation.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.model.OrderingSearchResult;

public interface CustomerOrderRepository extends CrudRepository<CustomerOrder, Integer> {

        @Query(nativeQuery = true, value = "select "
                        + " case when cf.id is not null then cf.label"
                        + " when r.id is not null then  r.firstname || ' '||r.lastname "
                        + " else case when t.denomination is not null and t.denomination!='' then t.denomination else t.firstname || ' '||t.lastname end end as customerOrderLabel,"
                        + " coalesce(case when t2.denomination is not null and t2.denomination!='' then t2.denomination else t.firstname || ' '||t.lastname end, case when t.denomination is not null and t.denomination!='' then t.denomination else t.firstname || ' '||t.lastname end) as tiersLabel,"
                        + " cos.label as customerOrderStatus,"
                        + " co.created_date as createdDate,"
                        + " co.last_status_update as lastStatusUpdate,"
                        + " coalesce(cf.id_commercial,r.id_commercial,t.id_commercial,t2.id_commercial) as salesEmployeeId,"
                        + " co.id_assigned_to as assignedToEmployeeId,"
                        + " co.id as customerOrderId,"
                        + " r.id as responsableId,"
                        + " min(quotation.id_quotation) as quotationId,"
                        + " coalesce(t2.id,t.id) as tiersId,"
                        + " cf.id as confrereId,"
                        + " sum(COALESCE(i.pre_tax_price,0)+COALESCE(i.vat_price,0)-COALESCE(i.discount_amount,0)) as totalPrice ,"
                        + " (select sum(COALESCE(deposit.deposit_amount,0)) from deposit where deposit.id_customer_order = co.id and deposit.is_cancelled=false ) as depositTotalAmount ,"
                        + " STRING_AGG(DISTINCT case when af.denomination is not null and af.denomination!='' then af.denomination else af.firstname || ' '||af.lastname end  || ' ('||city.label ||')' ,', ' ) as affaireLabel,"
                        + " STRING_AGG(DISTINCT af.siren ,', '  ) as affaireSiren,"
                        + " STRING_AGG(DISTINCT af.address ||' '||af.postal_code||' '||city.label ||' '||country.label ,', '  ) as affaireAddress,"
                        + " count(distinct provision.id_announcement) as announcementNbr,"
                        + " count(distinct provision.id_formalite) as formaliteNbr,"
                        + " count(distinct provision.id_bodacc) as bodaccNbr,"
                        + " count(distinct provision.id_domiciliation) as domiciliationNbr,"
                        + " count(distinct provision.id_simple_provision) as simpleProvisionNbr,"
                        + " co.description as customerOrderDescription"
                        + " from customer_order co"
                        + " join customer_order_status cos on cos.id = co.id_customer_order_status"
                        + " left join asso_quotation_customer_order quotation on quotation.id_customer_order = co.id"
                        + " left join asso_affaire_order asso on asso.id_customer_order = co.id"
                        + " left join provision on provision.id_asso_affaire_order = asso.id"
                        + " left join invoice_item i on i.id_provision = provision.id"
                        + " left join affaire af on af.id = asso.id_affaire"
                        + " left join city on af.id_city = city.id"
                        + " left join country on city.id_country = country.id"
                        + " left join confrere cf on cf.id = co.id_confrere"
                        + " left join responsable r on r.id = co.id_responsable"
                        + " left join tiers t on t.id = co.id_tiers"
                        + " left join tiers t2 on t2.id = r.id_tiers"
                        + " left join asso_quotation_customer_order asso_co on asso_co.id_customer_order = co.id "
                        + " where ( COALESCE(:customerOrderStatus) =0 or co.id_customer_order_status in (:customerOrderStatus)) "
                        + " and co.created_date>=:startDate and co.created_date<=:endDate "
                        + " and ( :quotationId =0 or asso_co.id_quotation = :quotationId)"
                        + " and ( COALESCE(:assignedToEmployee) =0 or co.id_assigned_to in (:assignedToEmployee))"
                        + " and ( COALESCE(:salesEmployee) =0 or cf.id_commercial in (:salesEmployee) or r.id_commercial in (:salesEmployee) or t.id_commercial in (:salesEmployee) or t.id_commercial is null and t2.id_commercial in (:salesEmployee))"
                        + " and ( COALESCE(:customerOrder)=0 or cf.id in (:customerOrder) or r.id in (:customerOrder) or t.id in (:customerOrder))"
                        + " and ( COALESCE(:affaire)=0 or af.id in (:affaire) )"
                        + " group by cf.id, cf.label, r.id, r.firstname, r.lastname, t.denomination, t.firstname, t.lastname, t2.denomination, t2.firstname, t2.lastname, cos.label, "
                        + " co.created_date, cf.id_commercial, r.id_commercial, t.id_commercial, t2.id_commercial, co.id, r.id, t.id,t2.id, cf.id, co.description,co.id_assigned_to ")
        List<OrderingSearchResult> findCustomerOrders(@Param("salesEmployee") List<Integer> salesEmployee,
                        @Param("assignedToEmployee") List<Integer> assignedToEmployee,
                        @Param("customerOrderStatus") List<Integer> customerOrderStatus,
                        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
                        @Param("customerOrder") List<Integer> customerOrder, @Param("affaire") List<Integer> affaire,
                        @Param("quotationId") Integer quotationId);

        @Query(value = "select n from CustomerOrder n where customerOrderStatus=:customerOrderStatus and thirdReminderDateTime is null ")
        List<CustomerOrder> findCustomerOrderForReminder(
                        @Param("customerOrderStatus") CustomerOrderStatus customerOrderStatus);

        @Query(value = "select c.* from customer_order c where exists (select 1 from asso_affaire_order a join provision p on p.id_asso_affaire_order = a.id where a.id_customer_order = c.id and  p.id_announcement = :announcementId)", nativeQuery = true)
        Optional<CustomerOrder> findCustomerOrderForAnnouncement(@Param("announcementId") Integer announcementId);

        List<CustomerOrder> findByQuotations_Id(Integer idQuotation);

}
