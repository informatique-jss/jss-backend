package com.jss.osiris.modules.reporting.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.service.CustomerOrderStatusService;
import com.jss.osiris.modules.reporting.model.IAnnouncementReporting;
import com.jss.osiris.modules.reporting.repository.AnnouncementReportingRepository;

@Service
public class AnnouncementReportingServiceImpl implements AnnouncementReportingService {

    @Autowired
    AnnouncementReportingRepository announcementReportingRepository;

    @Autowired
    CustomerOrderStatusService customerOrderStatusService;

    @Override
    public List<IAnnouncementReporting> getAnnouncementReporting() throws OsirisException {
        return announcementReportingRepository.getAnnouncementReporting(
                Arrays.asList(customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.ABANDONED)
                        .getId()));
    }

}
