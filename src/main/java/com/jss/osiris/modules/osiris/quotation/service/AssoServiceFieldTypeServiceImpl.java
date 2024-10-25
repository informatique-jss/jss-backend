package com.jss.osiris.modules.osiris.quotation.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.AssoServiceFieldType;
import com.jss.osiris.modules.osiris.quotation.model.ServiceFieldType;
import com.jss.osiris.modules.osiris.quotation.repository.AssoServiceFieldTypeRepository;

@Service
public class AssoServiceFieldTypeServiceImpl implements AssoServiceFieldTypeService {

    @Autowired
    AssoServiceFieldTypeRepository assoServiceFieldTypeRepository;

    @Override
    public AssoServiceFieldType getAssoServiceFieldType(Integer id) {
        Optional<AssoServiceFieldType> assoServiceFieldType = assoServiceFieldTypeRepository.findById(id);
        if (assoServiceFieldType.isPresent())
            return assoServiceFieldType.get();
        return null;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public AssoServiceFieldType addOrUpdateServiceFieldType(AssoServiceFieldType assoServiceFieldType) {
        return assoServiceFieldTypeRepository.save(assoServiceFieldType);
    }

    @Override
    public AssoServiceFieldType createAssoServiceFieldType(
            com.jss.osiris.modules.osiris.quotation.model.Service service, ServiceFieldType serviceFieldType) {
        AssoServiceFieldType assoServiceFieldType = new AssoServiceFieldType();
        assoServiceFieldType.setService(service);
        assoServiceFieldType.setServiceFieldType(serviceFieldType);
        return addOrUpdateServiceFieldType(assoServiceFieldType);
    }
}
