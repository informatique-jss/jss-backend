package com.jss.jssbackend.modules.quotation.service;

import com.jss.jssbackend.modules.quotation.model.Quotation;

public interface QuotationService {
    public Quotation getQuotation(Integer id);

    public Quotation addOrUpdateQuotation(Quotation quotation);
}
