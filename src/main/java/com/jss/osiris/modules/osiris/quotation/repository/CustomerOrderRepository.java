package com.jss.osiris.modules.osiris.quotation.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.crm.model.Voucher;
import com.jss.osiris.modules.osiris.miscellaneous.model.AttachmentType;
import com.jss.osiris.modules.osiris.miscellaneous.model.CustomerOrderOrigin;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.AssignationType;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.IOrderingSearchTaggedResult;
import com.jss.osiris.modules.osiris.quotation.model.OrderingSearchResult;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

import jakarta.persistence.QueryHint;

public interface CustomerOrderRepository
                extends QueryCacheCrudRepository<CustomerOrder, Integer>,
                PagingAndSortingRepository<CustomerOrder, Integer> {

        @Query(nativeQuery = true, value = "select "
                        + "  r.firstname || ' '||r.lastname as customerOrderLabel,"
                        + " case when t2.denomination is not null and t2.denomination!='' then t2.denomination else t2.firstname || ' '||t2.lastname end as tiersLabel,"
                        + " cos.label as customerOrderStatus,"
                        + " co.created_date as createdDate,"
                        + " co.last_status_update as lastStatusUpdate,"
                        + " coalesce( r.id_commercial ,t2.id_commercial) as salesEmployeeId,"
                        + " co.id as customerOrderId,"
                        + " r.id as responsableId,"
                        + " min(quotation.id_quotation) as quotationId,"
                        + " t2.id as tiersId,"
                        + " origin.label as customerOrderOriginLabel,"
                        + " STRING_AGG(DISTINCT service.service_label_to_display,', ') as serviceTypeLabel,"
                        + " sum(COALESCE(i.pre_tax_price,0)+COALESCE(i.vat_price,0)-COALESCE(i.discount_amount,0)) as totalPrice ,"
                        + " (select sum(COALESCE(payment.payment_amount,0)) from  payment where payment.id_customer_order = co.id and payment.is_cancelled=false ) as depositTotalAmount ,"
                        + " STRING_AGG(DISTINCT case when af.denomination is not null and af.denomination!='' then af.denomination else af.firstname || ' '||af.lastname end  || ' ('||city.label ||')' ,', ' ) as affaireLabel,"
                        + " STRING_AGG(DISTINCT af.siren ,', '  ) as affaireSiren,"
                        + " STRING_AGG(DISTINCT af.address ||' '||af.postal_code||' '||city.label ||' '||country.label ,', '  ) as affaireAddress,"
                        + " co.id_customer_order_parent_recurring as customerOrderParentRecurringId, "
                        + " co.recurring_period_start_date  as recurringPeriodStartDate, "
                        + " co.recurring_period_end_date  as recurringPeriodEndDate, "
                        + " co.recurring_start_date  as recurringStartDate, "
                        + " co.recurring_end_date  as recurringEndDate, "
                        + " co.is_recurring_automatically_billed  as isRecurringAutomaticallyBilled, "
                        + " co.description as customerOrderDescription,co.production_effective_date_time as productionEffectiveDate"
                        + " from customer_order co"
                        + " join customer_order_origin origin on origin.id = co.id_customer_order_origin"
                        + " join customer_order_status cos on cos.id = co.id_customer_order_status"
                        + " left join asso_quotation_customer_order quotation on quotation.id_customer_order = co.id"
                        + " left join asso_affaire_order asso on asso.id_customer_order = co.id"
                        + " left join service on service.id_asso_affaire_order = asso.id"
                        + " left join provision on provision.id_service = service.id"
                        + " left join invoice_item i on i.id_provision = provision.id"
                        + " left join affaire af on af.id = asso.id_affaire"
                        + " left join city on af.id_city = city.id"
                        + " left join country on city.id_country = country.id"
                        + " left join responsable r on r.id = co.id_responsable"
                        + " left join tiers t2 on t2.id = r.id_tiers"
                        + " left join asso_quotation_customer_order asso_co on asso_co.id_customer_order = co.id "
                        + " where ( COALESCE(:customerOrderStatus) =0 or co.id_customer_order_status in (:customerOrderStatus)) "
                        + " and co.created_date>=:startDate and co.created_date<=:endDate "
                        + " and ( :quotationId =0 or asso_co.id_quotation = :quotationId)"
                        + " and ( :idCustomerOrder =0 or co.id = :idCustomerOrder)"
                        + " and ( :recurringValidityDate<='1950-01-01'  or :isDisplayOnlyParentRecurringCustomerOrder =true and co.recurring_period_start_date <= :recurringValidityDate and  co.recurring_period_end_date >= :recurringValidityDate or  :isDisplayOnlyRecurringCustomerOrder =true and co.recurring_start_date <= :recurringValidityDate and  co.recurring_end_date >= :recurringValidityDate)"
                        + " and ( :isDisplayOnlyRecurringCustomerOrder =false or co.is_recurring = true or co.id_customer_order_parent_recurring is not null)"
                        + " and ( :isDisplayOnlyParentRecurringCustomerOrder =false or co.is_recurring = true)"
                        + " and ( :idCustomerOrderParentRecurring =0 or co.id_customer_order_parent_recurring = :idCustomerOrderParentRecurring)"
                        + " and ( :idCustomerOrderChildRecurring =0 or co.id = (select id_customer_order_parent_recurring from customer_order where id =:idCustomerOrderChildRecurring))"
                        + " and ( COALESCE(:salesEmployee) =0 or  r.id_commercial in (:salesEmployee)  or  t2.id_commercial in (:salesEmployee))"
                        + " and ( COALESCE(:customerOrder)=0 or  r.id in (:customerOrder) or t2.id in (:customerOrder))"
                        + " and ( COALESCE(:affaire)=0 or af.id =:affaire )"
                        + " group by r.id, r.firstname,origin.label,  r.lastname,  t2.denomination, t2.firstname, t2.lastname, cos.label, "
                        + " co.created_date, co.production_effective_date_time, r.id_commercial, t2.id_commercial, co.id, r.id, t2.id,  co.description ")
        List<OrderingSearchResult> findCustomerOrders(@Param("salesEmployee") List<Integer> salesEmployee,
                        @Param("customerOrderStatus") List<Integer> customerOrderStatus,
                        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
                        @Param("customerOrder") List<Integer> customerOrder, @Param("affaire") Integer affaire,
                        @Param("quotationId") Integer quotationId, @Param("idCustomerOrder") Integer idCustomerOrder,
                        @Param("idCustomerOrderParentRecurring") Integer idCustomerOrderParentRecurring,
                        @Param("idCustomerOrderChildRecurring") Integer idCustomerOrderChildRecurring,
                        @Param("isDisplayOnlyRecurringCustomerOrder") Boolean isDisplayOnlyRecurringCustomerOrder,
                        @Param("isDisplayOnlyParentRecurringCustomerOrder") Boolean isDisplayOnlyParentRecurringCustomerOrder,
                        @Param("recurringValidityDate") LocalDate recurringValidityDate);

        @Query(nativeQuery = true, value = "select "
                        + " case when t2.denomination is not null and t2.denomination!='' then t2.denomination else t2.firstname || ' '||t2.lastname end as tiersLabel,"
                        + " cos.label as customerOrderStatus,"
                        + " co.created_date as createdDate,"
                        + " co.last_status_update as lastStatusUpdate,"
                        + " coalesce( r.id_commercial ,t2.id_commercial) as salesEmployeeId,"
                        + " co.id as customerOrderId,"
                        + " r.id as responsableId,"
                        + " min(quotation.id_quotation) as quotationId,"
                        + " t2.id as tiersId,"
                        + " origin.label as customerOrderOriginLabel,"
                        + " STRING_AGG(DISTINCT service.service_label_to_display,', ') as serviceTypeLabel,"
                        + " sum(COALESCE(i.pre_tax_price,0)+COALESCE(i.vat_price,0)-COALESCE(i.discount_amount,0)) as totalPrice ,"
                        + " (select sum(COALESCE(payment.payment_amount,0)) from  payment where payment.id_customer_order = co.id and payment.is_cancelled=false ) as depositTotalAmount ,"
                        + " STRING_AGG(DISTINCT case when af.denomination is not null and af.denomination!='' then af.denomination else af.firstname || ' '||af.lastname end  || ' ('||city.label ||')' ,', ' ) as affaireLabel,"
                        + " STRING_AGG(DISTINCT af.siren ,', '  ) as affaireSiren,"
                        + " STRING_AGG(DISTINCT af.address ||' '||af.postal_code||' '||city.label ||' '||country.label ,', '  ) as affaireAddress,"
                        + " co.id_customer_order_parent_recurring as customerOrderParentRecurringId, "
                        + " co.recurring_period_start_date  as recurringPeriodStartDate, "
                        + " co.recurring_period_end_date  as recurringPeriodEndDate, "
                        + " co.recurring_start_date  as recurringStartDate, "
                        + " co.recurring_end_date  as recurringEndDate, "
                        + " co.is_recurring_automatically_billed  as isRecurringAutomaticallyBilled, "
                        + " co.description as customerOrderDescription,co.production_effective_date_time as productionEffectiveDate,  "
                        + " STRING_AGG(DISTINCT grp.label, ', ') as activeDirectoryGroupLabel "
                        + " from customer_order co"
                        + " join customer_order_origin origin on origin.id = co.id_customer_order_origin"
                        + " join customer_order_status cos on cos.id = co.id_customer_order_status"
                        + " left join asso_quotation_customer_order quotation on quotation.id_customer_order = co.id"
                        + " left join asso_affaire_order asso on asso.id_customer_order = co.id"
                        + " left join service on service.id_asso_affaire_order = asso.id"
                        + " left join provision on provision.id_service = service.id"
                        + " left join invoice_item i on i.id_provision = provision.id"
                        + " left join affaire af on af.id = asso.id_affaire"
                        + " left join city on af.id_city = city.id"
                        + " left join country on city.id_country = country.id"
                        + " left join responsable r on r.id = co.id_responsable"
                        + " left join tiers t2 on t2.id = r.id_tiers"
                        + " left join asso_quotation_customer_order asso_co on asso_co.id_customer_order = co.id "
                        + " join customer_order_comment com on co.id= com.id_customer_order "
                        + " join asso_customer_order_comment_active_directory_group asso_grp on com.id = asso_grp.id_customer_order_comment "
                        + " join active_directory_group grp on asso_grp.id_active_directory_group = grp.id "
                        + " where ( COALESCE(:customerOrderStatus) =0 or co.id_customer_order_status in (:customerOrderStatus)) "
                        + " and co.created_date>=:startDate and co.created_date<=:endDate "
                        + " and ( COALESCE(:assignedToEmployee) =0 or provision.id_employee in (:assignedToEmployee))"
                        + " and ( COALESCE(:salesEmployee) =0 or r.id_commercial in (:salesEmployee)  or t2.id_commercial in (:salesEmployee))"
                        + " and ( COALESCE(:activeDirectoryGroupId)=0 or exists (select 1 from asso_customer_order_comment_active_directory_group asso_grp2 "
                        + " join active_directory_group grp2 on asso_grp2.id_active_directory_group = grp2.id "
                        + " where grp2.id =:activeDirectoryGroupId and asso_grp2.id_customer_order_comment = com.id) ) "
                        + " and ( :isDisplayOnlyUnread and COALESCE(com.is_read,false)=false or :isDisplayOnlyUnread=false) "
                        + " group by   r.id, r.firstname,origin.label,  r.lastname,  t2.denomination, t2.firstname, t2.lastname, cos.label, "
                        + " co.created_date,co.production_effective_date_time,  r.id_commercial,  t2.id_commercial, co.id, r.id,  t2.id,   co.description ")
        List<IOrderingSearchTaggedResult> findTaggedCustomerOrders(
                        @Param("customerOrderStatus") List<Integer> customerOrderStatus,
                        @Param("salesEmployee") List<Integer> salesEmployee,
                        @Param("assignedToEmployee") List<Integer> assignedToEmployee,
                        @Param("activeDirectoryGroupId") Integer activeDirectoryGroupId,
                        @Param("isDisplayOnlyUnread") Boolean isDisplayOnlyUnread,
                        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

        @Query(value = "select n from CustomerOrder n where customerOrderStatus=:customerOrderStatus and thirdReminderDateTime is null ")
        List<CustomerOrder> findCustomerOrderForReminder(
                        @Param("customerOrderStatus") CustomerOrderStatus customerOrderStatus);

        @Query(value = "select c.* from customer_order c where exists (select 1 from asso_affaire_order a join service s on a.id =s.id_asso_affaire_order  join provision p on p.id_service = s.id where a.id_customer_order = c.id and  p.id_announcement = :announcementId)", nativeQuery = true)
        Optional<CustomerOrder> findCustomerOrderForAnnouncement(@Param("announcementId") Integer announcementId);

        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        List<CustomerOrder> findByQuotations_Id(Integer idQuotation);

        @Query(value = "select c from CustomerOrder c where isRecurring = true and customerOrderStatus not in (:openStatus, :waitingDepositStatus,:abandonnedStatus) and recurringPeriodStartDate<=current_date() and recurringPeriodEndDate>current_date()")
        List<CustomerOrder> findAllActiveRecurringCustomerOrders(@Param("openStatus") CustomerOrderStatus openStatus,
                        @Param("waitingDepositStatus") CustomerOrderStatus waitingDepositStatus,
                        @Param("abandonnedStatus") CustomerOrderStatus abandonnedStatus);

        List<CustomerOrder> findByCustomerOrderParentRecurringOrderByRecurringEndDateDesc(CustomerOrder customerOrder);

        @Query("select c from CustomerOrder c " +
                        " where responsable in :responsableToFilter " +
                        " and (customerOrderStatus in :customerorderStatusToFilter and coalesce(c.isPayed,false)=false or customerOrderStatus=:customerOrderStatusBilled and coalesce(c.isPayed,false)=true and :displayPayed=true)"
                        +
                        " and (:withMissingAttachment=false or exists (select 1 from AssoAffaireOrder aao join aao.services s join s.missingAttachmentQueries mq join s.provisions p  left join p.formalite f  on f.formaliteStatus.code = :formaliteStatusWaitingDoc left join p.simpleProvision sp  on sp.simpleProvisionStatus.code = :simpleProvisionStatusWaitingDoc left join p.announcement a on a.announcementStatus.code = :announcementStatusWaitingDoc where aao.customerOrder=c   and (a is not null or f is not null or sp is not null)  ) )")
        List<CustomerOrder> searchOrdersForCurrentUser(
                        @Param("responsableToFilter") List<Responsable> responsablesToFilter,
                        @Param("customerorderStatusToFilter") List<CustomerOrderStatus> customerOrderStatusToFilter,
                        Pageable pageableRequest,
                        @Param("customerOrderStatusBilled") CustomerOrderStatus customerOrderStatusBilled,
                        @Param("displayPayed") boolean displayPayed,
                        @Param("withMissingAttachment") boolean withMissingAttachment,
                        @Param("announcementStatusWaitingDoc") String announcementStatusWaitingDoc,
                        @Param("formaliteStatusWaitingDoc") String formaliteStatusWaitingDoc,
                        @Param("simpleProvisionStatusWaitingDoc") String simpleProvisionStatusWaitingDoc);

        @Query("select c from CustomerOrder c " +
                        " where  " +
                        "   responsable in :responsableToFilter and  exists (select 1 from AssoAffaireOrder aao where aao.affaire = :affaire and aao.customerOrder = c)")
        List<CustomerOrder> searchOrdersForCurrentUserAndAffaire(
                        @Param("responsableToFilter") List<Responsable> responsablesToFilter,
                        @Param("affaire") Affaire affaire);

        @Query("select c from CustomerOrder c where " +
                        " responsable in :responsables " +
                        " and customerOrderStatus in :customerOrderStatusToFilter" +
                        " and (:withMissingAttachment=false or exists (select 1 from AssoAffaireOrder aao join aao.services s join s.missingAttachmentQueries mq join s.provisions p  left join p.formalite f  on f.formaliteStatus.code = :formaliteStatusWaitingDoc left join p.simpleProvision sp  on sp.simpleProvisionStatus.code = :simpleProvisionStatusWaitingDoc left join p.announcement a on a.announcementStatus.code = :announcementStatusWaitingDoc where aao.customerOrder=c   and (a is not null or f is not null or sp is not null)  ) )")
        List<CustomerOrder> searchOrders(List<Responsable> responsables,
                        List<CustomerOrderStatus> customerOrderStatusToFilter,
                        @Param("withMissingAttachment") boolean withMissingAttachment,
                        @Param("announcementStatusWaitingDoc") String announcementStatusWaitingDoc,
                        @Param("formaliteStatusWaitingDoc") String formaliteStatusWaitingDoc,
                        @Param("simpleProvisionStatusWaitingDoc") String simpleProvisionStatusWaitingDoc);

        List<CustomerOrder> findByResponsable(Responsable responsable);

        List<CustomerOrder> findByResponsableInAndCreatedDateBetween(List<Responsable> responsable,
                        LocalDateTime startOfDay,
                        LocalDateTime endOfDay);

        @Query("SELECT c FROM CustomerOrder c " +
                        "WHERE c.createdDate BETWEEN :startOfDay AND :endOfDay " +
                        "AND (:status IS NULL OR c.customerOrderStatus = :status)")
        List<CustomerOrder> findByCreatedDateBetweenAndStatus(
                        @Param("startOfDay") LocalDateTime startOfDay,
                        @Param("endOfDay") LocalDateTime endOfDay,
                        @Param("status") CustomerOrderStatus customerOrderStatus);

        @Query("select c from CustomerOrder c left join c.responsable r left join fetch c.assoAffaireOrders a left join fetch a.affaire af "
                        +
                        "  where (0 in :commercials or r.salesEmployee.id in :commercials) " +
                        " and (0 in :invoicingEmployees or   c.invoicingEmployee.id in :invoicingEmployees)  " +
                        " and (1 not in :invoicingEmployees or  c.invoicingEmployee is not null)  " +
                        " and (0 in :orderingEmployees or   c.orderingEmployee.id in :orderingEmployees)  " +
                        " and (1 not in :orderingEmployees or  c.orderingEmployee is not null)  "
                        +
                        "    and (0 in :status or  c.customerOrderStatus.id in :status) order by c.createdDate desc ")
        List<CustomerOrder> searchCustomerOrders(List<Integer> commercials,
                        List<Integer> status, List<Integer> invoicingEmployees, List<Integer> orderingEmployees);

        @Query("select distinct c from CustomerOrder c  join c.assoAffaireOrders a  join a.services s join s.provisions p join p.attachments att  "
                        +
                        "  where  " +
                        "  att.attachmentType=:attachmentType  and coalesce(att.isValidated,false) = false " +
                        "    and   c.customerOrderStatus = :status   ")
        List<CustomerOrder> searchCustomerOrdersWithInvoiceToFill(AttachmentType attachmentType,
                        CustomerOrderStatus status,
                        Pageable pageableRequest);

        @Query("select c from CustomerOrder c where invoicingEmployee is null and c.customerOrderStatus=:customerOrderStatusToBilled ")
        List<CustomerOrder> findNewCustomerOrderToBilled(CustomerOrderStatus customerOrderStatusToBilled,
                        Pageable pageableRequest);

        @Query("select c from CustomerOrder c where orderingEmployee is null and c.customerOrderStatus=:customerOrderStatusDraft and c.customerOrderOrigin=:origin ")
        List<CustomerOrder> findNewCustomerOrderToOrder(CustomerOrderStatus customerOrderStatusDraft,
                        CustomerOrderOrigin origin,
                        Pageable pageableRequest);

        @Query("select c from CustomerOrder c where c.customerOrderStatus<>:statusAbandonned AND (:responsable IS NULL OR c.responsable = :responsable) and voucher=:voucher")
        List<CustomerOrder> findByVoucherAndResponsable(Voucher voucher, Responsable responsable,
                        CustomerOrderStatus statusAbandonned);

        List<CustomerOrder> findByVoucher(Voucher voucher);

        @Query(value = "select c from CustomerOrder c where customerOrderStatus=:customerOrderStatus and createdDate<:dateLimit ")
        List<CustomerOrder> findCustomerOrderOlderThanDate(
                        @Param("customerOrderStatus") CustomerOrderStatus customerOrderStatus,
                        @Param("dateLimit") LocalDateTime dateLimit);

        @Query("select c from CustomerOrder c join c.responsable r join c.customerOrderAssignations a where a.assignationType=:assignationType and r.formalisteEmployee in (:employees) and c.customerOrderStatus=:customerOrderStatus and a.isAssigned=false and (:assignedUser is null or a.employee = :assignedUser)  order by c.productionEffectiveDateTime")
        List<CustomerOrder> findCustomerOrderByFormalisteAndStatusAssigned(List<Employee> employees,
                        CustomerOrderStatus customerOrderStatus, Employee assignedUser,
                        AssignationType assignationType);

        @Query("select c from CustomerOrder c join c.responsable r join c.customerOrderAssignations a where a.assignationType=:assignationType and r.insertionEmployee in (:employees) and c.customerOrderStatus=:customerOrderStatus and a.isAssigned=false and (:assignedUser is null or a.employee = :assignedUser)  order by c.productionEffectiveDateTime")
        List<CustomerOrder> findCustomerOrderByPubliscisteAndStatusAssigned(List<Employee> employees,
                        CustomerOrderStatus customerOrderStatus, Employee assignedUser,
                        AssignationType assignationType);

        @Query("select c from CustomerOrder c join c.customerOrderAssignations a where  c.customerOrderStatus=:customerOrderStatus and a.isAssigned=false and  a.employee = :assignedUser  order by c.productionEffectiveDateTime")
        List<CustomerOrder> findCustomerOrderByForcedEmployeeAndStatusAssigned(CustomerOrderStatus customerOrderStatus,
                        Employee assignedUser);

}
