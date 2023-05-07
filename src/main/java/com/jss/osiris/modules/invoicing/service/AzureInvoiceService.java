package com.jss.osiris.modules.invoicing.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.invoicing.model.AzureInvoice;

public interface AzureInvoiceService {
    public AzureInvoice getAzureInvoice(Integer id);

    public List<AzureInvoice> getAzureInvoices(Boolean displayOnlyToCheck);

    public AzureInvoice addOrUpdateAzureInvoice(AzureInvoice azureInvoice) throws OsirisException;

    public AzureInvoice getAzureInvoiceByInvoiceId(String invoiceId);

    public void checkInvoiceToAnalyse() throws OsirisException, OsirisClientMessageException;
}
