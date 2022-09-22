package com.jss.osiris.modules.invoicing.repository;

import com.jss.osiris.modules.invoicing.model.Refund;

import org.springframework.data.repository.CrudRepository;

public interface RefundRepository extends CrudRepository<Refund, Integer> {
}