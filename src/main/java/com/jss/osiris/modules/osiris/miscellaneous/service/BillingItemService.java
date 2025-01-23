package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.BillingItem;
import com.jss.osiris.modules.osiris.miscellaneous.model.BillingType;

public interface BillingItemService {
    public List<BillingItem> getBillingItems();

    public BillingItem getBillingItem(Integer id);

    public BillingItem addOrUpdateBillingItem(BillingItem billingItem);

    public List<BillingItem> getBillingItemByBillingType(BillingType billingType);
}
