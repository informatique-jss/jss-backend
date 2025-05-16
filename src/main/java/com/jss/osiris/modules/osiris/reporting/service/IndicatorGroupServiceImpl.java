package com.jss.osiris.modules.osiris.reporting.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.reporting.model.IndicatorGroup;
import com.jss.osiris.modules.osiris.reporting.repository.IndicatorGroupRepository;

@Service
public class IndicatorGroupServiceImpl implements IndicatorGroupService {

    @Autowired
    IndicatorGroupRepository indicatorGroupRepository;

    @Override
    public List<IndicatorGroup> getIndicatorGroups() {
        return IterableUtils.toList(indicatorGroupRepository.findAll());
    }

    @Override
    public IndicatorGroup getIndicatorGroup(Integer id) {
        Optional<IndicatorGroup> indicatorGroup = indicatorGroupRepository.findById(id);
        if (indicatorGroup.isPresent())
            return indicatorGroup.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IndicatorGroup addOrUpdateIndicatorGroup(
            IndicatorGroup indicatorGroup) {
        return indicatorGroupRepository.save(indicatorGroup);
    }
}
