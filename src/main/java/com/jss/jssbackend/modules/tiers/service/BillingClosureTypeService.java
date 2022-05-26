package com.jss.jssbackend.modules.tiers.service;

import java.util.List;

import com.jss.jssbackend.modules.tiers.model.BillingClosureType;

public interface BillingClosureTypeService {
    public List<BillingClosureType> getBillingClosureTypes();

    public BillingClosureType getBillingClosureType(Integer id);
}
