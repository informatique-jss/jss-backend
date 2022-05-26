package com.jss.jssbackend.modules.tiers.service;

import java.util.List;

import com.jss.jssbackend.modules.tiers.model.BillingLabelType;

public interface BillingLabelTypeService {
    public List<BillingLabelType> getBillingLabelTypes();

    public BillingLabelType getBillingLabelType(Integer id);
}
