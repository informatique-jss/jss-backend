package com.jss.osiris.modules.reporting.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.service.CustomerOrderStatusService;
import com.jss.osiris.modules.reporting.model.IProvisionReporting;
import com.jss.osiris.modules.reporting.repository.ProvisionReportingRepository;

@Service
public class ProvisionReportingServiceImpl implements ProvisionReportingService {

    @Autowired
    ProvisionReportingRepository provisionReportingRepository;

    @Autowired
    CustomerOrderStatusService customerOrderStatusService;

    @Override
    public List<IProvisionReporting> getProvisionReporting() throws OsirisException {
        return provisionReportingRepository.getProvisionReporting(
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.BILLED).getId());
    }

}
