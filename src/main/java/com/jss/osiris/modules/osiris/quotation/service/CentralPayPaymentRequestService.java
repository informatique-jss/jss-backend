package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.quotation.model.CentralPayPaymentRequest;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;

public interface CentralPayPaymentRequestService {
        public List<CentralPayPaymentRequest> getCentralPayPaymentRequests();

        public CentralPayPaymentRequest getCentralPayPaymentRequest(Integer id);

        public CentralPayPaymentRequest addOrUpdateCentralPayPaymentRequest(
                        CentralPayPaymentRequest centralPayPaymentRequest);

        public void deleteCentralPayPaymentRequest(CentralPayPaymentRequest centralPayPaymentRequest);

        public void declareNewCentralPayPaymentRequest(String paymentRequestId, List<CustomerOrder> customerOrders,
                        Quotation quotation);

        public void checkAllPaymentRequests()
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void checkPaymentRequest(CentralPayPaymentRequest request)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public CentralPayPaymentRequest getCentralPayPaymentRequestByQuotation(Quotation quotation);
}
