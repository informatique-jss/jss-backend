package com.jss.osiris.modules.miscellaneous.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.miscellaneous.model.SalesComplain;

public interface SalesComplainRepository extends QueryCacheCrudRepository<SalesComplain, Integer> {

    @Query("SELECT s FROM SalesComplain s WHERE s.idTiers = :idTiers")
    List<SalesComplain> findByTiersId(@Param("idTiers") Integer idTiers);

}