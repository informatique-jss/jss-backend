package com.jss.osiris.modules.osiris.crm.model;

import java.time.LocalDate;
import java.util.List;

import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public interface IKpiCrm {

    public String getCode();

    public String getLabel();

    public String getAggregateType();

    public List<KpiCrmValue> getComputeValue(Responsable responsable, LocalDate startDate, LocalDate enDate);

    public AnalyticStatsType getKpiCrmAggregatedValue(List<Responsable> responsables, LocalDate startDate,
            LocalDate endDate);
}
