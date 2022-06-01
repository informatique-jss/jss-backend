package com.jss.jssbackend.modules.quotation.service;

import java.util.List;

import com.jss.jssbackend.modules.quotation.model.QuotationLabelType;

public interface QuotationLabelTypeService {
    public List<QuotationLabelType> getQuotationLabelTypes();

    public QuotationLabelType getQuotationLabelType(Integer id);
}
