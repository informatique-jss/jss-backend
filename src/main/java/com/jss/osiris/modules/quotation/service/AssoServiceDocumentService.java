package com.jss.osiris.modules.quotation.service;

import com.jss.osiris.modules.quotation.model.AssoServiceDocument;

public interface AssoServiceDocumentService {
    public AssoServiceDocument getAssoServiceDocument(Integer id);

    public AssoServiceDocument addOrUpdateAssoServiceDocument(AssoServiceDocument AssoServiceDocument);
}
