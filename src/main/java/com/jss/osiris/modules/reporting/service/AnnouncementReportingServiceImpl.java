package com.jss.osiris.modules.reporting.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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

    @Autowired
    ReportingHelper<IAnnouncementReporting> reportingHelper;

    @Override
    public ArrayList<HashMap<String, String>> getAnnouncementReporting(ArrayList<String> columns)
            throws OsirisException {
        return reportingHelper.filterOutputColumns(announcementReportingRepository.getAnnouncementReporting(
                Arrays.asList(customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.ABANDONED)
                        .getId())),
                columns,
                IAnnouncementReporting.class.getDeclaredMethods());
    }

    @Override
    public ArrayList<HashMap<String, String>> getFakeData() {
        return reportingHelper.getFakeData(IAnnouncementReporting.class.getDeclaredMethods());
    }

}
