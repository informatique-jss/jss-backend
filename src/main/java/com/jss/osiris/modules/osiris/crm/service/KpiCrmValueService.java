package com.jss.osiris.modules.osiris.crm.service;

import java.time.LocalDate;
import java.util.List;

import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public interface KpiCrmValueService {

    public KpiCrmValue getKpiCrmValue(Integer id);

    public KpiCrmValue getLastKpiCrmValueByKpiCrm(Integer id);

    public KpiCrmValue addOrUpdateKpiCrmValue(KpiCrmValue kpiCrmValue);

    public void addOrUpdateKpiCrmValues(List<KpiCrmValue> kpiCrmValues);

    public List<KpiCrmValue> getValuesForKpiCrmAndResponsablesAndDates(KpiCrm kpiCrm, List<Responsable> responsables,
            LocalDate startDate, LocalDate endDate);
}
