package com.jss.jssbackend.modules.miscellaneous.repository;

import com.jss.jssbackend.modules.miscellaneous.model.BillingType;

import org.springframework.data.repository.CrudRepository;

public interface BillingTypeRepository extends CrudRepository<BillingType, Integer> {
}