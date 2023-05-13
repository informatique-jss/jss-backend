package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.miscellaneous.model.CustomerOrderOrigin;
import com.jss.osiris.modules.miscellaneous.repository.CustomerOrderOriginRepository;

@Service
public class CustomerOrderOriginServiceImpl implements CustomerOrderOriginService {

    @Autowired
    CustomerOrderOriginRepository customerOrderOriginRepository;

    @Override
    public List<CustomerOrderOrigin> getCustomerOrderOrigins() {
        return IterableUtils.toList(customerOrderOriginRepository.findAll());
    }

    @Override
    public List<CustomerOrderOrigin> getByUsername(String username) {
        return customerOrderOriginRepository.findByUsername(username);
    }

    @Override
    public CustomerOrderOrigin getCustomerOrderOrigin(Integer id) {
        Optional<CustomerOrderOrigin> customerOrderOrigin = customerOrderOriginRepository.findById(id);
        if (customerOrderOrigin.isPresent())
            return customerOrderOrigin.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerOrderOrigin addOrUpdateCustomerOrderOrigin(
            CustomerOrderOrigin customerOrderOrigin) {
        return customerOrderOriginRepository.save(customerOrderOrigin);
    }
}
