package com.jss.osiris.modules.reporting.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.reporting.model.IProvisionProductionReporting;

public interface ProvisionProductionReportingService {

    List<IProvisionProductionReporting> getProvisionProductionReporting() throws OsirisException;

}
