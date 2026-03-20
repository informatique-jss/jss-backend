package com.jss.osiris.modules.osiris.quotation.repository.infoGreffe;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.KbisRequest;

public interface KbisRequestRepository extends QueryCacheCrudRepository<KbisRequest, Integer> {

    List<KbisRequest> findBySiret(String siret);

    @Modifying
    @Query(value = "update kbis_request set id_provision = null, id_attachment = null where id_provision = :idProvision", nativeQuery = true)
    void nullifyProvisionId(@Param("idProvision") Integer idProvision);

}
