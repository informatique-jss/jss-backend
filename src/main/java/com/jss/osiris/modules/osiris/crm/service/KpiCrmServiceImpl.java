package com.jss.osiris.modules.osiris.crm.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.crm.model.AnalyticStatsType;
import com.jss.osiris.modules.osiris.crm.model.AnalyticStatsValue;
import com.jss.osiris.modules.osiris.crm.model.IKpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.crm.repository.KpiCrmRepository;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

@Service
public class KpiCrmServiceImpl implements KpiCrmService {
    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    QuotationService quotationService;

    @Autowired
    KpiCrmRepository kpiCrmRepository;

    @Autowired
    KpiCrmValueService kpiCrmValueService;

    public KpiCrm addOrUpdateKpiCrm(KpiCrm kpiCrm) {
        return kpiCrmRepository.save(kpiCrm);
    }

    @Override
    public KpiCrm getKpiCrmByCode(String code) {
        return kpiCrmRepository.findByCode(code);
    }

    @Autowired
    List<? extends IKpiCrm> IKpiServiceList;

    @Override
    public List<KpiCrm> getKpiCrms() {
        return IterableUtils.toList(kpiCrmRepository.findAll());
    }

    @Transactional(rollbackFor = Exception.class)
    public void computeKpiCrm(Responsable responsable, LocalDate dateToCompute) {
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now().minusDays(1);
        if (IKpiServiceList != null && IKpiServiceList.size() > 0)
            for (IKpiCrm iKpi : IKpiServiceList) {
                List<KpiCrmValue> dailyKpiValues = iKpi.getComputeValue(responsable, startDate, endDate);
                KpiCrm kpi = new KpiCrm();
                kpi.setLastUpdate(LocalDateTime.now());
                kpi.setCode(iKpi.getCode());
                kpi.setAggregateType(iKpi.getAggregateType());
                addOrUpdateKpiCrm(kpi);

                for (KpiCrmValue kpiCrmValue : dailyKpiValues) {
                    kpiCrmValue.setKpiCrm(kpi);
                    kpiCrmValueService.addOrUpdateKpiCrmValue(kpiCrmValue);
                }
            }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<AnalyticStatsType> getAggregatedKpis(KpiCrm kpiCrm, List<Responsable> responsables,
            LocalDate startDate, LocalDate endDate) {
        List<AnalyticStatsType> aggregatedKpiValues = new ArrayList<>();

        if (kpiCrm.getAggregateType() != null && kpiCrm.getAggregateType().equals(KpiCrm.AGGREGATE_TYPE_CUMUL)) {
            List<KpiCrmValue> kpiCrmValues = kpiCrmValueService.getValuesForKpiCrmAndResponsablesAndDates(kpiCrm,
                    responsables, startDate, endDate);

            BigDecimal sum = kpiCrmValues.stream()
                    .map(KpiCrmValue::getValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            AnalyticStatsType analyticStatsType = new AnalyticStatsType();
            AnalyticStatsValue analyticStatsValue = new AnalyticStatsValue();
            analyticStatsValue.setValue(sum);
            analyticStatsType.setId(kpiCrm.getId());
            analyticStatsType.setTitle(kpiCrm.getLabel());
            analyticStatsType.setAnalyticStatsValue(analyticStatsValue);
            analyticStatsType.setValueDate(endDate);

            aggregatedKpiValues.add(analyticStatsType);
        }

        if (kpiCrm.getAggregateType() != null && kpiCrm.getAggregateType().equals(KpiCrm.AGGREGATE_TYPE_AVERAGE)) {
            for (IKpiCrm iKpi : IKpiServiceList) {
                if (iKpi.getCode().equals(kpiCrm.getCode())) {
                    aggregatedKpiValues.add(iKpi.getKpiCrmAggregatedValue(responsables, startDate, endDate));
                }
            }
        }

        if (kpiCrm.getAggregateType() != null && kpiCrm.getAggregateType().equals(KpiCrm.AGGREGATE_TYPE_HISTORIC)) {
            List<KpiCrmValue> kpiCrmValues = kpiCrmValueService
                    .getValuesForKpiCrmAndResponsablesAndDates(kpiCrm, responsables, startDate, endDate);

            Map<LocalDate, Map<KpiCrm, BigDecimal>> aggregatedList = kpiCrmValues.stream()
                    .collect(Collectors.groupingBy(
                            KpiCrmValue::getValueDate,
                            Collectors.groupingBy(
                                    KpiCrmValue::getKpiCrm,
                                    Collectors.mapping(
                                            KpiCrmValue::getValue,
                                            Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)))));

            for (Map.Entry<LocalDate, Map<KpiCrm, BigDecimal>> dateEntry : aggregatedList.entrySet()) {
                LocalDate date = dateEntry.getKey();
                Map<KpiCrm, BigDecimal> kpiMap = dateEntry.getValue();

                for (Map.Entry<KpiCrm, BigDecimal> kpiEntry : kpiMap.entrySet()) {
                    KpiCrm kpi = kpiEntry.getKey();
                    BigDecimal totalValue = kpiEntry.getValue();

                    AnalyticStatsType analyticStatsType = new AnalyticStatsType();
                    AnalyticStatsValue analyticStatsValue = new AnalyticStatsValue();

                    analyticStatsValue.setValue(totalValue);
                    analyticStatsType.setId(kpi.getId());
                    analyticStatsType.setTitle(kpi.getLabel());
                    analyticStatsType.setAnalyticStatsValue(analyticStatsValue);
                    analyticStatsType.setValueDate(date);

                    aggregatedKpiValues.add(analyticStatsType);
                }
            }
        }

        return aggregatedKpiValues;
    }
}
