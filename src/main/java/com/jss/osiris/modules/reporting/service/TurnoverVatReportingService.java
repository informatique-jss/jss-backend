package com.jss.osiris.modules.reporting.service;

import java.util.ArrayList;
import java.util.HashMap;

import com.jss.osiris.libs.exception.OsirisException;

public interface TurnoverVatReportingService {

    ArrayList<HashMap<String, String>> getTurnoverVatReporting(ArrayList<String> columns) throws OsirisException;

    ArrayList<HashMap<String, String>> getFakeData();

}
