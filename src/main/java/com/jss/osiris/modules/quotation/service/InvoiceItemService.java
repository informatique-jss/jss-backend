package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.InvoiceItem;

public interface InvoiceItemService {
    public List<InvoiceItem> getInvoiceItems();

    public InvoiceItem getInvoiceItem(Integer id);
	
	 public InvoiceItem addOrUpdateInvoiceItem(InvoiceItem invoiceItem);
}
