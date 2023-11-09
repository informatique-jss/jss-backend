package com.jss.osiris.modules.quotation.service;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.quotation.model.CentralPayPaymentRequest;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.repository.CentralPayPaymentRequestRepository;

@Service
public class CentralPayPaymentRequestServiceImpl implements CentralPayPaymentRequestService {

    @Autowired
    CentralPayPaymentRequestRepository centralPayPaymentRequestRepository;

    @Autowired
    QuotationService quotationService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Override
    public List<CentralPayPaymentRequest> getCentralPayPaymentRequests() {
        return IterableUtils.toList(centralPayPaymentRequestRepository.findAll());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CentralPayPaymentRequest addOrUpdateCentralPayPaymentRequest(
            CentralPayPaymentRequest centralPayPaymentRequest) {
        return centralPayPaymentRequestRepository.save(centralPayPaymentRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCentralPayPaymentRequest(CentralPayPaymentRequest centralPayPaymentRequest) {
        centralPayPaymentRequestRepository.delete(centralPayPaymentRequest);
    }

    @Override
    public void declareNewCentralPayPaymentRequest(String paymentRequestId, CustomerOrder customerOrder,
            Quotation quotation) {
        CentralPayPaymentRequest request = new CentralPayPaymentRequest();
        request.setCustomerOrder(customerOrder);
        request.setPaymentRequestId(paymentRequestId);
        request.setQuotation(quotation);
        addOrUpdateCentralPayPaymentRequest(request);
    }

    @Override
    public CentralPayPaymentRequest getCentralPayPaymentRequestByCustomerOrder(CustomerOrder customerOrder) {
        return centralPayPaymentRequestRepository.findByCustomerOrder(customerOrder);
    }

    @Override
    public CentralPayPaymentRequest getCentralPayPaymentRequestByQuotation(Quotation quotation) {
        return centralPayPaymentRequestRepository.findByQuotation(quotation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkAllPaymentRequests()
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        List<CentralPayPaymentRequest> requests = getCentralPayPaymentRequests();
        if (requests != null && requests.size() > 0) {
            for (CentralPayPaymentRequest request : requests) {
                if (request.getCustomerOrder() != null) {
                    if (customerOrderService.validateCardPaymentLinkForCustomerOrder(request.getCustomerOrder(),
                            request))
                        deleteCentralPayPaymentRequest(request);
                } else if (request.getQuotation() != null) {
                    if (quotationService.validateCardPaymentLinkForQuotationDeposit(request.getQuotation(),
                            request))
                        deleteCentralPayPaymentRequest(request);
                }
            }
        }
    }
}
