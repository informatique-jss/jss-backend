package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.Document;

public interface DocumentService {
    public List<Document> getDocuments();

    public Document getDocument(Integer id);

    /**
     * Return the first billing document found in a document list provided
     * 
     * @param documents Document list to search for
     * @return The billing document if found, null otherwise
     */
    public Document getBillingDocument(List<Document> documents) throws Exception;

    public Document getQuotationDocument(List<Document> documents) throws Exception;

    public Document cloneDocument(Document document);
}
