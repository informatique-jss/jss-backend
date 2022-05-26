package com.jss.jssbackend.modules.tiers.service;

import java.util.List;

import com.jss.jssbackend.modules.tiers.model.BillingClosureRecipientType;

public interface BillingClosureRecipientTypeService {
    public List<BillingClosureRecipientType> getBillingClosureRecipientTypes();

    public BillingClosureRecipientType getBillingClosureRecipientType(Integer id);
}
