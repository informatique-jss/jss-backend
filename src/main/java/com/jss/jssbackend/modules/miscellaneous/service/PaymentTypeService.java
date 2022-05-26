package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.PaymentType;

public interface PaymentTypeService {
    public List<PaymentType> getPaymentTypes();

    public PaymentType getPaymentType(Integer id);
}
