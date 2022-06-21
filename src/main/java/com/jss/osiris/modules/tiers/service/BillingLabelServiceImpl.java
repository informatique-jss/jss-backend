package com.jss.osiris.modules.tiers.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.tiers.model.BillingLabelType;
import com.jss.osiris.modules.tiers.repository.BillingLabelTypeRepository;

@Service
public class BillingLabelServiceImpl implements BillingLabelTypeService {

    @Autowired
    BillingLabelTypeRepository billingLabelRepository;

    @Override
    public List<BillingLabelType> getBillingLabelTypes() {
        return IterableUtils.toList(billingLabelRepository.findAll());
    }

    @Override
    public BillingLabelType getBillingLabelType(Integer id) {
        Optional<BillingLabelType> billingLabel = billingLabelRepository.findById(id);
        if (!billingLabel.isEmpty())
            return billingLabel.get();
        return null;
    }
}
