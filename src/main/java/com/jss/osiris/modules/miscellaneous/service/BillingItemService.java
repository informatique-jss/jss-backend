package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.BillingItem;
import com.jss.osiris.modules.miscellaneous.model.BillingType;

public interface BillingItemService {
    public List<BillingItem> getBillingItems();

    public BillingItem getBillingItem(Integer id);

    public BillingItem addOrUpdateBillingItem(BillingItem billingItem) throws Exception;

    public BillingItem addOrUpdateBillingItem(BillingItem billingItem, boolean mustBeChargeOrProduct) throws Exception;

    public List<BillingItem> getBillingItemByBillingType(BillingType billingType);
}
