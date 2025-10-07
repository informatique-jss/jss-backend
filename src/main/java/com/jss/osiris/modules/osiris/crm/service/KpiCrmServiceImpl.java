package com.jss.osiris.modules.osiris.crm.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.crm.dto.KpiWidgetDto;
import com.jss.osiris.modules.osiris.crm.model.IKpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.crm.repository.KpiCrmRepository;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationService;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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

    @Autowired
    ResponsableService responsableService;

    @Autowired
    BatchService batchService;

    @PersistenceContext
    EntityManager em;

    @Autowired
    List<? extends IKpiCrm> IKpiThreadList;

    public KpiCrm addOrUpdateKpiCrm(KpiCrm kpiCrm) {
        return kpiCrmRepository.save(kpiCrm);
    }

    @Override
    public KpiCrm getKpiCrmByCode(String code) {
        return kpiCrmRepository.findByCode(code);
    }

    @Override
    public KpiCrm getKpiCrmById(Integer id) {
        Optional<KpiCrm> kpiCrm = kpiCrmRepository.findById(id);
        if (kpiCrm.isPresent())
            return kpiCrm.get();
        return null;
    }

    @Override
    public List<KpiCrm> getKpiCrms() {
        return IterableUtils.toList(kpiCrmRepository.findAll());
    }

    public List<KpiCrm> getKpiCrmsByDisplayedPageCode(String displayedPageCode) {
        return kpiCrmRepository.getKpiCrmByDisplayedPage(displayedPageCode);
    }

    /**
     * Method called by batch to persist the KpiValues of a KpiCrm
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void computeKpiCrm(Integer kpiCrmId) {
        // Before updating the values of the table we truncate it
        KpiCrm kpiCrm = getKpiCrmById(kpiCrmId);
        for (IKpiCrm iKpiThread : IKpiThreadList) {
            if (iKpiThread.getCode().equals(kpiCrm.getCode())) {
                List<KpiCrmValue> kpiValues = iKpiThread.computeKpiCrmValues();
                kpiCrm.setLastUpdate(LocalDateTime.now());
                addOrUpdateKpiCrm(kpiCrm);

                for (KpiCrmValue kpiCrmValue : kpiValues) {
                    kpiCrmValue.setKpiCrm(kpiCrm);
                }
                kpiCrmValueService.addOrUpdateKpiCrmValues(kpiValues);
                break;
            }
        }
    }

    /**
     * Method to get the values of the KpiCrmValues for a KpiCrm to display series
     * of data
     */
    @Override
    public String getKpiValues(Integer kpiCrmId, List<Integer> responsablesIds) throws JsonProcessingException {
        String jsonPayloadString = "";

        KpiCrm kpiCrm = getKpiCrmById(kpiCrmId);

        if (kpiCrm != null) {

            String sqlNameAggregatedFunction = getSqlNameAggregatedFunction(kpiCrm.getAggregateType());

            StringBuilder dataSql = new StringBuilder();

            String responsablesIdsString = String.join(",", responsablesIds.stream().map(r -> r.toString()).toList());

            dataSql.append(String.format(
                    "select kcv.value_date, %s(kcv.value) as y_value " +
                            "from kpi_crm_value kcv " +
                            "where kcv.id_kpi = %d " +
                            "and kcv.id_responsable in (%s) " +
                            "group by kcv.value_date ",
                    sqlNameAggregatedFunction, kpiCrmId, responsablesIdsString));

            String finalSql = String.format("""
                    select
                        json_agg(
                            jsonb_build_object(
                                'data', data_points,
                                'label', '%s',
                                'type', 'line'
                            )
                        )
                    from (
                        select
                            json_agg(jsonb_build_array(value_date, y_value) order by value_date) as data_points
                        from (%s)
                    );
                     """, kpiCrm.getLabel(), dataSql);

            Object jsonPayload = em.createNativeQuery(finalSql).getSingleResult();
            ObjectMapper mapper = new ObjectMapper();
            jsonPayloadString = mapper.writeValueAsString(jsonPayload);

        }

        return jsonPayloadString;
    }

    /**
     * Method to get the list of the kpis in the form of a dto for a specific page
     * in the front and for a given timescale
     */
    @Override
    public List<KpiWidgetDto> getKpiCrmWidget(String displayedPageCode, String timescale,
            List<Integer> responsables) {
        List<KpiWidgetDto> kpisWidgetDtos = new ArrayList<>();

        List<KpiCrm> kpiCrms = this.getKpiCrmsByDisplayedPageCode(displayedPageCode);

        for (KpiCrm kpiCrm : kpiCrms) {

            LocalDate currentValueDate = LocalDate.now();
            LocalDate previousValueDate = getPreviousDate(currentValueDate, timescale);

            if (getKpiThread(kpiCrm) != null) {

                BigDecimal currentValue = kpiCrmRepository.findLastValueForResponsables(
                        responsables,
                        kpiCrm.getId(),
                        getKpiThread(kpiCrm).getClosestLastDate(currentValueDate),
                        currentValueDate);

                BigDecimal previousValue = kpiCrmRepository.findValueByDayOfMonthForResponsables(
                        responsables,
                        kpiCrm.getId(),
                        currentValueDate.getDayOfMonth(),
                        getKpiThread(kpiCrm).getClosestLastDate(previousValueDate),
                        previousValueDate);

                KpiWidgetDto kpiWidgetDto = new KpiWidgetDto();
                kpiWidgetDto.setIdKpi(kpiCrm.getId());
                kpiWidgetDto.setKpiValue(currentValue);
                if (previousValue != null && !currentValue.equals(new BigDecimal(0))) {
                    kpiWidgetDto.setKpiEvolution(
                            currentValue.subtract(previousValue).divide(currentValue)
                                    .setScale(0, RoundingMode.HALF_EVEN)
                                    .multiply(new BigDecimal(100.0)).setScale(0, RoundingMode.HALF_EVEN));
                }
                kpiWidgetDto.setName(kpiCrm.getLabel());
                kpiWidgetDto.setUnit(kpiCrm.getUnit());

                kpisWidgetDtos.add(kpiWidgetDto);
            }
        }

        return kpisWidgetDtos;
    }

    public void startComputeBatches() throws OsirisException {
        List<KpiCrm> kpiList = getKpiCrms();

        kpiCrmRepository.truncateTable();
        for (KpiCrm kpi : kpiList)
            batchService.declareNewBatch(Batch.COMPUTE_KPI_CRM, kpi.getId());
    }

    private LocalDate getPreviousDate(LocalDate currentDate, String timescale) {
        switch (timescale) {
            case KpiCrm.WEEKLY_PERIOD:
                return currentDate.minusWeeks(1);
            case KpiCrm.MONTHLY_PERIOD:
                return currentDate.minusMonths(1);
            case KpiCrm.ANNUALLY_PERIOD:
                return currentDate.minusYears(1);
            default:
                return null;
        }
    }

    private String getSqlNameAggregatedFunction(String aggregateType) {
        switch (aggregateType) {
            case KpiCrm.AGGREGATE_TYPE_AVERAGE:
                return "AVG";

            case KpiCrm.AGGREGATE_TYPE_SUM:
                return "SUM";

            default:
                return null;
        }
    }

    private IKpiCrm getKpiThread(KpiCrm kpiCrm) {
        for (IKpiCrm iKpiThread : IKpiThreadList) {
            if (iKpiThread.getCode().equals(kpiCrm.getCode())) {
                return iKpiThread;
            }
        }
        return null;
    }
}
