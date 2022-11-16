package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.IQuotation;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.model.QuotationSearch;
import com.jss.osiris.modules.quotation.model.QuotationSearchResult;
import com.jss.osiris.modules.tiers.model.ITiers;

public interface QuotationService {
    public Quotation getQuotation(Integer id);

    public Quotation addOrUpdateQuotation(Quotation quotation) throws Exception;

    public Quotation addOrUpdateQuotationFromUser(Quotation quotation) throws Exception;

    public IQuotation getAndSetInvoiceItemsForQuotation(IQuotation quotation) throws Exception;

    public Quotation addOrUpdateQuotationStatus(Quotation quotation, String targetStatusCode) throws Exception;

    public ITiers getCustomerOrderOfQuotation(IQuotation quotation) throws Exception;

    public List<QuotationSearchResult> searchQuotations(QuotationSearch orderingSearch);

    public void reindexQuotation();
}
