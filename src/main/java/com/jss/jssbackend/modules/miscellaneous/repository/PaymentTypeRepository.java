package com.jss.jssbackend.modules.miscellaneous.repository;

import com.jss.jssbackend.modules.miscellaneous.model.PaymentType;

import org.springframework.data.repository.CrudRepository;

public interface PaymentTypeRepository extends CrudRepository<PaymentType, Integer> {
}