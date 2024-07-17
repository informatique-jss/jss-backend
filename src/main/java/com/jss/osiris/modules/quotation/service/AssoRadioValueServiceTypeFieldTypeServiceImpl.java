package com.jss.osiris.modules.quotation.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.jss.osiris.modules.quotation.model.AssoRadioValueServiceTypeFieldType;
import com.jss.osiris.modules.quotation.repository.AssoRadioValueServiceTypeFieldTypeRepository;

public class AssoRadioValueServiceTypeFieldTypeServiceImpl implements AssoRadioValueServiceTypeFieldTypeService {
    @Autowired
    AssoRadioValueServiceTypeFieldTypeRepository assoRadioValueServiceTypeFieldTypeRepository;

    @Override
    public AssoRadioValueServiceTypeFieldType getAssoRadioValueServiceTypeFieldTypeService(Integer id) {
        Optional<AssoRadioValueServiceTypeFieldType> assoRadioValueServiceTypeFieldType = assoRadioValueServiceTypeFieldTypeRepository
                .findById(id);
        if (assoRadioValueServiceTypeFieldType.isPresent())
            return assoRadioValueServiceTypeFieldType.get();
        return null;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public AssoRadioValueServiceTypeFieldType addOrUpdateAssoRadioValueServiceTypeFieldTypeService(
            AssoRadioValueServiceTypeFieldType assoRadioValueServiceTypeFieldType) {
        return assoRadioValueServiceTypeFieldTypeRepository.save(assoRadioValueServiceTypeFieldType);
    }
}
