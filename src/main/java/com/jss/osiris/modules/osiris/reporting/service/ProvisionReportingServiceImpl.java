package com.jss.osiris.modules.osiris.reporting.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderStatusService;
import com.jss.osiris.modules.osiris.reporting.model.IProvisionReporting;
import com.jss.osiris.modules.osiris.reporting.repository.ProvisionReportingRepository;

@Service
public class ProvisionReportingServiceImpl implements ProvisionReportingService {

    @Autowired
    ProvisionReportingRepository provisionReportingRepository;

    @Autowired
    CustomerOrderStatusService customerOrderStatusService;

    @Autowired
    ReportingHelper<IProvisionReporting> reportingHelper;

    @Override
    public ArrayList<HashMap<String, String>> getProvisionReporting(ArrayList<String> columns) throws OsirisException {
        return reportingHelper.filterOutputColumns(provisionReportingRepository.getProvisionReporting(
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.BILLED).getId()), columns,
                IProvisionReporting.class.getDeclaredMethods());
    }

    @Override
    public ArrayList<HashMap<String, String>> getFakeData() {
        return reportingHelper.getFakeData(IProvisionReporting.class.getDeclaredMethods());
    }
}
