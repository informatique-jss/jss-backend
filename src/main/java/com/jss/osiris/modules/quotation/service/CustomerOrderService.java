package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.quotation.model.Announcement;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.IQuotation;
import com.jss.osiris.modules.quotation.model.OrderingSearch;
import com.jss.osiris.modules.quotation.model.OrderingSearchResult;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.model.centralPay.CentralPayPaymentRequest;

public interface CustomerOrderService {
        public CustomerOrder getCustomerOrder(Integer id);

        public CustomerOrder getCustomerOrderForAnnouncement(Announcement announcement);

        public CustomerOrder checkAllProvisionEnded(CustomerOrder customerOrderIn)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public CustomerOrder addOrUpdateCustomerOrder(CustomerOrder quotation, boolean isFromUser,
                        boolean checkAllProvisionEnded)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public CustomerOrder addOrUpdateCustomerOrderFromUser(CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public CustomerOrder addOrUpdateCustomerOrderStatus(CustomerOrder customerOrder, String targetStatusCode,
                        boolean isFromUser)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public CustomerOrder addOrUpdateCustomerOrderStatusFromUser(CustomerOrder customerOrder,
                        String targetStatusCode)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public List<OrderingSearchResult> searchOrders(OrderingSearch orderingSearch);

        public void reindexCustomerOrder();

        public CustomerOrder createNewCustomerOrderFromQuotation(Quotation quotation)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public void generateInvoiceMail(CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public String getCardPaymentLinkForCustomerOrderDeposit(CustomerOrder customerOrder, String mail,
                        String subject)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public String getCardPaymentLinkForPaymentInvoice(CustomerOrder customerOrder, String mail, String subject)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public void sendRemindersForCustomerOrderDeposit()
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public CustomerOrder unlockCustomerOrderFromDeposit(CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public Boolean validateCardPaymentLinkForCustomerOrder(CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public Float getTotalForCustomerOrder(IQuotation customerOrder);

        public Float getRemainingAmountToPayForCustomerOrder(CustomerOrder customerOrder);

        public void generateDepositOnCustomerOrderForCbPayment(CustomerOrder customerOrder,
                        CentralPayPaymentRequest centralPayPaymentRequest)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public void printMailingLabel(List<String> customerOrders)
                        throws OsirisException, OsirisClientMessageException;

        public void updateAssignedToForCustomerOrder(CustomerOrder customerOrder, Employee employee)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

}
