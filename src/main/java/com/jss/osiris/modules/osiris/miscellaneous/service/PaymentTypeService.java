package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.PaymentType;

public interface PaymentTypeService {
    public List<PaymentType> getPaymentTypes();

    public PaymentType getPaymentType(Integer id);

    public PaymentType addOrUpdatePaymentType(PaymentType paymentType);

    public PaymentType getPaymentTypeByCodeInpi(String codeInpi);
}
