package com.jss.osiris.modules.reporting.service;

import java.util.ArrayList;
import java.util.HashMap;

import com.jss.osiris.libs.exception.OsirisException;

public interface TurnoverReportingService {

    ArrayList<HashMap<String, String>> getTurnoverReporting(ArrayList<String> columns) throws OsirisException;

    ArrayList<HashMap<String, String>> getFakeData();

}
