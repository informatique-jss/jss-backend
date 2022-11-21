package com.jss.osiris.modules.invoicing.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.PaymentSearch;
import com.jss.osiris.modules.invoicing.model.PaymentSearchResult;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.tiers.model.ITiers;

public interface PaymentService {
        public List<Payment> getPayments();

        public Payment getPayment(Integer id);

        public Payment addOrUpdatePayment(Payment payment);

        public List<PaymentSearchResult> searchPayments(PaymentSearch payemntSearch);

        public void payementGrab() throws OsirisException;

        public List<Payment> getAdvisedPaymentForInvoice(Invoice invoice);

        public void manualMatchPaymentInvoicesAndGeneratePaymentAccountingRecords(Payment payment,
                        List<Invoice> correspondingInvoices, List<CustomerOrder> correspondingCustomerOrder,
                        Affaire affaireRefund,
                        ITiers tiersRefund, List<Float> byPassAmount)
                        throws OsirisException;

        public void unlinkPaymentFromInvoiceCustomerOrder(Payment payment);

        public void setExternallyAssociated(Payment payment);

        public void unsetExternallyAssociated(Payment payment);

}
