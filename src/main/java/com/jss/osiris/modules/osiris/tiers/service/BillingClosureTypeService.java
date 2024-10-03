package com.jss.osiris.modules.osiris.tiers.service;

import java.util.List;

import com.jss.osiris.modules.osiris.tiers.model.BillingClosureType;

public interface BillingClosureTypeService {
    public List<BillingClosureType> getBillingClosureTypes();

    public BillingClosureType getBillingClosureType(Integer id);

    public BillingClosureType addOrUpdateBillingClosureType(BillingClosureType billingClosureType);
}
