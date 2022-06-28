package com.jss.osiris.modules.tiers.service;

import java.util.List;

import com.jss.osiris.modules.tiers.model.SubscriptionPeriodType;

public interface SubscriptionPeriodTypeService {
    public List<SubscriptionPeriodType> getSubscriptionPeriodTypes();

    public SubscriptionPeriodType getSubscriptionPeriodType(Integer id);
	
	 public SubscriptionPeriodType addOrUpdateSubscriptionPeriodType(SubscriptionPeriodType subscriptionPeriodType);
}
