package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.DocumentType;

public interface DocumentTypeService {
    public List<DocumentType> getDocumentTypes();

    public DocumentType getDocumentType(Integer id);

    public DocumentType addOrUpdateDocumentType(DocumentType documentType);
}
