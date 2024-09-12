package com.jss.osiris.modules.tiers.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.tiers.model.TiersFollowup;

import jakarta.persistence.QueryHint;

public interface TiersFollowupRepository extends QueryCacheCrudRepository<TiersFollowup, Integer> {
    @Query(value = "select a.* from tiers_followup a where id_tiers =:idTiers", nativeQuery = true)
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    List<TiersFollowup> findByTiersId(@Param("idTiers") Integer idTiers);

    @Query(value = "select a.* from tiers_followup a where id_responsable =:idResponsable", nativeQuery = true)
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    List<TiersFollowup> findByResponsableId(@Param("idResponsable") Integer idResponsable);

    @Query(value = "select a.* from tiers_followup a where id_confrere =:idConfrere", nativeQuery = true)
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    List<TiersFollowup> findByConfrereId(@Param("idConfrere") Integer idConfrere);

    @Query(value = "select a.* from tiers_followup a where id_invoice =:idInvoice", nativeQuery = true)
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    List<TiersFollowup> findByInvoiceId(@Param("idInvoice") Integer idInvoice);

    @Query(value = "select a.* from tiers_followup a where id_affaire =:idAffaire", nativeQuery = true)
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    List<TiersFollowup> findByAffaireId(@Param("idAffaire") Integer idAffaire);
}