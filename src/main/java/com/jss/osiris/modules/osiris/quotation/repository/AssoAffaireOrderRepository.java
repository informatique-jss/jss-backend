package com.jss.osiris.modules.osiris.quotation.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrderSearchResult;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;

public interface AssoAffaireOrderRepository extends QueryCacheCrudRepository<AssoAffaireOrder, Integer> {

        @Query(nativeQuery = true, value = "with efi as (select id_formalite_infogreffe , code_etat from ( " +
                        "select id_formalite_infogreffe , sum(1) over(partition by id_formalite_infogreffe order by created_date desc) as n, code_etat  "
                        +
                        "from evenement_infogreffe ei where code_etat is not null and length(trim(code_etat))>0) t where n = 1) "
                        +
                        "select case when a.denomination is not null and a.denomination!='' then a.denomination else a.firstname || ' '||a.lastname end   as affaireLabel,"
                        +
                        " ci.label ||' - '|| a.address || ' - ' || a.postal_Code as affaireAddress," +
                        "case when t2.denomination is not null and t2.denomination!='' then t2.denomination else t2.firstname || ' '||t2.lastname end as tiersLabel,"
                        +
                        " r.firstname || ' '||r.lastname as responsableLabel," +
                        " cf.label as confrereLabel," +
                        " e1.id as responsibleId," +
                        " e2.id as assignedToId," +
                        " pf.label ||' - '||pt.label as provisionTypeLabel," +
                        " coalesce(ans.label,fs.label,doms.label, sps.label) as statusLabel," +
                        " asso.id_customer_order as customerOrderId," +
                        " STRING_AGG(DISTINCT case when service.custom_label is null then st.label else service.custom_label  end,', ') as serviceTypeLabel,"
                        +
                        " asso.id as assoId," +
                        " p.is_emergency as isEmergency," +
                        " p.id as provisionId, " +
                        " max(audit.datetime) as provisionStatusDatetime, " +
                        " c.production_effective_date_time as provisionCreatedDatetime,c.created_date as createdDate, "
                        +
                        " sp_ca.label as waitedCompetentAuthorityLabel, " +
                        " ca.label as competentAuthorityLabel " +
                        " from asso_affaire_order asso " +
                        " join affaire a on a.id = asso.id_affaire" +
                        " join customer_order c on c.id = asso.id_customer_order" +
                        " join customer_order_status cs on cs.id = c.id_customer_order_status" +
                        " join service on service.id_asso_affaire_order = asso.id" +
                        " join service_type st on st.id = service.id_service_type" +
                        " join provision p on p.id_service = service.id" +
                        " left join city ci on ci.id = a.id_city" +
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
                        " left join formalite_guichet_unique fgu on fgu.id_formalite = fo.id" +
                        " left join formalite_status fs on fs.id = fo.id_formalite_status" +
                        " left join formalite_infogreffe fi on fi.id_formalite = fo.id" +
                        " left join efi on efi.id_formalite_infogreffe = fi.id" +
                        " left join domiciliation dom on dom.id = p.id_domiciliation" +
                        " left join domiciliation_status doms on doms.id = dom.id_domicilisation_status " +
                        " left join simple_provision sp on sp.id = p.id_simple_provision" +
                        " left join simple_provision_status sps on sps.id = sp.id_simple_provision_status " +
                        " left join missing_attachment_query ma on ma.id_service = service.id and ma.third_customer_reminder_date_time is not null and (sp.id_simple_provision_status = :simpleProvisionStatusWaitingAttachmentId or fo.id_formalite_status = :formaliteStatusWaitingAttachmentId ) and  ma.third_customer_reminder_date_time+INTERVAL '8 day' < now() "
                        +
                        " left join competent_authority sp_ca on sp_ca.id = coalesce(sp.id_waited_competent_authority,fo.id_waited_competent_authority) "
                        +
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
                        " where (COALESCE(:customerOrder)!=0 or cs.code not in (:excludedCustomerOrderStatusCode)) and (COALESCE(:responsible)=0 or asso.id_employee in (:responsible))"
                        + " and ( COALESCE(:customerOrder)=0 or r.id in (:customerOrder)  )"
                        +
                        " and ( :waitedCompetentAuthorityId =0 or sp.id_waited_competent_authority =:waitedCompetentAuthorityId) "
                        +
                        " and ( :affaireId =0 or a.id =:affaireId) " +
                        " and ( :isMissingQueriesToManualRemind = false or ma.id is not null) " +
                        " and ( :commercialId = 0 or t2.id_commercial=:commercialId) " +
                        " and ( :formaliteGuichetUniqueStatusCode = '0' or fgu.id_status=:formaliteGuichetUniqueStatusCode) "
                        +
                        " and ( :formaliteInfogreffeStatusCode = '0' or efi.code_etat=:formaliteInfogreffeStatusCode) "
                        +
                        " and ( COALESCE(:assignedTo) =0 or p.id_employee in (:assignedTo)) " +
                        " and (:label ='' or upper(a.denomination)  like '%' || upper(CAST(:label as text))  || '%'  or upper(a.firstname)  like '%' || upper(CAST(:label as text)) || '%' or upper(a.lastname)  like '%' || upper(CAST(:label as text)) || '%') "
                        +
                        " and (COALESCE(:status) =0 or coalesce(ans.id,fs.id,doms.id,sps.id) in (:status) ) " +
                        " group by a.denomination, a.firstname , a.lastname,  " +
                        "  t2.denomination, t2.firstname , t2.lastname,  " +
                        "  r.firstname , r.lastname, asso.id, " +
                        "  a.address ,a.postal_Code ,ci.label ,c.created_date,c.production_effective_date_time,  " +
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
                        @Param("affaireId") Integer affaireId,
                        @Param("isMissingQueriesToManualRemind") Boolean isMissingQueriesToManualRemind,
                        @Param("simpleProvisionStatusWaitingAttachmentId") Integer simpleProvisionStatusWaitingAttachmentId,
                        @Param("formaliteStatusWaitingAttachmentId") Integer formaliteStatusWaitingAttachmentId,
                        @Param("commercialId") Integer commercialId,
                        @Param("formaliteGuichetUniqueStatusCode") String formaliteGuichetUniqueStatusCode,
                        @Param("formaliteInfogreffeStatusCode") String formaliteInfogreffeStatusCode);

        List<AssoAffaireOrder> findByCustomerOrderOrderByAffaire(CustomerOrder customerOrder);

        List<AssoAffaireOrder> findByQuotationOrderByAffaire(Quotation quotation);
}