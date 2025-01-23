package com.jss.osiris.modules.osiris.quotation.service;

import com.jss.osiris.modules.osiris.quotation.model.AssoServiceDocument;

public interface AssoServiceDocumentService {
    public AssoServiceDocument getAssoServiceDocument(Integer id);

    public AssoServiceDocument addOrUpdateAssoServiceDocument(AssoServiceDocument AssoServiceDocument);
}
