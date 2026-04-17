package com.jss.osiris.modules.osiris.tiers.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;
import com.jss.osiris.modules.osiris.tiers.model.IResponsableSearchResult;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;

public interface ResponsableRepository extends QueryCacheCrudRepository<Responsable, Integer> {

        Responsable findByLoginWeb(String loginWeb);

        @Query(nativeQuery = true, value = """
                        with rt as(
                        select
                        	r.id as id_responsable ,
                        	sum(rt.turnover_without_tax_with_debour) as turnover_without_tax_with_debour,
                        	sum(rt.turnover_with_tax_with_debour) as turnover_with_tax_with_debour,
                        	sum(rt.turnover_without_tax_without_debour) as turnover_without_tax_without_debour,
                        	sum(rt.turnover_with_tax_without_debour) as turnover_with_tax_without_debour,
                        	string_agg(cf.label, ', ' order by cf.label) as confrere
                        from reporting_turnover rt
                        left join responsable r on
                        	rt.id_responsable = r.id
                        left join confrere cf on
                        	cf.id_responsable = r.id
                        where
                        	rt.created_date >= :startDate
                        	and rt.created_date <= :endDate
                        group by
                        	r.id),
                        nbr_for as (
                        select
                        		r.id as id_responsable ,
                        		sum(case when a.id_confrere is not null and a.id_confrere = :jssSpelConfrereId then 1 else 0 end) as announcementJssNbr,
                        		sum(case when a.id_confrere is not null and a.id_confrere <> :jssSpelConfrereId then 1 else 0 end) as announcementConfrereNbr,
                        		sum(case when a.id_confrere is not null then 1 else 0 end) as announcementNbr,
                        		sum(case when p.id_formalite is not null or p.id_simple_provision is not null then 1 else 0 end ) as formalityNbr,
                        		min(co.created_date) as firstOrderDay,
                        		max(co.created_date) as lastOrderDay
                        from
                        	asso_affaire_order aao
                        left join service on
                        		service.id_asso_affaire_order = aao.id
                        left join provision p on
                        			p.id_service = service.id
                        left join announcement a on
                        			a.id = p.id_announcement
                        join customer_order co on
                        		co.id = aao.id_customer_order
                        join responsable r on
                        		r.id = co.id_responsable
                        where
                        	aao.id_customer_order is not null
                        group by
                        		r.id )
                        select
                        	coalesce(t.denomination,
                        	concat(t.firstname,
                        		' ',
                        	t.lastname)) tiersLabel,
                        	tc.label as tiersCategory,
                        	t.id as tiersId,
                        	tt.label as tiersTypeLabel,
                        	r.id as responsableId,
                        	concat(r.firstname,
                        		' ',
                        	r.lastname) as responsableLabel,
                        	tc2.label as responsableCategory,
                        	coalesce(concat(e2.firstname,
                        		' ',
                        	e2.lastname),
                        	concat(e1.firstname,
                        		' ',
                        	e1.lastname)) as salesEmployeeLabel,
                        	e2.id as salesEmployeeId,
                        	a1.created_date as createdDateDay,
                        	nbr_for.announcementJssNbr as announcementJssNbr,
                        	nbr_for.announcementConfrereNbr as announcementConfrereNbr,
                        	nbr_for.announcementNbr as announcementNbr,
                        	nbr_for.formalityNbr as formalityNbr,
                        	nbr_for.firstOrderDay as firstOrderDay,
                        	nbr_for.lastOrderDay as lastOrderDay,
                        	blt.label as billingLabelType,
                        	rt.confrere as confrere,
                        	rt.turnover_without_tax_with_debour as turnoverAmountWithoutTax,
                        	rt.turnover_with_tax_with_debour as turnoverAmountWithTax,
                        	rt.turnover_without_tax_without_debour as turnoverAmountWithoutDebourWithoutTax,
                        	rt.turnover_with_tax_without_debour as turnoverAmountWithoutDebourWithTax
                        from
                        	tiers t
                        left join tiers_category tc on
                        	tc.id = t.id_tiers_category
                        join responsable r on
                        	r.id_tiers = t.id
                        left join tiers_category tc2 on
                        	tc2.id = r.id_responsable_category
                        left join employee e1 on
                        	e1.id = t.id_commercial
                        left join index_entity a1 on
                        	a1.entity_type = 'Tiers'
                        	and a1.entity_id = t.id
                        left join employee e2 on
                        	e2.id = r.id_commercial
                        left join document d on
                        	d.id_responsable = r.id
                        	and d.id_document_type = :documentTypeBillingId
                        left join billing_label_type blt on
                        	blt.id = d.id_billing_label_type
                        left join nbr_for on
                        	nbr_for.id_responsable = r.id
                        left join rt on
                        	rt.id_responsable = r.id
                        left join tiers_type tt on
                        	tt.id = t.id_tiers_type
                        where
                        	( :tiersId = 0
                        		or t.id = :tiersId)
                        	and ( :salesEmployeeId = 0
                        		or e2.id = :salesEmployeeId)
                        	and ( :responsableId = 0
                        		or r.id = :responsableId)
                        	and ( :mail = ''
                        		or exists (
                        		select
                        			1
                        		from
                        			mail m
                        		where
                        			r.id_mail = m.id
                        			and lower(m.mail) like '%' || lower(trim(:mail)) || '%'))
                        	and (cast(:label as text) = ''
                        		or cast(r.id as text) = upper(cast(:label as text))
                        			or upper(concat(r.firstname, ' ', r.lastname)) like '%' || trim(upper(cast(:label as text))) || '%'
                        				or upper(t.denomination) like '%' || trim(upper(cast(:label as text))) || '%' )
                        	and(:withNonNullTurnover = false
                        				or coalesce(rt.turnover_without_tax_with_debour, 0) > 0)
                        """)
        List<IResponsableSearchResult> searchResponsable(@Param("tiersId") Integer tiersId,
                        @Param("responsableId") Integer responsableId,
                        @Param("salesEmployeeId") Integer salesEmployeeId,
                        @Param("mail") String mail,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate, @Param("label") String label,
                        @Param("jssSpelConfrereId") Integer jssSpelConfrereId,
                        @Param("documentTypeBillingId") Integer documentTypeBillingId,
                        @Param("withNonNullTurnover") Boolean withNonNullTurnover);

