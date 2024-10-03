package com.jss.osiris.modules.osiris.tiers.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.tiers.model.BillingClosureRecipientType;
import com.jss.osiris.modules.osiris.tiers.repository.BillingClosureRecipientTypeRepository;

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
        if (billingClosurRecipientType.isPresent())
            return billingClosurRecipientType.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BillingClosureRecipientType addOrUpdateBillingClosureRecipientType(
            BillingClosureRecipientType billingClosureRecipientType) {
        return billingClosurRecipientTypeRepository.save(billingClosureRecipientType);
    }
}
