package com.jss.osiris.modules.myjss.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.myjss.quotation.controller.model.DashboardUserStatistics;

public interface DashboardUserStatisticsService {

    public DashboardUserStatistics getDashboardUserStatistics(List<Integer> responsableIds) throws OsirisException;

}
