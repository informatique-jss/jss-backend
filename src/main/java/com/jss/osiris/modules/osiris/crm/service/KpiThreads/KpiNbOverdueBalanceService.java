package com.jss.osiris.modules.osiris.crm.service.KpiThreads;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.audit.service.AuditService;
import com.jss.osiris.modules.osiris.crm.model.IKpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmService;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;

@Service
public class KpiNbOverdueBalanceService implements IKpiCrm {

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    ConstantService constantService;

    @Autowired
    AuditService auditService;

    @Autowired
    KpiCrmService kpiCrmService;

    @Override
    public String getCode() {
        return KpiCrm.NB_OVERDUE_BALANCE;
    }

    @Override
    public String getAggregateType() {
        return null;
    }

    // TODO : test method
    @Override
    public List<KpiCrmValue> computeKpiCrmValues() {
        List<KpiCrmValue> dailyKpis = new ArrayList<>();

        // List<Invoice> invoices = new ArrayList<Invoice>();
        // try {
        // invoices =
        // invoiceService.searchInvoices(List.of(constantService.getInvoiceStatusSend()),
        // List.of(responsable));
        // } catch (OsirisException e) {
        // e.printStackTrace();
        // }
        // Long kpiTotal = 0L;

        // kpiTotal = invoices.stream()
        // .filter(order -> order.getFirstReminderDateTime() != null).count();

        // KpiCrmValue kpiCrmValue = new KpiCrmValue();
        // kpiCrmValue.setResponsable(responsable);
        // kpiCrmValue.setValueDate(endDate);
        // kpiCrmValue.setValue(BigDecimal.valueOf(kpiTotal));
        return dailyKpis;
    }

    @Override
    public LocalDate getClosestLastDate(LocalDate fromDate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getClosestLastDate'");
    }

    @Override
    public BigDecimal getDefaultValue() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDefaultValue'");
    }

    // // TODO : test method
    // @Override
    // public AnalyticStatsType getKpiCrmAggregatedValue(List<Responsable>
    // responsables, LocalDate startDate,
    // LocalDate endDate) {
    // AnalyticStatsType analyticStatsType = new AnalyticStatsType();
    // analyticStatsType.setTitle(getAggregateType());

    // BigDecimal kpiTotal = BigDecimal.ZERO;
    // AnalyticStatsValue analyticStatsValue = new AnalyticStatsValue();
    // KpiCrm kpiCrm = kpiCrmService.getKpiCrmByCode(getCode());

    // for (Responsable responsable : responsables) {
    // kpiTotal.add(getKpiCrmValues(responsable, startDate,
    // endDate).get(0).getValue());
    // }

    // analyticStatsValue.setValue(kpiTotal);
    // analyticStatsType.setAnalyticStatsValue(analyticStatsValue);
    // analyticStatsType.setValueDate(endDate);
    // analyticStatsType.setId(kpiCrm.getId());
    // analyticStatsType.setTitle(kpiCrm.getLabel());

    // return analyticStatsType;
    // }
}