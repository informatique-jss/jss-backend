package com.jss.osiris.modules.invoicing.service;

import java.io.InputStream;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.PaymentSearch;
import com.jss.osiris.modules.invoicing.model.PaymentSearchResult;
import com.jss.osiris.modules.invoicing.model.Refund;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.IGenericTiers;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.BankTransfert;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.centralPay.CentralPayPaymentRequest;
import com.jss.osiris.modules.tiers.model.ITiers;
import com.jss.osiris.modules.tiers.model.Tiers;

public interface PaymentService {
        public Payment getPayment(Integer id);

        public Payment addOrUpdatePayment(Payment payment);

        public void reindexPayments();

        public List<PaymentSearchResult> searchPayments(PaymentSearch payemntSearch);

        public void paymentGrab() throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void automatchPaymentFromUser(Payment payment)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void automatchPayment(Payment payment)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public List<Payment> getAdvisedPaymentForInvoice(Invoice invoice);

        public List<Payment> getAdvisedPaymentForCustomerOrder(CustomerOrder customerOrder);

        public void manualMatchPaymentInvoicesAndCustomerOrders(Payment payment,
                        List<Invoice> correspondingInvoices, List<CustomerOrder> correspondingCustomerOrder,
                        Affaire affaireRefund, Tiers tiersRefund,
                        Confrere confrereRefund, ITiers tiersOrder, List<Float> byPassAmount)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public List<Attachment> uploadOfxFile(InputStream file)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void addCashPaymentForCustomerInvoice(Payment cashPayment, Invoice invoice)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void addInboundCheckPayment(Payment cashPayment)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void addCashPaymentForCustomerOrder(Payment cashPayment, CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void movePaymentFromInvoiceToCustomerOrder(Payment payment, Invoice invoice,
                        CustomerOrder customerOrder)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException,
                        OsirisDuplicateException;

        public Payment addOutboundPaymentForProvision(Payment payment, Provision provision)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void movePaymentFromCustomerOrderToInvoice(Payment payment, CustomerOrder customerOrder,
                        Invoice invoice)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public Payment generateNewAccountPayment(Float paymentAmount, AccountingAccount sourceDepositAccountingAccount,
                        AccountingAccount targetAccountingAccount,
                        String label)
                        throws OsirisException;

        public Payment generateNewDirectDebitPayment(Float paymentAmount, String label) throws OsirisException;

        public void refundPayment(Payment payment, Tiers tiers, Affaire affaire)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public Payment generateDepositOnCustomerOrderForCbPayment(CustomerOrder customerOrder,
                        CentralPayPaymentRequest centralPayPaymentRequest)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void generatePaymentOnInvoiceForCbPayment(Invoice invoice,
                        CentralPayPaymentRequest centralPayPaymentRequest)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void unassociateInboundPaymentFromInvoice(Payment payment, Invoice invoice)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void cancelAppoint(Payment payment) throws OsirisException, OsirisValidationException;

        public Payment generateNewRefundPayment(Refund refund, Float paymentAmount, ITiers tiersToRefund,
                        Payment paymentToRefund)
                        throws OsirisException;

        public Payment generateNewBankTransfertPayment(BankTransfert bankTransfert, Float paymentAmount,
                        IGenericTiers tiersToPay)
                        throws OsirisException;

        public Payment getOriginalPaymentOfPayment(Payment payment);

        public void putPaymentInAccount(Payment payment, CompetentAuthority competentAuthority)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;
}
