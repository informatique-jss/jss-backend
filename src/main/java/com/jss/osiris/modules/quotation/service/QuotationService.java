package com.jss.osiris.modules.quotation.service;

import com.jss.osiris.modules.quotation.model.Quotation;

public interface QuotationService {
    public Quotation getQuotation(Integer id);

    public Quotation addOrUpdateQuotation(Quotation quotation);
}
