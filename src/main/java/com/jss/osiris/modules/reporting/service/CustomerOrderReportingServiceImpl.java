package com.jss.osiris.modules.reporting.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.service.CustomerOrderStatusService;
import com.jss.osiris.modules.reporting.model.ICustomerOrderReporting;
import com.jss.osiris.modules.reporting.repository.CustomerOrderReportingRepository;

@Service
public class CustomerOrderReportingServiceImpl implements CustomerOrderReportingService {

    @Autowired
    CustomerOrderReportingRepository customerOrderReportingRepository;

    @Autowired
    CustomerOrderStatusService customerOrderStatusService;

    @Autowired
    ConstantService constantService;

    @Override
    public List<ICustomerOrderReporting> getCustomerOrderReporting(Integer tiersId) throws OsirisException {
        ArrayList<Integer> invoiceStatusIds = new ArrayList<Integer>();
        invoiceStatusIds.add(constantService.getInvoiceStatusSend().getId());
        invoiceStatusIds.add(constantService.getInvoiceStatusPayed().getId());
        return customerOrderReportingRepository.getCustomerOrderReporting(
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.ABANDONED).getId(),
                invoiceStatusIds, tiersId);
    }

}
