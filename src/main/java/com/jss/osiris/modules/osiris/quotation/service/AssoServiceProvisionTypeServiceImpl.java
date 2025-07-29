package com.jss.osiris.modules.osiris.quotation.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.AssoServiceProvisionType;
import com.jss.osiris.modules.osiris.quotation.repository.AssoServiceProvisionTypeRepository;

@Service
public class AssoServiceProvisionTypeServiceImpl implements AssoServiceProvisionTypeService {

    @Autowired
    AssoServiceProvisionTypeRepository assoServiceProvisionTypeRepository;

    @Override
    public AssoServiceProvisionType getAssoServiceProvisionType(Integer id) {
        Optional<AssoServiceProvisionType> assoServiceProvisionType = assoServiceProvisionTypeRepository.findById(id);
        if (assoServiceProvisionType.isPresent())
            return assoServiceProvisionType.get();
        return null;
    }
}
