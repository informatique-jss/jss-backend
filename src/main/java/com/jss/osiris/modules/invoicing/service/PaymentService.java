package com.jss.osiris.modules.invoicing.service;

import java.io.InputStream;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.PaymentSearch;
import com.jss.osiris.modules.invoicing.model.PaymentSearchResult;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.tiers.model.ITiers;

public interface PaymentService {
        public Payment getPayment(Integer id);

        public Payment addOrUpdatePayment(Payment payment);

        public List<PaymentSearchResult> searchPayments(PaymentSearch payemntSearch);

        public void payementGrab() throws OsirisException, OsirisClientMessageException;

        public List<Payment> getAdvisedPaymentForInvoice(Invoice invoice);

        public void manualMatchPaymentInvoicesAndCustomerOrders(Payment payment,
                        List<Invoice> correspondingInvoices, List<CustomerOrder> correspondingCustomerOrder,
                        Affaire affaireRefund,
                        ITiers tiersRefund, List<Float> byPassAmount)
                        throws OsirisException, OsirisClientMessageException;

        public void setExternallyAssociated(Payment payment);

        public void unsetExternallyAssociated(Payment payment);

        public List<Attachment> uploadOfxFile(InputStream file) throws OsirisException, OsirisClientMessageException;

}
