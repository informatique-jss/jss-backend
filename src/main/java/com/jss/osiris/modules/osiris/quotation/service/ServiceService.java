package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.model.ServiceType;

public interface ServiceService {
        public Service getService(Integer id);

        public Service addOrUpdateService(Service service) throws OsirisException;

        public Boolean addOrUpdateServices(List<ServiceType> services, Integer affaireId, Integer assoAffaireOrderId,
                        String customLabel)
                        throws OsirisException;

        public Boolean deleteServiceFromUser(Service service);

        public Service modifyServiceType(List<ServiceType> serviceType, Service service) throws OsirisException;

        public List<Service> generateServiceInstanceFromMultiServiceTypes(List<ServiceType> serviceTypes,
                        Affaire affaire, String customLabel) throws OsirisException;

        public List<Attachment> getAttachmentsForProvisionOfService(Service service);
}
