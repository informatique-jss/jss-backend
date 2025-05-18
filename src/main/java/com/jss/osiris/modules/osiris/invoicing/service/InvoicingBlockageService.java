package com.jss.osiris.modules.osiris.invoicing.service;

import java.util.List;

import com.jss.osiris.modules.osiris.invoicing.model.InvoicingBlockage;

public interface InvoicingBlockageService {
    public List<InvoicingBlockage> getInvoicingBlockages();

    public InvoicingBlockage getInvoicingBlockage(Integer id);

    public InvoicingBlockage addOrUpdateInvoicingBlockage(InvoicingBlockage invoicingBlockage);
}
