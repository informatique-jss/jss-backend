package com.jss.osiris.modules.osiris.tiers.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.tiers.model.BillingClosureType;
import com.jss.osiris.modules.osiris.tiers.repository.BillingClosureTypeRepository;

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
        if (billingClosureType.isPresent())
            return billingClosureType.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BillingClosureType addOrUpdateBillingClosureType(
            BillingClosureType billingClosureType) {
        return billingClosureTypeRepository.save(billingClosureType);
    }
}
