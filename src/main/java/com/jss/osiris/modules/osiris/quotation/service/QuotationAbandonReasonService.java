package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.QuotationAbandonReason;

public interface QuotationAbandonReasonService {

    public List<QuotationAbandonReason> getQuotationAbandonReasons();

    public QuotationAbandonReason getAbandonReason(Integer id);

    public QuotationAbandonReason addOrUpdateQutationAbandonReason(QuotationAbandonReason abandonReason);
}
