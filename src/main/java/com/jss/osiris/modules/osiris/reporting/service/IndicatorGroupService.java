package com.jss.osiris.modules.osiris.reporting.service;

import java.util.List;

import com.jss.osiris.modules.osiris.reporting.model.IndicatorGroup;

public interface IndicatorGroupService {
    public List<IndicatorGroup> getIndicatorGroups();

    public IndicatorGroup getIndicatorGroup(Integer id);

    public IndicatorGroup addOrUpdateIndicatorGroup(IndicatorGroup indicatorGroup);
}
