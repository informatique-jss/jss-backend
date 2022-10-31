package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.repository.DocumentRepository;

@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    ConstantService constantService;

    @Override
    public List<Document> getDocuments() {
        return IterableUtils.toList(documentRepository.findAll());
    }

    @Override
    public Document getDocument(Integer id) {
        Optional<Document> document = documentRepository.findById(id);
        if (document.isPresent())
            return document.get();
        return null;
    }

    @Override
    public Document getBillingDocument(List<Document> documents) throws Exception {
        if (documents != null && documents.size() > 0)
            for (Document document : documents)
                if (document.getDocumentType() != null && document.getDocumentType().getCode() != null
                        && document.getDocumentType().getId().equals(constantService.getDocumentTypeBilling().getId()))
                    return document;
        return null;
    }
}
