package com.jss.jssbackend.modules.tiers.repository;

import com.jss.jssbackend.modules.tiers.model.BillingItem;

import org.springframework.data.repository.CrudRepository;

public interface BillingItemRepository extends CrudRepository<BillingItem, Integer> {
}