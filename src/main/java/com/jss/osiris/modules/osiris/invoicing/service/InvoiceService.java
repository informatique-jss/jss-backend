package com.jss.osiris.modules.osiris.invoicing.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceSearch;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceSearchResult;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceStatus;
import com.jss.osiris.modules.osiris.miscellaneous.model.Provider;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.tiers.model.BillingLabelType;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public interface InvoiceService {
        public List<Invoice> getAllInvoices();

        public Invoice getInvoice(Integer id);

        public Invoice generateInvoicePdf(Invoice invoice, CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public List<Invoice> findByProviderAndManualDocumentNumber(Provider provider,
                        String manualDocumentNumber);

        public List<Invoice> findByProviderAndManualDocumentNumberContains(
                        Provider provider, String manualDocumentNumber);

        public Invoice addOrUpdateInvoice(Invoice invoice) throws OsirisException;

        public List<InvoiceSearchResult> getInvoiceForCustomerOrder(Integer customerOrderId) throws OsirisException;

        public List<InvoiceSearchResult> searchInvoices(InvoiceSearch invoiceSearch) throws OsirisException;

        public void reindexInvoices() throws OsirisException;

        public Invoice addOrUpdateInvoiceFromUser(Invoice invoice)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void sendRemindersForInvoices()
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public BigDecimal getRemainingAmountToPayForInvoice(Invoice invoice) throws OsirisException;

        public List<InvoiceSearchResult> getProviderInvoiceForCustomerOrder(Integer customerOrderId)
                        throws OsirisException;

        public CustomerOrder getCustomerOrderByIdInvoice(Integer idInvoice);

        public Invoice generateProviderInvoiceCreditNote(Invoice newInvoice, Integer idOriginInvoiceForCreditNote)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public Invoice searchInvoicesByIdDirectDebitTransfert(Integer idToFind);

        public Invoice cancelInvoice(Invoice invoice)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void sendRemindersForInvoices(BillingLabelType billingLabelType)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public void remindInvoice(Invoice invoice)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public List<Invoice> searchInvoices(List<InvoiceStatus> invoiceStatus, List<Responsable> responsables);

        public void deleteDuplicateInvoices();

        public List<Invoice> getInvoicesForResponsablesAndDates(List<InvoiceStatus> invoiceStatus,
                        List<Responsable> responsables, LocalDate startDate, LocalDate endDate);

}
