package com.jss.osiris.modules.osiris.reporting.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.reporting.model.ICustomerOrderReporting;
import com.jss.osiris.modules.osiris.reporting.repository.CustomerOrderReportingRepository;

@Service
public class CustomerOrderReportingServiceImpl implements CustomerOrderReportingService {

    @Autowired
    CustomerOrderReportingRepository customerOrderReportingRepository;

    @Autowired
    ReportingHelper<ICustomerOrderReporting> reportingHelper;

    @Override
    public ArrayList<HashMap<String, String>> getCustomerOrderReporting(ArrayList<String> columns)
            throws OsirisException {
        return reportingHelper.filterOutputColumns(customerOrderReportingRepository.getCustomerOrderReporting(),
                columns,
                ICustomerOrderReporting.class.getDeclaredMethods());
    }

    @Override
    public ArrayList<HashMap<String, String>> getFakeData() {
        return reportingHelper.getFakeData(ICustomerOrderReporting.class.getDeclaredMethods());
    }
}
