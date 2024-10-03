package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.quotation.model.ProvisionFamilyType;
import com.jss.osiris.modules.osiris.quotation.repository.ProvisionFamilyTypeRepository;

@Service
public class ProvisionFamilyTypeServiceImpl implements ProvisionFamilyTypeService {

    @Autowired
    ProvisionFamilyTypeRepository provisionFamilyTypeRepository;

    @Override
    public List<ProvisionFamilyType> getProvisionFamilyTypes() {
        return provisionFamilyTypeRepository.findAllByOrderByCode();
    }

    @Override
    public ProvisionFamilyType getProvisionFamilyType(Integer id) {
        Optional<ProvisionFamilyType> provisionFamilyType = provisionFamilyTypeRepository.findById(id);
        if (provisionFamilyType.isPresent())
            return provisionFamilyType.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProvisionFamilyType addOrUpdateProvisionFamilyType(ProvisionFamilyType provisionFamilyType) {
        return provisionFamilyTypeRepository.save(provisionFamilyType);
    }
}
