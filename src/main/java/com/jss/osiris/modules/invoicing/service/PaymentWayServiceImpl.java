package com.jss.osiris.modules.invoicing.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.invoicing.model.PaymentWay;
import com.jss.osiris.modules.invoicing.repository.PaymentWayRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentWayServiceImpl implements PaymentWayService {

    @Autowired
    PaymentWayRepository paymentWayRepository;

    @Override
	@Cacheable(value = "paymentWayList", key = "#root.methodName")
    public List<PaymentWay> getPaymentWays() {
        return IterableUtils.toList(paymentWayRepository.findAll());
    }

    @Override
	@Cacheable(value = "paymentWay", key = "#id")
    public PaymentWay getPaymentWay(Integer id) {
        Optional<PaymentWay> paymentWay = paymentWayRepository.findById(id);
        if (!paymentWay.isEmpty())
            return paymentWay.get();
        return null;
    }
	
	 @Override
	 @Caching(evict = {
            @CacheEvict(value = "paymentWayList", allEntries = true),
            @CacheEvict(value = "paymentWay", key = "#paymentWay.id")
    })
    public PaymentWay addOrUpdatePaymentWay(
            PaymentWay paymentWay) {
        return paymentWayRepository.save(paymentWay);
    }
}
