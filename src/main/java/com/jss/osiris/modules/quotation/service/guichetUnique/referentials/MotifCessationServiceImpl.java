package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.MotifCessation;
import com.jss.osiris.modules.quotation.repository.guichetUnique.MotifCessationRepository;

@Service
public class MotifCessationServiceImpl implements MotifCessationService {

    @Autowired
    MotifCessationRepository MotifCessationRepository;

    @Override
    public List<MotifCessation> getMotifCessation() {
        return IterableUtils.toList(MotifCessationRepository.findAll());
    }
}
