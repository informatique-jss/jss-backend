package com.jss.osiris.modules.osiris.quotation.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.Service;

public interface ServiceRepository extends QueryCacheCrudRepository<Service, Integer> {

    @Modifying
    @Query(value = "DELETE FROM asso_service_service_type WHERE id_service = :serviceId", nativeQuery = true)
    void deleteAssociationsByServiceId(@Param("serviceId") Integer serviceId);
}