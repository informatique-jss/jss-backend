package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.ServiceType;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeDocument;

public interface TypeDocumentService {
    public List<TypeDocument> getTypeDocument();

    public TypeDocument getTypeDocumentByCode(String code);

    public TypeDocument addOrUpdateTypeDocument(TypeDocument typeDocument);

    public List<TypeDocument> getTypeDocumentMandatoryByServiceType(ServiceType serviceType);

}
