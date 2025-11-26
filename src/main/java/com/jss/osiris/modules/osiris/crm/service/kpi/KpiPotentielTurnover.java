package com.jss.osiris.modules.osiris.crm.service.kpi;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.crm.model.IKpiThread;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmService;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmValueService;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderStatusService;
import com.jss.osiris.modules.osiris.reporting.model.ReportingWidget;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class KpiPotentielTurnover implements IKpiThread {

    @Autowired
    KpiCrmValueService kpiCrmValueService;

    @Autowired
    KpiCrmService kpiCrmService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    CustomerOrderStatusService customerOrderStatusService;

    @Autowired
    KpiPotentielTurnoverInProgressOrder kpiPotentielTurnoverInProgressOrder;

    @Autowired
    KpiPotentielTurnoverQuotation kpiPotentielTurnoverQuotation;

    @Autowired
    KpiPotentielTurnoverReccurringOrder kpiPotentielTurnoverReccurringOrder;

    @Autowired
    ResponsableService responsableService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public String getCode() {
        return "POTENTIAL_TURNOVER";
    }

    @Override
    public BigDecimal getDefaultValue() {
        return new BigDecimal(0);
    }

    @Override
    public String getAggregateTypeForResponsable() {
        return KpiCrm.AGGREGATE_TYPE_SUM;
    }

    @Override
    public String getAggregateTypeForTimePeriod() {
        return KpiCrm.AGGREGATE_TYPE_LAST_VALUE;
    }

    @Override
    public String getLabelType() {
        return ReportingWidget.LABEL_TYPE_CATEGORY;
    }

    @Override
    public String getUnit() {
        return "€";
    }

    @Override
    public String getIcon() {
        return "tablerGhost";
    }

    @Override
    public Boolean getIsPositiveEvolutionGood() {
        return true;
    }

    @Override
    public String getGraphType() {
        return null;
    }

    @Override
    public void computeKpiCrmValues() throws OsirisException {
        KpiCrm kpiCrm = kpiCrmService.getKpiCrmByCode(getCode());
        if (kpiCrm == null)
            throw new OsirisException("KpiCrm not defined for code " + getCode());

        List<KpiCrmValue> newValuesInProgress = kpiPotentielTurnoverInProgressOrder.getKpiValues(kpiCrm);
        List<KpiCrmValue> newValuesQuotation = kpiPotentielTurnoverQuotation.getKpiValues(kpiCrm);
        List<KpiCrmValue> newValuesRecurring = kpiPotentielTurnoverReccurringOrder.getKpiValues(kpiCrm);

        // Aggregate all values
        List<KpiCrmValue> allValues = Stream.of(newValuesInProgress, newValuesQuotation, newValuesRecurring)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        Map<AggregationKey, BigDecimal> aggregatedMap = allValues.stream()
                .collect(Collectors.groupingBy(
                        value -> new AggregationKey(
                                value.getResponsable().getId(),
                                value.getValueDate(),
                                value.getKpiCrm().getId()),
                        Collectors.reducing(
                                BigDecimal.ZERO, // default value
                                KpiCrmValue::getValue, // mapped value
                                BigDecimal::add // mapped function
                        )));

        // Remapped to value
        List<KpiCrmValue> aggregatedList = aggregatedMap.entrySet().stream()
                .map(entry -> {
                    AggregationKey key = entry.getKey();
                    BigDecimal totalValue = entry.getValue();
                    KpiCrmValue aggregatedKpi = new KpiCrmValue();

                    aggregatedKpi.setKpiCrm(kpiCrm);
                    aggregatedKpi.setResponsable(responsableService.getResponsable(key.responsableId));
                    aggregatedKpi.setValueDate(key.valueDate);
                    aggregatedKpi.setValue(totalValue);

                    return aggregatedKpi;
                })
                .collect(Collectors.toList());

        if (aggregatedList != null && aggregatedList.size() > 0) {
            kpiCrmValueService.deleteKpiCrmValuesForKpiCrm(kpiCrm);
            entityManager.flush();
            kpiCrmService.saveValuesForKpiAndDay(kpiCrm, aggregatedList);
        }
    }

    private static class AggregationKey {
        private final Integer responsableId;
        private final LocalDate valueDate;
        private final Integer kpiCrmId;

        public AggregationKey(Integer responsableId, LocalDate valueDate, Integer kpiCrmId) {
            this.responsableId = responsableId;
            this.valueDate = valueDate;
            this.kpiCrmId = kpiCrmId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            AggregationKey that = (AggregationKey) o;
            return responsableId.equals(that.responsableId) && kpiCrmId.equals(that.kpiCrmId) &&
                    valueDate.isEqual(that.valueDate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(kpiCrmId, responsableId, valueDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        }

    }

}
