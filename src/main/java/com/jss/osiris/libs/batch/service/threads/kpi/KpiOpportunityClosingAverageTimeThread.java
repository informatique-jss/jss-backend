package com.jss.osiris.libs.batch.service.threads.kpi;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.crm.model.IKpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationStatusService;
import com.jss.osiris.modules.osiris.reporting.model.ReportingWidget;

@Service
public class KpiOpportunityClosingAverageTimeThread implements IKpiCrm {

    @Autowired
    QuotationService quotationService;

    @Autowired
    KpiCrmService kpiCrmService;

    @Autowired
    QuotationStatusService quotationStatusService;

    @Override
    public String getCode() {
        return KpiCrm.OPPORTUNITY_CLOSING_AVERAGE_TIME;
    }

    @Override
    public String getLabelType() {
        return ReportingWidget.LABEL_TYPE_DATETIME;
    }

    @Override
    public String getAggregateType() {
        return KpiCrm.AGGREGATE_TYPE_AVERAGE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<KpiCrmValue> computeKpiCrmValues() {
        List<KpiCrmValue> dailyKpis = new ArrayList<>();

        // for (LocalDate date = startDate; !date.isAfter(endDate); date =
        // date.plusDays(1)) {
        // LocalDateTime startOfDay = date.atStartOfDay();
        // LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // List<Quotation> quotations = quotationService
        // .getQuotationsByResponsablesAndStatusAndDates(List.of(responsable),
        // startOfDay, endOfDay,
        // quotationStatusService.getQuotationStatusByCode(QuotationStatus.SENT_TO_CUSTOMER));

        // if (!quotations.isEmpty()) {
        // BigDecimal kpiTotal = BigDecimal.ZERO;
        // int kpiValuesCount = 0;

        // for (Quotation quotation : quotations) {
        // if (!quotation.getCustomerOrders().isEmpty()
        // && quotation.getCustomerOrders().get(0).getCreatedDate() != null && quotation
        // .getCreatedDate().isBefore(quotation.getCustomerOrders().get(0).getCreatedDate()))
        // {
        // kpiValuesCount++;
        // long daysBetween = Duration.between(
        // quotation.getCreatedDate(),
        // quotation.getCustomerOrders().get(0).getCreatedDate()).toMinutes();

        // kpiTotal = kpiTotal.add(BigDecimal.valueOf(daysBetween));
        // }
        // }

        // if (kpiValuesCount > 0) {
        // KpiCrmValue kpiCrmValue = new KpiCrmValue();
        // kpiCrmValue.setResponsable(responsable);
        // kpiCrmValue.setValueDate(date);
        // kpiCrmValue.setValue(kpiTotal.divide(BigDecimal.valueOf(kpiValuesCount),
        // RoundingMode.HALF_UP));
        // dailyKpis.add(kpiCrmValue);
        // }
        // }
        // }

        return dailyKpis;
    }

    @Override
    public BigDecimal getDefaultValue() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDefaultValue'");
    }

    // @Override
    // public AnalyticStatsType getKpiCrmAggregatedValue(List<Responsable>
    // responsables, LocalDate startDate,
    // LocalDate endDate) {

    // LocalDateTime startOfDay = startDate.atStartOfDay();
    // LocalDateTime endOfDay = endDate.atTime(LocalTime.MAX);
    // AnalyticStatsType analyticStatsType = new AnalyticStatsType();
    // AnalyticStatsValue analyticStatsValue = new AnalyticStatsValue();
    // KpiCrm kpiCrm = kpiCrmService.getKpiCrmByCode(getCode());

    // List<Quotation> quotations =
    // quotationService.getQuotationsByResponsablesAndStatusAndDates(
    // responsables, startOfDay, endOfDay,
    // quotationStatusService.getQuotationStatusByCode(QuotationStatus.SENT_TO_CUSTOMER));

    // if (!quotations.isEmpty()) {
    // BigDecimal kpiTotal = BigDecimal.ZERO;
    // int kpiValuesCount = 0;

    // for (Quotation quotation : quotations) {
    // if (!quotation.getCustomerOrders().isEmpty()
    // && quotation.getCustomerOrders().get(0).getCreatedDate() != null && quotation
    // .getCreatedDate().isBefore(quotation.getCustomerOrders().get(0).getCreatedDate()))
    // {
    // kpiValuesCount++;
    // long daysBetween = Duration.between(
    // quotation.getCreatedDate(),
    // quotation.getCustomerOrders().get(0).getCreatedDate()).toHours();

    // kpiTotal = kpiTotal.add(BigDecimal.valueOf(daysBetween));
    // }
    // }

    // if (kpiValuesCount > 0) {
    // analyticStatsValue.setValue(kpiTotal.divide(BigDecimal.valueOf(kpiValuesCount)));
    // analyticStatsValue.setSuffix("Heures");
    // analyticStatsType.setAnalyticStatsValue(analyticStatsValue);
    // analyticStatsType.setValueDate(endDate);
    // analyticStatsType.setId(kpiCrm.getId());
    // analyticStatsType.setTitle(kpiCrm.getLabel());
    // }
    // }
    // return analyticStatsType;
    // }
}