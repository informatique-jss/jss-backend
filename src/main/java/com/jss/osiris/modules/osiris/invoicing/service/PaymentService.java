package com.jss.osiris.modules.osiris.invoicing.service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.myjss.quotation.controller.model.MyJssImage;
import com.jss.osiris.modules.osiris.accounting.model.AccountingAccount;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.OutboundCheckSearch;
import com.jss.osiris.modules.osiris.invoicing.model.OutboundCheckSearchResult;
import com.jss.osiris.modules.osiris.invoicing.model.Payment;
import com.jss.osiris.modules.osiris.invoicing.model.PaymentSearch;
import com.jss.osiris.modules.osiris.invoicing.model.PaymentSearchResult;
import com.jss.osiris.modules.osiris.invoicing.model.Refund;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.Provider;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.BankTransfert;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.DirectDebitTransfert;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.centralPay.CentralPayPaymentRequest;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;

public interface PaymentService {
        public Payment getPayment(Integer id);

        public List<OutboundCheckSearchResult> searchOutboundChecks(OutboundCheckSearch outboundCheckSearch);

        public Payment addOrUpdatePayment(Payment payment) throws OsirisException;

        public void reindexPayments() throws OsirisException;

        public List<PaymentSearchResult> searchPayments(PaymentSearch payemntSearch);

        public List<Payment> getMatchingOfxPayments(PaymentSearch paymentSearch);

        public void paymentGrab() throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void automatchPayment(Payment payment)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public List<Payment> getAdvisedPaymentForInvoice(Invoice invoice);

        public List<Payment> getAdvisedPaymentForCustomerOrder(CustomerOrder customerOrder) throws OsirisException;

        public void manualMatchPaymentInvoicesAndCustomerOrders(Payment payment,
                        List<Invoice> correspondingInvoices, List<CustomerOrder> correspondingCustomerOrder,
                        Affaire affaireRefund, Tiers tiersRefund, Responsable responsable,
                        List<BigDecimal> byPassAmount)
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

        public Payment movePaymentToWaitingAccount(Payment payment)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public Payment addOutboundPaymentForProvision(Payment payment, Provision provision)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void movePaymentFromCustomerOrderToInvoice(Payment payment, CustomerOrder customerOrder,
                        Invoice invoice)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public Payment generateNewAccountPayment(BigDecimal paymentAmount,
                        AccountingAccount sourceDepositAccountingAccount,
                        AccountingAccount targetAccountingAccount,
                        String label)
                        throws OsirisException;

        public Payment generateNewDirectDebitPayment(BigDecimal paymentAmount, String label,
                        DirectDebitTransfert directDebitTransfert) throws OsirisException;

        public void refundPayment(Payment payment, Tiers tiers, Affaire affaire)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public Payment generateDepositOnCustomerOrderForCbPayment(List<CustomerOrder> customerOrders,
                        CentralPayPaymentRequest centralPayPaymentRequest)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void generatePaymentOnInvoiceForCbPayment(List<Invoice> invoices,
                        CentralPayPaymentRequest centralPayPaymentRequest)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void unassociateInboundPaymentFromInvoice(Payment payment, Invoice invoice)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void cancelAppoint(Payment payment) throws OsirisException, OsirisValidationException;

        public Payment generateNewRefundPayment(Refund refund, BigDecimal paymentAmount, Tiers tiersToRefund,
                        Payment paymentToRefund)
                        throws OsirisException;

        public Payment generateNewBankTransfertPayment(BankTransfert bankTransfert, BigDecimal paymentAmount,
                        Provider tiersToPay, Responsable responsableToPay)
                        throws OsirisException;

        public Payment getOriginalPaymentOfPayment(Payment payment);

        public void putPaymentInAccount(Payment payment, AccountingAccount accountingAccount)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public Payment cutPayment(Payment payment, BigDecimal amount)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public MyJssImage downloadQrCodeForOrderPayment(CustomerOrder customerOrder, String mail)
                        throws OsirisException;

        public MyJssImage downloadQrCodeForQuotationPayment(Quotation quotation, String mail) throws OsirisException;

        public List<Payment> getPaymentForCustomerOrder(CustomerOrder customerOrder);

        List<Payment> getPaymentsForResponsable(Responsable responsable);

}
