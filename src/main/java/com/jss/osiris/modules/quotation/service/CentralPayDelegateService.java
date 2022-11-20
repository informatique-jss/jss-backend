package com.jss.osiris.modules.quotation.service;

import com.jss.osiris.modules.quotation.model.centralPay.CentralPayPaymentRequest;

public interface CentralPayDelegateService {

    public CentralPayPaymentRequest cancelPaymentRequest(String centralPayPaymentRequestId) throws Exception;

    public CentralPayPaymentRequest generatePayPaymentRequest(Float amount, String mail, String entityId,
            String subject)
            throws Exception;

    public CentralPayPaymentRequest getPaymentRequest(String centralPayPaymentRequestId) throws Exception;
}
