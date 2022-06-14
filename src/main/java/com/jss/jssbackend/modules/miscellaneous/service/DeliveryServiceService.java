package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.DeliveryService;

public interface DeliveryServiceService {
    public List<DeliveryService> getDeliveryServices();

    public DeliveryService getDeliveryService(Integer id);
}
