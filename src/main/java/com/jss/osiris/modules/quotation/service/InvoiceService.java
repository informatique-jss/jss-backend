package com.jss.osiris.modules.quotation.service;

import com.jss.osiris.modules.quotation.model.Invoice;

public interface InvoiceService {
    public Invoice getInvoice(Integer id);

    public Invoice addOrUpdateInvoice(Invoice invoice);

    public Invoice createInvoice();

    public Float getDiscountTotal(Invoice invoice);

    public Float getPreTaxPriceTotal(Invoice invoice);

    public Float getVatTotal(Invoice invoice);

    public Float getPriceTotal(Invoice invoice);
}
