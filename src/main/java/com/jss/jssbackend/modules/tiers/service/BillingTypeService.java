package com.jss.jssbackend.modules.tiers.service;

import java.util.List;

import com.jss.jssbackend.modules.tiers.model.BillingType;

public interface BillingTypeService {
    public List<BillingType> getBillingTypes();

    public BillingType getBillingType(Integer id);
}
