package com.jss.osiris.modules.reporting.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.reporting.model.IProvisionProductionReporting;
import com.jss.osiris.modules.reporting.repository.ProvisionProductionReportingRepository;

@Service
public class ProvisionProductionReportingServiceImpl implements ProvisionProductionReportingService {

    @Autowired
    ProvisionProductionReportingRepository provisionProductionReportingRepository;

    @Override
    public List<IProvisionProductionReporting> getProvisionProductionReporting() throws OsirisException {
        return provisionProductionReportingRepository.getProvisionProductionReporting();
    }

}
