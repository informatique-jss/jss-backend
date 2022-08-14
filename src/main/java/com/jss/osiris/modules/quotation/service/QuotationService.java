package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.IQuotation;
import com.jss.osiris.modules.quotation.model.InvoiceItem;
import com.jss.osiris.modules.quotation.model.Quotation;

public interface QuotationService {
    public Quotation getQuotation(Integer id);

    public Quotation addOrUpdateQuotation(Quotation quotation);

    public List<InvoiceItem> getInvoiceItemsForQuotation(IQuotation quotation);
}
