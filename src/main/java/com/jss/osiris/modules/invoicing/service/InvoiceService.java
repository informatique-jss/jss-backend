package com.jss.osiris.modules.invoicing.service;

import java.time.LocalDate;
import java.util.List;

import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceSearch;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.tiers.model.ITiers;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

public interface InvoiceService {
    public List<Invoice> getAllInvoices();

    public Invoice getInvoice(Integer id);

    public Invoice addOrUpdateInvoice(Invoice invoice);

    /**
     * Generate an invoice for a ording order
     * Warning ! This method DOES NOT associate invoice items and DOES NOT compute
     * total price of the invoice (do it with setPriceTotal of same service)
     * Do not forget to do it afterward
     * 
     * @param orderingCustomer Ordering customer of the invoice
     * @param affaire          : affaire of the invoice, defined it only if there is
     *                         only one affaire in the invoice (it's to define
     *                         payer)
     */
    public Invoice createInvoice(CustomerOrder customerOrder, ITiers orderingCustomer) throws Exception;

    public Invoice getInvoiceForCustomerOrder(Integer customerOrderId);

    public LocalDate getFirstBillingDateForTiers(Tiers tiers);

    public LocalDate getFirstBillingDateForResponsable(Responsable responsable);

    public List<Invoice> searchInvoices(InvoiceSearch invoiceSearch) throws Exception;

    public void reindexInvoices();

}
