package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;

public interface CustomerOrderStatusService {
    public List<CustomerOrderStatus> getCustomerOrderStatus();

    public CustomerOrderStatus getCustomerOrderStatus(Integer id);

    public CustomerOrderStatus addOrUpdateCustomerOrderStatus(CustomerOrderStatus CustomerOrderStatus);

    public CustomerOrderStatus getCustomerOrderStatusByCode(String code);

    public void updateStatusReferential() throws Exception;

}
