package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.quotation.model.QuotationStatus;

public interface QuotationStatusService {
    public List<QuotationStatus> getQuotationStatus();

    public QuotationStatus getQuotationStatus(Integer id);

    public QuotationStatus addOrUpdateQuotationStatus(QuotationStatus quotationStatus);

    public QuotationStatus getQuotationStatusByCode(String code);

    public void updateStatusReferential() throws OsirisException;

}
