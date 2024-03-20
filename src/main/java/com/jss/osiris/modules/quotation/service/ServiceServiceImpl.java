package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.quotation.model.Service;
import com.jss.osiris.modules.quotation.repository.ServiceRepository;

@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServiceService {

    @Autowired
    ServiceRepository serviceRepository;

    @Override
    public List<Service> getServices() {
        return IterableUtils.toList(serviceRepository.findAll());
    }

    @Override
    public Service getService(Integer id) {
        Optional<Service> service = serviceRepository.findById(id);
        if (service.isPresent())
            return service.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Service addOrUpdateService(
            Service service) {
        return serviceRepository.save(service);
    }
}
