package com.jss.osiris.modules.osiris.tiers.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.tiers.model.ITiersSearchResult;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;
import com.jss.osiris.modules.osiris.tiers.model.TiersType;

import jakarta.persistence.QueryHint;

public interface TiersRepository extends QueryCacheCrudRepository<Tiers, Integer> {

        @Query(value = "select a from Tiers a where id = :idTiers and isIndividual = true")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        List<Tiers> findByIsIndividualAndIdTiers(@Param("idTiers") Integer idTiers);

        List<Tiers> findByTiersType(TiersType tiersTypeClient);

        @Query(nativeQuery = true, value = "select distinct t.* " +
                        " from tiers t " +
                        " left join responsable r on r.id_tiers = t.id " +
                        " left join invoice i2 on i2.id_responsable = r.id and i2.id_invoice_status = :invoiceStatusSendId "
                        +
                        " left join customer_order c2 on c2.id_responsable = t.id  " +
                        " left join payment d2 on d2.id_customer_order = c2.id and d2.is_cancelled = false  " +
                        " where t.id_tiers_type = :tiersTypeClientId " +
                        " and coalesce(i2.id,d2.id) is not null ")
        List<Tiers> findAllTiersForBillingClosureReceiptSend(@Param("invoiceStatusSendId") Integer invoiceStatusSendId,
                        @Param("tiersTypeClientId") Integer tiersTypeClientId);

        @Query(value = "select a from Tiers a where postalCode = :postalCode and isIndividual = true and trim(upper(firstname))=upper(trim(:firstname)) and trim(upper(lastname))=trim(upper(trim(:lastname))) ")
        List<Tiers> findByPostalCodeAndName(@Param("postalCode") String postalCode,
                        @Param("firstname") String firstname, @Param("lastname") String lastname);

        @Query(value = "select a from Tiers a where postalCode = :postalCode and isIndividual = false and trim(upper(denomination))=upper(trim(:denomination))  ")
        List<Tiers> findByPostalCodeAndDenomination(@Param("postalCode") String postalCode,
                        @Param("denomination") String denomination);