        Responsable findFirst1ByIsActiveAndMail_MailIgnoreCase(Boolean isActive, String mail);

        /*
         * |============================================================================
         * |______________________METHODS FOR OSIRIS V2_________________________________
         * |============================================================================
         */

        @Query("""
                        select r from Responsable r
                        where :searchValue = ''
                        or upper(concat(r.firstname, ' ', r.lastname)) like upper(concat('%', :searchValue, '%'))
                        """)
        Page<Responsable> findResponsableByName(String searchValue, Pageable pageable);

        List<Responsable> findByTiers(Tiers tiers);

        List<Responsable> findByIsActiveTrue();

        List<Responsable> findByMailAndIsActive(Mail mail, boolean isActive);

        @Query("""
                        select r
                        from Responsable r
                        left join r.mail m
                        where (:salesEmployeeId = 0 or r.salesEmployee.id = :salesEmployeeId)
                        and (:mail = '' or m.mail = :mail)
                        and (
                        :label = ''
                        or upper( concat(r.firstname, r.lastname))
                                like concat('%', upper(:label), '%')
                        )
                        and (:tiersCategory = '' or r.tiers.tiersCategory.label = :tiersCategory)
                        """)
        List<Responsable> searchForResponsables(@Param("salesEmployeeId") Integer salesEmployeeId,
                        @Param("mail") String mail,
                        @Param("label") String label,
                        @Param("tiersCategory") String tiersCategory);

}