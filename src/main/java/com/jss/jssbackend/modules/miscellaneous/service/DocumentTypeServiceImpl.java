package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.miscellaneous.model.DocumentType;
import com.jss.jssbackend.modules.miscellaneous.repository.DocumentTypeRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if (!documentType.isEmpty())
            return documentType.get();
        return null;
    }
}
