package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.miscellaneous.model.CustomerOrderFrequency;
import com.jss.osiris.modules.osiris.miscellaneous.repository.CustomerOrderFrequencyRepository;

@Service
public class CustomerOrderFrequencyServiceImpl implements CustomerOrderFrequencyService {

    @Autowired
    CustomerOrderFrequencyRepository customerOrderFrequencyRepository;

    @Override
    public List<CustomerOrderFrequency> getCustomerOrderFrequencies() {
        return IterableUtils.toList(customerOrderFrequencyRepository.findAll());
    }

    @Override
    public CustomerOrderFrequency getCustomerOrderFrequency(Integer id) {
        Optional<CustomerOrderFrequency> customerOrderFrequency = customerOrderFrequencyRepository.findById(id);
        if (customerOrderFrequency.isPresent())
            return customerOrderFrequency.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerOrderFrequency addOrUpdateCustomerOrderFrequency(
            CustomerOrderFrequency customerOrderFrequency) {
        return customerOrderFrequencyRepository.save(customerOrderFrequency);
    }
}
