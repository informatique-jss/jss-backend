package com.jss.osiris.modules.reporting.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.service.CustomerOrderStatusService;
import com.jss.osiris.modules.reporting.model.IQuotationReporting;
import com.jss.osiris.modules.reporting.repository.QuotationReportingRepository;

@Service
public class QuotationReportingServiceImpl implements QuotationReportingService {

    @Autowired
    QuotationReportingRepository quotationReportingRepository;

    @Autowired
    CustomerOrderStatusService customerOrderStatusService;

    @Autowired
    ConstantService constantService;

    @Override
    @Cacheable(value = "quotationReportingList", key = "#tiersId")
    public List<IQuotationReporting> getQuotationReporting(Integer tiersId) throws OsirisException {
        if (tiersId == null)
            tiersId = 0;

        ArrayList<Integer> invoiceStatusIds = new ArrayList<Integer>();
        invoiceStatusIds.add(constantService.getInvoiceStatusSend().getId());
        invoiceStatusIds.add(constantService.getInvoiceStatusPayed().getId());
        return quotationReportingRepository.getQuotationReporting(
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.ABANDONED).getId(),
                invoiceStatusIds, tiersId);
    }

}
