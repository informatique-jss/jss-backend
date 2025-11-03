package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.OrderBlockage;

public interface OrderBlockageService {
    public List<OrderBlockage> getOrderBlockages();

    public OrderBlockage getOrderBlockage(Integer id);

    public OrderBlockage addOrUpdateOrderBlockage(OrderBlockage orderBlockage);
}
