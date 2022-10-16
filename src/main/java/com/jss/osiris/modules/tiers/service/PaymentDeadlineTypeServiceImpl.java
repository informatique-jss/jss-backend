package com.jss.osiris.modules.tiers.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.tiers.model.PaymentDeadlineType;
import com.jss.osiris.modules.tiers.repository.PaymentDeadlineTypeRepository;

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
        if (paymentDeadlineType.isPresent())
            return paymentDeadlineType.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentDeadlineType addOrUpdatePaymentDeadlineType(
            PaymentDeadlineType paymentDeadlineType) {
        return paymentDeadlineTypeRepository.save(paymentDeadlineType);
    }
}
