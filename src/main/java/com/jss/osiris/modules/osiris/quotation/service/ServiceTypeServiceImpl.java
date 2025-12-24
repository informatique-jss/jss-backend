package com.jss.osiris.modules.osiris.quotation.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.BillingItem;
import com.jss.osiris.modules.osiris.miscellaneous.model.BillingType;
import com.jss.osiris.modules.osiris.miscellaneous.service.BillingItemService;
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

    @Autowired
    BillingItemService billingItemService;

    @Override
    public List<ServiceType> getServiceTypes() {
        return IterableUtils.toList(serviceTypeRepository.findAll());
    }

    @Override
    public ServiceType getServiceType(Integer id) {
        return this.getServiceType(id, false);
    }

    @Override
    public ServiceType getServiceType(Integer id, Boolean isFetchOnlyMandatoryDocuments) {
        Optional<ServiceType> serviceType = serviceTypeRepository.findById(id);
        ServiceType serviceFinal = null;

        if (serviceType.isPresent()) {
            serviceFinal = serviceType.get();
            if (!serviceFinal.getAssoServiceTypeDocuments().isEmpty() && isFetchOnlyMandatoryDocuments)
                serviceFinal.getAssoServiceTypeDocuments().removeIf(asso -> !asso.getIsMandatory());
            if (!serviceFinal.getAssoServiceTypeFieldTypes().isEmpty() && isFetchOnlyMandatoryDocuments)
                serviceFinal.getAssoServiceTypeFieldTypes().removeIf(asso -> !asso.getIsMandatory());
        }

        return getCurrentBillingItemsForServiceType(serviceFinal);
    }

    private ServiceType getCurrentBillingItemsForServiceType(ServiceType serviceType) {
        BigDecimal totalPrice = new BigDecimal(0f);
        BigDecimal deboursAmount = new BigDecimal(0f);

        if (serviceType.getAssoServiceProvisionTypes() != null && !serviceType.getAssoServiceProvisionTypes().isEmpty())
            for (AssoServiceProvisionType asso : serviceType.getAssoServiceProvisionTypes())
                if (asso.getProvisionType() != null && asso.getProvisionType().getBillingTypes() != null
                        && !asso.getProvisionType().getBillingTypes().isEmpty())
                    for (BillingType billingType : asso.getProvisionType().getBillingTypes()) {
                        BillingItem newBillingItem = billingItemService.getCurrentBillingItemByBillingType(billingType);
                        if (newBillingItem != null) {
                            if (billingType.getIsDebour() && newBillingItem.getPreTaxPrice() != null)
                                deboursAmount = deboursAmount.add(newBillingItem.getPreTaxPrice());
                            totalPrice = totalPrice.add(newBillingItem.getPreTaxPrice());
                        }
                    }
        serviceType.setTotalPreTaxPrice(totalPrice);
        serviceType.setDeboursAmount(deboursAmount);
        return serviceType;
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
    public List<ServiceType> getServiceTypesForFamilyForMyJss(ServiceFamily serviceFamily) throws OsirisException {
        List<ServiceType> serviceTypes = serviceTypeRepository.findByServiceFamilyForMyJss(serviceFamily);
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
