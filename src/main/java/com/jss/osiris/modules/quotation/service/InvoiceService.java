package com.jss.osiris.modules.quotation.service;

import java.time.LocalDate;

import com.jss.osiris.modules.quotation.model.Invoice;
import com.jss.osiris.modules.tiers.model.ITiers;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

public interface InvoiceService {
    public Invoice getInvoice(Integer id);

    public Invoice addOrUpdateInvoice(Invoice invoice);

    /**
     * @param orderingCustomer Ordering customer of the invoice
     */
    public Invoice createInvoice(ITiers orderingCustomer) throws Exception;

    public Float getDiscountTotal(Invoice invoice);

    public Float getPreTaxPriceTotal(Invoice invoice);

    public Float getVatTotal(Invoice invoice);

    public Float getPriceTotal(Invoice invoice);

    public ITiers getCustomerOrder(Invoice invoice) throws Exception;

    public Invoice getInvoiceForCustomerOrder(Integer customerOrderId);

    public LocalDate getFirstBillingDateForTiers(Tiers tiers);

    public LocalDate getFirstBillingDateForResponsable(Responsable responsable);

}
