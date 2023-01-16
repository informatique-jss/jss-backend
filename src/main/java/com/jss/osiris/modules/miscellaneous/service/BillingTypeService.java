package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.miscellaneous.model.BillingType;

public interface BillingTypeService {
    public List<BillingType> getBillingTypes();

    public BillingType getBillingType(Integer id);

    public BillingType addOrUpdateBillingType(BillingType billingType) throws OsirisException;

    public List<BillingType> getBillingTypesDebour();
}
