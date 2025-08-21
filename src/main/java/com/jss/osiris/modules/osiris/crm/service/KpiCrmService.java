package com.jss.osiris.modules.osiris.crm.service;

import java.time.LocalDate;
import java.util.List;

import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public interface KpiCrmService {

    public List<KpiCrm> getTiersAverageKpi(List<Responsable> responsables, LocalDate starDate, LocalDate endDate);

    public List<KpiCrm> getTiersCumulKpi(List<Responsable> responsables, LocalDate startDate, LocalDate endDate);
}
