package com.jss.jssbackend.modules.tiers.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.tiers.model.BillingClosureType;
import com.jss.jssbackend.modules.tiers.repository.BillingClosureTypeRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
