package com.jss.jssbackend.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.quotation.model.ProvisionFamilyType;
import com.jss.jssbackend.modules.quotation.repository.ProvisionFamilyTypeRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProvisionFamilyTypeServiceImpl implements ProvisionFamilyTypeService {

    @Autowired
    ProvisionFamilyTypeRepository provisionFamilyTypeRepository;

    @Override
    public List<ProvisionFamilyType> getProvisionFamilyTypes() {
        return IterableUtils.toList(provisionFamilyTypeRepository.findAll());
    }

    @Override
    public ProvisionFamilyType getProvisionFamilyType(Integer id) {
        Optional<ProvisionFamilyType> provisionFamilyType = provisionFamilyTypeRepository.findById(id);
        if (!provisionFamilyType.isEmpty())
            return provisionFamilyType.get();
        return null;
    }
}
