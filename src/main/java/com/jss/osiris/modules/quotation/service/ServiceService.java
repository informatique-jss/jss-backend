package com.jss.osiris.modules.quotation.service;

import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.Service;
import com.jss.osiris.modules.quotation.model.ServiceType;

public interface ServiceService {
    public Service getService(Integer id);

    public Service addOrUpdateService(Service service);

    public Service getServiceForServiceTypeAndAffaire(ServiceType serviceType, Affaire affaire);

    public Service modifyServiceType(ServiceType serviceType, Service service);
}
