package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.BillingCenter;

public interface BillingCenterService {
    public List<BillingCenter> getBillingCenters();

    public BillingCenter getBillingCenter(Integer id);

    public BillingCenter addOrUpdateBillingCenter(BillingCenter billingCenter) throws Exception;
}
