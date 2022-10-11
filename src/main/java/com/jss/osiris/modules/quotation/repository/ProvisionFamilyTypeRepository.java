package com.jss.osiris.modules.quotation.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.quotation.model.ProvisionFamilyType;

public interface ProvisionFamilyTypeRepository extends CrudRepository<ProvisionFamilyType, Integer> {

    List<ProvisionFamilyType> findAllByOrderByCode();
}