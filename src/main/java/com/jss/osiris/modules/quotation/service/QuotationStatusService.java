package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.QuotationStatus;

public interface QuotationStatusService {
    public List<QuotationStatus> getQuotationStatus();

    public QuotationStatus getQuotationStatus(Integer id);

    public QuotationStatus addOrUpdateQuotationStatus(QuotationStatus quotationStatus);
}
