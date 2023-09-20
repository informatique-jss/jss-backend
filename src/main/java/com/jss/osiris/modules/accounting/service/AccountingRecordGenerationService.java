package com.jss.osiris.modules.accounting.service;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.Refund;
import com.jss.osiris.modules.quotation.model.CustomerOrder;

public interface AccountingRecordGenerationService {
        public void generateAccountingRecordsOnInvoiceEmission(Invoice invoice)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void generateAccountingRecordsOnInvoiceEmissionCancellation(Invoice invoice, Invoice creditNote)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void generateAccountingRecordsOnCreditNoteReception(Invoice invoice, Invoice originalInvoice)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void generateAccountingRecordsOnInvoiceReception(Invoice invoice)
                        throws OsirisException, OsirisValidationException;

        public void generateAccountingRecordsOnInvoiceReceptionCancellation(Invoice invoice)
                        throws OsirisException, OsirisValidationException;

        public void generateAccountingRecordOnIncomingPaymentCreation(Payment payment, boolean isOdJournal)
                        throws OsirisException, OsirisValidationException;

        public void generateAccountingRecordOnOutgoingPaymentCreation(Payment payment, boolean isForRefund)
                        throws OsirisException, OsirisValidationException;

        public void generateAccountingRecordOnPaymentCancellation(Payment payment)
                        throws OsirisException, OsirisValidationException;

        public void generateAccountingRecordsForSaleOnInvoicePayment(Invoice invoice, Payment payment)
                        throws OsirisException, OsirisValidationException;

        public void generateAccountingRecordsForSaleOnCustomerOrderDeposit(CustomerOrder customerOrder, Payment payment)
                        throws OsirisException, OsirisValidationException;

        public void generateAccountingRecordsForProviderInvoiceRefund(Invoice invoice, Payment payment)
                        throws OsirisException, OsirisValidationException;

        public void generateAccountingRecordsForPurschaseOnInvoicePayment(Invoice invoice, Payment payment)
                        throws OsirisException, OsirisValidationException;

        public void generateAccountingRecordsForRefundGeneration(Refund refund)
                        throws OsirisException, OsirisValidationException;

        public void generateAccountingRecordsForRefundExport(Refund refund)
                        throws OsirisException, OsirisValidationException;

        public void generateAccountingRecordOnPaymentOnDepositCompetentAuthorityAccount(Payment payment)
                        throws OsirisException, OsirisValidationException;

}
