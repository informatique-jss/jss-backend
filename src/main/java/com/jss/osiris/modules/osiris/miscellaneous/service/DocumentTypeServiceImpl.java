package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.miscellaneous.model.DocumentType;
import com.jss.osiris.modules.osiris.miscellaneous.repository.DocumentTypeRepository;

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
    @Transactional(rollbackFor = Exception.class)
    public DocumentType addOrUpdateDocumentType(
            DocumentType documentType) {
        return documentTypeRepository.save(documentType);
    }
}
