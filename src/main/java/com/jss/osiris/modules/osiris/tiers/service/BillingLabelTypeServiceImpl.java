package com.jss.osiris.modules.osiris.tiers.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.tiers.model.BillingLabelType;
import com.jss.osiris.modules.osiris.tiers.repository.BillingLabelTypeRepository;

@Service
public class BillingLabelTypeServiceImpl implements BillingLabelTypeService {

    @Autowired
    BillingLabelTypeRepository billingLabelTypeRepository;

    @Override
    public List<BillingLabelType> getBillingLabelTypes() {
        return IterableUtils.toList(billingLabelTypeRepository.findAll());
    }

    @Override
    public BillingLabelType getBillingLabelType(Integer id) {
        Optional<BillingLabelType> billingLabelType = billingLabelTypeRepository.findById(id);
        if (billingLabelType.isPresent())
            return billingLabelType.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BillingLabelType addOrUpdateBillingLabelType(
            BillingLabelType billingLabelType) {
        return billingLabelTypeRepository.save(billingLabelType);
    }
}
