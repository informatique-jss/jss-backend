package com.jss.osiris.modules.osiris.crm.service;

import java.time.LocalDate;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.crm.model.AnalyticStatsType;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public interface KpiCrmService {

    public KpiCrm getKpiCrmByCode(String code);

    public KpiCrm getKpiCrmById(Integer id);

    public void updateIndicatorsValues() throws OsirisException;

    public void computeKpiCrm(Integer responsableId, LocalDate dateToCompute);

    public List<AnalyticStatsType> getAggregatedKpis(KpiCrm kpiCrm, List<Responsable> responsables,
            LocalDate startDate, LocalDate endDate);

    public List<KpiCrm> getKpiCrms();

}
