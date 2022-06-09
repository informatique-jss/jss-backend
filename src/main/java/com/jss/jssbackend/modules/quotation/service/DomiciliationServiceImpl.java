package com.jss.jssbackend.modules.quotation.service;

import java.util.Optional;

import com.jss.jssbackend.modules.quotation.model.Domiciliation;
import com.jss.jssbackend.modules.quotation.repository.DomiciliationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
