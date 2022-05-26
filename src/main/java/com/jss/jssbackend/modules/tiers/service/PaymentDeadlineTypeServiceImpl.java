package com.jss.jssbackend.modules.tiers.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.tiers.model.PaymentDeadlineType;
import com.jss.jssbackend.modules.tiers.repository.PaymentDeadlineTypeRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentDeadlineTypeServiceImpl implements PaymentDeadlineTypeService {

    @Autowired
    PaymentDeadlineTypeRepository paymentDeadlineTypeRepository;

    @Override
    public List<PaymentDeadlineType> getPaymentDeadlineTypes() {
        return IterableUtils.toList(paymentDeadlineTypeRepository.findAll());
    }

    @Override
    public PaymentDeadlineType getPaymentDeadlineType(Integer id) {
        Optional<PaymentDeadlineType> paymentDeadlineType = paymentDeadlineTypeRepository.findById(id);
        if (!paymentDeadlineType.isEmpty())
            return paymentDeadlineType.get();
        return null;
    }
}
