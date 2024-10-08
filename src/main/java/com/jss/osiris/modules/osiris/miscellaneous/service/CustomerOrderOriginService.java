package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.CustomerOrderOrigin;

public interface CustomerOrderOriginService {
    public List<CustomerOrderOrigin> getCustomerOrderOrigins();

    public CustomerOrderOrigin getCustomerOrderOrigin(Integer id);

    public CustomerOrderOrigin addOrUpdateCustomerOrderOrigin(CustomerOrderOrigin customerOrderOrigin);

    public List<CustomerOrderOrigin> getByUsername(String username);
}
