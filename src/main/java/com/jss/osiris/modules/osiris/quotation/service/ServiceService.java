package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.model.ServiceType;

public interface ServiceService {
    public Service getService(Integer id);

    public Service addOrUpdateService(Service service);

    public Boolean deleteService(Service service);

    public Service modifyServiceType(List<ServiceType> services, Service service);

    public Service getServiceForMultiServiceTypesAndAffaire(List<ServiceType> serviceTypes, Affaire affaire,
            String customLabel)
            throws OsirisException;

    public List<Attachment> getAttachmentsForProvisionOfService(Service service);
}
