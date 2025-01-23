package com.jss.osiris.modules.osiris.reporting.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.reporting.model.IQuotationReporting;
import com.jss.osiris.modules.osiris.reporting.repository.QuotationReportingRepository;

@Service
public class QuotationReportingServiceImpl implements QuotationReportingService {

    @Autowired
    QuotationReportingRepository quotationReportingRepository;

    @Autowired
    ReportingHelper<IQuotationReporting> reportingHelper;

    @Override
    public ArrayList<HashMap<String, String>> getQuotationReporting(ArrayList<String> columns) throws OsirisException {
        return reportingHelper.filterOutputColumns(quotationReportingRepository.getQuotationReporting(), columns,
                IQuotationReporting.class.getDeclaredMethods());
    }

    @Override
    public ArrayList<HashMap<String, String>> getFakeData() {
        return reportingHelper.getFakeData(IQuotationReporting.class.getDeclaredMethods());
    }
}
