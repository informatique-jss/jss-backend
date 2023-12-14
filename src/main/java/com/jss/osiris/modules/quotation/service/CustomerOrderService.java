package com.jss.osiris.modules.quotation.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.quotation.model.Announcement;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.IQuotation;
import com.jss.osiris.modules.quotation.model.OrderingSearch;
import com.jss.osiris.modules.quotation.model.OrderingSearchResult;
import com.jss.osiris.modules.quotation.model.Quotation;

public interface CustomerOrderService {
        public CustomerOrder getCustomerOrder(Integer id);

        public CustomerOrder getCustomerOrderForAnnouncement(Announcement announcement);

        public CustomerOrder checkAllProvisionEnded(CustomerOrder customerOrderIn)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public CustomerOrder addOrUpdateCustomerOrder(CustomerOrder quotation, boolean isFromUser,
                        boolean checkAllProvisionEnded)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public CustomerOrder addOrUpdateCustomerOrderFromUser(CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void generateCreditNoteForCustomerOrderInvoice(CustomerOrder customerOrder, Invoice invoiceToRefund)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void reinitInvoicing(CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public CustomerOrder addOrUpdateCustomerOrderStatus(CustomerOrder customerOrder, String targetStatusCode,
                        boolean isFromUser)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public CustomerOrder addOrUpdateCustomerOrderStatusFromUser(CustomerOrder customerOrder,
                        String targetStatusCode)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public List<OrderingSearchResult> searchOrders(OrderingSearch orderingSearch);

        public void reindexCustomerOrder();

        public CustomerOrder createNewCustomerOrderFromQuotation(Quotation quotation)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void generateInvoiceMail(CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public void sendInvoiceMail(CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public String getCardPaymentLinkForCustomerOrderDeposit(CustomerOrder customerOrder, String mail,
                        String subject)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public String getCardPaymentLinkForPaymentInvoice(CustomerOrder customerOrder, String mail, String subject)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public void sendRemindersForCustomerOrderDeposit()
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public CustomerOrder unlockCustomerOrderFromDeposit(CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public Boolean validateCardPaymentLinkForCustomerOrder(CustomerOrder customerOrder,
                        com.jss.osiris.modules.quotation.model.CentralPayPaymentRequest request)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public Float getTotalForCustomerOrder(IQuotation customerOrder);

        public Float getRemainingAmountToPayForCustomerOrder(CustomerOrder customerOrder);

        public ResponseEntity<byte[]> printMailingLabel(List<String> customerOrders, boolean printLabel,
                        boolean printLetters)
                        throws OsirisException, OsirisClientMessageException;

        public void updateAssignedToForCustomerOrder(CustomerOrder customerOrder, Employee employee)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public List<OrderingSearchResult> searchByQuotationId(Integer idQuotation);

        public void offerCustomerOrder(CustomerOrder customerOrder) throws OsirisException,
                        OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException;

}
