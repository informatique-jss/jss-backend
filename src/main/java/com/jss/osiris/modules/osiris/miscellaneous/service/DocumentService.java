package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.model.DocumentType;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public interface DocumentService {

    public Document addOrUpdateDocument(Document document);

    public List<Document> getDocuments();

    public Document getDocument(Integer id);

    /**
     * Return the first billing document found in a document list provided
     * 
     * @param documents Document list to search for
     * @return The billing document if found, null otherwise
     * @throws OsirisException
     */
    public Document getBillingDocument(List<Document> documents) throws OsirisException;

    public Document getDocumentByDocumentType(List<Document> documents, DocumentType documentType)
            throws OsirisException;

    public Document getBillingClosureDocument(List<Document> documents) throws OsirisException;

    public Document getRefundDocument(List<Document> documents) throws OsirisException;

    public Document cloneDocument(Document document, Document documentToSet);

    public Document findDocumentByDocumentTypeAndResponsable(DocumentType documentType, Responsable responsable);
}
