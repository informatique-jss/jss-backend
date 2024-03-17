package com.jss.osiris.modules.reporting.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.reporting.model.IRecoveryReporting;
import com.jss.osiris.modules.reporting.repository.RecoveryReportingRepository;

@Service
public class RecoveryReportingServiceImpl implements RecoveryReportingService {

    @Autowired
    RecoveryReportingRepository recoveryReportingRepository;

    @Autowired
    ConstantService constantService;

    @Autowired
    ReportingHelper<IRecoveryReporting> reportingHelper;

    @Override
    public ArrayList<HashMap<String, String>> getRecoveryReporting(ArrayList<String> columns) throws OsirisException {
        return reportingHelper.filterOutputColumns(
                recoveryReportingRepository.getRecoveryReporting(constantService.getInvoiceStatusPayed().getId(),
                        constantService.getInvoiceStatusSend().getId(),
                        constantService.getTiersFollowupTypeInvoiceReminder().getId()),
                columns,
                IRecoveryReporting.class.getDeclaredMethods());
    }

    @Override
    public ArrayList<HashMap<String, String>> getFakeData() {
        return reportingHelper.getFakeData(IRecoveryReporting.class.getDeclaredMethods());
    }

}
