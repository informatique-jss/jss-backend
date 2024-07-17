package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.quotation.model.ServiceFieldType;
import com.jss.osiris.modules.quotation.model.ServiceFieldTypePossibleValue;
import com.jss.osiris.modules.quotation.repository.ServiceFieldTypeRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceFieldTypeServiceImpl implements ServiceFieldTypeService {

    @Autowired
    ServiceFieldTypeRepository serviceFieldTypeRepository;

    @Override
    public List<ServiceFieldType> getServiceFieldTypes() {
        return IterableUtils.toList(serviceFieldTypeRepository.findAll());
    }

    @Override
    public ServiceFieldType getServiceFieldType(Integer id) {
        Optional<ServiceFieldType> serviceFieldType = serviceFieldTypeRepository.findById(id);
        if (serviceFieldType.isPresent())
            return serviceFieldType.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceFieldType addOrUpdateServiceFieldType(
            ServiceFieldType serviceFieldType) {
        if (serviceFieldType.getServiceFieldTypePossibleValues() != null)
            for (ServiceFieldTypePossibleValue possibleValue : serviceFieldType.getServiceFieldTypePossibleValues())
                possibleValue.setServiceFieldType(serviceFieldType);
        return serviceFieldTypeRepository.save(serviceFieldType);
    }
}
