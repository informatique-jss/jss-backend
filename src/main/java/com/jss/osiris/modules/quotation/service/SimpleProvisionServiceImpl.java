package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.quotation.model.SimpleProvision;
import com.jss.osiris.modules.quotation.repository.SimpleProvisionRepository;

@Service
public class SimpleProvisionServiceImpl implements SimpleProvisionService {

    @Autowired
    SimpleProvisionRepository simpleProvisionRepository;

    @Override
    public List<SimpleProvision> getSimpleProvisions() {
        return IterableUtils.toList(simpleProvisionRepository.findAll());
    }

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
