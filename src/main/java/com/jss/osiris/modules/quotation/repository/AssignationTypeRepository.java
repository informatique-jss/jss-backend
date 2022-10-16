package com.jss.osiris.modules.quotation.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.quotation.model.AssignationType;

public interface AssignationTypeRepository extends CrudRepository<AssignationType, Integer> {

    Object findByCode(String code);
}