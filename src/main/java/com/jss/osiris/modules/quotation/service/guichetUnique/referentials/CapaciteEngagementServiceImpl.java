package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.CapaciteEngagement;
import com.jss.osiris.modules.quotation.repository.guichetUnique.CapaciteEngagementRepository;

@Service
public class CapaciteEngagementServiceImpl implements CapaciteEngagementService {

    @Autowired
    CapaciteEngagementRepository CapaciteEngagementRepository;

    @Override
    @Cacheable(value = "capaciteEngagementList", key = "#root.methodName")
    public List<CapaciteEngagement> getCapaciteEngagement() {
        return IterableUtils.toList(CapaciteEngagementRepository.findAll());
    }
}
