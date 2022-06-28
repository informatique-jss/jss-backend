package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.BillingItem;

public interface BillingItemService {
    public List<BillingItem> getBillingItems();

    public BillingItem getBillingItem(Integer id);
	
	 public BillingItem addOrUpdateBillingItem(BillingItem billingItem);
}
