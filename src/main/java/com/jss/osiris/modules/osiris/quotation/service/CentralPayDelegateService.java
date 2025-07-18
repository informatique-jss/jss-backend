package com.jss.osiris.modules.osiris.quotation.service;

import java.math.BigDecimal;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.quotation.model.centralPay.CentralPayPaymentRequest;
import com.jss.osiris.modules.osiris.quotation.model.centralPay.CentralPayTransaction;

public interface CentralPayDelegateService {

        public CentralPayPaymentRequest cancelPaymentRequest(String centralPayPaymentRequestId);

        public CentralPayPaymentRequest generatePayPaymentRequest(BigDecimal amount, String mail, String entityId,
                        String subject, boolean isQuotation);

        public CentralPayPaymentRequest getPaymentRequest(String centralPayPaymentRequestId) throws OsirisException;

        public CentralPayTransaction getTransaction(CentralPayPaymentRequest centralPayPaymentRequest)
                        throws OsirisException;
}
