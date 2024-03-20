package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.Service;

public interface ServiceService {
    public List<Service> getServices();

    public Service getService(Integer id);

    public Service addOrUpdateService(Service service);
}
