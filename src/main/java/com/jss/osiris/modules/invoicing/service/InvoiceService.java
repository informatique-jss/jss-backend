package com.jss.osiris.modules.invoicing.service;

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

public interface InvoiceService {
        public List<Invoice> getAllInvoices();

        public Invoice getInvoice(Integer id);

        public List<Invoice> findByCompetentAuthorityAndManualDocumentNumber(CompetentAuthority competentAuthority,
                        String manualDocumentNumber);

        public List<Invoice> findByCompetentAuthorityAndManualDocumentNumberContains(
                        CompetentAuthority competentAuthority, String manualDocumentNumber);

        public Invoice addOrUpdateInvoice(Invoice invoice);

        public List<InvoiceSearchResult> getInvoiceForCustomerOrder(Integer customerOrderId) throws OsirisException;

        public List<InvoiceSearchResult> searchInvoices(InvoiceSearch invoiceSearch) throws OsirisException;

        public void reindexInvoices();

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

}
