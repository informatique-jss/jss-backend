package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.MotifFinEirl;
import com.jss.osiris.modules.quotation.repository.guichetUnique.MotifFinEirlRepository;

@Service
public class MotifFinEirlServiceImpl implements MotifFinEirlService {

    @Autowired
    MotifFinEirlRepository MotifFinEirlRepository;

    @Override
    @Cacheable(value = "motifFinEirlList", key = "#root.methodName")
    public List<MotifFinEirl> getMotifFinEirl() {
        return IterableUtils.toList(MotifFinEirlRepository.findAll());
    }
}
