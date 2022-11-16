package com.jss.osiris.modules.quotation.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrderSearchResult;

public interface AssoAffaireOrderRepository extends CrudRepository<AssoAffaireOrder, Integer> {

        @Query(nativeQuery = true, value = "select case when a.denomination is not null and a.denomination!='' then a.denomination else a.firstname || ' '||a.lastname end as affaireLabel,"
                        +
                        " a.address || ' - ' || a.postal_Code ||' - '||ci.label as affaireAddress," +
                        " case when t.denomination is not null and t.denomination!='' then t.denomination else t.firstname || ' '||t.lastname end as tiersLabel,"
                        +
                        " r.firstname || ' '||r.lastname as responsableLabel," +
                        " cf.label as confrereLabel," +
                        " e1.id as responsibleId," +
                        " e2.id as assignedToId," +
                        " pf.label ||' - '||pt.label as provisionTypeLabel," +
                        " coalesce(ans.label,fs.label,doms.label, bos.label) as statusLabel," +
                        " asso.id as assoId," +
                        " p.id as provisionId" +
                        " from asso_affaire_order asso " +
                        " join affaire a on a.id = asso.id_affaire" +
                        " join customer_order c on c.id = asso.id_customer_order" +
                        " join provision p on p.id_asso_affaire_order = asso.id" +
                        " left join city ci on ci.id = a.id_city" +
                        " left join tiers t on t.id = c.id_tiers" +
                        " left join responsable r on r.id = c.id_responsable" +
                        " left join confrere cf on cf.id = c.id_confrere" +
                        " left join employee e1 on e1.id = asso.id_employee" +
                        " left join employee e2 on e2.id = p.id_employee" +
                        " left join provision_type pt on pt.id = p.id_provision_type" +
                        " left join provision_family_type pf on pf.id = pt.id_provision_family_type" +
                        " left join announcement an on a.id = p.id_announcement " +
                        " left join announcement_status ans on an.id_announcement_status = ans.id" +
                        " left join formalite fo on fo.id = p.id_formalite" +
                        " left join formalite_status fs on fs.id = fo.id_formalite_status" +
                        " left join domiciliation dom on dom.id = p.id_domiciliation" +
                        " left join domiciliation_status doms on doms.id = dom.id_domicilisation_status " +
                        " left join bodacc bo on bo.id = p.id_bodacc" +
                        " left join bodacc_status bos on bos.id = bo.id_bodacc_status" +
                        " where (COALESCE(:responsible) is null or asso.id_employee in (:responsible))" +
                        " and ( COALESCE(:assignedTo) is null or p.id_employee in (:assignedTo)) " +
                        " and (:label ='' or upper(a.denomination)  like '%' || upper(CAST(:label as text))  || '%'  or upper(a.firstname)  like '%' || upper(CAST(:label as text)) || '%' or upper(a.lastname)  like '%' || upper(CAST(:label as text)) || '%') "
                        +
                        " and (COALESCE(:status) is null or coalesce(ans.id,fs.id,doms.id, bos.id) in (:status) )")
        ArrayList<AssoAffaireOrderSearchResult> findAsso(@Param("responsible") List<Integer> responsibleIds,
                        @Param("assignedTo") List<Integer> assignedToIds,
                        @Param("label") String label, @Param("status") ArrayList<Integer> arrayList);
}