package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeDocument;
import com.jss.osiris.modules.quotation.repository.guichetUnique.TypeDocumentRepository;

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
}
