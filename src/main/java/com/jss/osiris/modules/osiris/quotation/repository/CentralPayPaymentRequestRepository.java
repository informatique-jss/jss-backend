package com.jss.osiris.modules.osiris.quotation.repository;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.CentralPayPaymentRequest;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;

public interface CentralPayPaymentRequestRepository
        extends QueryCacheCrudRepository<CentralPayPaymentRequest, Integer> {

    CentralPayPaymentRequest findByQuotation(Quotation quotation);
}