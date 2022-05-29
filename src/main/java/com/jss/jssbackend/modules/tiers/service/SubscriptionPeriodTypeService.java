package com.jss.jssbackend.modules.tiers.service;

import java.util.List;

import com.jss.jssbackend.modules.tiers.model.SubscriptionPeriodType;

public interface SubscriptionPeriodTypeService {
    public List<SubscriptionPeriodType> getSubscriptionPeriodTypes();

    public SubscriptionPeriodType getSubscriptionPeriodType(Integer id);
}
