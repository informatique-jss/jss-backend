package com.jss.osiris.modules.osiris.tiers.service;

import java.util.List;

import com.jss.osiris.modules.osiris.tiers.model.BillingClosureRecipientType;

public interface BillingClosureRecipientTypeService {
    public List<BillingClosureRecipientType> getBillingClosureRecipientTypes();

    public BillingClosureRecipientType getBillingClosureRecipientType(Integer id);

    public BillingClosureRecipientType addOrUpdateBillingClosureRecipientType(
            BillingClosureRecipientType billingClosureRecipientType);
}
