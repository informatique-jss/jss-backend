package com.jss.jssbackend.modules.quotation.service;

import java.util.List;

import com.jss.jssbackend.modules.quotation.model.QuotationStatus;

public interface QuotationStatusService {
    public List<QuotationStatus> getQuotationStatus();

    public QuotationStatus getQuotationStatus(Integer id);
}
