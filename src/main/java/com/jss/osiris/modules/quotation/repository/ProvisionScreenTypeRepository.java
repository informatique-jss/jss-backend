package com.jss.osiris.modules.quotation.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.quotation.model.ProvisionScreenType;

public interface ProvisionScreenTypeRepository extends CrudRepository<ProvisionScreenType, Integer> {

    ProvisionScreenType findByCode(String code);
}