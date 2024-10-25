package com.jss.osiris.modules.osiris.quotation.service;

import com.jss.osiris.modules.osiris.quotation.model.AssoServiceDocument;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeDocument;

public interface AssoServiceDocumentService {
    public AssoServiceDocument getAssoServiceDocument(Integer id);

    public AssoServiceDocument addOrUpdateAssoServiceDocument(AssoServiceDocument AssoServiceDocument);

    public AssoServiceDocument createAssoServiceDocument(Service service, TypeDocument typeDocument);
}
