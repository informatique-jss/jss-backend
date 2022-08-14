package com.jss.osiris.modules.miscellaneous.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.miscellaneous.model.BillingItem;
import com.jss.osiris.modules.miscellaneous.model.BillingType;

public interface BillingItemRepository extends CrudRepository<BillingItem, Integer> {

    List<BillingItem> findByBillingType(BillingType billingType);
}