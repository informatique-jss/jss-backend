package com.jss.osiris.modules.osiris.accounting.service;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.Payment;
import com.jss.osiris.modules.osiris.invoicing.model.Refund;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;

public interface AccountingRecordGenerationService {
        public void generateAccountingRecordsOnInvoiceEmission(Invoice invoice)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void generateAccountingRecordsOnInvoiceEmissionCancellation(Invoice invoice, Invoice creditNote)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void generateAccountingRecordsOnCreditNoteReception(Invoice invoice, Invoice originalInvoice)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void generateAccountingRecordsOnInvoiceReception(Invoice invoice)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void generateAccountingRecordsOnInvoiceReceptionCancellation(Invoice invoice)
                        throws OsirisException, OsirisValidationException;

        public void generateAccountingRecordOnIncomingPaymentCreation(Payment payment, boolean isOdJournal)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void generateAccountingRecordOnOutgoingPaymentCreation(Payment payment, Boolean isOdJournal)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void generateAccountingRecordOnPaymentCancellation(Payment payment)
                        throws OsirisException, OsirisValidationException;

        public void generateAccountingRecordsForSaleOnInvoicePayment(Invoice invoice, Payment payment)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void generateAccountingRecordsForSaleOnCustomerOrderDeposit(CustomerOrder customerOrder, Payment payment)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void generateAccountingRecordsForProviderInvoiceRefund(Invoice invoice, Payment payment)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void generateAccountingRecordsForPurschaseOnInvoicePayment(Invoice invoice, Payment payment)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void generateAccountingRecordsForRefundGeneration(Refund refund)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void generateAccountingRecordsForRefundExport(Refund refund)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void generateAccountingRecordOnIncomingPaymentOnDepositCompetentAuthorityAccount(Payment payment)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void generateAccountingRecordOnOutgoingPaymentOnDepositCompetentAuthorityAccount(Payment payment)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

}
