package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.BillingItem;

public interface BillingItemService {
    public List<BillingItem> getBillingItems();

    public BillingItem getBillingItem(Integer id);
}
