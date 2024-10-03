package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.quotation.model.Announcement;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.QuotationSearch;
import com.jss.osiris.modules.osiris.quotation.model.QuotationSearchResult;

public interface QuotationService {
        public Quotation getQuotation(Integer id);

        public Quotation getQuotationForAnnouncement(Announcement announcement);

        public Quotation addOrUpdateQuotation(Quotation quotation)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public Quotation addOrUpdateQuotationFromUser(Quotation quotation)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public Quotation addOrUpdateQuotationStatus(Quotation quotation, String targetStatusCode)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public List<QuotationSearchResult> searchQuotations(QuotationSearch orderingSearch);

        public void reindexQuotation() throws OsirisException;

        public String getCardPaymentLinkForQuotationDeposit(Quotation quotation, String mail,
                        String subject)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public Boolean validateCardPaymentLinkForQuotationDeposit(Quotation quotation,
                        com.jss.osiris.modules.osiris.quotation.model.CentralPayPaymentRequest request)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void sendRemindersForQuotation() throws OsirisException;

        public void sendReminderForQuotation(Quotation quotation)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public void updateAssignedToForQuotation(Quotation quotation, Employee employee)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public List<QuotationSearchResult> searchByCustomerOrderId(Integer idCustomerOrder);

        public boolean getIsOpenedQuotation(IQuotation quotation);

        public void validateQuotationFromCustomer(Quotation quotation)
                        throws OsirisValidationException, OsirisException, OsirisClientMessageException,
                        OsirisDuplicateException;

        public Quotation unlockQuotationFromDeposit(Quotation quotation)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public Quotation associateCustomerOrderToQuotation(Quotation quotation, CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public Quotation generateQuotationPdf(Quotation quotation) throws OsirisClientMessageException,
                        OsirisValidationException, OsirisDuplicateException, OsirisException;
}
