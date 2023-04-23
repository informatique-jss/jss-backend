package com.jss.osiris.modules.quotation.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.quotation.model.CentralPayPaymentRequest;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Quotation;

public interface CentralPayPaymentRequestRepository extends CrudRepository<CentralPayPaymentRequest, Integer> {

    CentralPayPaymentRequest findByCustomerOrder(CustomerOrder customerOrder);

    CentralPayPaymentRequest findByQuotation(Quotation quotation);
}