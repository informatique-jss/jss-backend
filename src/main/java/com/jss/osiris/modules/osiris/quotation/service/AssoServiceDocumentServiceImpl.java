package com.jss.osiris.modules.osiris.quotation.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.quotation.model.AssoServiceDocument;
import com.jss.osiris.modules.osiris.quotation.repository.AssoServiceTypeDocumentRepository;

@Service
public class AssoServiceDocumentServiceImpl implements AssoServiceDocumentService {

    @Autowired
    AssoServiceTypeDocumentRepository assoServiceDocumentRepository;

    @Override
    public AssoServiceDocument getAssoServiceDocument(Integer id) {
        Optional<AssoServiceDocument> assoServiceDocument = assoServiceDocumentRepository.findById(id);
        if (assoServiceDocument.isPresent())
            return assoServiceDocument.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AssoServiceDocument addOrUpdateAssoServiceDocument(
            AssoServiceDocument assoServiceDocument) {
        return assoServiceDocumentRepository.save(assoServiceDocument);
    }
}
