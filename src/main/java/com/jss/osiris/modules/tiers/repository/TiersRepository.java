package com.jss.osiris.modules.tiers.repository;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.tiers.model.ITiersSearchResult;
import com.jss.osiris.modules.tiers.model.Tiers;
import com.jss.osiris.modules.tiers.model.TiersType;

public interface TiersRepository extends QueryCacheCrudRepository<Tiers, Integer> {

        @Query(value = "select a from Tiers a where id = :idTiers and isIndividual = true")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        List<Tiers> findByIsIndividualAndIdTiers(@Param("idTiers") Integer idTiers);

        List<Tiers> findByTiersType(TiersType tiersTypeClient);

        @Query(nativeQuery = true, value = "select distinct t.* " +
                        " from tiers t " +
                        " left join responsable r on r.id_tiers = t.id " +
                        " left join invoice i1 on i1.id_tiers = t.id and i1.id_invoice_status = :invoiceStatusSendId " +
                        " left join invoice i2 on i2.id_responsable = r.id and i2.id_invoice_status = :invoiceStatusSendId "
                        +
                        " left join customer_order c1 on c1.id_tiers = t.id  " +
                        " left join customer_order c2 on c2.id_responsable = t.id  " +
                        " left join payment d1 on d1.id_customer_order = c1.id and d1.is_cancelled = false " +
                        " left join payment d2 on d2.id_customer_order = c2.id and d2.is_cancelled = false  " +
                        " where t.id_tiers_type = :tiersTypeClientId " +
                        " and coalesce(i1.id, i2.id, d1.id, d2.id) is not null and coalesce(is_receip_sent,false)=false   ")
        List<Tiers> findAllTiersForBillingClosureReceiptSend(@Param("invoiceStatusSendId") Integer invoiceStatusSendId,
                        @Param("tiersTypeClientId") Integer tiersTypeClientId);

        @Query(value = "select a from Tiers a where postalCode = :postalCode and isIndividual = true and trim(upper(firstname))=upper(trim(:firstname)) and trim(upper(lastname))=trim(upper(trim(:lastname))) ")
        List<Tiers> findByPostalCodeAndName(@Param("postalCode") String postalCode,
                        @Param("firstname") String firstname, @Param("lastname") String lastname);

        @Query(value = "select a from Tiers a where postalCode = :postalCode and isIndividual = false and trim(upper(denomination))=upper(trim(:denomination))  ")
        List<Tiers> findByPostalCodeAndDenomination(@Param("postalCode") String postalCode,
                        @Param("denomination") String denomination);

