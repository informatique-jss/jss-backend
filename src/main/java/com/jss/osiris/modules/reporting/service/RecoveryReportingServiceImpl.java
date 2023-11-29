package com.jss.osiris.modules.reporting.service;

import java.util.List;

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

    @Override
    public List<IRecoveryReporting> getRecoveryReporting() throws OsirisException {
        return recoveryReportingRepository.getRecoveryReporting(constantService.getInvoiceStatusPayed().getId(),
                constantService.getInvoiceStatusSend().getId(),
                constantService.getTiersFollowupTypeInvoiceReminder().getId());
    }

}
