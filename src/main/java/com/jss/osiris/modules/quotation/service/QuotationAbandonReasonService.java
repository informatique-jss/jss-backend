package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.QuotationAbandonReason;

public interface QuotationAbandonReasonService {

    public List<QuotationAbandonReason> getQuotationAbandonReasons();

    public QuotationAbandonReason getAbandonReason(Integer id);

    public QuotationAbandonReason addOrUpdateQutationAbandonReason(QuotationAbandonReason abandonReason);
}
