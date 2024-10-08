package com.jss.osiris.modules.osiris.invoicing.service;

import java.util.List;

import com.jss.osiris.modules.osiris.invoicing.model.InvoiceStatus;

public interface InvoiceStatusService {
    public List<InvoiceStatus> getInvoiceStatus();

    public InvoiceStatus getInvoiceStatus(Integer id);

    public InvoiceStatus addOrUpdateInvoiceStatus(InvoiceStatus invoiceStatus);

    public InvoiceStatus getInvoiceStatusByCode(String invoiceStatusPayedCode);

}
