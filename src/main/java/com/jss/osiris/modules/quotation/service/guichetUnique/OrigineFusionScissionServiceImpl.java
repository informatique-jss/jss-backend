package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.OrigineFusionScission;
import com.jss.osiris.modules.quotation.repository.guichetUnique.OrigineFusionScissionRepository;

@Service
public class OrigineFusionScissionServiceImpl implements OrigineFusionScissionService {

    @Autowired
    OrigineFusionScissionRepository OrigineFusionScissionRepository;

    @Override
    @Cacheable(value = "origineFusionScissionList", key = "#root.methodName")
    public List<OrigineFusionScission> getOrigineFusionScission() {
        return IterableUtils.toList(OrigineFusionScissionRepository.findAll());
    }
}
