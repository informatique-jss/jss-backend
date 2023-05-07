package com.jss.osiris.libs.azure;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.invoicing.model.AzureInvoice;
import com.jss.osiris.modules.miscellaneous.model.Attachment;

public interface FormRecognizerService {
    public AzureInvoice recongnizeInvoice(Attachment attachment) throws OsirisException;
}
