package com.jss.osiris.modules.invoicing.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.invoicing.model.InfogreffeInvoice;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.quotation.model.Provision;

public interface InfogreffeInvoiceService {
    public List<InfogreffeInvoice> getInfogreffeInvoices();

    public List<InfogreffeInvoice> getInfogreffeInvoicesByCustomerReference(String customerReference);

    public InfogreffeInvoice getInfogreffeInvoice(Integer id);

    public InfogreffeInvoice addOrUpdateInfogreffeInvoice(InfogreffeInvoice infogreffeInvoice);

    public Boolean importInfogreffeInvoices(String csv)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException;

    public Invoice generateInvoiceFromProvisionAndGreffeInvoice(InfogreffeInvoice greffeInvoice, Provision provision)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException;
}
