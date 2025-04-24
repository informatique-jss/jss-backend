package com.jss.osiris.modules.osiris.quotation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.quotation.model.AssoServiceTypeFieldType;
import com.jss.osiris.modules.osiris.quotation.model.ServiceFieldType;
import com.jss.osiris.modules.osiris.quotation.model.ServiceType;
import com.jss.osiris.modules.osiris.quotation.model.ServiceTypeFieldTypePossibleValue;
import com.jss.osiris.modules.osiris.quotation.repository.ServiceFieldTypeRepository;

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
            for (ServiceTypeFieldTypePossibleValue possibleValue : serviceFieldType.getServiceFieldTypePossibleValues())
                possibleValue.setServiceFieldType(serviceFieldType);

        return serviceFieldTypeRepository.save(serviceFieldType);
    }

    @Override
    public List<ServiceFieldType> getServiceFieldTypeByServiceType(ServiceType serviceType) {
        List<ServiceFieldType> mandatoryFieldTypes = new ArrayList<>();
        List<Integer> mandatoryFieldTypeIds = new ArrayList<>();

        if (serviceType != null && !serviceType.getAssoServiceTypeFieldTypes().isEmpty()) {
            for (AssoServiceTypeFieldType asso : serviceType.getAssoServiceTypeFieldTypes()) {
                if (asso.getServiceFieldType() != null && asso.getIsMandatory()
                        && !mandatoryFieldTypeIds.contains(asso.getServiceFieldType().getId())) {
                    mandatoryFieldTypeIds.add(asso.getServiceFieldType().getId());
                    mandatoryFieldTypes.add(asso.getServiceFieldType());
                }
            }
        }
        return mandatoryFieldTypes;
    }
}
