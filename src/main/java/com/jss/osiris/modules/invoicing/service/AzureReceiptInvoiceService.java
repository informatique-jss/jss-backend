package com.jss.osiris.modules.invoicing.service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.invoicing.model.AzureReceiptInvoice;
import com.jss.osiris.modules.invoicing.model.AzureReceiptInvoiceStatus;

public interface AzureReceiptInvoiceService {
    public AzureReceiptInvoice addOrUpdateAzureReceiptInvoice(AzureReceiptInvoice azureReceipt);

    public AzureReceiptInvoice markAsReconciliated(AzureReceiptInvoice azureReceiptInvoice, boolean isReconciliated);

    public AzureReceiptInvoice getAzureReceiptInvoice(Integer id);

    public AzureReceiptInvoiceStatus getAzureReceiptInvoiceStatus(AzureReceiptInvoice azureReceiptInvoice)
            throws OsirisException;
}
