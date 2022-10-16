package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.miscellaneous.model.DeliveryService;
import com.jss.osiris.modules.miscellaneous.repository.DeliveryServiceRepository;

@Service
public class DeliveryServiceServiceImpl implements DeliveryServiceService {

    @Autowired
    DeliveryServiceRepository deliveryServiceRepository;

    @Override
    public List<DeliveryService> getDeliveryServices() {
        return IterableUtils.toList(deliveryServiceRepository.findAll());
    }

    @Override
    public DeliveryService getDeliveryService(Integer id) {
        Optional<DeliveryService> deliveryService = deliveryServiceRepository.findById(id);
        if (deliveryService.isPresent())
            return deliveryService.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeliveryService addOrUpdateDeliveryService(
            DeliveryService deliveryService) {
        return deliveryServiceRepository.save(deliveryService);
    }
}
