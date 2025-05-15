package com.jss.osiris.modules.osiris.reporting.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.reporting.model.Indicator;
import com.jss.osiris.modules.osiris.reporting.model.IndicatorValue;
import com.jss.osiris.modules.osiris.reporting.model.Kpi;
import com.jss.osiris.modules.osiris.reporting.repository.IndicatorRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

@Service
public class IndicatorServiceImpl implements IndicatorService {

    @Autowired
    IndicatorRepository indicatorRepository;

    @Autowired
    BatchService batchService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    IndicatorValueService indicatorValueService;

    @Autowired
    EntityManager entityManager;

    @Autowired
    KpiService kpiService;

    @Override
    public List<Indicator> getIndicators() {
        return IterableUtils.toList(indicatorRepository.findAll());
    }

    @Override
    public Indicator getIndicator(Integer id) {
        Optional<Indicator> indicator = indicatorRepository.findById(id);
        if (indicator.isPresent())
            return indicator.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Indicator addOrUpdateIndicator(
            Indicator indicator) {
        if (indicator.getKpis() != null)
            for (Kpi kpi : indicator.getKpis())
                kpi.setIndicator(indicator);
        return indicatorRepository.save(indicator);
    }

    @Override
    public void updateIndicatorsValues() throws OsirisException {
        List<Indicator> indicators = getIndicators();
        if (indicators != null)
            for (Indicator indicator : indicators)
                batchService.declareNewBatch(Batch.COMPUTE_INDICATOR, indicator.getId());

    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void computeIndicator(Integer entityId) {
        Indicator indicator = getIndicator(entityId);
        String sql = indicator.getSqlText();

        Query query = entityManager.createNativeQuery(sql, Map.class);
        List<Map<String, Object>> results = query.getResultList();

        mapAndSaveValue(results, indicator);
    }

    private void mapAndSaveValue(List<Map<String, Object>> results, Indicator indicator) {
        if (results != null) {
            indicatorValueService.deleteIndicatorValuesForIndicator(indicator);
            for (Map<String, Object> result : results) {
                Integer employeeId = (Integer) result.get("id_employee");
                LocalDate date = ((Timestamp) result.get("date")).toLocalDateTime().toLocalDate();
                BigDecimal value = new BigDecimal((Long) result.get("value"));

                Employee employee = null;
                if (employeeId != null)
                    employee = employeeService.getEmployee(employeeId);

                IndicatorValue indicatorValue = new IndicatorValue();
                indicatorValue.setIndicator(indicator);
                indicatorValue.setEmployee(employee);
                indicatorValue.setDate(date);
                indicatorValue.setValue(value);

                if (employee != null) {
                    Kpi kpi = kpiService.getKpiForEmployeeAndDate(indicator, employee, date);
                    if (kpi != null) {
                        if (kpi.getMinValue() == null || value.compareTo(kpi.getMinValue()) > 0) {
                            indicatorValue.setIsMinValueReached(true);
                        } else {
                            indicatorValue.setIsMinValueReached(false);
                        }
                        if (kpi.getMediumValue() == null || value.compareTo(kpi.getMediumValue()) > 0) {
                            indicatorValue.setIsMediumValueReached(true);
                        } else {
                            indicatorValue.setIsMediumValueReached(false);
                        }
                        if (value.compareTo(kpi.getMaxValue()) > 0) {
                            indicatorValue.setIsMaxValueReached(true);
                        } else {
                            indicatorValue.setIsMaxValueReached(false);
                        }

                        BigDecimal baseValue = kpi.getBaseValue() != null ? kpi.getBaseValue() : new BigDecimal(0);
                        indicatorValue.setSuccededValue(indicatorValue.getValue().subtract(baseValue));

                        indicatorValue
                                .setSuccededPercentage(indicatorValue.getSuccededValue().subtract(kpi.getMaxValue())
                                        .divide(kpi.getMaxValue(), 10, RoundingMode.HALF_EVEN)
                                        .setScale(2, RoundingMode.HALF_EVEN)
                                        .floatValue());
                    }
                }

                indicatorValueService.addOrUpdateIndicatorValue(indicatorValue);
            }

            indicator.setLastUpdate(LocalDateTime.now());
            addOrUpdateIndicator(indicator);
        }
    }
}