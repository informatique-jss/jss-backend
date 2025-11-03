package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.osiris.quotation.model.OrderBlockage;
import com.jss.osiris.modules.osiris.quotation.repository.OrderBlockageRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderBlockageServiceImpl implements OrderBlockageService {

    @Autowired
    OrderBlockageRepository orderBlockageRepository;

    @Override
    public List<OrderBlockage> getOrderBlockages() {
        return IterableUtils.toList(orderBlockageRepository.findAll());
    }

    @Override
    public OrderBlockage getOrderBlockage(Integer id) {
        Optional<OrderBlockage> orderBlockage = orderBlockageRepository.findById(id);
        if (orderBlockage.isPresent())
            return orderBlockage.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderBlockage addOrUpdateOrderBlockage(
            OrderBlockage orderBlockage) {
        return orderBlockageRepository.save(orderBlockage);
    }
}
