package com.jss.osiris.modules.osiris.invoicing.facade;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.invoicing.model.Payment;
import com.jss.osiris.modules.osiris.invoicing.model.PaymentDto;
import com.jss.osiris.modules.osiris.invoicing.model.PaymentSearch;
import com.jss.osiris.modules.osiris.invoicing.service.PaymentService;

@Service
public class InvoicingFacade {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private InvoicingDtoHelper invoicingDtoHelper;

    public List<PaymentDto> searchForPayments(PaymentSearch paymentSearch) {
        List<Payment> payments = paymentService.searchForPayments(paymentSearch);
        return invoicingDtoHelper.mapPayments(payments);
    }
}
