package com.jss.osiris.modules.osiris.quotation.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceFieldType;

public interface AssoServiceFieldTypeRepository extends QueryCacheCrudRepository<AssoServiceFieldType, Integer> {

    @Modifying
    @Query("DELETE FROM AssoServiceFieldType a WHERE a.service.id = :serviceId")
    void deleteAssoServiceFieldTypeByServiceId(@Param("serviceId") Integer serviceId);

}
