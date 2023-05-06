package com.jss.osiris.modules.quotation.repository;

import com.jss.osiris.libs.QueryCacheCrudRepository;

import com.jss.osiris.modules.quotation.model.CentralPayPaymentRequest;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Quotation;

public interface CentralPayPaymentRequestRepository
        extends QueryCacheCrudRepository<CentralPayPaymentRequest, Integer> {

    CentralPayPaymentRequest findByCustomerOrder(CustomerOrder customerOrder);

    CentralPayPaymentRequest findByQuotation(Quotation quotation);
}