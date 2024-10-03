package com.jss.osiris.modules.osiris.invoicing.service;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.invoicing.model.AzureReceiptInvoice;
import com.jss.osiris.modules.osiris.invoicing.model.AzureReceiptInvoiceStatus;

public interface AzureReceiptInvoiceService {
    public AzureReceiptInvoice addOrUpdateAzureReceiptInvoice(AzureReceiptInvoice azureReceipt);

    public AzureReceiptInvoice markAsReconciliated(AzureReceiptInvoice azureReceiptInvoice, boolean isReconciliated);

    public AzureReceiptInvoice getAzureReceiptInvoice(Integer id);

    public AzureReceiptInvoiceStatus getAzureReceiptInvoiceStatus(AzureReceiptInvoice azureReceiptInvoice)
            throws OsirisException, OsirisClientMessageException;
}
