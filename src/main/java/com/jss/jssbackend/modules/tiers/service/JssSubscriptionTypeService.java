package com.jss.jssbackend.modules.tiers.service;

import java.util.List;

import com.jss.jssbackend.modules.tiers.model.JssSubscriptionType;

public interface JssSubscriptionTypeService {
    public List<JssSubscriptionType> getJssSubscriptionTypes();

    public JssSubscriptionType getJssSubscriptionType(Integer id);
}
