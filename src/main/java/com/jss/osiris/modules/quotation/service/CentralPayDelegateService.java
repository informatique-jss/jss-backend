package com.jss.osiris.modules.quotation.service;

import com.jss.osiris.modules.quotation.model.centralPay.CentralPayPaymentRequest;

public interface CentralPayDelegateService {

    public CentralPayPaymentRequest cancelPaymentRequest(String centralPayPaymentRequestId);

    public CentralPayPaymentRequest generatePayPaymentRequest(Float amount, String mail, String entityId,
            String subject);

    public CentralPayPaymentRequest getPaymentRequest(String centralPayPaymentRequestId);
}
