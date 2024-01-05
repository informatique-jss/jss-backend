package com.jss.osiris.modules.reporting.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.reporting.model.ITurnoverReporting;
import com.jss.osiris.modules.reporting.repository.TurnoverReportingRepository;

@Service
public class TurnoverReportingServiceImpl implements TurnoverReportingService {

    @Autowired
    TurnoverReportingRepository turnoverReportingRepository;

    @Autowired
    ConstantService constantService;

    @Autowired
    ReportingHelper<ITurnoverReporting> reportingHelper;

    @Override
    public ArrayList<HashMap<String, String>> getTurnoverReporting(ArrayList<String> columns) throws OsirisException {
        return reportingHelper.filterOutputColumns(turnoverReportingRepository.getTurnoverReporting(Arrays.asList(
                constantService.getInvoiceStatusPayed().getId(), constantService.getInvoiceStatusSend().getId(),
                constantService.getInvoiceStatusCreditNoteEmited().getId(),
                constantService.getInvoiceStatusCancelled().getId())), columns,
                ITurnoverReporting.class.getDeclaredMethods());
    }

    @Override
    public ArrayList<HashMap<String, String>> getFakeData() {
        return reportingHelper.getFakeData(ITurnoverReporting.class.getDeclaredMethods());
    }

}
