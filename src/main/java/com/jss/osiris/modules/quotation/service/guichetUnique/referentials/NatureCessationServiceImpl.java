package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.NatureCessation;
import com.jss.osiris.modules.quotation.repository.guichetUnique.NatureCessationRepository;

@Service
public class NatureCessationServiceImpl implements NatureCessationService {

    @Autowired
    NatureCessationRepository NatureCessationRepository;

    @Override
    public List<NatureCessation> getNatureCessation() {
        return IterableUtils.toList(NatureCessationRepository.findAll());
    }
}
