package com.jss.jssbackend.modules.tiers.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.tiers.model.BillingClosureRecipientType;
import com.jss.jssbackend.modules.tiers.repository.BillingClosureRecipientTypeRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BillingClosureRecipientTypeServiceImpl implements BillingClosureRecipientTypeService {

    @Autowired
    BillingClosureRecipientTypeRepository billingClosurRecipientTypeRepository;

    @Override
    public List<BillingClosureRecipientType> getBillingClosureRecipientTypes() {
        return IterableUtils.toList(billingClosurRecipientTypeRepository.findAll());
    }

    @Override
    public BillingClosureRecipientType getBillingClosureRecipientType(Integer id) {
        Optional<BillingClosureRecipientType> billingClosurRecipientType = billingClosurRecipientTypeRepository
                .findById(id);
        if (!billingClosurRecipientType.isEmpty())
            return billingClosurRecipientType.get();
        return null;
    }
}
