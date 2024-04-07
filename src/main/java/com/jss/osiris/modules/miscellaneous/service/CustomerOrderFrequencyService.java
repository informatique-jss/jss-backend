package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.CustomerOrderFrequency;

public interface CustomerOrderFrequencyService {
    public List<CustomerOrderFrequency> getCustomerOrderFrequencies();

    public CustomerOrderFrequency getCustomerOrderFrequency(Integer id);
	
	 public CustomerOrderFrequency addOrUpdateCustomerOrderFrequency(CustomerOrderFrequency customerOrderFrequency);
}
