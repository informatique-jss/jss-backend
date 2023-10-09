package com.jss.osiris.modules.reporting.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.reporting.model.ICustomerOrderReporting;
import com.jss.osiris.modules.reporting.repository.CustomerOrderReportingRepository;

@Service
public class CustomerOrderReportingServiceImpl implements CustomerOrderReportingService {

    @Autowired
    CustomerOrderReportingRepository customerOrderReportingRepository;

    @Override
    public List<ICustomerOrderReporting> getCustomerOrderReporting() throws OsirisException {
        return customerOrderReportingRepository.getCustomerOrderReporting();
    }

}
