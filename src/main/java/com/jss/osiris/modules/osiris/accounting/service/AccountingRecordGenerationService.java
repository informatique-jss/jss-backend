package com.jss.osiris.modules.osiris.accounting.service;

import java.time.LocalDateTime;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.accounting.model.AccountingRecord;
import com.jss.osiris.modules.osiris.accounting.model.SageRecord;
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

        public void generateAccountingRecordOnIncomingPaymentCreation(Payment payment, boolean isOdJournal,
                        boolean isOriginalPayment)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void generateAccountingRecordOnOutgoingPaymentCreation(Payment payment, Boolean isOdJournal,
                        boolean isOriginalPayment)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void generateAccountingRecordOnPaymentCancellation(Payment payment)
                        throws OsirisException, OsirisValidationException;

        public void generateAccountingRecordsForSaleOnInvoicePayment(Invoice invoice, Payment payment,
                        boolean isOriginalPayment)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void generateAccountingRecordsForSaleOnCustomerOrderDeposit(CustomerOrder customerOrder, Payment payment,
                        boolean isOriginalPayment)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void generateAccountingRecordsForProviderInvoiceRefund(Invoice invoice, Payment payment,
                        boolean isOriginalPayment)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void generateAccountingRecordsForPurschaseOnInvoicePayment(Invoice invoice, Payment payment,
                        boolean isOriginalPayment)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void generateAccountingRecordsForRefundGeneration(Refund refund)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void generateAccountingRecordsForRefundExport(Refund refund)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public LocalDateTime getPaymentDateForAccounting(Payment payment, boolean isOriginalPayment)
                        throws OsirisException;

        public void checkInvoiceForLettrage(Invoice invoice) throws OsirisException;

        public void generateAccountingRecordForSageRecord(List<SageRecord> sageRecords) throws OsirisException;

        public void generateAccountingRecordOnIncomingPaymentOnAccountingAccount(Payment payment)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void generateAccountingRecordOnOutgoingPaymentOnAccountingAccount(Payment payment)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;

        public void counterPartExistingManualRecords(List<AccountingRecord> records, LocalDateTime counterPartDateTime)
                        throws OsirisException;
}
