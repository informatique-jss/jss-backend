package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.quotation.model.IQuotation;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.model.QuotationSearch;
import com.jss.osiris.modules.quotation.model.QuotationSearchResult;
import com.jss.osiris.modules.tiers.model.ITiers;

public interface QuotationService {
        public Quotation getQuotation(Integer id);

        public Quotation addOrUpdateQuotation(Quotation quotation)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public Quotation addOrUpdateQuotationFromUser(Quotation quotation)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public Quotation addOrUpdateQuotationStatus(Quotation quotation, String targetStatusCode)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public ITiers getCustomerOrderOfQuotation(IQuotation quotation) throws OsirisException;

        public List<QuotationSearchResult> searchQuotations(QuotationSearch orderingSearch);

        public void reindexQuotation();

        public String getCardPaymentLinkForQuotationDeposit(Quotation quotation, String mail,
                        String subject)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public Boolean validateCardPaymentLinkForQuotationDeposit(Quotation quotation)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public void sendRemindersForQuotation()
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

}
