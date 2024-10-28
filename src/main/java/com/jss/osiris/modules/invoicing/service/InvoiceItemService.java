package com.jss.osiris.modules.invoicing.service;

import java.util.List;

import com.jss.osiris.modules.invoicing.model.InvoiceItem;

public interface InvoiceItemService {
    public List<InvoiceItem> getInvoiceItems();

    public InvoiceItem getInvoiceItem(Integer id);

    public InvoiceItem addOrUpdateInvoiceItem(InvoiceItem invoiceItem);

    public void deleteInvoiceItem(InvoiceItem invoiceItem);

    public void deleteDuplicateInvoiceItem();

    public void deleteDuplicateInvoiceItemOrigin();

    public InvoiceItem cloneInvoiceItem(InvoiceItem invoiceItem);
}
