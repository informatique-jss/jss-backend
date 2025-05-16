package com.jss.osiris.modules.osiris.reporting.service;

import java.util.List;

import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.reporting.model.Indicator;
import com.jss.osiris.modules.osiris.reporting.model.IndicatorValue;

public interface IndicatorValueService {
    public IndicatorValue getIndicatorValue(Integer id);

    public IndicatorValue addOrUpdateIndicatorValue(IndicatorValue indicatorValue);

    public void deleteIndicatorValuesForIndicator(Indicator indicator);

    public List<IndicatorValue> getIndicatorValuesForIndicator(Indicator indicator);

    public List<IndicatorValue> getLatestIndicatorValuesForEmployee(Employee employee);
}
