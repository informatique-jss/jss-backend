package com.jss.osiris.modules.tiers.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.tiers.model.BillingClosureType;
import com.jss.osiris.modules.tiers.repository.BillingClosureTypeRepository;

@Service
public class BillingClosureTypeServiceImpl implements BillingClosureTypeService {

    @Autowired
    BillingClosureTypeRepository billingClosureTypeRepository;

    @Override
    public List<BillingClosureType> getBillingClosureTypes() {
        return IterableUtils.toList(billingClosureTypeRepository.findAll());
    }

    @Override
    public BillingClosureType getBillingClosureType(Integer id) {
        Optional<BillingClosureType> billingClosureType = billingClosureTypeRepository.findById(id);
        if (!billingClosureType.isEmpty())
            return billingClosureType.get();
        return null;
    }
}
