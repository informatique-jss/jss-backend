package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.OrderingSearch;
import com.jss.osiris.modules.quotation.model.OrderingSearchResult;
import com.jss.osiris.modules.quotation.model.Quotation;

public interface CustomerOrderService {
        public CustomerOrder getCustomerOrder(Integer id);

        public CustomerOrder checkAllProvisionEnded(CustomerOrder customerOrderIn) throws OsirisException;

        public CustomerOrder addOrUpdateCustomerOrder(CustomerOrder quotation) throws OsirisException;

        public CustomerOrder addOrUpdateCustomerOrderFromUser(CustomerOrder customerOrder) throws OsirisException;

        public CustomerOrder addOrUpdateCustomerOrderStatus(CustomerOrder customerOrder, String targetStatusCode,
                        boolean isFromUser)
                        throws OsirisException;

        public CustomerOrder addOrUpdateCustomerOrderStatusFromUser(CustomerOrder customerOrder,
                        String targetStatusCode) throws OsirisException;

        public List<OrderingSearchResult> searchOrders(OrderingSearch orderingSearch);

        public void reindexCustomerOrder();

        public CustomerOrder createNewCustomerOrderFromQuotation(Quotation quotation)
                        throws OsirisException;

        public void generateInvoiceMail(CustomerOrder customerOrder) throws OsirisException;

        public CustomerOrder unlockCustomerOrderFromDeposit(CustomerOrder customerOrder, Float effectivePayment)
                        throws OsirisException;

        public String getCardPaymentLinkForCustomerOrderDeposit(CustomerOrder customerOrder, String mail,
                        String subject)
                        throws OsirisException;

        public Boolean validateCardPaymentLinkForCustomerOrderDeposit(CustomerOrder customerOrder)
                        throws OsirisException;

        public String getCardPaymentLinkForPaymentInvoice(CustomerOrder customerOrder, String mail, String subject)
                        throws OsirisException;

        public Boolean validateCardPaymentLinkForInvoice(CustomerOrder customerOrder) throws OsirisException;

        public void sendRemindersForCustomerOrderDeposit() throws OsirisException;

}
