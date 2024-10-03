package com.jss.osiris.libs.azure;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.invoicing.model.AzureInvoice;
import com.jss.osiris.modules.osiris.invoicing.model.AzureReceipt;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;

public interface FormRecognizerService {
    public AzureInvoice recongnizeInvoice(Attachment attachment) throws OsirisException;

    public AzureInvoice checkInvoiceAmountConfidence(AzureInvoice azureInvoice);

    public AzureReceipt recongnizeRecipts(Attachment attachment) throws OsirisException;
}
