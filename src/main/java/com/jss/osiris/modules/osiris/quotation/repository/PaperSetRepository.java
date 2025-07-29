package com.jss.osiris.modules.osiris.quotation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.IPaperSetResult;
import com.jss.osiris.modules.osiris.quotation.model.PaperSet;

public interface PaperSetRepository extends QueryCacheCrudRepository<PaperSet, Integer> {

        @Query(nativeQuery = true, value = " " +
                        "     select " +
                        " 	ps.id, " +
                        "   ps.is_validated as isValidated," +
                        "   ps.is_cancelled as isCancelled, " +
                        " 	pst.label as paperSetTypeLabel, " +
                        " 	co.id as customerOrderId, " +
                        " 	cos.label as customerOrderStatus, " +
                        "       ca.label as competentAuthorityLabel, " +
                        " 	coalesce(t.denomination, " +
                        " 	concat(t.firstname, " +
                        " 	' ', " +
                        " 	t.lastname)) as tiersLabel, " +
                        " 	t.id as tiersId, " +
                        " 	concat(r.firstname, " +
                        " 	' ', " +
                        " 	r.lastname) as responsableLabel, " +
                        " 	r.id as responsableId, " +
                        " 	 STRING_AGG(distinct case " +
                        " 		when af.denomination is not null " +
                        " 		and af.denomination != '' then af.denomination " +
                        " 		else af.firstname || ' ' || af.lastname " +
                        " 	end || ' (' || city.label || ')' , " +
                        " 	', ' ) as affaireLabel, " +
                        " 	STRING_AGG(distinct case " +
                        " 		when s.custom_label is null then st.label " +
                        " 		else s.custom_label " +
                        " 	end, " +
                        " 	', ') as servicesLabel, " +
                        " 	ps.location_number as locationNumber, " +
                        " 	ps.creation_comment as creationComment, " +
                        " 	ps.validation_comment as validationComment, " +
                        " max(case when a.field_name ='id' then concat(e.firstname, ' ', e.lastname) end) as createdBy, "
                        +
                        " max(case when a.field_name ='id' then a.datetime end) as createdDateTime, " +
                        "              max(case when a.field_name in ('isValidated','isCancelled') then concat(e.firstname, ' ', e.lastname) end) as validatedBy, "
                        +
                        " max(case when a.field_name in ('isValidated','isCancelled')  then a.datetime end) as validationDateTime "
                        +
                        " from " +
                        " 	paper_set ps " +
                        " join paper_set_type pst on " +
                        " 	pst.id = ps.id_paper_set_type " +
                        " join customer_order co on " +
                        " 	co.id = ps.id_customer_order " +
                        " join asso_affaire_order aao on " +
                        " 	aao.id_customer_order = co.id " +
                        " join affaire af on " +
                        " 	af.id = aao.id_affaire " +
                        " left join city on " +
                        " 	city.id = af.id_city " +
                        " join customer_order_status cos on " +
                        " 	cos.id = co.id_customer_order_status " +
                        " left join responsable r on " +
                        " 	r.id = co.id_responsable " +
                        " left join tiers t on " +
                        " 	t.id = r.id_tiers" +
                        " left join competent_authority ca on " +
                        "       ps.id_competent_authority = ca.id" +
                        " join service s on " +
                        " 	s.id_asso_affaire_order = aao.id " +
                        " join  asso_service_service_type ast on ast.id_service  = s.id join service_type st on st.id=ast.id_service_type "
                        +
                        " left join audit a on a.entity_id = ps.id and a.entity = 'PaperSet' " +
                        " left join employee e on e.username = a.username " +
                        " 	where (:isDisplayCancelled or ps.is_cancelled=:isDisplayCancelled) " +
                        " and  (:isDisplayValidated or ps.is_validated=:isDisplayValidated) " +
                        " and concat(co.id, '-', ps.location_number, '-', af.id, '-', st.label, '-',ps.creation_comment ) like '%' || :textSearch || '%' "
                        +
                        " group by " +
                        " 	ps.id, " +
                        "   ps.is_validated, " +
                        "   ps.is_cancelled, " +
                        " 	pst.label, " +
                        " 	co.id, " +
                        " 	cos.label, " +
                        "       ca.label, " +
                        " 	coalesce(t.denomination, " +
                        " 	concat(t.firstname, " +
                        " 	' ', " +
                        " 	t.lastname)), " +
                        " 	t.id, " +
                        " 	concat(r.firstname, " +
                        " 	' ', " +
                        " 	r.lastname), " +
                        " 	r.id, " +
                        " 	ps.location_number, " +
                        "       ps.creation_comment order by ps.location_number" +
                        "")
        List<IPaperSetResult> findPaperSets(@Param("textSearch") String textSearch,
                        @Param("isDisplayValidated") Boolean isDisplayValidated,
                        @Param("isDisplayCancelled") Boolean isDisplayCancelled);

        @Query("select p from PaperSet p where isCancelled = false and isValidated = false order by locationNumber asc")
        List<PaperSet> findAllByOrderByLocationNumberAsc();
}