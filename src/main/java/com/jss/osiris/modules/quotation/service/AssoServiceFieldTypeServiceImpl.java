package com.jss.osiris.modules.quotation.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.quotation.model.AssoServiceFieldType;
import com.jss.osiris.modules.quotation.repository.AssoServiceFieldTypeRepository;

@Service
public class AssoServiceFieldTypeServiceImpl implements AssoServiceFieldTypeService {

    @Autowired
    AssoServiceFieldTypeRepository assoServiceFieldTypeRepository;

    @Autowired
    MissingAttachmentQueryService missingAttachmentQueryService;

    @Override
    public AssoServiceFieldType getAssoServiceFieldType(Integer id) {
        Optional<AssoServiceFieldType> assoServiceFieldType = assoServiceFieldTypeRepository.findById(id);
        if (assoServiceFieldType.isPresent())
            return assoServiceFieldType.get();
        return null;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public AssoServiceFieldType addOrUpdateServiceFieldType(AssoServiceFieldType assoServiceFieldType)
            throws OsirisException {
        missingAttachmentQueryService.checkCompleteAttachmentAndFieldListAndComment(null, assoServiceFieldType, null);
        return assoServiceFieldTypeRepository.save(assoServiceFieldType);
    }
}
