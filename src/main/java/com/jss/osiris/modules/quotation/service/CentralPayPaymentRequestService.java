package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.quotation.model.CentralPayPaymentRequest;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Quotation;

public interface CentralPayPaymentRequestService {
    public List<CentralPayPaymentRequest> getCentralPayPaymentRequests();

    public CentralPayPaymentRequest addOrUpdateCentralPayPaymentRequest(
            CentralPayPaymentRequest centralPayPaymentRequest);

    public void deleteCentralPayPaymentRequest(CentralPayPaymentRequest centralPayPaymentRequest);

    public void declareNewCentralPayPaymentRequest(String paymentRequestId, CustomerOrder customerOrder,
            Quotation quotation, Boolean isForInvoice);

    public void checkAllPaymentRequests()
            throws OsirisException, OsirisClientMessageException, OsirisValidationException;

    public CentralPayPaymentRequest getCentralPayPaymentRequestByCustomerOrder(CustomerOrder customerOrder);

    public CentralPayPaymentRequest getCentralPayPaymentRequestByQuotation(Quotation quotation);
}
