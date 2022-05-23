package com.jss.jssbackend.modules.tiers.service;

import java.util.List;

import com.jss.jssbackend.modules.tiers.model.TiersDocument;

public interface TiersDocumentService {
    public List<TiersDocument> getTiersDocuments();

    public TiersDocument getTiersDocument(Integer id);
}
