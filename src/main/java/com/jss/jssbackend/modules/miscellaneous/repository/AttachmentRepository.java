package com.jss.jssbackend.modules.miscellaneous.repository;

import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.Attachment;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface AttachmentRepository extends CrudRepository<Attachment, Integer> {
    @Query(value = "select a from Attachment a where id_tiers =:idTiers")
    List<Attachment> findByTiersId(@Param("idTiers") Integer idTiers);

    @Query(value = "select a from Attachment a where id_responsable =:idResponsable")
    List<Attachment> findByResponsableId(@Param("idResponsable") Integer idResponsable);

    @Query(value = "select a from Attachment a where id_quotation =:idQuotation")
    List<Attachment> findByQuotationId(@Param("idQuotation") Integer idQuotation);

    @Query(value = "select a from Attachment a where id_domiciliation =:idDomiciliation")
    List<Attachment> findByDomiciliationId(@Param("idDomiciliation") Integer idDomiciliation);
}