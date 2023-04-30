package com.jss.osiris.modules.tiers.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.tiers.model.TiersFollowup;

public interface TiersFollowupRepository extends QueryCacheCrudRepository<TiersFollowup, Integer> {
    @Query(value = "select a from TiersFollowup a where id_tiers =:idTiers")
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    List<TiersFollowup> findByTiersId(@Param("idTiers") Integer idTiers);

    @Query(value = "select a from TiersFollowup a where id_responsable =:idResponsable")
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    List<TiersFollowup> findByResponsableId(@Param("idResponsable") Integer idResponsable);

    @Query(value = "select a from TiersFollowup a where id_confrere =:idConfrere")
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    List<TiersFollowup> findByConfrereId(@Param("idConfrere") Integer idConfrere);

    @Query(value = "select a from TiersFollowup a where id_invoice =:idInvoice")
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    List<TiersFollowup> findByInvoiceId(@Param("idInvoice") Integer idInvoice);
}