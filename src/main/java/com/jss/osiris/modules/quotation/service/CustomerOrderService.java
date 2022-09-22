package com.jss.osiris.modules.quotation.service;

import com.jss.osiris.modules.quotation.model.CustomerOrder;

public interface CustomerOrderService {
    public CustomerOrder getCustomerOrder(Integer id);

    public CustomerOrder addOrUpdateCustomerOrder(CustomerOrder quotation) throws Exception;

    public CustomerOrder addOrUpdateCustomerOrderStatus(CustomerOrder customerOrder, String targetStatusCode)
            throws Exception;

}
