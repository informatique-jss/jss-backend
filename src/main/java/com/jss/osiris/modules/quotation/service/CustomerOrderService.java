package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.OrderingSearch;
import com.jss.osiris.modules.quotation.model.OrderingSearchResult;
import com.jss.osiris.modules.quotation.model.Quotation;

public interface CustomerOrderService {
        public CustomerOrder getCustomerOrder(Integer id);

        public CustomerOrder checkAllProvisionEnded(CustomerOrder customerOrderIn) throws Exception;

        public CustomerOrder addOrUpdateCustomerOrder(CustomerOrder quotation) throws Exception;

        public CustomerOrder addOrUpdateCustomerOrderFromUser(CustomerOrder customerOrder) throws Exception;

        public CustomerOrder addOrUpdateCustomerOrderStatus(CustomerOrder customerOrder, String targetStatusCode)
                        throws Exception;

        public CustomerOrder addOrUpdateCustomerOrderStatusFromUser(CustomerOrder customerOrder,
                        String targetStatusCode)
                        throws Exception;

        public List<OrderingSearchResult> searchOrders(OrderingSearch orderingSearch);

        public void reindexCustomerOrder();

        public CustomerOrder createNewCustomerOrderFromQuotation(Quotation quotation) throws Exception;

        public void generateInvoiceMail(CustomerOrder customerOrder) throws Exception;

        public CustomerOrder unlockCustomerOrderFromDeposit(CustomerOrder customerOrder, Float effectivePayment)
                        throws Exception;

        public String getCardPaymentLinkForPaymentDeposit(CustomerOrder customerOrder, String mail, String subject)
                        throws Exception;

        public Boolean validateCardPaymentLinkForDeposit(CustomerOrder customerOrder) throws Exception;

        public String getCardPaymentLinkForPaymentInvoice(CustomerOrder customerOrder, String mail, String subject)
                        throws Exception;

        public Boolean validateCardPaymentLinkForInvoice(CustomerOrder customerOrder) throws Exception;

}
