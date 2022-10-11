package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.miscellaneous.model.DocumentType;
import com.jss.osiris.modules.miscellaneous.repository.DocumentTypeRepository;

@Service
public class DocumentTypeServiceImpl implements DocumentTypeService {

    @Autowired
    DocumentTypeRepository documentTypeRepository;

    @Override
    public List<DocumentType> getDocumentTypes() {
        return IterableUtils.toList(documentTypeRepository.findAll());
    }

    @Override
    public DocumentType getDocumentType(Integer id) {
        Optional<DocumentType> documentType = documentTypeRepository.findById(id);
        if (documentType.isPresent())
            return documentType.get();
        return null;
    }

    @Override
    public DocumentType addOrUpdateDocumentType(
            DocumentType documentType) {
        return documentTypeRepository.save(documentType);
    }
}
