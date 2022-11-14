package com.jss.osiris.modules.quotation.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.quotation.model.BodaccStatus;

public interface BodaccStatusRepository extends CrudRepository<BodaccStatus, Integer> {

    BodaccStatus findByCode(String code);
}