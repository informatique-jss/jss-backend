package com.jss.osiris.modules.myjss.quotation.service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.myjss.quotation.controller.model.DashboardUserStatistics;

public interface DashboardUserStatisticsService {

    public DashboardUserStatistics getDashboardUserStatistics() throws OsirisException;

}
