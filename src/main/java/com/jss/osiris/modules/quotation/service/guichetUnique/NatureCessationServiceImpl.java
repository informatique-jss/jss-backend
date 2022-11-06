package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.NatureCessation;
import com.jss.osiris.modules.quotation.repository.guichetUnique.NatureCessationRepository;

@Service
public class NatureCessationServiceImpl implements NatureCessationService {

    @Autowired
    NatureCessationRepository NatureCessationRepository;

    @Override
    @Cacheable(value = "natureCessationList", key = "#root.methodName")
    public List<NatureCessation> getNatureCessation() {
        return IterableUtils.toList(NatureCessationRepository.findAll());
    }
}
