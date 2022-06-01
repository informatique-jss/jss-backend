package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.Document;

public interface DocumentService {
    public List<Document> getDocuments();

    public Document getDocument(Integer id);
}
