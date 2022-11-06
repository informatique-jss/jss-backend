package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.RegimeImpositionTVA;
import com.jss.osiris.modules.quotation.repository.guichetUnique.RegimeImpositionTVARepository;

@Service
public class RegimeImpositionTVAServiceImpl implements RegimeImpositionTVAService {

    @Autowired
    RegimeImpositionTVARepository RegimeImpositionTVARepository;

    @Override
    @Cacheable(value = "regimeImpositionTVAList", key = "#root.methodName")
    public List<RegimeImpositionTVA> getRegimeImpositionTVA() {
        return IterableUtils.toList(RegimeImpositionTVARepository.findAll());
    }
}
