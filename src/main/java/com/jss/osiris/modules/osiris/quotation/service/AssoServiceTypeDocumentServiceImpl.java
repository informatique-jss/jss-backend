package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.AssoServiceTypeDocument;
import com.jss.osiris.modules.osiris.quotation.model.ServiceType;
import com.jss.osiris.modules.osiris.quotation.repository.AssoServiceTypeDocumentRepository;

@Service
public class AssoServiceTypeDocumentServiceImpl implements AssoServiceTypeDocumentService {

    @Autowired
    AssoServiceTypeDocumentRepository assoServiceDocumentRepository;

    @Override
    public List<AssoServiceTypeDocument> getAssoServiceTypeDocumentMandatoryByServiceType(ServiceType serviceType) {
        return assoServiceDocumentRepository.findByServiceTypeAndIsMandatory(serviceType, true);
    }

}
