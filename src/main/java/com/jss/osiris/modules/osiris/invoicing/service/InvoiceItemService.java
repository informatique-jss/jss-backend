package com.jss.osiris.modules.osiris.invoicing.service;

import java.math.BigDecimal;
import java.util.List;

import com.jss.osiris.modules.osiris.invoicing.model.InvoiceItem;

public interface InvoiceItemService {
    public List<InvoiceItem> getInvoiceItems();

    public InvoiceItem getInvoiceItem(Integer id);

    public InvoiceItem addOrUpdateInvoiceItem(InvoiceItem invoiceItem);

    public void deleteInvoiceItem(InvoiceItem invoiceItem);

    public InvoiceItem cloneInvoiceItem(InvoiceItem invoiceItem);

    public InvoiceItem updateInvoiceItemFromInvoice(InvoiceItem invoiceItem, BigDecimal amount);
}
