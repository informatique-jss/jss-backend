package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.RegimeImpositionTVA;
import com.jss.osiris.modules.quotation.repository.guichetUnique.RegimeImpositionTVARepository;

@Service
public class RegimeImpositionTVAServiceImpl implements RegimeImpositionTVAService {

    @Autowired
    RegimeImpositionTVARepository RegimeImpositionTVARepository;

    @Override
    public List<RegimeImpositionTVA> getRegimeImpositionTVA() {
        return IterableUtils.toList(RegimeImpositionTVARepository.findAll());
    }
}
