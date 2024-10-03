package com.jss.osiris.modules.osiris.reporting.service;

import java.util.ArrayList;
import java.util.HashMap;

import com.jss.osiris.libs.exception.OsirisException;

public interface ProvisionProductionReportingService {

    ArrayList<HashMap<String, String>> getProvisionProductionReporting(ArrayList<String> columns)
            throws OsirisException;

    ArrayList<HashMap<String, String>> getFakeData();

}
