package com.jss.osiris.modules.osiris.reporting.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.reporting.model.Indicator;

public interface IndicatorService {
    public List<Indicator> getIndicators();

    public Indicator getIndicator(Integer id);

    public Indicator addOrUpdateIndicator(Indicator indicator);

    public void updateIndicatorsValues() throws OsirisException;

    public void computeIndicator(Integer entityId);
}
