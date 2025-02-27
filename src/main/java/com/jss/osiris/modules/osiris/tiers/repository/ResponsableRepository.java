package com.jss.osiris.modules.osiris.tiers.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.tiers.model.IResponsableSearchResult;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public interface ResponsableRepository extends QueryCacheCrudRepository<Responsable, Integer> {

        Responsable findByLoginWeb(String loginWeb);

        @Query(nativeQuery = true, value = "" +
                        "  with nbr_for as ( " +
                        "  select " +
                        "          r.id as id_responsable , " +
                        "          sum(case when a.id_confrere is not null and a.id_confrere = :jssSpelConfrereId then 1 else 0 end) as announcementJssNbr, "
                        +
                        "          sum(case when a.id_confrere is not null and a.id_confrere <> :jssSpelConfrereId then 1 else 0 end) as announcementConfrereNbr, "
                        +
                        "          sum(case when a.id_confrere is not null then 1 else 0 end) as announcementNbr, " +
                        "          sum(case when p.id_formalite is not null or p.id_simple_provision is not null then 1 else 0 end ) as formalityNbr "
                        +
                        "  from " +
                        "          asso_affaire_order aao " +
                        "  left join service on service.id_asso_affaire_order = aao.id left join provision p on " +
                        "          p.id_service= service.id " +
                        "  left join announcement a on " +
                        "          a.id = p.id_announcement " +
                        "          join customer_order co on co.id = aao.id_customer_order " +
                        "          join responsable r on r.id = co.id_responsable " +
                        "  where " +
                        "          aao.id_customer_order is not null " +
                        "  group by " +
                        "          r.id )  " +
                        " select " +
                        " 	coalesce(t.denomination, " +
                        " 	concat(t.firstname, " +
                        " 	' ', " +
                        " 	t.lastname)) tiersLabel, " +
                        " 	tc.label as tiersCategory, " +
                        " 	t.id as tiersId, " +
                        " 	r.id as responsableId, " +
                        " 	concat(r.firstname, " +
                        " 	' ', " +
                        " 	r.lastname) as responsableLabel, " +
                        " 	tc2.label as responsableCategory, " +
                        " 	coalesce(concat(e2.firstname, " +
                        " 	' ', " +
                        " 	e2.lastname), " +
                        " 	concat(e1.firstname, " +
                        " 	' ', " +
                        " 	e1.lastname)) as salesEmployeeLabel, " +
                        " 	e2.id as salesEmployeeId, " +
                        " 	min(co2.created_date) as firstOrderDay, " +
                        " 	max(co2.created_date) as lastOrderDay,  " +
                        " 	min(a1.created_date) as createdDateDay, " +
                        " 	max(nbr_for.announcementJssNbr) as announcementJssNbr, " +
                        " 	max(nbr_for.announcementConfrereNbr) as announcementConfrereNbr, " +
                        " 	max(nbr_for.announcementNbr) as announcementNbr, " +
                        " 	max(nbr_for.formalityNbr) as formalityNbr, " +
                        " 	blt.label as billingLabelType, " +
                        " 	sum( case when i.id_invoice_status =115359  then -1 else 1 end *(ii.pre_tax_price-coalesce (ii.discount_amount, 0) ) ) as turnoverAmountWithoutTax, "
                        +
                        " 	sum( case when i.id_invoice_status =115359  then -1 else 1 end *(ii.pre_tax_price + coalesce (ii.vat_price, 0)-coalesce (ii.discount_amount, 0)) ) as turnoverAmountWithTax, "
                        +
                        " 	sum(case when i.id_invoice_status =115359  then -1 else 1 end *case when bt.id is not null and bt.is_debour is not null and bt.is_debour then 0 else 1 end * (ii.pre_tax_price-coalesce (ii.discount_amount, 0) ) ) as turnoverAmountWithoutDebourWithoutTax, "
                        +
                        " 	sum(case when i.id_invoice_status =115359  then -1 else 1 end *case when bt.id is not null and bt.is_debour is not null and bt.is_debour then 0 else 1 end * (ii.pre_tax_price + coalesce (ii.vat_price, 0)-coalesce (ii.discount_amount, 0) ) ) as turnoverAmountWithoutDebourWithTax "
                        +
                        " from " +
                        " 	tiers t " +
                        " left join tiers_category tc on " +
                        " 	tc.id = t.id_tiers_category " +
                        " join responsable r on " +
                        " 	r.id_tiers = t.id " +
                        " left join tiers_category tc2 on " +
                        " 	tc2.id = r.id_responsable_category " +
                        " left join employee e1 on " +
                        " 	e1.id = t.id_commercial " +
                        " left join employee e2 on " +
                        " 	e2.id = r.id_commercial " +
                        " left join customer_order co2 on " +
                        " 	co2.id_responsable = r.id  "
                        +
                        " left join index_entity a1 on " +
                        " 	 a1.entity_type = 'Tiers' " +
                        " 	and a1.entity_id = t.id " +
                        " left join document d on " +
                        " 	d.id_responsable = r.id " +
                        " 	and d.id_document_type = :documentTypeBillingId " +
                        " left join billing_label_type blt on " +
                        " 	blt.id = d.id_billing_label_type " +
                        " left join invoice i on " +
                        " 	i.customer_order_id = co2.id " +
                        " 	and i.id_invoice_status in (:invoiceStatusIds) and  i.created_date>=:startDate and i.created_date<=:endDate "
                        +
                        " left join invoice_item ii on " +
                        " 	ii.id_invoice = i.id " +
                        " left join billing_item bi on " +
                        " 	bi.id = ii.id_billing_item " +
                        " left join billing_type bt on " +
                        " 	bt.id = bi.id_billing_type " +
                        " left join nbr_for on " +
                        " 	nbr_for.id_responsable = r.id " +
                        " where  " +
                        "  ( :tiersId =0 or t.id = :tiersId) " +
                        " and  ( :salesEmployeeId =0 or e2.id = :salesEmployeeId) " +
                        " and  ( :responsableId =0 or r.id = :responsableId) " +
                        " and ( :mail='' or exists (select 1 from mail m  where r.id_mail = m.id and lower(m.mail) like '%' || lower(trim(:mail))  || '%')) "
                        +
                        " and (CAST(:label as text) ='' or CAST(r.id as text) = upper(CAST(:label as text)) or  upper(concat(r.firstname, ' ',r.lastname))  like '%' || trim(upper(CAST(:label as text)))  || '%' or  upper(t.denomination)  like '%' || trim(upper(CAST(:label as text)))  || '%'  ) "
                        +
                        " group by " +
                        " 	coalesce(t.denomination, " +
                        " 	concat(t.firstname, " +
                        " 	' ', " +
                        " 	t.lastname)), " +
                        " 	tc.label, " +
                        " 	concat(r.firstname, " +
                        " 	' ', " +
                        " 	r.lastname) , " +
                        " 	tc2.label , " +
                        " 	coalesce(concat(e2.firstname, " +
                        " 	' ', " +
                        " 	e2.lastname)," +
                        " 	concat(e1.firstname, " +
                        " 	' ', " +
                        " 	e1.lastname)), " +
                        " 	blt.label, e2.id ,t.id, r.id having :withNonNullTurnover=false or sum( (ii.pre_tax_price-coalesce (ii.discount_amount, 0) ) )>0 "
                        +
                        "")
        List<IResponsableSearchResult> searchResponsable(@Param("tiersId") Integer tiersId,
                        @Param("responsableId") Integer responsableId,
                        @Param("salesEmployeeId") Integer salesEmployeeId, 
                        @Param("mail") String mail, 
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate, @Param("label") String label,
                        @Param("jssSpelConfrereId") Integer jssSpelConfrereId,
                        @Param("invoiceStatusIds") List<Integer> invoiceStatusIds,
                        @Param("documentTypeBillingId") Integer documentTypeBillingId,
                        @Param("withNonNullTurnover") Boolean withNonNullTurnover);

        Responsable findFirst1ByMail_MailIgnoreCase(String mail);
}