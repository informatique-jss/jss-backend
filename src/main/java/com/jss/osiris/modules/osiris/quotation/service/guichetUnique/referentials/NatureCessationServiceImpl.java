package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.NatureCessation;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.NatureCessationRepository;

@Service
public class NatureCessationServiceImpl implements NatureCessationService {

    @Autowired
    NatureCessationRepository NatureCessationRepository;

    @Override
    public List<NatureCessation> getNatureCessation() {
        return IterableUtils.toList(NatureCessationRepository.findAll());
    }
}
