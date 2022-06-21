package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.DeliveryService;

public interface DeliveryServiceService {
    public List<DeliveryService> getDeliveryServices();

    public DeliveryService getDeliveryService(Integer id);
}
