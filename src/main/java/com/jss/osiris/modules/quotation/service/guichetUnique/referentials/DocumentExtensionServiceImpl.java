package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.DocumentExtension;
import com.jss.osiris.modules.quotation.repository.guichetUnique.DocumentExtensionRepository;

@Service
public class DocumentExtensionServiceImpl implements DocumentExtensionService {

    @Autowired
    DocumentExtensionRepository DocumentExtensionRepository;

    @Override
    @Cacheable(value = "documentExtensionList", key = "#root.methodName")
    public List<DocumentExtension> getDocumentExtension() {
        return IterableUtils.toList(DocumentExtensionRepository.findAll());
    }
}
