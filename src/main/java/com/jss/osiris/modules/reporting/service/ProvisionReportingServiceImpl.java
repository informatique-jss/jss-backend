package com.jss.osiris.modules.reporting.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.reporting.model.IProvisionReporting;
import com.jss.osiris.modules.reporting.repository.ProvisionReportingRepository;

@Service
public class ProvisionReportingServiceImpl implements ProvisionReportingService {

    @Autowired
    ProvisionReportingRepository provisionReportingRepository;

    @Override
    public List<IProvisionReporting> getProvisionReporting() throws OsirisException {
        return provisionReportingRepository.getProvisionReporting();
    }

}
