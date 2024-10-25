package com.jss.osiris.modules.osiris.quotation.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceDocument;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeDocument;
import com.jss.osiris.modules.osiris.quotation.repository.AssoServiceDocumentRepository;

@Service
public class AssoServiceDocumentServiceImpl implements AssoServiceDocumentService {

    @Autowired
    AssoServiceDocumentRepository assoServiceDocumentRepository;

    @Override
    public AssoServiceDocument getAssoServiceDocument(Integer id) {
        Optional<AssoServiceDocument> assoServiceDocument = assoServiceDocumentRepository.findById(id);
        if (assoServiceDocument.isPresent())
            return assoServiceDocument.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AssoServiceDocument addOrUpdateAssoServiceDocument(
            AssoServiceDocument assoServiceDocument) {
        return assoServiceDocumentRepository.save(assoServiceDocument);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AssoServiceDocument createAssoServiceDocument(com.jss.osiris.modules.osiris.quotation.model.Service service,
            TypeDocument typeDocument) {
        AssoServiceDocument assoServiceDocument = new AssoServiceDocument();
        assoServiceDocument.setService(service);
        assoServiceDocument.setTypeDocument(typeDocument);
        return addOrUpdateAssoServiceDocument(assoServiceDocument);
    }

}
