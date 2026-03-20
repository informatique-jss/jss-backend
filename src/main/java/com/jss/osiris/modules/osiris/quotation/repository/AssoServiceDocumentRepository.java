package com.jss.osiris.modules.osiris.quotation.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceDocument;

public interface AssoServiceDocumentRepository extends QueryCacheCrudRepository<AssoServiceDocument, Integer> {

    @Modifying
    @Query("DELETE FROM AssoServiceDocument a WHERE a.service.id = :serviceId")
    void deleteByServiceId(@Param("serviceId") Integer serviceId);

}