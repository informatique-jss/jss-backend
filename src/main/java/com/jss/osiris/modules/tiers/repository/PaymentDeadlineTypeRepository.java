package com.jss.osiris.modules.tiers.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.tiers.model.PaymentDeadlineType;

public interface PaymentDeadlineTypeRepository extends CrudRepository<PaymentDeadlineType, Integer> {
}