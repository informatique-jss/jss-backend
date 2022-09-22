package com.jss.osiris.modules.invoicing.service;

import java.util.List;

import com.jss.osiris.modules.invoicing.model.PaymentWay;

public interface PaymentWayService {
    public List<PaymentWay> getPaymentWays();

    public PaymentWay getPaymentWay(Integer id);
	
	 public PaymentWay addOrUpdatePaymentWay(PaymentWay paymentWay);
}
