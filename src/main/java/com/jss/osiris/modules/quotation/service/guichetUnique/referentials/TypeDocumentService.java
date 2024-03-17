package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeDocument;

public interface TypeDocumentService {
    public List<TypeDocument> getTypeDocument();

    public TypeDocument getTypeDocumentByCode(String code);

    public TypeDocument addOrUpdateTypeDocument(TypeDocument typeDocument);
}
