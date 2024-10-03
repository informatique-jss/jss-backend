package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.OrigineFusionScission;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.OrigineFusionScissionRepository;

@Service
public class OrigineFusionScissionServiceImpl implements OrigineFusionScissionService {

    @Autowired
    OrigineFusionScissionRepository OrigineFusionScissionRepository;

    @Override
    public List<OrigineFusionScission> getOrigineFusionScission() {
        return IterableUtils.toList(OrigineFusionScissionRepository.findAll());
    }
}