        @Query(nativeQuery = true, value = "" +
                        "     with nbr_for as ( " +
                        " select " +
                        " 	aao.id_customer_order , " +
                        " 	sum(case when a.id_confrere is not null and a.id_confrere = :jssSpelConfrereId then 1 else 0 end) as announcementJssNbr, "
                        +
                        " 	sum(case when a.id_confrere is not null and a.id_confrere <> :jssSpelConfrereId then 1 else 0 end) as announcementConfrereNbr, "
                        +
                        " 	sum(case when a.id_confrere is not null then 1 else 0 end) as announcementNbr, " +
                        " 	sum(case when p.id_formalite is not null or p.id_simple_provision is not null then 1 else 0 end ) as formalityNbr "
                        +
                        " from " +
                        " 	asso_affaire_order aao " +
                        " left join provision p on " +
                        " 	p.id_asso_affaire_order = aao.id " +
                        " left join announcement a on " +
                        " 	a.id = p.id_announcement " +
                        " where " +
                        " 	aao.id_customer_order is not null " +
                        " group by " +
                        " 	aao.id_customer_order ) " +
                        "          select " +
                        "          coalesce(t.denomination, " +
                        "          concat(t.firstname, " +
                        "          ' ', " +
                        "          t.lastname)) tiersLabel, " +
                        "          tc.label as tiersCategory, " +
                        "          t.id as tiersId, " +
                        "          concat(e1.firstname, " +
                        "          ' ', " +
                        "          e1.lastname) as salesEmployeeLabel, " +
                        "          e1.id as salesEmployeeId, " +
                        "          min(co2.created_date) as firstOrderDay, " +
                        "          max(co2.created_date) as lastOrderDay,  " +
                        "          min(a1.datetime) as createdDateDay, " +
                        "          max(tf2.followup_date) as lastResponsableFollowupDate , " +
                        "          sum(nbr_for.announcementJssNbr) as announcementJssNbr, " +
                        "          sum(nbr_for.announcementJssNbr) as announcementConfrereNbr, " +
                        "          sum(nbr_for.announcementJssNbr) as announcementNbr, " +
                        "          sum(nbr_for.announcementJssNbr) as formalityNbr, " +
                        "          concat(e2.firstname,' ',e2.lastname) as formalisteLabel, " +
                        "          e2.id as formalisteId, " +
                        "          blt.label as billingLabelType, " +
                        "          sum( (ii.pre_tax_price-coalesce (ii.discount_amount, 0) ) ) as turnoverAmountWithoutTax, "
                        +
                        "          sum( ii.pre_tax_price + coalesce (ii.vat_price, 0)-coalesce (ii.discount_amount, 0) ) as turnoverAmountWithTax, "
                        +
                        "          sum(case when bt.id is not null and bt.is_debour is not null and bt.is_debour then 0 else 1 end * (ii.pre_tax_price-coalesce (ii.discount_amount, 0) ) ) as turnoverAmountWithoutDebourWithoutTax, "
                        +
                        "          sum(case when bt.id is not null and bt.is_debour is not null and bt.is_debour then 0 else 1 end * (ii.pre_tax_price + coalesce (ii.vat_price, 0)-coalesce (ii.discount_amount, 0) ) ) as turnoverAmountWithoutDebourWithTax "
                        +
                        "  from " +
                        "          tiers t " +
                        "  left join tiers_category tc on " +
                        "          tc.id = t.id_tiers_category  left join employee e2 on e2.id = t.id_formaliste   " +
                        "  left join responsable r on " +
                        "          r.id_tiers = t.id " +
                        "  left join employee e1 on " +
                        "          e1.id = t.id_commercial " +
                        "  left join customer_order co1 on " +
                        "          co1.id_tiers= t.id " +
                        "  left join customer_order co2 on " +
                        "          co2.id_responsable = r.id and  co2.created_date>=:startDate and co2.created_date<=:endDate  "
                        +
                        "  left join audit a1 on " +
                        "          a1.field_name = 'id' " +
                        "          and a1.entity = 'Tiers' " +
                        "          and a1.entity_id = t.id " +
                        "  left join tiers_followup tf2 on " +
                        "          tf2.id_responsable = r.id or tf2.id_tiers = t.id " +
                        "  left join document d on " +
                        "          d.id_tiers= t.id " +
                        "          and d.id_document_type = :documentTypeBillingId " +
                        "  left join billing_label_type blt on " +
                        "          blt.id = d.id_billing_label_type " +
                        "  left join invoice i on " +
                        "          i.customer_order_id = coalesce(co2.id, co1.id) " +
                        "          and i.id_invoice_status in (:invoiceStatusIds) " +
                        "  left join invoice_item ii on " +
                        "          ii.id_invoice = i.id " +
                        "  left join billing_item bi on " +
                        "          bi.id = ii.id_billing_item " +
                        "  left join billing_type bt on " +
                        "          bt.id = bi.id_billing_type " +
                        "  left join nbr_for on " +
                        "          nbr_for.id_customer_order = coalesce(co2.id, co1.id) " +
                        " where " +
                        "    ( :tiersId =0 or t.id = :tiersId) " +
                        " and  ( :salesEmployeeId =0 or e1.id = :salesEmployeeId) " +
                        " and (CAST(:label as text) ='' or CAST(r.id as text) = upper(CAST(:label as text)) or  upper(concat(r.firstname, ' ',r.lastname))  like '%' || trim(upper(CAST(:label as text)))  || '%' or  upper(t.denomination)  like '%' || trim(upper(CAST(:label as text)))  || '%'  ) "
                        +
                        " group by " +
                        " 	coalesce(t.denomination, " +
                        " 	concat(t.firstname, " +
                        " 	' ', " +
                        " 	t.lastname)), " +
                        " 	tc.label, " +
                        " 	coalesce(concat(e1.firstname, " +
                        " 	' ', " +
                        " 	e1.lastname)), concat(e2.firstname,' ',e2.lastname),e2.id," +
                        " 	 e1.id, " +
                        " 	blt.label ,t.id " +
                        "")
        List<ITiersSearchResult> searchTiers(@Param("tiersId") Integer tiersId,
                        @Param("salesEmployeeId") Integer salesEmployeeId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate, @Param("label") String label,
                        @Param("jssSpelConfrereId") Integer jssSpelConfrereId,
                        @Param("invoiceStatusIds") List<Integer> invoiceStatusIds,
                        @Param("documentTypeBillingId") Integer documentTypeBillingId);

}