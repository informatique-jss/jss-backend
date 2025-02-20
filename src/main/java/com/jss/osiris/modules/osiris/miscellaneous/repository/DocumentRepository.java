package com.jss.osiris.modules.osiris.miscellaneous.repository;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.model.DocumentType;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public interface DocumentRepository extends QueryCacheCrudRepository<Document, Integer> {

    Document findByDocumentTypeAndResponsable(DocumentType documentType, Responsable responsable);
}