package com.jss.jssbackend.modules.miscellaneous.repository;

import com.jss.jssbackend.modules.miscellaneous.model.BillingItem;

import org.springframework.data.repository.CrudRepository;

public interface BillingItemRepository extends CrudRepository<BillingItem, Integer> {
}