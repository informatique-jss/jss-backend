package com.jss.osiris.modules.reporting.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.reporting.model.ITiersReporting;
import com.jss.osiris.modules.reporting.repository.TiersReportingRepository;

@Service
public class TiersReportingServiceImpl implements TiersReportingService {

    @Autowired
    TiersReportingRepository tiersReportingRepository;

    @Override
    public List<ITiersReporting> getTiersReporting() throws OsirisException {
        return tiersReportingRepository.getTiersReporting();
    }

}
