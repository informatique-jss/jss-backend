package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.miscellaneous.model.Document;
import com.jss.jssbackend.modules.miscellaneous.repository.DocumentRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    DocumentRepository documentRepository;

    @Override
    public List<Document> getDocuments() {
        return IterableUtils.toList(documentRepository.findAll());
    }

    @Override
    public Document getDocument(Integer id) {
        Optional<Document> document = documentRepository.findById(id);
        if (!document.isEmpty())
            return document.get();
        return null;
    }
}
