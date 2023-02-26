package com.jss.osiris.modules.invoicing.service;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.PaymentSearch;
import com.jss.osiris.modules.invoicing.model.PaymentSearchResult;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Debour;
import com.jss.osiris.modules.tiers.model.ITiers;

public interface PaymentService {
        public Payment getPayment(Integer id);

        public Payment addOrUpdatePayment(Payment payment);

        public List<PaymentSearchResult> searchPayments(PaymentSearch payemntSearch);

        public void payementGrab() throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public List<Payment> getAdvisedPaymentForInvoice(Invoice invoice);

        public List<Payment> getAdvisedPaymentForCustomerOrder(CustomerOrder customerOrder);

        public void manualMatchPaymentInvoicesAndCustomerOrders(Payment payment,
                        List<Invoice> correspondingInvoices, List<CustomerOrder> correspondingCustomerOrder,
                        Affaire affaireRefund,
                        ITiers tiersRefund, List<Float> byPassAmount)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public void setExternallyAssociated(Payment payment);

        public void unsetExternallyAssociated(Payment payment)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public List<Attachment> uploadOfxFile(InputStream file)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public Float associateOutboundPaymentAndDebourFromUser(Payment payment, List<Debour> debours)
                        throws OsirisException;

        public Payment generateNewPaymentFromDebour(Debour debour) throws OsirisException;

        public Float associateOutboundPaymentAndInvoice(Payment payment, Invoice correspondingInvoice,
                        MutableBoolean generateWaitingAccountAccountingRecords, List<Float> byPassAmount)
                        throws OsirisException;

        public void addCashPaymentForInvoice(Payment cashPayment, Invoice invoice) throws OsirisException;

        public void addCashPaymentForCustomerOrder(Payment cashPayment, CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

}
