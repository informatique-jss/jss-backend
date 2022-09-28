package com.jss.osiris.modules.invoicing.service;

import java.util.List;

import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.PaymentSearch;

public interface PaymentService {
    public List<Payment> getPayments();

    public Payment getPayment(Integer id);

    public Payment addOrUpdatePayment(Payment payment);

    public List<Payment> searchPayments(PaymentSearch payemntSearch) throws Exception;

    public void payementGrab() throws Exception;

    public List<Payment> getAdvisedPaymentForInvoice(Invoice invoice);

}
