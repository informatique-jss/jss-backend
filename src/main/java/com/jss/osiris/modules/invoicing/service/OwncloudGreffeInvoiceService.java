package com.jss.osiris.modules.invoicing.service;

import java.util.List;

import com.jss.osiris.modules.invoicing.model.OwncloudGreffeInvoice;
import com.jss.osiris.modules.quotation.model.CustomerOrder;

public interface OwncloudGreffeInvoiceService {
    public OwncloudGreffeInvoice getOwncloudGreffeInvoice(Integer id);

    public OwncloudGreffeInvoice addOrUpdateOwncloudGreffeInvoice(OwncloudGreffeInvoice owncloudGreffeInvoice);

    public List<OwncloudGreffeInvoice> getCorrespondingGreffeInvoiceForCustomerOrder(CustomerOrder customerOrder);

    public List<OwncloudGreffeInvoice> getOwncloudGreffeInvoiceByNumero(String numero);
}
