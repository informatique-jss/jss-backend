package com.jss.osiris.modules.miscellaneous.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.miscellaneous.model.SalesReclamation;

public interface SalesReclamationRepository extends QueryCacheCrudRepository<SalesReclamation, Integer> {

    @Query("SELECT s FROM SalesReclamation s WHERE s.idTiers = :idTiers")
    List<SalesReclamation> findByTiersId(@Param("idTiers") Integer idTiers);

}