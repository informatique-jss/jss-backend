package com.jss.osiris.modules.quotation.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.quotation.model.FormaliteStatus;

public interface FormaliteStatusRepository extends CrudRepository<FormaliteStatus, Integer> {

    FormaliteStatus findByCode(String code);
}