package com.jss.osiris.modules.miscellaneous.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.miscellaneous.model.PaymentType;

public interface PaymentTypeRepository extends CrudRepository<PaymentType, Integer> {

    PaymentType findByCodeInpi(String codeInpi);
}