        @Query(nativeQuery = true, value = "" +
                        "   with nbr_for as ( " +
                        "   select " +
                        "          r.id_tiers as id_tiers , " +
                        "           sum(case when a.id_confrere is not null and a.id_confrere = :jssSpelConfrereId then 1 else 0 end) as announcementJssNbr, "
                        +
                        "           sum(case when a.id_confrere is not null and a.id_confrere <> :jssSpelConfrereId then 1 else 0 end) as announcementConfrereNbr, "
                        +
                        "           sum(case when a.id_confrere is not null then 1 else 0 end) as announcementNbr, " +
                        "           sum(case when p.id_formalite is not null or p.id_simple_provision is not null then 1 else 0 end ) as formalityNbr "
                        +
                        "   from " +
                        "           asso_affaire_order aao " +
                        "  left join service on service.id_asso_affaire_order = aao.id left join provision p on " +
                        "           p.id_service= service.id " +
                        "   left join announcement a on " +
                        "           a.id = p.id_announcement " +
                        "           join customer_order co on co.id = aao.id_customer_order " +
                        "   left join responsable r on r.id = co.id_responsable " +
                        "   where " +
                        "           aao.id_customer_order is not null " +
                        "   group by " +
                        "           r.id_tiers )   " +
                        "          select " +
                        "          coalesce(t.denomination, " +
                        "          concat(t.firstname, " +
                        "          ' ', " +
                        "          t.lastname)) tiersLabel, " +
                        "          tc.label as tiersCategory, " +
                        "          concat(t.address, ' ', city.label) as address, tc.label as tiersCategory, " +
                        "          t.id as tiersId, " +
                        "          concat(e1.firstname, " +
                        "          ' ', " +
                        "          e1.lastname) as salesEmployeeLabel, " +
                        "          e1.id as salesEmployeeId, " +
                        "          min(co2.created_date) as firstOrderDay, " +
                        "          max(co2.created_date) as lastOrderDay,  " +
                        "          min(a1.created_date) as createdDateDay, " +
                        "          max(nbr_for.announcementJssNbr) as announcementJssNbr, " +
                        "          max(nbr_for.announcementConfrereNbr) as announcementConfrereNbr, " +
                        "          max(nbr_for.announcementNbr) as announcementNbr, " +
                        "          max(nbr_for.formalityNbr) as formalityNbr, " +
                        "          concat(e2.firstname,' ',e2.lastname) as formalisteLabel, " +
                        "          e2.id as formalisteId, " +
                        "          t.is_new_tiers as isNewTiers, " +
                        "          blt.label as billingLabelType, " +
                        "          sum( case when i.id_invoice_status =115359  then -1 else 1 end * (ii.pre_tax_price-coalesce (ii.discount_amount, 0) ) ) as turnoverAmountWithoutTax, "
                        +
                        "          sum(case when i.id_invoice_status =115359  then -1 else 1 end *( ii.pre_tax_price + coalesce (ii.vat_price, 0)-coalesce (ii.discount_amount, 0)) ) as turnoverAmountWithTax, "
                        +
                        "          sum(case when i.id_invoice_status =115359  then -1 else 1 end * case when bt.id is not null and bt.is_debour is not null and bt.is_debour then 0 else 1 end * (ii.pre_tax_price-coalesce (ii.discount_amount, 0) ) ) as turnoverAmountWithoutDebourWithoutTax, "
                        +
                        "          sum(case when i.id_invoice_status =115359  then -1 else 1 end * case when bt.id is not null and bt.is_debour is not null and bt.is_debour then 0 else 1 end * (ii.pre_tax_price + coalesce (ii.vat_price, 0)-coalesce (ii.discount_amount, 0) ) ) as turnoverAmountWithoutDebourWithTax "
                        +
                        "  from " +
                        "          tiers t left join city on city.id =  t.id_city " +
                        "  left join tiers_category tc on " +
                        "          tc.id = t.id_tiers_category  left join employee e2 on e2.id = t.id_formaliste   " +
                        "  left join responsable r on " +
                        "          r.id_tiers = t.id " +
                        "  left join employee e1 on " +
                        "          e1.id = t.id_commercial " +
                        "  left join customer_order co2 on " +
                        "          co2.id_responsable = r.id   "
                        +
                        "  left join index_entity a1 on " +
                        "            a1.entity_type = 'Tiers' " +
                        "          and a1.entity_id = t.id " +
                        "  left join document d on " +
                        "          d.id_tiers= t.id " +
                        "          and d.id_document_type = :documentTypeBillingId " +
                        "  left join billing_label_type blt on " +
                        "          blt.id = d.id_billing_label_type " +
                        "  left join invoice i on " +
                        "          i.customer_order_id = co2.id " +
                        "          and i.id_invoice_status in (:invoiceStatusIds) and  i.created_date>=:startDate and i.created_date<=:endDate "
                        +
                        "  left join invoice_item ii on " +
                        "          ii.id_invoice = i.id " +
                        "  left join billing_item bi on " +
                        "          bi.id = ii.id_billing_item " +
                        "  left join billing_type bt on " +
                        "          bt.id = bi.id_billing_type " +
                        "  left join nbr_for on " +
                        "         nbr_for.id_tiers = t.id " +
                        " where " +
                        "    ( :tiersId =0 or t.id = :tiersId) " +
                        "   and ( :isNewTiers =false or coalesce(t.is_new_tiers,false) = true) " +
                        " and  ( :salesEmployeeId =0 or e1.id = :salesEmployeeId) " +
                        " and (:mail='' or exists (select 1 from asso_tiers_mail a join mail m on m.id = a.id_mail where t.id = a.id_tiers and lower(m.mail) like '%' || lower(trim(:mail)) || '%')) "
                        +
                        " and (CAST(:label as text) ='' or CAST(r.id as text) = upper(CAST(:label as text)) or  upper(concat(r.firstname, ' ',r.lastname))  like '%' || trim(upper(CAST(:label as text)))  || '%' or  upper(t.denomination)  like '%' || trim(upper(CAST(:label as text)))  || '%' or  upper(concat(t.firstname, ' ',t.lastname))  like '%' || trim(upper(CAST(:label as text)))  || '%' ) "
                        +
                        " group by " +
                        " 	 t.is_new_tiers, coalesce(t.denomination, " +
                        " 	concat(t.firstname, " +
                        " 	' ', " +
                        " 	t.lastname)), " +
                        " 	tc.label,concat(t.address, ' ', city.label) , " +
                        " 	coalesce(concat(e1.firstname, " +
                        " 	' ', " +
                        " 	e1.lastname)), concat(e2.firstname,' ',e2.lastname),e2.id," +
                        " 	 e1.id, " +
                        " 	blt.label ,t.id   having :withNonNullTurnover=false or sum( (ii.pre_tax_price-coalesce (ii.discount_amount, 0) ) )>0 "
                        +
                        "")
        List<ITiersSearchResult> searchTiers(@Param("tiersId") Integer tiersId,
                        @Param("salesEmployeeId") Integer salesEmployeeId,
                        @Param("mail") String mail,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate, @Param("label") String label,
                        @Param("jssSpelConfrereId") Integer jssSpelConfrereId,
                        @Param("invoiceStatusIds") List<Integer> invoiceStatusIds,
                        @Param("documentTypeBillingId") Integer documentTypeBillingId,
                        @Param("withNonNullTurnover") Boolean withNonNullTurnover,
                        @Param("isNewTiers") Boolean isNewTiers);

        @Query("""
                        select t
                        from Tiers t
                        left join t.mails m
                        where (:salesEmployeeId = 0 or t.salesEmployee.id = :salesEmployeeId)
                        and (:mail = '' or m.mail = :mail)
                        and (
                        :label = ''
                        or upper(coalesce(t.denomination, concat(t.firstname, t.lastname)))
                                like concat('%', upper(:label), '%')
                        )
                        and (:isNewTiers = false or t.isNewTiers = true)
                        and (:tiersCategory = '' or t.tiersCategory.label = :tiersCategory)
                        """)
        List<Tiers> searchForTiers(@Param("salesEmployeeId") Integer salesEmployeeId,
                        @Param("mail") String mail,
                        @Param("label") String label,
                        @Param("isNewTiers") Boolean isNewTiers,
                        @Param("tiersCategory") String tiersCategory);

        Page<Tiers> findByDenominationContainingIgnoreCaseOrFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(
                        String denomination, String firstname, String lastname, Pageable pageable);

}