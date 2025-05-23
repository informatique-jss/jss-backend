package com.jss.osiris.modules.osiris.quotation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.IWorkflowElement;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.ProvisionBoardResult;

public interface ProvisionRepository extends QueryCacheCrudRepository<Provision, Integer> {

        @Query(nativeQuery = true, value = " " +
                        " select  " +
                        " e.firstname || ' '|| e.lastname as employeeName, " +
                        " e.id as employeeId, " +
                        " coalesce(ans.aggregate_status,fos.aggregate_status,  doms.aggregate_status,sps.aggregate_status) as aggregateStatus, "
                        +
                        " case  " +
                        " when ans.aggregate_status is not null then 'Annonce légale' " +
                        " when fos.aggregate_status is not null then 'Formalité' " +
                        " when doms.aggregate_status is not null then 'Domiciliation' " +
                        " when sps.aggregate_status is not null then 'Formalité simple' " +
                        " end as type, " +
                        " count(*) as number " +
                        " from provision p  " +
                        " join service on service.id = p.id_service " +
                        " join asso_affaire_order asso on asso.id = service.id_asso_affaire_order " +
                        " join customer_order c on c.id = asso.id_customer_order " +
                        " join customer_order_status cs on cs.id = c.id_customer_order_status " +
                        " join employee e on e.id = p.id_employee " +
                        " left join announcement an on an.id = p.id_announcement " +
                        " left join announcement_status ans on ans.id = an.id_announcement_status " +
                        " left join formalite fo on fo.id = p.id_formalite " +
                        " left join formalite_status fos on fos.id = fo.id_formalite_status " +
                        " left join domiciliation dom on dom.id = p.id_domiciliation " +
                        " left join domiciliation_status doms on doms.id = dom.id_domicilisation_status " +
                        " left join simple_provision sp on sp.id = p.id_simple_provision " +
                        " left join simple_provision_status sps on sps.id = sp.id_simple_provision_status " +
                        " where c.id_customer_order_status not in (:customerOrderStatusIdWaitingDeposit,:customerOrderStatusIdAbandonned,:customerOrderStatusIdOpen) "
                        +
                        " and p.id_employee in (:employeeIds) " +
                        " group by e.id, e.firstname, e.lastname,ans.aggregate_status,fos.aggregate_status, doms.aggregate_status,sps.aggregate_status "
                        +
                        "")
        List<ProvisionBoardResult> getDashboardEmployee(@Param("employeeIds") List<Integer> employeeIds,
                        @Param("customerOrderStatusIdAbandonned") Integer customerOrderStatusIdAbandonned,
                        @Param("customerOrderStatusIdWaitingDeposit") Integer customerOrderStatusIdWaitingDeposit,
                        @Param("customerOrderStatusIdOpen") Integer customerOrderStatusIdOpen);

        @Query("select p from Provision p left join p.announcement " +
                        " left join p.simpleProvision left join p.domiciliation left join p.formalite " +
                        " join fetch p.service s join fetch s.assoAffaireOrder a join fetch a.affaire  join fetch a.customerOrder co "
                        +
                        " where (p.announcement.announcementStatus in (:announcementStatus) or" +
                        " p.formalite.formaliteStatus in (:formaliteStatus) or " +
                        " p.simpleProvision.simpleProvisionStatus in (:simpleProvisionStatus) or " +
                        " p.domiciliation.domiciliationStatus in (:domiciliationStatus))  " +
                        " and    (0 in :formalisteIds or p.assignedTo.id in :formalisteIds) and co.customerOrderStatus =:customerOrderStatusInProgress    ")
        List<Provision> searchProvision(List<Integer> formalisteIds, List<IWorkflowElement> announcementStatus,
                        List<IWorkflowElement> simpleProvisionStatus, List<IWorkflowElement> formaliteStatus,
                        List<IWorkflowElement> domiciliationStatus, CustomerOrderStatus customerOrderStatusInProgress);
}