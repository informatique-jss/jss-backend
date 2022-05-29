package com.jss.jssbackend.modules.tiers.repository;

import java.util.List;

import com.jss.jssbackend.modules.tiers.model.Responsable;
import com.jss.jssbackend.modules.tiers.model.Tiers;
import com.jss.jssbackend.modules.tiers.model.TiersAttachment;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TiersAttachmentRepository extends CrudRepository<TiersAttachment, Integer> {
    List<TiersAttachment> findByTiers(Tiers tiers);

    List<TiersAttachment> findByResponsable(Responsable responsable);

    @Query(value = "select a from TiersAttachment a where id_tiers =:idTiers")
    List<TiersAttachment> findByTiersId(@Param("idTiers") Integer idTiers);

    @Query(value = "select a from TiersAttachment a where id_responsable =:idResponsable")
    List<TiersAttachment> findByResponsableId(@Param("idResponsable") Integer idResponsable);
}