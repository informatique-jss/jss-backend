package com.jss.osiris.modules.osiris.reporting.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.reporting.model.Indicator;
import com.jss.osiris.modules.osiris.reporting.model.IndicatorValue;
import com.jss.osiris.modules.osiris.reporting.repository.IndicatorValueRepository;

@Service
public class IndicatorValueServiceImpl implements IndicatorValueService {

    @Autowired
    IndicatorValueRepository indicatorValueRepository;

    @Override
    public IndicatorValue getIndicatorValue(Integer id) {
        Optional<IndicatorValue> indicatorValue = indicatorValueRepository.findById(id);
        if (indicatorValue.isPresent())
            return indicatorValue.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IndicatorValue addOrUpdateIndicatorValue(
            IndicatorValue indicatorValue) {
        return indicatorValueRepository.save(indicatorValue);
    }

    @Override
    public void deleteIndicatorValuesForIndicator(Indicator indicator) {
        List<IndicatorValue> values = getIndicatorValuesForIndicator(indicator);
        if (values != null)
            for (IndicatorValue value : values)
                indicatorValueRepository.delete(value);
    }

    @Override
    public List<IndicatorValue> getIndicatorValuesForIndicator(Indicator indicator) {
        return indicatorValueRepository.findByIndicatorOrderByDate(indicator);
    }

    @Override
    public List<IndicatorValue> getLatestIndicatorValuesForEmployee(Employee employee) {
        return indicatorValueRepository.findLatestIndicatorValuesForEmployee(employee);
    }
}
