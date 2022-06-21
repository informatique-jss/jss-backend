package com.jss.osiris.modules.tiers.service;

import java.util.List;

import com.jss.osiris.modules.tiers.model.PaymentDeadlineType;

public interface PaymentDeadlineTypeService {
    public List<PaymentDeadlineType> getPaymentDeadlineTypes();

    public PaymentDeadlineType getPaymentDeadlineType(Integer id);
}
