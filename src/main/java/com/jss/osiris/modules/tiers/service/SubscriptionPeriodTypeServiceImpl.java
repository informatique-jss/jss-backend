package com.jss.osiris.modules.tiers.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.tiers.model.SubscriptionPeriodType;
import com.jss.osiris.modules.tiers.repository.SubscriptionPeriodTypeRepository;

@Service
public class SubscriptionPeriodTypeServiceImpl implements SubscriptionPeriodTypeService {

    @Autowired
    SubscriptionPeriodTypeRepository subscriptionPeriodTypeRepository;

    @Override
    public List<SubscriptionPeriodType> getSubscriptionPeriodTypes() {
        return IterableUtils.toList(subscriptionPeriodTypeRepository.findAll());
    }

    @Override
    public SubscriptionPeriodType getSubscriptionPeriodType(Integer id) {
        Optional<SubscriptionPeriodType> subscriptionPeriodType = subscriptionPeriodTypeRepository.findById(id);
        if (subscriptionPeriodType.isPresent())
            return subscriptionPeriodType.get();
        return null;
    }
	
	 @Override
    public SubscriptionPeriodType addOrUpdateSubscriptionPeriodType(
            SubscriptionPeriodType subscriptionPeriodType) {
        return subscriptionPeriodTypeRepository.save(subscriptionPeriodType);
    }
}
