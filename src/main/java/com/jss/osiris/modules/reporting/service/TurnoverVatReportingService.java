package com.jss.osiris.modules.reporting.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.reporting.model.ITurnoverVatReporting;

public interface TurnoverVatReportingService {

    List<ITurnoverVatReporting> getTurnoverVatReporting() throws OsirisException;

}