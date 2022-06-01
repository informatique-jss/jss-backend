package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.BillingType;

public interface BillingTypeService {
    public List<BillingType> getBillingTypes();

    public BillingType getBillingType(Integer id);
}
