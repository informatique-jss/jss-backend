package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.DeliveryService;

public interface DeliveryServiceService {
    public List<DeliveryService> getDeliveryServices();

    public DeliveryService getDeliveryService(Integer id);

    public DeliveryService addOrUpdateDeliveryService(DeliveryService deliveryService);
}
