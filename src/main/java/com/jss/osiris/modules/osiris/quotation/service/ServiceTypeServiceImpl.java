package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceProvisionType;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceTypeDocument;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceTypeFieldType;
import com.jss.osiris.modules.osiris.quotation.model.ProvisionScreenType;
import com.jss.osiris.modules.osiris.quotation.model.ServiceFamily;
import com.jss.osiris.modules.osiris.quotation.model.ServiceType;
import com.jss.osiris.modules.osiris.quotation.repository.ServiceTypeRepository;

@org.springframework.stereotype.Service
public class ServiceTypeServiceImpl implements ServiceTypeService {

    @Autowired
    ServiceTypeRepository serviceTypeRepository;

    @Autowired
    ConstantService constantService;

    @Override
    public List<ServiceType> getServiceTypes() {
        return IterableUtils.toList(serviceTypeRepository.findAll());
    }

    @Override
    public ServiceType getServiceType(Integer id) {
        return this.getServiceType(id, false);
    }

    @Override
    public ServiceType getServiceType(Integer id, Boolean isMandatory) {
        Optional<ServiceType> serviceType = serviceTypeRepository.findById(id);
        ServiceType serviceFinal = null;

        if (serviceType.isPresent()) {
            serviceFinal = serviceType.get();
            if (!serviceFinal.getAssoServiceTypeDocuments().isEmpty())
                serviceFinal.getAssoServiceTypeDocuments()
                        .removeIf(asso -> !asso.getIsMandatory().equals(isMandatory));
            if (!serviceFinal.getAssoServiceTypeFieldTypes().isEmpty())
                serviceFinal.getAssoServiceTypeFieldTypes()
                        .removeIf(asso -> !asso.getIsMandatory().equals(isMandatory));
        }
        return serviceFinal;
    }

    @Override
    public ServiceType getServiceTypeByCode(String code) {
        return serviceTypeRepository.findByCode(code);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceType addOrUpdateServiceType(
            ServiceType serviceType) {
        if (serviceType.getAssoServiceProvisionTypes() != null)
            for (AssoServiceProvisionType asso : serviceType.getAssoServiceProvisionTypes())
                asso.setServiceType(serviceType);

        if (serviceType.getAssoServiceTypeDocuments() != null)
            for (AssoServiceTypeDocument asso : serviceType.getAssoServiceTypeDocuments())
                asso.setServiceType(serviceType);

        if (serviceType.getAssoServiceTypeFieldTypes() != null)
            for (AssoServiceTypeFieldType asso : serviceType.getAssoServiceTypeFieldTypes())
                asso.setServiceType(serviceType);

        return serviceTypeRepository.save(serviceType);
    }

    @Override
    public List<ServiceType> getServiceTypesForFamily(ServiceFamily serviceFamily) throws OsirisException {
        List<ServiceType> serviceTypes = serviceTypeRepository.findByServiceFamily(serviceFamily);
        ProvisionScreenType provisionScreenTypeAnnouncement = constantService.getProvisionScreenTypeAnnouncement();
        if (serviceTypes != null)
            for (ServiceType serviceType : serviceTypes) {
                boolean findOtherThanAnnouncement = false;
                serviceType.setHasAnnouncement(false);
                serviceType.setHasOnlyAnnouncement(false);
                if (serviceType.getAssoServiceProvisionTypes() != null)
                    for (AssoServiceProvisionType provisionType : serviceType.getAssoServiceProvisionTypes())
                        if (provisionType.getProvisionType().getProvisionScreenType().getId()
                                .equals(provisionScreenTypeAnnouncement.getId()))
                            serviceType.setHasAnnouncement(true);
                        else
                            findOtherThanAnnouncement = true;
                if (!findOtherThanAnnouncement && serviceType.getHasAnnouncement())
                    serviceType.setHasOnlyAnnouncement(true);
            }
        return serviceTypes;
    }
}
