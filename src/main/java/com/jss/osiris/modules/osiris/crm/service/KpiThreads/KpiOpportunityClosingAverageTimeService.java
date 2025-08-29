package com.jss.osiris.modules.osiris.crm.service.KpiThreads;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.crm.model.AnalyticStatsType;
import com.jss.osiris.modules.osiris.crm.model.AnalyticStatsValue;
import com.jss.osiris.modules.osiris.crm.model.IKpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmService;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.QuotationStatus;
import com.jss.osiris.modules.osiris.quotation.service.QuotationService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationStatusService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

@Service
public class KpiOpportunityClosingAverageTimeService implements IKpiCrm {

    @Autowired
    QuotationService quotationService;

    @Autowired
    KpiCrmService kpiCrmService;

    @Autowired
    QuotationStatusService quotationStatusService;

    @Override
    public String getLabel() {

        throw new UnsupportedOperationException("Unimplemented method 'getLabel'");
    }

    @Override
    public String getCode() {
        return KpiCrm.OPPORTUNITY_CLOSING_AVERAGE_TIME;
    }

    @Override
    public String getAggregateType() {
        return KpiCrm.AGGREGATE_TYPE_AVERAGE;
    }

    @Override
    public List<KpiCrmValue> getComputeValue(Responsable responsable, LocalDate startDate, LocalDate endDate) {
        List<KpiCrmValue> dailyKpis = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            List<Quotation> quotations = quotationService
                    .getQuotationsByResponsablesAndStatusAndDates(List.of(responsable), startOfDay, endOfDay,
                            quotationStatusService.getQuotationStatusByCode(QuotationStatus.SENT_TO_CUSTOMER));

            if (!quotations.isEmpty()) {
                BigDecimal kpiTotal = BigDecimal.ZERO;
                int kpiValuesCount = 0;

                for (Quotation quotation : quotations) {
                    if (!quotation.getCustomerOrders().isEmpty()
                            && quotation.getCustomerOrders().get(0).getCreatedDate() != null) {
                        kpiValuesCount++;
                        long daysBetween = Duration.between(
                                quotation.getCreatedDate(),
                                quotation.getCustomerOrders().get(0).getCreatedDate()).toDays();

                        kpiTotal = kpiTotal.add(BigDecimal.valueOf(daysBetween));
                    }
                }

                if (kpiValuesCount > 0) {
                    KpiCrmValue kpiCrmValue = new KpiCrmValue();
                    kpiCrmValue.setResponsable(responsable);
                    kpiCrmValue.setValueDate(date);
                    kpiCrmValue.setValue(kpiTotal.divide(BigDecimal.valueOf(kpiValuesCount), RoundingMode.HALF_UP));
                    dailyKpis.add(kpiCrmValue);
                }
            }
        }

        return dailyKpis;
    }

    @Override
    public AnalyticStatsType getKpiCrmAggregatedValue(List<Responsable> responsables, LocalDate startDate,
            LocalDate endDate) {

        LocalDateTime startOfDay = startDate.atStartOfDay();
        LocalDateTime endOfDay = endDate.atTime(LocalTime.MAX);
        AnalyticStatsType analyticStatsType = new AnalyticStatsType();
        AnalyticStatsValue analyticStatsValue = new AnalyticStatsValue();
        KpiCrm kpiCrm = kpiCrmService.getKpiCrmByCode(getCode());

        List<Quotation> quotations = quotationService.getQuotationsByResponsablesAndStatusAndDates(
                responsables, startOfDay, endOfDay,
                quotationStatusService.getQuotationStatusByCode(QuotationStatus.SENT_TO_CUSTOMER));

        if (!quotations.isEmpty()) {
            BigDecimal kpiTotal = BigDecimal.ZERO;
            int kpiValuesCount = 0;

            for (Quotation quotation : quotations) {
                if (!quotation.getCustomerOrders().isEmpty()
                        && quotation.getCustomerOrders().get(0).getCreatedDate() != null) {
                    kpiValuesCount++;
                    long daysBetween = Duration.between(
                            quotation.getCreatedDate(),
                            quotation.getCustomerOrders().get(0).getCreatedDate()).toDays();

                    kpiTotal = kpiTotal.add(BigDecimal.valueOf(daysBetween));
                }
            }

            if (kpiValuesCount > 0) {
                analyticStatsValue.setValue(kpiTotal.divide(BigDecimal.valueOf(kpiValuesCount)));
                analyticStatsType.setAnalyticStatsValue(analyticStatsValue);
                analyticStatsType.setValueDate(endDate);
                analyticStatsType.setId(kpiCrm.getId());
                analyticStatsType.setTitle(kpiCrm.getLabel());
            }
        }
        return analyticStatsType;
    }
}