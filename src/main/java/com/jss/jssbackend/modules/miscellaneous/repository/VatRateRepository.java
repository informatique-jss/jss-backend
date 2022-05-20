package com.jss.jssbackend.modules.miscellaneous.repository;

import com.jss.jssbackend.modules.miscellaneous.model.VatRate;

import org.springframework.data.repository.CrudRepository;

public interface VatRateRepository extends CrudRepository<VatRate, Integer> {
}