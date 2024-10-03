package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.CustomerOrderFrequency;

public interface CustomerOrderFrequencyService {
    public List<CustomerOrderFrequency> getCustomerOrderFrequencies();

    public CustomerOrderFrequency getCustomerOrderFrequency(Integer id);

    public CustomerOrderFrequency addOrUpdateCustomerOrderFrequency(CustomerOrderFrequency customerOrderFrequency);
}
