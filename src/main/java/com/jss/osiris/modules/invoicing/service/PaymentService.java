package com.jss.osiris.modules.invoicing.service;

import java.io.InputStream;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.PaymentSearch;
import com.jss.osiris.modules.invoicing.model.PaymentSearchResult;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.tiers.model.ITiers;
import com.jss.osiris.modules.tiers.model.Tiers;

public interface PaymentService {
        public Payment getPayment(Integer id);

        public Payment addOrUpdatePayment(Payment payment);

        public List<PaymentSearchResult> searchPayments(PaymentSearch payemntSearch);

        public void paymentGrab() throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public void automatchPaymentInvoicesAndGeneratePaymentAccountingRecords(Payment payment)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public List<Payment> getAdvisedPaymentForInvoice(Invoice invoice);

        public List<Payment> getAdvisedPaymentForCustomerOrder(CustomerOrder customerOrder);

        public void manualMatchPaymentInvoicesAndCustomerOrders(Payment payment,
                        List<Invoice> correspondingInvoices, List<CustomerOrder> correspondingCustomerOrder,
                        Affaire affaireRefund,
                        ITiers tiersRefund, List<Float> byPassAmount)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public List<Attachment> uploadOfxFile(InputStream file, Integer targetAccountingAccountId)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public void addCashPaymentForInvoice(Payment cashPayment, Invoice invoice) throws OsirisException;

        public void addCheckPayment(Payment cashPayment)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public void addCashPaymentForCustomerOrder(Payment cashPayment, CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public void movePaymentFromInvoiceToCustomerOrder(Payment payment, Invoice invoice,
                        CustomerOrder customerOrder);

        public void movePaymentFromCustomerOrderToInvoice(Payment payment, CustomerOrder customerOrder,
                        Invoice invoice);

        public Payment generateNewAccountPayment(Float paymentAmount, AccountingAccount targetAccountingAccount)
                        throws OsirisException;

        public void refundPayment(Payment payment, Tiers tiers, Affaire affaire)
                        throws OsirisException, OsirisClientMessageException;

}
