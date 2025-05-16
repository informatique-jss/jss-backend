package com.jss.osiris.modules.osiris.reporting.service;

import java.time.LocalDate;
import java.util.List;

import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.reporting.model.Indicator;
import com.jss.osiris.modules.osiris.reporting.model.Kpi;

public interface KpiService {
    public List<Kpi> getKpis();

    public Kpi getKpi(Integer id);

    public Kpi addOrUpdateKpi(Kpi kpi);

    public Kpi getKpiForEmployeeAndDate(Indicator indicator, Employee employee, LocalDate date);
}
