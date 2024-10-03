package com.jss.osiris.modules.osiris.reporting.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.reporting.model.ITiersReporting;
import com.jss.osiris.modules.osiris.reporting.repository.TiersReportingRepository;

@Service
public class TiersReportingServiceImpl implements TiersReportingService {

    @Autowired
    TiersReportingRepository tiersReportingRepository;

    @Autowired
    ReportingHelper<ITiersReporting> reportingHelper;

    @Override
    public ArrayList<HashMap<String, String>> getTiersReporting(ArrayList<String> columns) throws OsirisException {
        return reportingHelper.filterOutputColumns(tiersReportingRepository.getTiersReporting(), columns,
                ITiersReporting.class.getDeclaredMethods());
    }

    @Override
    public ArrayList<HashMap<String, String>> getFakeData() {
        return reportingHelper.getFakeData(ITiersReporting.class.getDeclaredMethods());
    }

}
