package com.jss.osiris.modules.reporting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.reporting.model.IRecoveryReporting;

public interface RecoveryReportingRepository extends CrudRepository<Quotation, Integer> {

        @Query(nativeQuery = true, value = "" +
                        " with invoice_ca as ( " +
                        " select " +
                        " i.id, " +
                        " sum(ii.pre_tax_price-coalesce (ii.discount_amount, 0) ) as turnoverAmountWithoutTax, " +
                        " sum(ii.pre_tax_price + coalesce (ii.vat_price, 0)-coalesce (ii.discount_amount, 0) ) as turnoverAmountWithTax, "
                        +
                        " sum(case when bt.id is not null and bt.is_debour is not null and bt.is_debour then 0 else 1 end * (ii.pre_tax_price-coalesce (ii.discount_amount, 0) ) ) as turnoverAmountWithoutDebourWithoutTax, "
                        +
                        " sum(case when bt.id is not null and bt.is_debour is not null and bt.is_debour then 0 else 1 end * (ii.pre_tax_price + coalesce (ii.vat_price, 0)-coalesce (ii.discount_amount, 0) ) ) as turnoverAmountWithoutDebourWithTax , "
                        +
                        " case " +
                        " when i.first_reminder_date_time is null then true " +
                        " else false " +
                        " end noReminder, " +
                        " case " +
                        " when i.first_reminder_date_time is not null " +
                        " and i.second_reminder_date_time is null then true " +
                        " else false " +
                        " end reminderOnce, " +
                        " case " +
                        " when i.second_reminder_date_time is not null " +
                        " and i.third_reminder_date_time is null then true " +
                        " else false " +
                        " end reminderTwice, " +
                        " case " +
                        " when i.third_reminder_date_time is not null " +
                        " and tf.id_invoice is null then true " +
                        " else false " +
                        " end reminderThird, " +
                        " case " +
                        " when i.third_reminder_date_time is not null " +
                        " and tf.id_invoice is not null then true " +
                        " else false " +
                        " end manualReminder, " +
                        " case " +
                        " when i.id_invoice_status = :invoiceStatusBilledId " +
                        " and i.first_reminder_date_time is null then true " +
                        " else false " +
                        " end payedBeforeFirstReminder, " +
                        " case " +
                        " when i.id_invoice_status = :invoiceStatusBilledId " +
                        " and i.first_reminder_date_time is not null " +
                        " and i.second_reminder_date_time is null then true " +
                        " else false " +
                        " end payedBeforeSecondReminder, " +
                        " case " +
                        " when i.id_invoice_status = :invoiceStatusBilledId " +
                        " and i.second_reminder_date_time is not null " +
                        " and i.third_reminder_date_time is null then true " +
                        " else false " +
                        " end payedBeforeThirdReminder, " +
                        " case " +
                        " when i.id_invoice_status = :invoiceStatusBilledId " +
                        " and i.third_reminder_date_time is not null " +
                        " and tf.id_invoice is null then true " +
                        " else false " +
                        " end payedBeforeManuelReminder, " +
                        " case " +
                        " when i.id_invoice_status = :invoiceStatusBilledId " +
                        " and i.third_reminder_date_time is not null " +
                        " and tf.id_invoice is not null then true " +
                        " else false " +
                        " end payedAfterManualReminder " +
                        " from " +
                        " invoice i " +
                        " left join ( " +
                        " select " +
                        " distinct tf.id_invoice " +
                        " from " +
                        " tiers_followup tf " +
                        " where " +
                        " tf.id_tiers_followup_type = :tiersFollowUpTypeInvoiceReminderId) tf on " +
                        " tf.id_invoice = i.id " +
                        " left join invoice_item ii on " +
                        " ii.id_invoice = i.id " +
                        " left join billing_item bi on " +
                        " bi.id = ii.id_billing_item " +
                        " left join billing_type bt on " +
                        " bt.id = bi.id_billing_type " +
                        " where " +
                        " i.id_invoice_status in (:invoiceStatusBilledId, :invoiceStatusSentId) " +
                        " group by " +
                        " i.id, " +
                        " tf.id_invoice " +
                        " ) " +
                        " select " +
                        " case " +
                        " when noReminder then 'Non relancé' " +
                        " when reminderOnce then 'Relancé une fois' " +
                        " when reminderTwice then 'Relancé deux fois' " +
                        " when reminderThird then 'Relancé trois fois' " +
                        " when manualReminder then 'Relancé manuellement' " +
                        " else 'Indéterminé' " +
                        " end as reminderType, " +
                        " case " +
                        " when payedBeforeFirstReminder then 'Payé avant relance' " +
                        " when payedBeforeSecondReminder then 'Payé après une relance' " +
                        " when payedBeforeThirdReminder then 'Payé après deux relance' " +
                        " when payedBeforeManuelReminder then 'Payé après trois relance' " +
                        " when payedAfterManualReminder then 'Payé après relance manuelle' " +
                        " else 'Non payé' " +
                        " end as payedPeriod, " +
                        " sum(turnoverAmountWithoutTax) as turnoverAmountWithoutTax, " +
                        " sum(turnoverAmountWithTax) as turnoverAmountWithTax, " +
                        " sum(turnoverAmountWithoutDebourWithoutTax) as turnoverAmountWithoutDebourWithoutTax , " +
                        " sum(turnoverAmountWithoutDebourWithTax) as turnoverAmountWithoutDebourWithTax, " +
                        " sum(1) as nbrInvoices " +
                        " from " +
                        " invoice_ca i " +
                        " group by " +
                        " case " +
                        " when noReminder then 'Non relancé' " +
                        " when reminderOnce then 'Relancé une fois' " +
                        " when reminderTwice then 'Relancé deux fois' " +
                        " when reminderThird then 'Relancé trois fois' " +
                        " when manualReminder then 'Relancé manuellement' " +
                        " else 'Indéterminé' " +
                        " end , " +
                        " case " +
                        " when payedBeforeFirstReminder then 'Payé avant relance' " +
                        " when payedBeforeSecondReminder then 'Payé après une relance' " +
                        " when payedBeforeThirdReminder then 'Payé après deux relance' " +
                        " when payedBeforeManuelReminder then 'Payé après trois relance' " +
                        " when payedAfterManualReminder then 'Payé après relance manuelle' " +
                        " else 'Non payé' " +
                        " end  " +
                        "")
        List<IRecoveryReporting> getRecoveryReporting(@Param("invoiceStatusBilledId") Integer invoiceStatusBilledId,
                        @Param("invoiceStatusSentId") Integer invoiceStatusSentId,
                        @Param("tiersFollowUpTypeInvoiceReminderId") Integer tiersFollowUpTypeInvoiceReminderId);
}