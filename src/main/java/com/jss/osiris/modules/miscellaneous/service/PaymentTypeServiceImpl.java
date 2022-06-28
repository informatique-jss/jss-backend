package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.miscellaneous.model.PaymentType;
import com.jss.osiris.modules.miscellaneous.repository.PaymentTypeRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentTypeServiceImpl implements PaymentTypeService {

    @Autowired
    PaymentTypeRepository paymentTypeRepository;

    @Override
    public List<PaymentType> getPaymentTypes() {
        return IterableUtils.toList(paymentTypeRepository.findAll());
    }

    @Override
    public PaymentType getPaymentType(Integer id) {
        Optional<PaymentType> paymentType = paymentTypeRepository.findById(id);
        if (!paymentType.isEmpty())
            return paymentType.get();
        return null;
    }
	
	 @Override
    public PaymentType addOrUpdatePaymentType(
            PaymentType paymentType) {
        return paymentTypeRepository.save(paymentType);
    }
}
