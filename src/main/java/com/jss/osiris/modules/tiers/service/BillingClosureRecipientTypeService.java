package com.jss.osiris.modules.tiers.service;

import java.util.List;

import com.jss.osiris.modules.tiers.model.BillingClosureRecipientType;

public interface BillingClosureRecipientTypeService {
    public List<BillingClosureRecipientType> getBillingClosureRecipientTypes();

    public BillingClosureRecipientType getBillingClosureRecipientType(Integer id);

    public BillingClosureRecipientType addOrUpdateBillingClosureRecipientType(
            BillingClosureRecipientType billingClosureRecipientType);
}
