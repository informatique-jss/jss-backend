package com.jss.osiris.modules.osiris.crm.repository;

import java.time.LocalDate;
import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public interface KpiCrmRepository extends QueryCacheCrudRepository<KpiCrm, Integer> {
    List<KpiCrm> findByResponsableAndCodeAndCreatedDateBetween(List<Responsable> responsables,
            String kpiCode, LocalDate starDate,
            LocalDate endDate);
}
