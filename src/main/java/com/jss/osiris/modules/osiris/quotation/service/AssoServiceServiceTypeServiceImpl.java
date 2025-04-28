package com.jss.osiris.modules.osiris.quotation.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.quotation.model.AssoServiceServiceType;
import com.jss.osiris.modules.osiris.quotation.repository.AssoServiceServiceTypeRepository;

@Service
public class AssoServiceServiceTypeServiceImpl implements AssoServiceServiceTypeService {
    @Autowired
    AssoServiceServiceTypeRepository assoServiceServiceTypeRepository;

    @Override
    public AssoServiceServiceType getAssoServiceServiceType(Integer id) {
        Optional<AssoServiceServiceType> assoServiceServiceType = assoServiceServiceTypeRepository.findById(id);
        if (assoServiceServiceType.isPresent())
            return assoServiceServiceType.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AssoServiceServiceType addOrUpdateAssoServiceServiceType(AssoServiceServiceType assoServiceServiceType) {
        return assoServiceServiceTypeRepository.save(assoServiceServiceType);
    }
}
