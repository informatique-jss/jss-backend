package com.jss.osiris.modules.miscellaneous.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.miscellaneous.model.BillingType;

public interface BillingTypeRepository extends CrudRepository<BillingType, Integer> {

    List<BillingType> findByIsDebourOrIsFee(boolean isDebour, boolean isFee);
}