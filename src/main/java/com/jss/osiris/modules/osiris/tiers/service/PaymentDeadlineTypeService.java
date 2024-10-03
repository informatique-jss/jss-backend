package com.jss.osiris.modules.osiris.tiers.service;

import java.util.List;

import com.jss.osiris.modules.osiris.tiers.model.PaymentDeadlineType;

public interface PaymentDeadlineTypeService {
    public List<PaymentDeadlineType> getPaymentDeadlineTypes();

    public PaymentDeadlineType getPaymentDeadlineType(Integer id);

    public PaymentDeadlineType addOrUpdatePaymentDeadlineType(PaymentDeadlineType paymentDeadlineType);
}
