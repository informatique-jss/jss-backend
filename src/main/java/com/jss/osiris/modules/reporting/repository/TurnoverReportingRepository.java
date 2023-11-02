package com.jss.osiris.modules.reporting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.reporting.model.ITurnoverReporting;

public interface TurnoverReportingRepository extends CrudRepository<Quotation, Integer> {

        @Query(nativeQuery = true, value = "" +
                        " select " +
                        " to_char(date_trunc('year', " +
                        " i.created_date),'YYYY') as invoiceDateYear, " +
                        " to_char(date_trunc('month', " +
                        " i.created_date),'YYYY-MM') as invoiceDateMonth, " +
                        " to_char(date_trunc('week', " +
                        " i.created_date),'YYYY-MM - tmw') as invoiceDateWeek, " +
                        " to_char(date_trunc('day', " +
                        " i.created_date),'YYYY-MM-DD') as invoiceDateDay, " +
                        " sum(case when i.is_credit_note then -1 else 1 end * (ii.pre_tax_price-coalesce (ii.discount_amount, 0) ) ) as turnoverAmountWithoutTax, "
                        +
                        " sum(case when i.is_credit_note then -1 else 1 end * (ii.pre_tax_price + coalesce (ii.vat_price, 0)-coalesce (ii.discount_amount, 0) ) ) as turnoverAmountWithTax, "
                        +
                        " sum(case when bt.id is not null and bt.is_debour is not null and bt.is_debour then 0 when i.is_credit_note then -1 else 1 end * (ii.pre_tax_price-coalesce (ii.discount_amount, 0) ) ) as turnoverAmountWithoutDebourWithoutTax, "
                        +
                        " sum(case when bt.id is not null and bt.is_debour is not null and bt.is_debour then 0 when i.is_credit_note then -1 else 1 end * (ii.pre_tax_price + coalesce (ii.vat_price, 0)-coalesce (ii.discount_amount, 0) ) ) as turnoverAmountWithoutDebourWithTax, "
                        +
                        " count (distinct case when i.is_credit_note = false and i.customer_order_id is not null then i.id else 0 end) as nbrCustomerOrder, "
                        +
                        " count(distinct case when i.is_credit_note = false then i.id end) as nbrInvoices, " +
                        " count(distinct case when i.is_credit_note = true then i.id end) as nbrCreditNote, " +
                        " coalesce(case " +
                        " when t1.denomination is not null then t1.denomination " +
                        " when t1.id is not null then concat(t1.firstname, " +
                        " ' ', " +
                        " t1.lastname) " +
                        " end , " +
                        " case " +
                        " when t2.denomination is not null then t2.denomination " +
                        " else concat(t2.firstname, " +
                        " ' ', " +
                        " t2.lastname) " +
                        " end ) as tiersLabel, " +
                        " coalesce(tt1.label, " +
                        " tt2.label) as tiersCategory, " +
                        " concat (e1.firstname, " +
                        " ' ', " +
                        " e1.lastname) as invoiceCreator, " +
                        " coalesce(concat (e2.firstname, " +
                        " ' ', " +
                        " e2.lastname), " +
                        " concat (e2.firstname, " +
                        " ' ', " +
                        " e2.lastname)) as salesEmployeeLabel, " +
                        " ist.label as invoiceStatusLabel, " +
                        " vat.label as vatLabel, " +
                        " sum(coalesce((select count(*) from announcement a join provision p on p.id_announcement =a.id join asso_affaire_order aao on aao.id = p.id_asso_affaire_order where aao.id_customer_order = i.customer_order_id),0)) as nbrAnnouncement, "
                        +
                        " (select string_agg(distinct cast(d.code as text),', ' )  from announcement a join department d on d.id = a.id_department  join provision p on p.id_announcement = a.id join asso_affaire_order aao on aao.id = p.id_asso_affaire_order where aao.id_customer_order = i.customer_order_id) as announcementDepartment "
                        +
                        " from " +
                        " invoice i " +
                        " left join invoice_item ii on " +
                        " ii.id_invoice = i.id " +
                        " join vat on vat.id = ii.id_vat " +
                        " join invoice_status ist on " +
                        " ist.id = i.id_invoice_status " +
                        " left join billing_item bi on " +
                        " bi.id = ii.id_billing_item " +
                        " left join billing_type bt on " +
                        " bt.id = bi.id_billing_type " +
                        " left join responsable r on " +
                        " r.id = i.id_responsable " +
                        " left join tiers t1 on " +
                        " t1.id = i.id_tiers " +
                        " left join tiers t2 on " +
                        " t2.id = r.id_tiers " +
                        " left join tiers_category tt1 on " +
                        " tt1.id = t1.id_tiers_category " +
                        " left join tiers_category tt2 on " +
                        " tt2.id = t2.id_tiers_category " +
                        " left join audit a1 on " +
                        " a1.entity_id = i.id " +
                        " and a1.entity = 'Invoice' " +
                        " and field_name = 'id' " +
                        " left join employee e1 on " +
                        " e1.username = a1.username " +
                        " left join customer_order co on co.id = i.customer_order_id " +
                        " left join employee e2 on " +
                        " e2.id = co.id_assigned_to " +
                        " where " +
                        " i.id_invoice_status in :invoiceStatusId " +
                        " and i.is_invoice_from_provider = false " +
                        " and i.is_provider_credit_note = false " +
                        " group by " +
                        " date_trunc('year', " +
                        " i.created_date), " +
                        " date_trunc('month', " +
                        " i.created_date) , " +
                        " date_trunc('week', " +
                        " i.created_date) , " +
                        " date_trunc('day', " +
                        " i.created_date) , " +
                        " coalesce(case " +
                        " when t1.denomination is not null then t1.denomination " +
                        " when t1.id is not null then concat(t1.firstname, " +
                        " ' ', " +
                        " t1.lastname) " +
                        " end , " +
                        " case " +
                        " when t2.denomination is not null then t2.denomination " +
                        " else concat(t2.firstname, " +
                        " ' ', " +
                        " t2.lastname) " +
                        " end ) , " +
                        " coalesce(tt1.label, " +
                        " tt2.label) , " +
                        " concat (e1.firstname, " +
                        " ' ', " +
                        " e1.lastname) , " +
                        " coalesce(concat (e2.firstname, " +
                        " ' ', " +
                        " e2.lastname), " +
                        " concat (e2.firstname, " +
                        " ' ', " +
                        " e2.lastname)) , " +
                        " ist.label, vat.label , " +
                        " (select  string_agg(distinct  cast(d.code as text),', ' )  from announcement a join department d on d.id = a.id_department  join provision p on p.id_announcement = a.id join asso_affaire_order aao on aao.id = p.id_asso_affaire_order where aao.id_customer_order = i.customer_order_id) "
                        +
                        "")
        List<ITurnoverReporting> getTurnoverReporting(@Param("invoiceStatusId") List<Integer> invoiceStatusId);
}