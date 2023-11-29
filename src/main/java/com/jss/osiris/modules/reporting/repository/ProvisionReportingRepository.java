package com.jss.osiris.modules.reporting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.reporting.model.IProvisionReporting;

public interface ProvisionReportingRepository extends CrudRepository<Quotation, Integer> {

        @Query(nativeQuery = true, value = "" +
                        "         select " +
                        "         to_char(date_trunc('year', " +
                        " co.created_date), " +
                        " 'YYYY') as customerOrderCreatedDateYear, " +
                        " to_char(date_trunc('month', " +
                        " co.created_date), " +
                        " 'YYYY-MM') as customerOrderCreatedDateMonth, " +
                        " to_char(date_trunc('week', " +
                        " co.created_date), " +
                        " 'YYYY-MM - tmw') as customerOrderCreatedDateWeek, " +
                        " to_char(date_trunc('day', " +
                        " co.created_date), " +
                        " 'YYYY-MM-DD') as customerOrderCreatedDateDay,   " +
                        "     cos2.label as customerOrderStatusLabel,  " +
                        "     concat (e.firstname, " +
                        " ' ', " +
                        " e.lastname) as provisionAssignedToLabel, " +
                        "     pft.label as provisionFamilyTypeLabel, " +
                        "     count(distinct p.id) as provisionNumber, " +
                        "     coalesce(as2.label, " +
                        " sps.label, " +
                        " fst.label, " +
                        " ds.label, " +
                        " bs.label) as provisionStatus, " +
                        " coalesce(ca1.label, ca2.label) as waitedCompetentAuthorityLabel, " +
                        " array_to_string(array[ " +
                        " case " +
                        " when sum(case when pft.code like 'B%' then 1 else 0 end)>0 then 'Formalité' " +
                        " end, " +
                        " case " +
                        " when sum(case when p.id_announcement is not null then 1 else 0 end)>0 then 'AL' " +
                        " end, " +
                        " case " +
                        " when sum(case when pft.code not like 'B%' and p.id_announcement is null then 1 else 0 end)>0 then 'Autre' "
                        +
                        " end], " +
                        " ' / ') as aggregateProvisionTypeLabel, " +
                        " sum(ii.pre_tax_price-coalesce (ii.discount_amount, 0)  ) as turnoverAmountWithoutTax,  " +
                        " sum(ii.pre_tax_price + coalesce (ii.vat_price, 0)-coalesce (ii.discount_amount, 0) ) as turnoverAmountWithTax,  "
                        +
                        " sum(case when bt.id is not null and bt.is_debour is not null and bt.is_debour then 0 else 1 end * (ii.pre_tax_price-coalesce (ii.discount_amount, 0) ) ) as turnoverAmountWithoutDebourWithoutTax,  "
                        +
                        " sum(case when bt.id is not null and bt.is_debour is not null and bt.is_debour then 0 else 1 end * (ii.pre_tax_price + coalesce (ii.vat_price, 0)-coalesce (ii.discount_amount, 0) ) ) as turnoverAmountWithoutDebourWithTax                         "
                        +
                        " from " +
                        "     customer_order co " +
                        " join customer_order_status cos2 on " +
                        "     cos2.id = co.id_customer_order_status " +
                        " left join asso_affaire_order aao on " +
                        "     aao.id_customer_order = co.id " +
                        " left join provision p on " +
                        "     p.id_asso_affaire_order = aao.id " +
                        " left join invoice_item ii on ii.id_provision = p.id and co.id_customer_order_status =:customerOrderStatusBilledId "
                        +
                        " left join billing_item bi on  bi.id = ii.id_billing_item  " +
                        " left join billing_type bt on  bt.id = bi.id_billing_type                          " +
                        " left join provision_family_type pft on " +
                        "     pft.id = p.id_provision_family_type " +
                        " left join employee e on " +
                        "     e.id = p.id_employee " +
                        " left join announcement a on " +
                        " a.id = p.id_announcement " +
                        " left join announcement_status as2 on " +
                        " as2.id = a.id_announcement_status " +
                        " left join simple_provision sp on " +
                        " sp.id = p.id_simple_provision " +
                        " left join simple_provision_status sps on " +
                        " sps.id = sp.id_simple_provision_status " +
                        " left join formalite f on " +
                        " f.id = p.id_formalite " +
                        " left join formalite_status fst on " +
                        " fst.id = f.id_formalite_status " +
                        " left join domiciliation d on " +
                        " d.id = p.id_domiciliation " +
                        " left join domiciliation_status ds on " +
                        " ds.id = d.id_domicilisation_status " +
                        " left join bodacc b on " +
                        " b.id = p.id_bodacc " +
                        " left join bodacc_status bs on " +
                        " bs.id = b.id_bodacc_status " +
                        " left join competent_authority ca1 on " +
                        " ca1.id = sp.id_waited_competent_authority " +
                        " left join competent_authority ca2 on " +
                        " ca2.id = f.id_waited_competent_authority " +
                        " group by " +
                        " to_char(date_trunc('year', " +
                        " co.created_date), " +
                        " 'YYYY') , " +
                        " to_char(date_trunc('month', " +
                        " co.created_date), " +
                        " 'YYYY-MM') , " +
                        " to_char(date_trunc('week', " +
                        " co.created_date), " +
                        " 'YYYY-MM - tmw'), " +
                        " to_char(date_trunc('day', " +
                        " co.created_date), " +
                        " 'YYYY-MM-DD'), " +
                        "     cos2.label , " +
                        "     concat (e.firstname, " +
                        " ' ', " +
                        " e.lastname) , " +
                        "     pft.label,  " +
                        "     coalesce(as2.label, " +
                        " sps.label, " +
                        " fst.label, " +
                        " ds.label, " +
                        " bs.label), " +
                        " coalesce(ca1.label, ca2.label) " +
                        "")
        List<IProvisionReporting> getProvisionReporting(
                        @Param("customerOrderStatusBilledId") Integer customerOrderStatusBilledId);
}