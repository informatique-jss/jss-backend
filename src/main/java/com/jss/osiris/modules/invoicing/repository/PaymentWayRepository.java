package com.jss.osiris.modules.invoicing.repository;

import com.jss.osiris.modules.invoicing.model.PaymentWay;

import org.springframework.data.repository.CrudRepository;

public interface PaymentWayRepository extends CrudRepository<PaymentWay, Integer> {
}