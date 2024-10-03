package com.jss.osiris.modules.osiris.quotation.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.quotation.model.SimpleProvision;
import com.jss.osiris.modules.osiris.quotation.repository.SimpleProvisionRepository;

@Service
public class SimpleProvisionServiceImpl implements SimpleProvisionService {

    @Autowired
    SimpleProvisionRepository simpleProvisionRepository;

    @Override
    public SimpleProvision getSimpleProvision(Integer id) {
        Optional<SimpleProvision> simpleProvision = simpleProvisionRepository.findById(id);
        if (simpleProvision.isPresent())
            return simpleProvision.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SimpleProvision addOrUpdateSimpleProvision(
            SimpleProvision simpleProvision) {
        return simpleProvisionRepository.save(simpleProvision);
    }
}
