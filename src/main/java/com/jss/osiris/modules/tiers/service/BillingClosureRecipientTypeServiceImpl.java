package com.jss.osiris.modules.tiers.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.tiers.model.BillingClosureRecipientType;
import com.jss.osiris.modules.tiers.repository.BillingClosureRecipientTypeRepository;

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
