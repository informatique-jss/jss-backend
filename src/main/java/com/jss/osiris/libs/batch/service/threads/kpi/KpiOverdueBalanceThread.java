package com.jss.osiris.libs.batch.service.threads.kpi;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.crm.model.IKpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmService;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.reporting.model.ReportingWidget;

@Service
public class KpiOverdueBalanceThread implements IKpiCrm {

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    ConstantService constantService;

    @Autowired
    KpiCrmService kpiCrmService;

    @Override
    public String getCode() {
        return KpiCrm.OVERDUE_BALANCE;
    }

    @Override
    public String getLabelType() {
        return ReportingWidget.LABEL_TYPE_DATETIME;
    }

    @Override
    public String getAggregateType() {
        return KpiCrm.AGGREGATE_TYPE_SUM;
    }

    // TODO : test method
    @Override
    public List<KpiCrmValue> computeKpiCrmValues() {
        List<Invoice> invoices = new ArrayList<Invoice>();
        List<KpiCrmValue> dailyKpis = new ArrayList<>();

        // try {
        // invoices =
        // invoiceService.searchInvoices(List.of(constantService.getInvoiceStatusSend()),
        // List.of(responsable));
        // } catch (OsirisException e) {
        // e.printStackTrace();
        // }
        // BigDecimal kpiTotal = BigDecimal.ZERO;

        // List<Invoice> filteredInvoices = invoices.stream()
        // .filter(order -> order.getFirstReminderDateTime() != null).toList();

        // for (Invoice invoice : filteredInvoices) {
        // kpiTotal.add(invoice.getTotalPrice());
        // }

        // KpiCrmValue kpiCrmValue = new KpiCrmValue();
        // kpiCrmValue.setResponsable(responsable);
        // kpiCrmValue.setValueDate(endDate);
        // kpiCrmValue.setValue(kpiTotal);
        return dailyKpis;
    }

    @Override
    public BigDecimal getDefaultValue() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDefaultValue'");
    }

    // TODO : test method
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
