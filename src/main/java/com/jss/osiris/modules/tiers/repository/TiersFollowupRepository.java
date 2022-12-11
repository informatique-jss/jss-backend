package com.jss.osiris.modules.tiers.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.tiers.model.TiersFollowup;

public interface TiersFollowupRepository extends CrudRepository<TiersFollowup, Integer> {
    @Query(value = "select a from TiersFollowup a where id_tiers =:idTiers")
    List<TiersFollowup> findByTiersId(@Param("idTiers") Integer idTiers);

    @Query(value = "select a from TiersFollowup a where id_responsable =:idResponsable")
    List<TiersFollowup> findByResponsableId(@Param("idResponsable") Integer idResponsable);

    @Query(value = "select a from TiersFollowup a where id_confrere =:idConfrere")
    List<TiersFollowup> findByConfrereId(@Param("idConfrere") Integer idConfrere);
}