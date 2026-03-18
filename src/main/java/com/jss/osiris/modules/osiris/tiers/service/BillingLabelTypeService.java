package com.jss.osiris.modules.osiris.tiers.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.tiers.model.BillingLabelType;

public interface BillingLabelTypeService {
    public List<BillingLabelType> getBillingLabelTypes();

    public List<BillingLabelType> getBillingLabelTypesExcludingOtherType() throws OsirisException;

    public BillingLabelType getBillingLabelType(Integer id);

    public BillingLabelType addOrUpdateBillingLabelType(BillingLabelType billingLabelType);
}
