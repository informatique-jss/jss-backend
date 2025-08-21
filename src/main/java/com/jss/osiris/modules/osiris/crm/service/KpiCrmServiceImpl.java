package com.jss.osiris.modules.osiris.crm.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
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

    public KpiCrm addOrUpdateKpiCrm(KpiCrm kpiCrm) {
        return kpiCrmRepository.save(kpiCrm);
    }

    @Autowired
    List<? extends IKpiCrm> IKpiServiceList;

    public void computeKpiCrm(Responsable responsable, LocalDate dateToCompute) {
        if (IKpiServiceList != null && IKpiServiceList.size() > 0)
            for (IKpiCrm iKpi : IKpiServiceList) {
                Map<LocalDate, BigDecimal> dailyKpiValues = iKpi.getComputeValue(responsable);

                for (Map.Entry<LocalDate, BigDecimal> entry : dailyKpiValues.entrySet()) {
                    KpiCrm kpi = new KpiCrm();
                    // kpi.setResponsable(responsable);
                    // kpi.setCreatedDate(entry.getKey());
                    // kpi.setCode(iKpi.getCode());
                    // kpi.setComputeType(iKpi.getComputeType());
                    // kpi.setValue(entry.getValue());

                    addOrUpdateKpiCrm(kpi);
                }
            }
    }

    @Override
    public List<KpiCrm> getTiersAverageKpi(List<Responsable> responsables, LocalDate startDate, LocalDate endDate) {
        List<KpiCrm> kpiAggregated = new ArrayList<>();

        if (IKpiServiceList != null && !IKpiServiceList.isEmpty()) {
            IKpiServiceList.stream()
                    .filter(kpi -> kpi.getComputeType().equals(KpiCrm.COMPUTE_TYPE_AVERAGE))
                    .collect(Collectors.toList());
            for (IKpiCrm iKpi : IKpiServiceList) {
                List<KpiCrm> kpiToAggregate = kpiCrmRepository.findByResponsableAndCodeAndCreatedDateBetween(
                        responsables, iKpi.getCode(), startDate, endDate);

                if (!kpiToAggregate.isEmpty()) {
                    BigDecimal sum = BigDecimal.ZERO;

                    for (KpiCrm kpi : kpiToAggregate) {
                        // if (kpi.getValue() != null) {
                        // sum = sum.add(kpi.getValue());
                        // }
                    }

                    BigDecimal avg = sum.divide(BigDecimal.valueOf(kpiToAggregate.size()), RoundingMode.HALF_UP);

                    KpiCrm aggregate = new KpiCrm();
                    aggregate.setCode(iKpi.getCode());
                    aggregate.setLabel(iKpi.getLabel());
                    aggregate.setComputeType(iKpi.getComputeType());
                    // aggregate.setCreatedDate(endDate);
                    // aggregate.setValue(avg);

                    kpiAggregated.add(aggregate);
                }
            }
        }
        return kpiAggregated;
    }

    @Override
    public List<KpiCrm> getTiersCumulKpi(List<Responsable> responsables, LocalDate startDate, LocalDate endDate) {
        List<KpiCrm> kpiAggregated = new ArrayList<>();

        if (IKpiServiceList != null && !IKpiServiceList.isEmpty()) {
            IKpiServiceList.stream()
                    .filter(kpi -> kpi.getComputeType().equals(KpiCrm.COMPUTE_TYPE_CUMUL))
                    .collect(Collectors.toList());
            for (IKpiCrm iKpi : IKpiServiceList) {
                List<KpiCrm> kpiToAggregate = kpiCrmRepository.findByResponsableAndCodeAndCreatedDateBetween(
                        responsables, iKpi.getCode(), startDate, endDate);
                if (!kpiToAggregate.isEmpty()) {
                    BigDecimal sum = BigDecimal.ZERO;

                    for (KpiCrm kpi : kpiToAggregate) {
                        // if (kpi.getValue() != null) {
                        // sum = sum.add(kpi.getValue());
                        // }
                    }

                    KpiCrm aggregate = new KpiCrm();
                    aggregate.setCode(iKpi.getCode());
                    aggregate.setLabel(iKpi.getLabel());
                    aggregate.setComputeType(iKpi.getComputeType());
                    // aggregate.setCreatedDate(endDate);
                    // aggregate.setValue(sum);

                    kpiAggregated.add(aggregate);
                }
            }
        }
        return kpiAggregated;
    }

}
