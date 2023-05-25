package com.jss.osiris.modules.invoicing.service;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.OwncloudGreffeInvoice;
import com.jss.osiris.modules.quotation.model.Provision;

public interface OwncloudGreffeDelegate {
    public void grabAllFiles() throws OsirisException, OsirisClientMessageException, OsirisValidationException;

    public Invoice generateInvoiceFromProvisionAndGreffeInvoice(OwncloudGreffeInvoice greffeInvoice,
            Provision provision) throws OsirisClientMessageException, OsirisException, OsirisValidationException;
}
