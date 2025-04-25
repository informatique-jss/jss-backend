package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.AssoServiceTypeDocument;
import com.jss.osiris.modules.osiris.quotation.model.ServiceType;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeDocument;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.TypeDocumentRepository;

@Service
public class TypeDocumentServiceImpl implements TypeDocumentService {

    @Autowired
    TypeDocumentRepository typeDocumentRepository;

    @Autowired
    AssoServiceTypeDocumentService assoServiceTypeDocumentService;

    @Override
    public List<TypeDocument> getTypeDocument() {
        return IterableUtils.toList(typeDocumentRepository.findAll());
    }

    @Override
    public TypeDocument addOrUpdateTypeDocument(TypeDocument typeDocument) {
        return typeDocumentRepository.save(typeDocument);
    }

    @Override
    public TypeDocument getTypeDocumentByCode(String code) {
        return typeDocumentRepository.findByCode(code);
    }

    @Override
    public List<TypeDocument> getTypeDocumentMandatoryByServiceType(ServiceType serviceType) {
        return assoServiceTypeDocumentService.getAssoServiceTypeDocumentMandatoryByServiceType(serviceType).stream()
                .map(AssoServiceTypeDocument::getTypeDocument).toList();
    }
}
