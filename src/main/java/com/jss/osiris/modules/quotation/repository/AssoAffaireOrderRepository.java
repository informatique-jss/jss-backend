package com.jss.osiris.modules.quotation.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrderSearchResult;

public interface AssoAffaireOrderRepository extends QueryCacheCrudRepository<AssoAffaireOrder, Integer> {

        @Query(nativeQuery = true, value = "select case when a.denomination is not null and a.denomination!='' then a.denomination else a.firstname || ' '||a.lastname end   as affaireLabel,"
                        +
                        " a.address || ' - ' || a.postal_Code ||' - '||ci.label as affaireAddress," +
                        "  coalesce(case when t.denomination is not null and t.denomination!='' then t.denomination else t.firstname || ' '||t.lastname end,"
                        + "case when t2.denomination is not null and t2.denomination!='' then t2.denomination else t2.firstname || ' '||t2.lastname end) as tiersLabel,"
                        +
                        " r.firstname || ' '||r.lastname as responsableLabel," +
                        " cf.label as confrereLabel," +
                        " e1.id as responsibleId," +
                        " e2.id as assignedToId," +
                        " pf.label ||' - '||pt.label as provisionTypeLabel," +
                        " coalesce(ans.label,fs.label,doms.label, sps.label) as statusLabel," +
                        " asso.id_customer_order as customerOrderId," +
                        " asso.id as assoId," +
                        " p.is_emergency as isEmergency," +
                        " p.id as provisionId, " +
                        " max(audit.datetime) as provisionStatusDatetime, " +
                        " coalesce(min(audit2.created_date),c.created_date) as provisionCreatedDatetime, " +
                        " sp_ca.label as waitedCompetentAuthorityLabel, " +
                        " ca.label as competentAuthorityLabel " +
                        " from asso_affaire_order asso " +
                        " join affaire a on a.id = asso.id_affaire" +
                        " join customer_order c on c.id = asso.id_customer_order" +
                        " join customer_order_status cs on cs.id = c.id_customer_order_status" +
                        " join provision p on p.id_asso_affaire_order = asso.id" +
                        " left join city ci on ci.id = a.id_city" +
                        " left join tiers t on t.id = c.id_tiers" +
                        " left join responsable r on r.id = c.id_responsable" +
                        " left join tiers t2 on r.id_tiers = t2.id" +
                        " left join employee e1 on e1.id = asso.id_employee" +
                        " left join employee e2 on e2.id = p.id_employee" +
                        " left join provision_type pt on pt.id = p.id_provision_type" +
                        " left join provision_family_type pf on pf.id = pt.id_provision_family_type" +
                        " left join announcement an on an.id = p.id_announcement " +
                        " left join announcement_status ans on an.id_announcement_status = ans.id" +
                        " left join confrere cf on cf.id = an.id_confrere" +
                        " left join formalite fo on fo.id = p.id_formalite" +
                        " left join formalite_status fs on fs.id = fo.id_formalite_status" +
                        " left join domiciliation dom on dom.id = p.id_domiciliation" +
                        " left join domiciliation_status doms on doms.id = dom.id_domicilisation_status " +
                        " left join simple_provision sp on sp.id = p.id_simple_provision" +
                        " left join simple_provision_status sps on sps.id = sp.id_simple_provision_status " +
                        " left join competent_authority sp_ca on sp_ca.id = sp.id_waited_competent_authority " +
                        " left join competent_authority ca on ca.id = a.id_competent_authority " +
                        " left join audit on " +
                        "  audit.entity_id=an.id and audit.entity = 'Announcement' and audit.field_name = 'announcementStatus' "
                        +
                        "  or audit.entity_id=fo.id and audit.entity = 'Formalite' and audit.field_name = 'formaliteStatus' "
                        +
                        "  or audit.entity_id=dom.id and audit.entity = 'Domiciliation' and audit.field_name = 'domiciliationStatus' "
                        +
                        "  or audit.entity_id=sp.id and audit.entity = 'SimpleProvision' and audit.field_name = 'simpleProvisionStatus' "
                        +
                        " left join index_entity audit2 on " +
                        "  audit2.entity_id=an.id and audit2.entity_type in ('Announcement','Formalite','Domiciliation','SimpleProvision')  "
                        +
                        " where cs.code not in (:excludedCustomerOrderStatusCode) and (COALESCE(:responsible)=0 or asso.id_employee in (:responsible))"
                        + " and ( COALESCE(:customerOrder)=0 or r.id in (:customerOrder) or t.id in (:customerOrder))"
                        +
                        " and ( :waitedCompetentAuthorityId =0 or sp.id_waited_competent_authority =:waitedCompetentAuthorityId) "
                        +
                        " and ( :affaireId =0 or a.id =:affaireId) "
                        +
                        " and ( COALESCE(:assignedTo) =0 or p.id_employee in (:assignedTo)) " +
                        " and (:label ='' or upper(a.denomination)  like '%' || upper(CAST(:label as text))  || '%'  or upper(a.firstname)  like '%' || upper(CAST(:label as text)) || '%' or upper(a.lastname)  like '%' || upper(CAST(:label as text)) || '%') "
                        +
                        " and (COALESCE(:status) =0 or coalesce(ans.id,fs.id,doms.id,sps.id) in (:status) ) " +
                        " group by a.denomination, a.firstname , a.lastname,  " +
                        "  t.denomination, t.firstname , t.lastname,  " +
                        "  t2.denomination, t2.firstname , t2.lastname,  " +
                        "  r.firstname , r.lastname, asso.id, " +
                        "  a.address ,a.postal_Code ,ci.label ,c.created_date,  " +
                        "  cf.label,e1.id,e2.id , pf.label ,pt.label,ans.label,fs.label,doms.label,  sps.label, "
                        +
                        " asso.id_customer_order,p.is_emergency,p.id  ,sp_ca.label,ca.label " +
                        "")
        ArrayList<AssoAffaireOrderSearchResult> findAsso(@Param("responsible") List<Integer> responsibleIds,
                        @Param("assignedTo") List<Integer> assignedToIds,
                        @Param("label") String label, @Param("status") ArrayList<Integer> status,
                        @Param("excludedCustomerOrderStatusCode") List<String> excludedCustomerOrderStatusCode,
                        @Param("customerOrder") List<Integer> customerOrder,
                        @Param("waitedCompetentAuthorityId") Integer waitedCompetentAuthorityId,
                        @Param("affaireId") Integer affaireId);
}