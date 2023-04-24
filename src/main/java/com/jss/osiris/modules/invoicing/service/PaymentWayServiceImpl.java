package com.jss.osiris.modules.invoicing.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.invoicing.model.PaymentWay;
import com.jss.osiris.modules.invoicing.repository.PaymentWayRepository;

@Service
public class PaymentWayServiceImpl implements PaymentWayService {

    @Autowired
    PaymentWayRepository paymentWayRepository;

    @Override
    public List<PaymentWay> getPaymentWays() {
        return IterableUtils.toList(paymentWayRepository.findAll());
    }

    @Override
    public PaymentWay getPaymentWay(Integer id) {
        Optional<PaymentWay> paymentWay = paymentWayRepository.findById(id);
        if (paymentWay.isPresent())
            return paymentWay.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentWay addOrUpdatePaymentWay(
            PaymentWay paymentWay) {
        return paymentWayRepository.save(paymentWay);
    }
}
