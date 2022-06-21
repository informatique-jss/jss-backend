package com.jss.osiris.modules.quotation.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.Domiciliation;
import com.jss.osiris.modules.quotation.repository.DomiciliationRepository;

@Service
public class DomiciliationServiceImpl implements DomiciliationService {

    @Autowired
    DomiciliationRepository domiciliationRepository;

    @Override
    public Domiciliation getDomiciliation(Integer id) {
        Optional<Domiciliation> domiciliation = domiciliationRepository.findById(id);
        if (!domiciliation.isEmpty())
            return domiciliation.get();
        return null;
    }
}
