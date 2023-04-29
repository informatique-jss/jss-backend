package com.jss.osiris.modules.reporting.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.reporting.model.IVatReporting;
import com.jss.osiris.modules.reporting.repository.VatReportingRepository;

@Service
public class VatReportingServiceImpl implements VatReportingService {

    @Autowired
    VatReportingRepository vatReportingRepository;

    @Override
    public List<IVatReporting> getVatReporting() throws OsirisException {
        return vatReportingRepository.getVatReporting();
    }

}
