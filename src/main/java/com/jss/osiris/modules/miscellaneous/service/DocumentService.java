package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.Document;

public interface DocumentService {
    public List<Document> getDocuments();

    public Document getDocument(Integer id);
}
