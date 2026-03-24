package com.jss.osiris.modules.osiris.quotation.repository;

import org.springframework.data.jpa.repository.Modifying;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceFieldType;

public interface AssoServiceFieldTypeRepository extends QueryCacheCrudRepository<AssoServiceFieldType, Integer> {

    @Modifying
    void deleteByServiceId(Integer serviceId);
}
