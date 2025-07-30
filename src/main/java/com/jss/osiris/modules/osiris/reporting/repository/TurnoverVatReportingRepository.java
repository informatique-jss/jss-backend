package com.jss.osiris.modules.osiris.reporting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.reporting.model.ITurnoverVatReporting;

public interface TurnoverVatReportingRepository extends CrudRepository<Quotation, Integer> {

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
                        "  sum( case when i.id_invoice_status =115359  then -1 else 1 end * (ii.pre_tax_price-coalesce (ii.discount_amount, 0) ) ) as turnoverAmountWithoutTax, "
                        +
                        " sum(case when i.id_invoice_status =115359  then -1 else 1 end *( ii.pre_tax_price + coalesce (ii.vat_price, 0)-coalesce (ii.discount_amount, 0)) ) as turnoverAmountWithTax, "
                        +
                        " sum(case when i.id_invoice_status =115359  then -1 else 1 end * case when bt.id is not null and bt.is_debour is not null and bt.is_debour then 0 else 1 end * (ii.pre_tax_price-coalesce (ii.discount_amount, 0) ) ) as turnoverAmountWithoutDebourWithoutTax, "
                        +
                        " sum(case when i.id_invoice_status =115359  then -1 else 1 end * case when bt.id is not null and bt.is_debour is not null and bt.is_debour then 0 else 1 end * (ii.pre_tax_price + coalesce (ii.vat_price, 0)-coalesce (ii.discount_amount, 0) ) ) as turnoverAmountWithoutDebourWithTax, "
                        +
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
                        " vat.label as vatLabel, case when i.id_provider is not null then 1 else 0 end isProviderInvoice "
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
                        " e2.id = t2.id_commercial " +
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
                        " ist.label, vat.label,i.id_provider " +
                        "")
        List<ITurnoverVatReporting> getTurnoverVatReporting();
}