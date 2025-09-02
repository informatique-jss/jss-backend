package com.jss.osiris.modules.osiris.crm.service.KpiThreads;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.crm.model.AnalyticStatsType;
import com.jss.osiris.modules.osiris.crm.model.IKpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

@Service
public class KpiOverdueBalanceService implements IKpiCrm {

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    ConstantService constantService;

    @Override
    public String getCode() {
        return KpiCrm.OVERDUE_BALANCE;
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public String getAggregateType() {
        return KpiCrm.AGGREGATE_TYPE_CUMUL;
    }

    @Override
    public List<KpiCrmValue> getComputeValue(Responsable responsable, LocalDate startDate, LocalDate endDate) {
        List<Invoice> invoices = new ArrayList<Invoice>();
        List<KpiCrmValue> dailyKpis = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            try {
                invoices = invoiceService.getDueInvoicesForResponsablesAndDueDates(
                        List.of(constantService.getInvoiceStatusSend()), List.of(responsable), startDate, endDate);
            } catch (OsirisException e) {
                e.printStackTrace();
            }

            if (!invoices.isEmpty()) {
                BigDecimal dueAmount = BigDecimal.ZERO;
                int invoiceCount = 0;

                for (Invoice invoice : invoices) {
                    BigDecimal currentAmount = BigDecimal.ZERO;
                    try {
                        currentAmount = invoiceService.getRemainingAmountToPayForInvoice(invoice);
                    } catch (OsirisException e) {
                        e.printStackTrace();
                    }
                    if (currentAmount.compareTo(BigDecimal.ZERO) > 0) {
                        dueAmount = dueAmount.add(currentAmount);
                        invoiceCount++;
                    }
                }
                if (invoiceCount > 0) {
                    KpiCrmValue kpiCrmValue = new KpiCrmValue();
                    kpiCrmValue.setResponsable(responsable);
                    kpiCrmValue.setValueDate(date);
                    kpiCrmValue.setValue(dueAmount);
                    dailyKpis.add(kpiCrmValue);
                }
            }
        }
        return dailyKpis;
    }

    @Override
    public AnalyticStatsType getKpiCrmAggregatedValue(List<Responsable> responsables, LocalDate startDate,
            LocalDate endDate) {

        return null;

    }

}
