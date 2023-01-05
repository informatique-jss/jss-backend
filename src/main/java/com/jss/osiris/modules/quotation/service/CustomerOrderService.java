package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.OrderingSearch;
import com.jss.osiris.modules.quotation.model.OrderingSearchResult;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.model.centralPay.CentralPayPaymentRequest;

public interface CustomerOrderService {
        public CustomerOrder getCustomerOrder(Integer id);

        public CustomerOrder checkAllProvisionEnded(CustomerOrder customerOrderIn)
                        throws OsirisException, OsirisClientMessageException;

        public CustomerOrder addOrUpdateCustomerOrder(CustomerOrder quotation)
                        throws OsirisException, OsirisClientMessageException;

        public CustomerOrder addOrUpdateCustomerOrderFromUser(CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException;

        public CustomerOrder addOrUpdateCustomerOrderStatus(CustomerOrder customerOrder, String targetStatusCode,
                        boolean isFromUser)
                        throws OsirisException, OsirisClientMessageException;

        public CustomerOrder addOrUpdateCustomerOrderStatusFromUser(CustomerOrder customerOrder,
                        String targetStatusCode) throws OsirisException, OsirisClientMessageException;

        public List<OrderingSearchResult> searchOrders(OrderingSearch orderingSearch);

        public void reindexCustomerOrder();

        public CustomerOrder createNewCustomerOrderFromQuotation(Quotation quotation)
                        throws OsirisException, OsirisClientMessageException;

        public void generateInvoiceMail(CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException;

        public String getCardPaymentLinkForCustomerOrderDeposit(CustomerOrder customerOrder, String mail,
                        String subject)
                        throws OsirisException, OsirisClientMessageException;

        public String getCardPaymentLinkForPaymentInvoice(CustomerOrder customerOrder, String mail, String subject)
                        throws OsirisException, OsirisClientMessageException;

        public void sendRemindersForCustomerOrderDeposit() throws OsirisException, OsirisClientMessageException;

        public CustomerOrder unlockCustomerOrderFromDeposit(CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException;

        public Boolean validateCardPaymentLinkForCustomerOrder(CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException;

        public Float getRemainingAmountToPayForCustomerOrder(CustomerOrder customerOrder);

        public void generateDepositOnCustomerOrderForCbPayment(CustomerOrder customerOrder,
                        CentralPayPaymentRequest centralPayPaymentRequest)
                        throws OsirisException, OsirisClientMessageException;

}
