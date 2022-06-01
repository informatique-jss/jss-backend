package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.DocumentType;

public interface DocumentTypeService {
    public List<DocumentType> getDocumentTypes();

    public DocumentType getDocumentType(Integer id);
}
