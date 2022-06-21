package com.jss.osiris.modules.miscellaneous.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.miscellaneous.model.BillingItem;

public interface BillingItemRepository extends CrudRepository<BillingItem, Integer> {
}