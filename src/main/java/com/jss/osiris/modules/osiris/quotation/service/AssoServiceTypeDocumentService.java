package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.AssoServiceTypeDocument;
import com.jss.osiris.modules.osiris.quotation.model.ServiceType;

public interface AssoServiceTypeDocumentService {
    public List<AssoServiceTypeDocument> getAssoServiceTypeDocumentMandatoryByServiceType(ServiceType serviceType);
}
