package com.jss.osiris.modules.osiris.invoicing.service;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.invoicing.model.AzureReceipt;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;

public interface AzureReceiptService {
    public AzureReceipt getAzureReceipt(Integer id);

    public AzureReceipt getAzureReceiptFromUser(Integer id) throws OsirisException;

    public AzureReceipt addOrUpdateAzureReceipt(AzureReceipt azureReceipt);

    public void analyseReceipt(Attachment attachment) throws OsirisException, OsirisClientMessageException;

    public AzureReceipt checkAllInvoicesReconciliated(AzureReceipt azureReceipt);

}
