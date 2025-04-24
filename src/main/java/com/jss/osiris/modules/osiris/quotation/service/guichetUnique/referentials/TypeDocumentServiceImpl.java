package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.ArrayList;
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
    TypeDocumentRepository TypeDocumentRepository;

    @Override
    public List<TypeDocument> getTypeDocument() {
        return IterableUtils.toList(TypeDocumentRepository.findAll());
    }

    @Override
    public TypeDocument addOrUpdateTypeDocument(TypeDocument typeDocument) {
        return TypeDocumentRepository.save(typeDocument);
    }

    @Override
    public TypeDocument getTypeDocumentByCode(String code) {
        return TypeDocumentRepository.findByCode(code);
    }

    @Override
    public List<TypeDocument> getTypeDocumentMandatoryByServiceType(ServiceType serviceType) {
        List<TypeDocument> mandatoryTypeDocuments = new ArrayList<>();
        List<String> mandatoryTypeDocumentCodes = new ArrayList<>();

        if (serviceType != null && !serviceType.getAssoServiceTypeDocuments().isEmpty()) {
            for (AssoServiceTypeDocument asso : serviceType.getAssoServiceTypeDocuments()) {
                if (asso.getTypeDocument() != null && asso.getIsMandatory()
                        && !mandatoryTypeDocumentCodes.contains(asso.getTypeDocument().getCode())) {
                    mandatoryTypeDocumentCodes.add(asso.getTypeDocument().getCode());
                    mandatoryTypeDocuments.add(asso.getTypeDocument());
                }
            }
        }
        return mandatoryTypeDocuments;
    }
}
