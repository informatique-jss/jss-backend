package com.jss.osiris.modules.reporting.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.reporting.model.ICustomerOrderReporting;

public interface CustomerOrderReportingService {

    List<ICustomerOrderReporting> getCustomerOrderReporting(Integer tiersId) throws OsirisException;

}
