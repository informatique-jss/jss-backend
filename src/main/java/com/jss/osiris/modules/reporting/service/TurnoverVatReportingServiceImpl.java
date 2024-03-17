package com.jss.osiris.modules.reporting.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.reporting.model.ITurnoverVatReporting;
import com.jss.osiris.modules.reporting.repository.TurnoverVatReportingRepository;

@Service
public class TurnoverVatReportingServiceImpl implements TurnoverVatReportingService {

    @Autowired
    TurnoverVatReportingRepository turnoverVatReportingRepository;

    @Autowired
    ConstantService constantService;

    @Autowired
    ReportingHelper<ITurnoverVatReporting> reportingHelper;

    @Override
    public ArrayList<HashMap<String, String>> getTurnoverVatReporting(ArrayList<String> columns)
            throws OsirisException {
        return reportingHelper.filterOutputColumns(turnoverVatReportingRepository.getTurnoverVatReporting(), columns,
                ITurnoverVatReporting.class.getDeclaredMethods());
    }

    @Override
    public ArrayList<HashMap<String, String>> getFakeData() {
        return reportingHelper.getFakeData(ITurnoverVatReporting.class.getDeclaredMethods());
    }

}
