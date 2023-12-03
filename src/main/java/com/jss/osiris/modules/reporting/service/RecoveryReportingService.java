package com.jss.osiris.modules.reporting.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.reporting.model.IRecoveryReporting;

public interface RecoveryReportingService {

    List<IRecoveryReporting> getRecoveryReporting() throws OsirisException;

}
