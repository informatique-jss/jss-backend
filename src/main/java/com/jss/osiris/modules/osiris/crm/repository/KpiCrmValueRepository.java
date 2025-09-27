package com.jss.osiris.modules.osiris.crm.repository;

import java.time.LocalDate;
import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public interface KpiCrmValueRepository extends QueryCacheCrudRepository<KpiCrmValue, Integer> {
    List<KpiCrmValue> findByKpiCrmAndResponsableInAndValueDateBetween(KpiCrm kpiCrm, List<Responsable> responsables,
            LocalDate startDate, LocalDate endDate);
}
