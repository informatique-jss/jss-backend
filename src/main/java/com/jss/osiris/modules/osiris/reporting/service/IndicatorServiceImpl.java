package com.jss.osiris.modules.osiris.reporting.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.datasource.DataSource;
import com.jss.osiris.libs.datasource.DataSourceType;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.reporting.model.Indicator;
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
    IndicatorMappingHelper indicatorMappingHelper;

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
    @DataSource(DataSourceType.SLAVE)
    public void computeIndicator(Integer entityId) {
        Indicator indicator = getIndicator(entityId);
        String sql = indicator.getSqlText();

        Query query = entityManager.createNativeQuery(sql, Map.class);
        List<Map<String, Object>> results = query.getResultList();

        indicatorMappingHelper.mapAndSaveValue(results, indicator);
    }

}
