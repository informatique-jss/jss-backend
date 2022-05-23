package com.jss.jssbackend.modules.tiers.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.tiers.model.TiersDocument;
import com.jss.jssbackend.modules.tiers.repository.TiersDocumentRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TiersDocumentServiceImpl implements TiersDocumentService {

    @Autowired
    TiersDocumentRepository tiersDocumentRepository;

    @Override
    public List<TiersDocument> getTiersDocuments() {
        return IterableUtils.toList(tiersDocumentRepository.findAll());
    }

    @Override
    public TiersDocument getTiersDocument(Integer id) {
        Optional<TiersDocument> tiersDocument = tiersDocumentRepository.findById(id);
        if (!tiersDocument.isEmpty())
            return tiersDocument.get();
        return null;
    }
}
