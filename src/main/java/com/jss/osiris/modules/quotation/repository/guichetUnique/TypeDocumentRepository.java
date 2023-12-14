package com.jss.osiris.modules.quotation.repository.guichetUnique;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeDocument;

public interface TypeDocumentRepository extends QueryCacheCrudRepository<TypeDocument, String> {

    TypeDocument findByCode(String code);
}
