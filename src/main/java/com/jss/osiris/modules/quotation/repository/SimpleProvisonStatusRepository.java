package com.jss.osiris.modules.quotation.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.quotation.model.SimpleProvisionStatus;

public interface SimpleProvisonStatusRepository extends CrudRepository<SimpleProvisionStatus, Integer> {

    SimpleProvisionStatus findByCode(String code);
}