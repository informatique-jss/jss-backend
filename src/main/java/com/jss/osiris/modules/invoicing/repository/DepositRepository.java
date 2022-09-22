package com.jss.osiris.modules.invoicing.repository;

import com.jss.osiris.modules.invoicing.model.Deposit;

import org.springframework.data.repository.CrudRepository;

public interface DepositRepository extends CrudRepository<Deposit, Integer> {
}