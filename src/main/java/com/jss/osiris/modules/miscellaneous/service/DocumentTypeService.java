package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.DocumentType;

public interface DocumentTypeService {
    public List<DocumentType> getDocumentTypes();

    public DocumentType getDocumentType(Integer id);
}
