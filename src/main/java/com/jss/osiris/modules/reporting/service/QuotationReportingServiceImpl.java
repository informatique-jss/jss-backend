package com.jss.osiris.modules.reporting.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.reporting.model.IQuotationReporting;
import com.jss.osiris.modules.reporting.repository.QuotationReportingRepository;

@Service
public class QuotationReportingServiceImpl implements QuotationReportingService {

    @Autowired
    QuotationReportingRepository quotationReportingRepository;

    @Override
    public List<IQuotationReporting> getQuotationReporting() {
        return quotationReportingRepository.getQuotationReporting();
    }

}
