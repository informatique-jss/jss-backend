package com.jss.osiris.modules.quotation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.quotation.model.IPaperSetResult;
import com.jss.osiris.modules.quotation.model.PaperSet;

public interface PaperSetRepository extends QueryCacheCrudRepository<PaperSet, Integer> {

    @Query(nativeQuery = true, value = " " +
            "     select " +
            " 	ps.id, " +
            " 	pst.label as paperSetTypeLabel, " +
            " 	co.id as customerOrderId, " +
            " 	cos.label as customerOrderStatus, " +
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
            " 	ps.location_number as locationNumber " +
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
            " 	t.id = coalesce(r.id_tiers, " +
            " 	co.id_tiers) " +
            " join service s on " +
            " 	s.id_asso_affaire_order = aao.id " +
            " join service_type st on " +
            " 	st.id = s.id_service_type " +
            " 	where coalesce(ps.is_cancelled,false)=false " +
            " and  coalesce(ps.is_validated,false)=false " +
            " group by " +
            " 	ps.id, " +
            " 	pst.label, " +
            " 	co.id, " +
            " 	cos.label, " +
            " 	coalesce(t.denomination, " +
            " 	concat(t.firstname, " +
            " 	' ', " +
            " 	t.lastname)), " +
            " 	t.id, " +
            " 	concat(r.firstname, " +
            " 	' ', " +
            " 	r.lastname), " +
            " 	r.id, " +
            " 	ps.location_number order by ps.location_number" +
            "")
    List<IPaperSetResult> findPaperSets();


    @Query(nativeQuery = true, value = " " +
    "     select " +
    " 	ps.id, " +
    " 	pst.label as paperSetTypeLabel, " +
    " 	co.id as customerOrderId, " +
    " 	cos.label as customerOrderStatus, " +
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
    " 	ps.location_number as locationNumber " +
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
    " 	t.id = coalesce(r.id_tiers, " +
    " 	co.id_tiers) " +
    " join service s on " +
    " 	s.id_asso_affaire_order = aao.id " +
    " join service_type st on " +
    " 	st.id = s.id_service_type " +
    " 	where coalesce(ps.is_cancelled,false)=:isDisplayCanceled " +
    " and  coalesce(ps.is_validated,false)=:isDisplayValidated " +
    " and concat(co.id, '-', ps.location_number, '-', af.id) like '%:textSearch%'" + 
            "" + 
    " group by " +
    " 	ps.id, " +
    " 	pst.label, " +
    " 	co.id, " +
    " 	cos.label, " +
    " 	coalesce(t.denomination, " +
    " 	concat(t.firstname, " +
    " 	' ', " +
    " 	t.lastname)), " +
    " 	t.id, " +
    " 	concat(r.firstname, " +
    " 	' ', " +
    " 	r.lastname), " +
    " 	r.id, " +
    " 	ps.location_number order by ps.location_number" +
    "")
List<IPaperSetResult> filteredPaperSets(@Param("textSearch") String textSearch, @Param("isDisplayValidated") Boolean isDisplayValidated, @Param("isDisplayCanceled") Boolean isDisplayCanceled);

    @Query("select p from PaperSet p where coalesce(isCancelled,false) = false and coalesce(isValidated, false) = false order by locationNumber asc")
    List<PaperSet> findAllByOrderByLocationNumberAsc();
}