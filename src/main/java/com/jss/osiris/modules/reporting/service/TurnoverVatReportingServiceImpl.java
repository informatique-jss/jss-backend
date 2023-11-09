package com.jss.osiris.modules.reporting.service;

import java.util.Arrays;
import java.util.List;

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

    @Override
    public List<ITurnoverVatReporting> getTurnoverVatReporting() throws OsirisException {
        return turnoverVatReportingRepository.getTurnoverVatReporting(Arrays.asList(
                constantService.getInvoiceStatusPayed().getId(), constantService.getInvoiceStatusSend().getId(),
                constantService.getInvoiceStatusCreditNoteEmited().getId(),
                constantService.getInvoiceStatusCancelled().getId()));
    }

}
