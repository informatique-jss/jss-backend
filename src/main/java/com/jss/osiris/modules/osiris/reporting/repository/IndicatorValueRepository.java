package com.jss.osiris.modules.osiris.reporting.repository;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.reporting.model.Indicator;
import com.jss.osiris.modules.osiris.reporting.model.IndicatorValue;

public interface IndicatorValueRepository extends QueryCacheCrudRepository<IndicatorValue, Integer> {

    List<IndicatorValue> findByIndicator(Indicator indicator);
}