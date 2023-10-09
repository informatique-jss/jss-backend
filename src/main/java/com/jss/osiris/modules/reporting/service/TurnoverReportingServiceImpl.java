package com.jss.osiris.modules.reporting.service;

import java.util.Arrays;
import java.util.List;

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

    @Override
    public List<ITurnoverReporting> getTurnoverReporting() throws OsirisException {
        return turnoverReportingRepository.getTurnoverReporting(Arrays.asList(
                constantService.getInvoiceStatusPayed().getId(), constantService.getInvoiceStatusSend().getId(),
                constantService.getInvoiceStatusCreditNoteEmited().getId()));
    }

}
