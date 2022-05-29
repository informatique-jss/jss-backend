package com.jss.jssbackend.modules.tiers.repository;

import java.util.List;

import com.jss.jssbackend.modules.tiers.model.TiersFollowup;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TiersFollowupRepository extends CrudRepository<TiersFollowup, Integer> {
    @Query(value = "select a from TiersFollowup a where id_tiers =:idTiers")
    List<TiersFollowup> findByTiersId(@Param("idTiers") Integer idTiers);

    @Query(value = "select a from TiersFollowup a where id_responsable =:idResponsable")
    List<TiersFollowup> findByResponsableId(@Param("idResponsable") Integer idResponsable);
}