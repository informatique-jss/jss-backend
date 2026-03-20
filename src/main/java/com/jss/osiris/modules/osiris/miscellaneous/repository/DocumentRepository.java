package com.jss.osiris.modules.osiris.miscellaneous.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.model.DocumentType;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public interface DocumentRepository extends QueryCacheCrudRepository<Document, Integer> {

    Document findByDocumentTypeAndResponsable(DocumentType documentType, Responsable responsable);

    @Modifying
    @Query(value = "DELETE FROM document WHERE id_announcement IN " +
            "(SELECT id_announcement FROM provision WHERE id = :provisionId)", nativeQuery = true)
    void deleteDocumentsByProvisionId(@Param("provisionId") Integer provisionId);
}