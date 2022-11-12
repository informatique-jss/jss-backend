package com.jss.osiris.modules.quotation.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;

public interface CustomerOrderStatusRepository extends CrudRepository<CustomerOrderStatus, Integer> {

    CustomerOrderStatus findByCode(String code);
}