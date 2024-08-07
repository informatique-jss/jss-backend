package com.jss.osiris.modules.invoicing.service;

import java.time.LocalDate;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceSearch;
import com.jss.osiris.modules.invoicing.model.InvoiceSearchResult;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.tiers.model.BillingLabelType;

public interface InvoiceService {
        public List<Invoice> getAllInvoices();

        public Invoice getInvoice(Integer id);

        public Invoice generateInvoicePdf(Invoice invoice, CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public List<Invoice> findByCompetentAuthorityAndManualDocumentNumber(CompetentAuthority competentAuthority,
                        String manualDocumentNumber);

        public List<Invoice> findByCompetentAuthorityAndManualDocumentNumberContains(
                        CompetentAuthority competentAuthority, String manualDocumentNumber);

        public Invoice addOrUpdateInvoice(Invoice invoice) throws OsirisException;

        public List<InvoiceSearchResult> getInvoiceForCustomerOrder(Integer customerOrderId) throws OsirisException;

        public List<InvoiceSearchResult> searchInvoices(InvoiceSearch invoiceSearch) throws OsirisException;

        public void reindexInvoices() throws OsirisException;

        public Invoice addOrUpdateInvoiceFromUser(Invoice invoice)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void sendRemindersForInvoices()
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public Float getRemainingAmountToPayForInvoice(Invoice invoice) throws OsirisException;

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

        public void sendRemindersForInvoices(LocalDate startDate, LocalDate endDate, BillingLabelType billingLabelType)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public void remindInvoice(Invoice invoice)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

}
