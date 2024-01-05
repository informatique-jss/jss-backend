package com.jss.osiris.modules.reporting.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.reporting.model.IProvisionProductionReporting;
import com.jss.osiris.modules.reporting.repository.ProvisionProductionReportingRepository;

@Service
public class ProvisionProductionReportingServiceImpl implements ProvisionProductionReportingService {

    @Autowired
    ProvisionProductionReportingRepository provisionProductionReportingRepository;

    @Autowired
    ReportingHelper<IProvisionProductionReporting> reportingHelper;

    @Override
    public ArrayList<HashMap<String, String>> getProvisionProductionReporting(ArrayList<String> columns)
            throws OsirisException {
        return reportingHelper.filterOutputColumns(
                provisionProductionReportingRepository.getProvisionProductionReporting(), columns,
                IProvisionProductionReporting.class.getDeclaredMethods());
    }

    @Override
    public ArrayList<HashMap<String, String>> getFakeData() {
        return reportingHelper.getFakeData(IProvisionProductionReporting.class.getDeclaredMethods());
    }
}
